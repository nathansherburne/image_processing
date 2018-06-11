/***************************************************************************

  ConvolutionTool.java   Version 1.3 [2000/02/06]

  This program displays an image, a convolution kernel and the result of
  convolving the image with the kernel.  The image filename and kernel
  dimensions are specified on the command line.  Kernel dimensions can be
  omitted if desired, in which case a default 3x3 kernel is used.  The
  dimensions, if specified, must be odd integers.

  Kernel coefficients can be edited, but are restricted to integer values.
  (Normalisation is done automatically if the sum of coefficients is
  greater than 1.) Kernels can be loaded and saved via the 'File' menu.
  The current kernel can be cleared or applied to the image via the
  'Convolve' menu.  This menu also has various options to control the
  convolution process.

  Examples of use:

    java ConvolutionTool image.jpg
    java ConvolutionTool image.jpg 5 5


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
import javax.swing.*;
import javax.swing.border.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.*;



public class ConvolutionTool extends JFrame implements ActionListener {


  //////////////////////////// CLASS CONSTANTS /////////////////////////////


  private static final String STAR_ICON = "star.gif";
  private static final String EQUALS_ICON = "equals.gif";


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private int width;                     // width of kernel
  private int height;                    // height of kernel
  private float[] kernelData;            // kernel coefficients
  private int borderStrategy             // what to do at image borders
   = NeighbourhoodOp.NO_BORDER_OP;
  private int rescaleStrategy            // what to do with output values
   = ConvolutionOp.NO_RESCALING;
  private BufferedImage inputImage;      // image to be convolved
  private KernelPane kernelPane;         // component representing kernel
  private ImageView filteredView;        // view of convolved image
  private JMenu fileMenu;                // menu for load, save, exit, etc
  private JMenu convolveMenu;            // menu controlling convolution
  private JFileChooser fileChooser       // selects kernel files
   = new JFileChooser(
      System.getProperty("user.dir"));
  private JLabel statusBar               // displays program status
   = new JLabel("Ready");
  private IntervalTimer timer            // convolution timer
   = new IntervalTimer();


  //////////////////////////////// METHODS /////////////////////////////////


  public ConvolutionTool(String filename, int w, int h)
   throws IOException, ImageDecoderException, OperationException {

    super("ConvolutionTool: " + filename);

    // Check that kernel dimensions are odd

    if (w % 2 == 0 || h % 2 == 0)
      throw new OperationException("invalid kernel dimensions");

    width = w;
    height = h;
    kernelData = new float[w*h];

    // Load image and create image display components

    ImageDecoder input = ImageFile.createImageDecoder(filename);
    inputImage = input.decodeAsBufferedImage();
    if (inputImage.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new OperationException("invalid image type");
    ImageView inputView = new ImageView(inputImage);
    filteredView = new ImageView(inputImage);

    // Create other components and add them to frame

    kernelPane = new KernelPane(width, height);
    JLabel star = new JLabel(
     new ImageIcon(getClass().getResource(STAR_ICON)));
    JLabel equals = new JLabel(
     new ImageIcon(getClass().getResource(EQUALS_ICON)));

    JPanel mainPane = new JPanel();
    mainPane.add(new JScrollPane(inputView));
    mainPane.add(star);
    mainPane.add(kernelPane);
    mainPane.add(equals);
    mainPane.add(new JScrollPane(filteredView));

    JPanel pane = new JPanel(new BorderLayout());
    pane.add(mainPane, BorderLayout.CENTER);
    pane.add(statusBar, BorderLayout.SOUTH);
    setContentPane(pane);

    // Add a menu bar

    JMenuBar menuBar = new JMenuBar();
    createFileMenu();
    createConvolveMenu();
    menuBar.add(fileMenu);
    menuBar.add(convolveMenu);
    setJMenuBar(menuBar);

    addWindowListener(new WindowMonitor());

  }


  // Creates menu of file-based operations

  public void createFileMenu() {
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    String[] commands = { "Load kernel", "Save kernel", "Exit" };
    char[] shortcuts = { 'L', 'S', 'X' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i], shortcuts[i]);
      item.addActionListener(this);
      fileMenu.add(item);
    }
  }


  // Creates menu that controls convolution

  public void createConvolveMenu() {

    convolveMenu = new JMenu("Convolve");
    convolveMenu.setMnemonic('C');
    String[] commands = { "Reset kernel", "Convolve" };
    char[] shortcuts = { 'R', 'V' };
    JMenuItem item;
    for (int i = 0; i < commands.length; ++i) {
      convolveMenu.add(item = new JMenuItem(commands[i], shortcuts[i]));
      item.addActionListener(this);
    }

    JMenu optionsMenu = new JMenu("Options");
    String[] borderOptions = {
     "No operation at borders",
     "Copy border pixels",
     "Reflected indexing",
     "Circular indexing"
    };
    addOptions(borderOptions, optionsMenu);
    optionsMenu.addSeparator();

    String[] rescaleOptions = {
     "No rescaling",
     "Rescale max only",
     "Rescale min and max"
    };
    addOptions(rescaleOptions, optionsMenu);
    convolveMenu.add(optionsMenu);

  }


  // Adds a group of radiobutton options to a menu

  public void addOptions(String[] names, JMenu menu) {
    ButtonGroup group = new ButtonGroup();
    JMenuItem item;
    for (int i = 0; i < names.length; ++i) {
      menu.add(item = new JRadioButtonMenuItem(names[i]));
      item.addActionListener(this);
      group.add(item);
      if (i == 0)
        item.setSelected(true);
    }
  }


  // Handles menu selection events

  public void actionPerformed(ActionEvent event) {
    String cmd = event.getActionCommand();
    if (cmd.equals("Load kernel")) {
      loadKernel();
      repaint();
    }
    else if (cmd.equals("Save kernel")) {
      saveKernel();
      repaint();
    }
    else if (cmd.equals("Exit")) {
      setVisible(false);
      dispose();
      System.exit(0);
    }
    else if (cmd.equals("Reset kernel"))
      kernelPane.reset();
    else if (cmd.equals("Convolve"))
      convolve();
    else if (cmd.equals("No operation at borders"))
      borderStrategy = NeighbourhoodOp.NO_BORDER_OP;
    else if (cmd.equals("Copy border pixels"))
      borderStrategy = NeighbourhoodOp.COPY_BORDER_PIXELS;
    else if (cmd.equals("Reflected indexing"))
      borderStrategy = NeighbourhoodOp.REFLECTED_INDEXING;
    else if (cmd.equals("Circular indexing"))
      borderStrategy = NeighbourhoodOp.CIRCULAR_INDEXING;
    else if (cmd.equals("No rescaling"))
      rescaleStrategy = ConvolutionOp.NO_RESCALING;
    else if (cmd.equals("Rescale max only"))
      rescaleStrategy = ConvolutionOp.RESCALE_MAX_ONLY;
    else if (cmd.equals("Rescale min and max"))
      rescaleStrategy = ConvolutionOp.RESCALE_MIN_AND_MAX;
  }


  // Loads a new kernel from a user-selected file

  public void loadKernel() {
    fileChooser.setDialogTitle("Load kernel");
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        StreamTokenizer parser =
         new StreamTokenizer(new BufferedReader(
           new FileReader(fileChooser.getSelectedFile())));
        parser.commentChar('#');
        int w = getNumber(parser);
        int h = getNumber(parser);
        getNumber(parser);
        if (w != width || h != height)
          throw new IOException("Invalid kernel dimensions!");
        for (int i = 0; i < kernelData.length; ++i)
          kernelPane.setCoeff(i, getNumber(parser));
        statusBar.setText("Kernel loaded");
      }
      catch (FileNotFoundException e) {
        statusBar.setText("Cannot access the specified kernel file!");
      }
      catch (IOException e) {
        statusBar.setText(e.getMessage());
      }
    }
  }


  // Retrieves a numeric token and returns its value

  public int getNumber(StreamTokenizer in) throws IOException {
    in.nextToken();
    if (in.ttype == StreamTokenizer.TT_NUMBER)
      return (int) in.nval;
    else if (in.ttype == StreamTokenizer.TT_EOF)
      throw new EOFException("Kernel appears to be truncated!");
    else
      throw new IOException("Invalid kernel file!");
  }


  // Saves the current kernel to a user-selected file

  public void saveKernel() {
    fileChooser.setDialogTitle("Save kernel");
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
        PrintWriter out =
         new PrintWriter(new BufferedWriter(new FileWriter(file)));
        out.println("# convolution kernel");
        out.println(width + " " + height + " 0");
        int i = 0;
        for (int k = 0; k < height; ++k) {
          for (int j = 0; j < width; ++j, ++i)
            out.print(StringTools.rightJustify(kernelPane.getCoeff(i), 4));
          out.println();
        }
        out.flush();
        fileChooser.rescanCurrentDirectory();
        statusBar.setText("Kernel saved");
      }
      catch (IOException e) {
        statusBar.setText(e.getMessage());
      }
    }
  }


  // Convolves input image with current kernel

  public void convolve() {

    // Get kernel coefficients, normalising if they sum to > 1

    int i;
    float sum = 0.0f;
    for (i = 0; i < kernelData.length; ++i) {
      kernelData[i] = (float) kernelPane.getCoeff(i);
      sum += kernelData[i];
    }
    if (sum > 1.0f)
      for (i = 0; i < kernelData.length; ++i)
        kernelData[i] /= sum;

    // Create kernel and convolution operator, and filter image

    Kernel kernel = new Kernel(width, height, kernelData);
    BufferedImageOp convolution = new ConvolutionOp(
     kernel, borderStrategy, ConvolutionOp.SINGLE_PASS, rescaleStrategy);
    statusBar.setText("Convolving image...");
    timer.start();
    BufferedImage result = convolution.filter(inputImage, null);
    statusBar.setText("Convolution finished [" + timer.stop() + " sec]");

    // Update display with result of convolution 

    filteredView.setImage(result);
    filteredView.repaint();

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        int w = 3, h = 3;
        if (argv.length > 2) {
          w = Integer.parseInt(argv[1]);
          h = Integer.parseInt(argv[2]);
        }
        JFrame frame = new ConvolutionTool(argv[0], w, h);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java ConvolutionTool <imagefile> [<width> <height>]");
      System.exit(1);
    }
  }


}
