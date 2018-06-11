/***************************************************************************

  LogPolar.java   Version 1.2 [1999/09/17]

  This program simulates an image acquired by a log-polar sensor.  The
  input is a PGM, PPM, PNG, JPEG or SIF image.  A log-polar transformation
  is applied to the image prior to display.  The rows and columns of the
  displayed image correspond to radial and angular coordinates,
  respectively.  As the cursor moves across the image, the panel under
  the image shows the (r,theta) coordinates of the cursor and the
  corresponding (x,y) coordinates of the pixel in the original image.


  Written by Nick Efford.

  Copyright (c) 2000, Pearson Education Ltd.  All rights reserved.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

***************************************************************************/



import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.gui.WindowMonitor;



public class LogPolar extends JFrame implements ActionListener {


  private BufferedImage sourceImage;   // original image
  private BufferedImage logPolarImage; // transformed image
  private Rectangle imageRect;         // rectangle holding image dimensions
  private int numSectors, numRings;    // sampling density in log-polar space
  private float cx, cy;                // image centre coordinates
  private float dr, dtheta;            // increments in r and theta
  private float[] ctheta;              // lookup table for cos
  private float[] stheta;              // lookup table for sin
  private JLabel view;                 // image display component
  private LogPolarInfo info
            = new LogPolarInfo();      // panel holding coordinate data
  private JFileChooser fileChooser
            = new JFileChooser(System.getProperty("user.dir"));


  public LogPolar(String imageFile, int sectors, int rings)
   throws IOException, ImageDecoderException {

    super("LogPolar: " + imageFile);

    // Read image and create a log-polar warping of it

    readImage(imageFile);
    numSectors = sectors;
    numRings = rings;
    computeMappingParameters();
    logPolarImage = createLogPolarImage();

    // Create display component and configure it to track mouse motion

    view = new JLabel(new ImageIcon(logPolarImage));
    view.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent event) {
	Point cursor = event.getPoint();
	float r = dr*cursor.y;
	int x = Math.round(cx + r*ctheta[cursor.x]);
	int y = Math.round(cy + r*stheta[cursor.x]);
	if (imageRect.contains(x, y)) {
	  float theta = dtheta*cursor.x;
	  info.display(r, theta, x, y);
	}
	else
	  info.clearDisplay();
      }
    });

    // Add components to frame

    Container pane = getContentPane();
    pane.add(view, BorderLayout.CENTER);
    pane.add(info, BorderLayout.SOUTH);

    // Create menu

    JMenu menu = new JMenu("File");
    menu.setMnemonic('F');
    JMenuItem item = new JMenuItem("Save image", 'S');
    item.addActionListener(this);
    menu.add(item);
    item = new JMenuItem("Exit", 'X');
    item.addActionListener(this);
    menu.add(item);
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menu);
    setJMenuBar(menuBar);

    fileChooser.setDialogTitle("Save image");
    addWindowListener(new WindowMonitor());

  }


  // Read image from named file

  public void readImage(String imageFile)
   throws IOException, ImageDecoderException {
    ImageDecoder input = ImageFile.createImageDecoder(imageFile);
    sourceImage = input.decodeAsBufferedImage();
  }


  public void computeMappingParameters() {

    // Determine image dimensions and centre

    imageRect =
     new Rectangle(sourceImage.getWidth(), sourceImage.getHeight());
    cx = sourceImage.getWidth() / 2.0f;
    cy = sourceImage.getHeight() / 2.0f;

    // Compute r and theta increments

    dr = (float) (Math.sqrt(cx*cx + cy*cy) / numRings);
    dtheta = 360.0f / numSectors;

    // Compute lookup tables for cos and sin

    ctheta = new float[numSectors];
    stheta = new float[numSectors];
    double theta = 0.0, dt = dtheta*(Math.PI/180.0);
    for (int j = 0; j < numSectors; ++j, theta += dt) {
      ctheta[j] = (float) Math.cos(theta);
      stheta[j] = (float) Math.sin(theta);
    }

  }


  // Create a log-polar warped version of source image

  public BufferedImage createLogPolarImage() {

    BufferedImage logPolarImage =
     new BufferedImage(numSectors, numRings, sourceImage.getType());

    // Fill first ring (r = 0) with data

    int centralValue = sourceImage.getRGB(Math.round(cx), Math.round(cy));
    for (int j = 0; j < numSectors; ++j) {
      logPolarImage.setRGB(j, 0, centralValue);
    }

    // Fill rings with r > 0

    int x, y;
    float r = dr;
    for (int k = 1; k < numRings; ++k, r += dr)
      for (int j = 0; j < numSectors; ++j) {
	x = Math.round(cx + r*ctheta[j]);
	y = Math.round(cy + r*stheta[j]);
	if (imageRect.contains(x, y))
	  logPolarImage.setRGB(j, k, sourceImage.getRGB(x, y));
	else
	  logPolarImage.setRGB(j, k, 0);
      }

    return logPolarImage;

  }


  public void saveImage() {
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        File file = fileChooser.getSelectedFile();
        if (file.exists()) {
          int response = JOptionPane.showConfirmDialog(this,
           "File will be overwritten!  Are you sure?", "File exists",
           JOptionPane.OK_CANCEL_OPTION);
          if (response != JOptionPane.OK_OPTION)
            return;
        }
        ImageEncoder output =
         ImageFile.createImageEncoder(file.getAbsolutePath());
        output.encode(logPolarImage);
        fileChooser.rescanCurrentDirectory();
      }
      catch (ImageEncoderException e) {
        error("Cannot determine an appropriate image format!");
      }
      catch (IOException e) {
        error("Error writing image!");
      }
    }
  }


  // Displays error messages in a dialog box

  public void error(String message) {
    JOptionPane.showMessageDialog(this, message, "Error",
     JOptionPane.ERROR_MESSAGE);
  }


  // Handles menu selections

  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    if (command.equals("Save image")) {
      saveImage();
      repaint();
    }
    else if (command.equals("Exit")) {
      setVisible(false);
      dispose();
      System.exit(0);
    }
  }


  public static void main(String[] argv) {
    if (argv.length > 2) {
      try {
	int numSectors = Integer.parseInt(argv[1]);
	int numRings = Integer.parseInt(argv[2]);
	JFrame frame = new LogPolar(argv[0], numSectors, numRings);
	frame.pack();
	frame.setVisible(true);
      }
      catch (Exception e) {
	System.err.println(e);
	System.exit(1);
      }
    }
    else {
      System.err.println("usage: java LogPolar <imagefile> <sectors> <rings>");
      System.exit(1);
    }
  }


}
