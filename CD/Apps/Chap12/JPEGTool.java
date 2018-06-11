/***************************************************************************

  JPEGTool.java   Version 1.0 [1999/09/06]

  This program reads an image from a named file and displays it in a
  tabbed pane.  Also displayed is a version of the input image that has
  gone through one cycle of JPEG compression and decompression.  The
  compression quality, a value between 0.0 and 1.0, can be varied using
  the slider beneath the tabbed display.  The compression ratio and
  RMS error for this quality setting are shown beneath the slider.
  A menu is provided, allowing the output image and the difference
  between the input and output images to be saved.


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
import java.io.*;
import java.text.*;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.sun.image.codec.jpeg.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.StringTools;



public class JPEGTool
 extends JFrame implements ChangeListener, ActionListener {


  ///////////////////////////// INNER CLASSES //////////////////////////////


  // Displays a label containing a piece of numeric data

  class DataPanel extends JPanel {

    private JLabel label = new JLabel();

    public DataPanel(String description, float value) {
      add(new JLabel(description));
      label.setFont(fixedFont);
      label.setForeground(Color.black);
      updateText(value);
      add(label);
    }

    public void updateText(float value) {
      label.setText(StringTools.rightJustify(number.format(value), 7));
    }

  }


  //////////////////////////// CLASS CONSTANTS /////////////////////////////


  private static final float DEFAULT_QUALITY = 0.5f;


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private BufferedImage inputImage;          // image to be compressed
  private int width;                         // width of input image
  private int height;                        // height of input image
  private JPEGEncodeParam parameters;        // compression parameters
  private float compressionRatio;            // current compression ratio
  private float rmsError;                    // current RMS error
  private BufferedImage outputImage;         // image after decompression
  private ImageView outputView;              // displays output image
  private JSlider qualitySlider;             // compression quality control
  private NumberFormat number =              // formatter for ratio/error
   new DecimalFormat("0.000");
  private Font fixedFont =                   // font used for ratio/error
   new Font("Monospaced", Font.PLAIN, 12);
  private DataPanel ratio =                  // displays compression ratio
   new DataPanel("Compression ratio ", 0.0f);
  private DataPanel error =                  // displays RMS error
   new DataPanel("RMS Error ", 0.0f);
  private JFileChooser fileChooser =         // chooses files for output
   new JFileChooser(System.getProperty("user.dir"));


  //////////////////////////////// METHODS /////////////////////////////////


  public JPEGTool(String filename) throws IOException, ImageDecoderException {

    super("JPEGTool: " + filename);

    // Read input image, generate output image and create display components

    readImage(filename);
    compressImage(DEFAULT_QUALITY);
    ImageView inputView = new ImageView(inputImage);
    outputView = new ImageView(outputImage);

    // Create various panels and menus

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
    tabbedPane.add(new JScrollPane(inputView), "input");
    tabbedPane.add(new JScrollPane(outputView), "output");
    JPanel controlPane = createControlPane();

    JPanel mainPane = new JPanel(new BorderLayout());
    mainPane.add(tabbedPane, BorderLayout.CENTER);
    mainPane.add(controlPane, BorderLayout.SOUTH);
    setContentPane(mainPane);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(createMenu());
    setJMenuBar(menuBar);
    addWindowListener(new WindowMonitor());

  }


  // Creates menu of file operations

  public JMenu createMenu() {
    JMenu menu = new JMenu("File");
    menu.setMnemonic('F');
    String[] commands = { "Save output", "Save difference", "Exit" };
    char[] shortcuts = { 'O', 'D', 'X' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i], shortcuts[i]);
      item.addActionListener(this);
      menu.add(item);
    }
    return menu;
  }


  // Creates a control panel containing the slider and labels
  // giving compression ratio and RMS error

  public JPanel createControlPane() {
    JPanel controlPane = new JPanel(new BorderLayout());
    createQualitySlider();
    JPanel sliderPane = new JPanel();
    sliderPane.add(qualitySlider);
    controlPane.add(sliderPane, BorderLayout.CENTER);
    JPanel infoPane = new JPanel();
    infoPane.add(ratio);
    infoPane.add(error);
    controlPane.add(infoPane, BorderLayout.SOUTH);
    return controlPane;
  }


  // Creates slider to control the quality of the compressed image

  public void createQualitySlider() {
    qualitySlider = new JSlider(0, 100, 50);
    qualitySlider.setMajorTickSpacing(20);
    qualitySlider.setMinorTickSpacing(5);
    Hashtable labels = new Hashtable();
    for (int i = 0; i <= 100; i += 20)
      labels.put(new Integer(i), new JLabel(String.valueOf(i/100.0f)));
    qualitySlider.setLabelTable(labels);
    qualitySlider.setPaintTicks(true);
    qualitySlider.setPaintLabels(true);
    Border border = new CompoundBorder(
     new TitledBorder("Quality"), new EmptyBorder(5, 10, 5, 10));
    qualitySlider.setBorder(border);
    qualitySlider.addChangeListener(this);
  }


  // Handles changes in image quality

  public void stateChanged(ChangeEvent event) {
    if (!qualitySlider.getValueIsAdjusting()) {
      try {
        float quality = qualitySlider.getValue() / 100.0f;
        compressImage(quality);
        outputView.setImage(outputImage);
        outputView.repaint();
      }
      catch (Exception e) {}
    }
  }


  // Handles menu selections

  public void actionPerformed(ActionEvent event) {
    String cmd = event.getActionCommand();
    if (cmd.equals("Save output"))
      saveImage("Save output image", outputImage);
    else if (cmd.equals("Save difference"))
      saveImage("Save difference image", getDifferenceImage());
    else if (cmd.equals("Exit")) {
      setVisible(false);
      dispose();
      System.exit(0);
    }
  }


  // Saves an image to a file chosen with the file chooser dialog

  public void saveImage(String dialogTitle, BufferedImage image) {
    fileChooser.setDialogTitle(dialogTitle);
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      if (file.exists()) {
        int response = JOptionPane.showConfirmDialog(this,
         "File will be overwritten!  Are you sure?", "File exists",
         JOptionPane.OK_CANCEL_OPTION);
        if (response != JOptionPane.OK_OPTION)
          return;
      }
      try {
        ImageEncoder encoder =
         ImageFile.createImageEncoder(file.getAbsolutePath());
        encoder.encode(image);
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


  // Reads input image from a file

  public void readImage(String filename)
   throws IOException, ImageDecoderException {
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    inputImage = input.decodeAsBufferedImage();
    width = inputImage.getWidth();
    height = inputImage.getHeight();
    parameters = JPEGCodec.getDefaultJPEGEncodeParam(inputImage);
  }


  // Subjects image to a cycle of compression and decompression

  public void compressImage(float quality) throws IOException {
    parameters.setQuality(quality, false);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out, parameters);
    encoder.encode(inputImage);
    computeRatio(out.size());
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
    outputImage = decoder.decodeAsBufferedImage();
    computeError();
  }


  // Calculates compression ratio

  public void computeRatio(int n) {
    compressionRatio = (float) width*height / n;
    ratio.updateText(compressionRatio);
  }


  // Calculates RMS error

  public void computeError() {
    Raster in = inputImage.getRaster();
    Raster out = outputImage.getRaster();
    double d, sum = 0.0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x) {
        d = in.getSample(x, y, 0) - out.getSample(x, y, 0);
        sum += d*d;
      }
    rmsError = (float) Math.sqrt(sum/(width*height));
    error.updateText(rmsError);
  }


  // Computes and returns difference image

  public BufferedImage getDifferenceImage() {
    Raster in = inputImage.getRaster();
    Raster out = outputImage.getRaster();
    BufferedImage differenceImage =
     new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster difference = differenceImage.getRaster();
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x)
        difference.setSample(x, y, 0,
         Math.abs(in.getSample(x, y, 0) - out.getSample(x, y, 0)));
    return differenceImage;
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new JPEGTool(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java JPEGTool <infile>");
      System.exit(1);
    }
  }


}
