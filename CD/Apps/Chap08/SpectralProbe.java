/***************************************************************************

  SpectralProbe.java   Version 1.0 [1999/08/02]

  This program reads an image from a file named on the command line and
  performs an FFT to compute the spectrum of a small, square region of
  interest, defined by the user.  The logarithmically scaled spectrum is
  displayed alongside the image.  A ROI is defined by clicking at a
  particular location in the image.  A control panel is provided to allow
  different ROI sizes and windowing functions to be selected.


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


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import javax.swing.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;



public class SpectralProbe extends JFrame {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private BufferedImage image;                 // input image
  private Rectangle imageArea;                 // bounds of image
  private ViewWithROI imageView;               // image display component
  private Point centre = new Point();          // centre of ROI
  private Rectangle region = new Rectangle();  // current ROI
  private int window = ImageFFT.NO_WINDOW;     // windowing function
  private JLabel spectrumView;                 // spectrum display component


  ////////////////////////////// INNER CLASSES /////////////////////////////


  // Wraps around image display component and handles mouse events

  class Display extends JPanel {
    public Display() {
      add(imageView);
      addMouseListener(new MouseAdapter() {
         public void mouseReleased(MouseEvent event) {
           changeRegion(event.getPoint());
         }
       });
    }
  }


  // Provides a control panel for the application

  class Controls extends JPanel implements ActionListener {

    private JComboBox sizeSelector = new JComboBox();
    private JComboBox windowSelector = new JComboBox();

    public Controls() {
      add(new JLabel("Region size "));
      String[] sizes = { "16", "32", "64" };
      for (int i = 0; i < sizes.length; ++i)
        sizeSelector.addItem(sizes[i]);
      sizeSelector.addActionListener(this);
      add(sizeSelector);
      add(new JLabel(" Window "));
      String[] windows = { "none", "Bartlett", "Hamming", "Hanning" };
      for (int i = 0; i < windows.length; ++i)
        windowSelector.addItem(windows[i]);
      windowSelector.addActionListener(this);
      add(windowSelector);
    }

    public void actionPerformed(ActionEvent event) {
      String item = (String) ((JComboBox) event.getSource()).getSelectedItem();
      if (item.equals("16") || item.equals("32") || item.equals("64")) {
        int size = Integer.parseInt(item);
        changeRegion(size);
      }
      else if (item.equals("none")
       && window != ImageFFT.NO_WINDOW) {
        window = ImageFFT.NO_WINDOW;
        spectrumView.setIcon(getRegionSpectrum());
      }
      else if (item.equals("Bartlett")
       && window != ImageFFT.BARTLETT_WINDOW) {
        window = ImageFFT.BARTLETT_WINDOW;
        spectrumView.setIcon(getRegionSpectrum());
      }
      else if (item.equals("Hamming")
       && window != ImageFFT.HAMMING_WINDOW) {
        window = ImageFFT.HAMMING_WINDOW;
        spectrumView.setIcon(getRegionSpectrum());
      }
      else if (item.equals("Hanning")
       && window != ImageFFT.HANNING_WINDOW) {
        window = ImageFFT.HANNING_WINDOW;
        spectrumView.setIcon(getRegionSpectrum());
      }
    }

  }


  ///////////////////////////////// METHODS ////////////////////////////////


  public SpectralProbe(String filename)
   throws IOException, ImageDecoderException, OperationException {

    // Load image from file

    super("SpectralProbe: " + filename);
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    image = input.decodeAsBufferedImage();
    if (image.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new OperationException("image must be 8-bit greyscale");
    imageArea = new Rectangle(0, 0, image.getWidth(), image.getHeight());

    // Add display components and controls to frame

    JPanel viewPane = new JPanel();
    viewPane.setLayout(new FlowLayout());
    imageView = new ViewWithROI(image);
    centre.x = image.getWidth()/2;
    centre.y = image.getHeight()/2;
    region.x = centre.x - 8;
    region.y = centre.y - 8;
    region.width = region.height = 16;
    imageView.setROI(region);
    viewPane.add(new Display());
    spectrumView = new JLabel(getRegionSpectrum());
    viewPane.add(spectrumView);

    Container pane = getContentPane();
    pane.add(viewPane, BorderLayout.CENTER);
    pane.add(new Controls(), BorderLayout.SOUTH);
    addWindowListener(new WindowMonitor());

  }


  // Moves region to a new position and updates spectrum

  public void changeRegion(Point position) {
    int x = position.x - region.width/2;
    int y = position.y - region.height/2;
    if (imageArea.contains(x, y)
     && imageArea.contains(x+region.width-1, y+region.height-1)) {
      centre = position;
      region.x = x;
      region.y = y;
      imageView.setROI(region);
      spectrumView.setIcon(getRegionSpectrum());
    }
  }


  // Changes region size and updates spectrum

  public void changeRegion(int size) {
    int x = centre.x - size/2;
    int y = centre.y - size/2;
    if (imageArea.contains(x, y)
     && imageArea.contains(x+size-1, y+size-1)) {
      region.x = x;
      region.y = y;
      region.width = region.height = size;
      imageView.setROI(region);
      spectrumView.setIcon(getRegionSpectrum());
    }
  }


  // Computes and visualises the spectrum of the current region

  public ImageIcon getRegionSpectrum() {
    BufferedImage roi = image.getSubimage(
     region.x, region.y, region.width, region.height);
    try {
      ImageFFT fft = new ImageFFT(roi, window);
      fft.transform();
      Image regionSpectrum =
       fft.getSpectrum().getScaledInstance(128, 128, Image.SCALE_FAST);
      return new ImageIcon(regionSpectrum);
    }
    catch (FFTException e) {
      return null;
    }
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new SpectralProbe(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java SpectralProbe <imagefile>");
      System.exit(1);
    }
  }


}
