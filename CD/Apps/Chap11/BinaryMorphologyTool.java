/***************************************************************************

  BinaryMorphologyTool.java   Version 1.0 [1999/09/06]

  This program displays a binary image, a structuring element and the
  result of performing a morphological operation on the image using the
  structuring element.  The image filename and structuring element
  dimensions are specified on the command line.  If the dimensions are
  omitted, a default of 3x3 is used.

  A structuring element pixel value can be toggled from 0 to 1 and vice
  versa by clicking on that value.  Structuring elements can be loaded
  and saved via the 'File' menu.  The current structuring element can be
  filled with ones or zeros via the 'Element' menu.  An operation is
  performed by selecting from the 'Operation' menu.

  Examples of use:

    java BinaryMorphologyTool binary.pgm
    java BinaryMorphologyTool binary.pgm 5 5


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



public class BinaryMorphologyTool
 extends JFrame implements Runnable, ActionListener {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private int width;                      // width of structuring element
  private int height;                     // height of structuring element
  private BufferedImage inputImage;       // image to be processed
  private BufferedImage outputImage;      // processed image
  private StructElementPane controlPane;  // structuring element pixels
  private ImageView inputView;            // view of input image
  private ImageView outputView;           // view of processed image
  private JMenu fileMenu;                 // menu for load, save, exit, etc
  private JMenu configMenu;
  private JMenu taskMenu;                 // morphological operations menu
  private JFileChooser fileChooser        // selects kernel files
   = new JFileChooser(
      System.getProperty("user.dir"));
  private JLabel statusBar                // displays program status
   = new JLabel("Ready");
  private IntervalTimer timer             // operation timer
   = new IntervalTimer();
  private BufferedImageOp operation;
  private Thread operationThread;
  private boolean operating;


  //////////////////////////////// METHODS /////////////////////////////////


  public BinaryMorphologyTool(String filename, int w, int h)
   throws IOException, ImageDecoderException, OperationException {

    super("BinaryMorphologyTool: " + filename);

    width = w;
    height = h;

    // Load image and create image display components

    ImageDecoder input = ImageFile.createImageDecoder(filename);
    inputImage = input.decodeAsBufferedImage();
    if (inputImage.getType() != BufferedImage.TYPE_BYTE_GRAY
     && inputImage.getType() != BufferedImage.TYPE_BYTE_BINARY)
      throw new OperationException("invalid image type");
    inputView = new ImageView(inputImage);
    inputView.setToolTipText("Input image");
    outputImage = inputImage;
    outputView = new ImageView(outputImage);
    outputView.setToolTipText("Output image");

    // Create other components and add them to frame

    JPanel mainPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
    mainPane.add(new JScrollPane(inputView));
    controlPane = new StructElementPane(width, height);
    mainPane.add(controlPane);
    mainPane.add(new JScrollPane(outputView));

    JPanel pane = new JPanel(new BorderLayout());
    pane.add(mainPane, BorderLayout.CENTER);
    pane.add(statusBar, BorderLayout.SOUTH);
    setContentPane(pane);

    // Add a menu bar

    JMenuBar menuBar = new JMenuBar();
    createFileMenu();
    createConfigMenu();
    createTaskMenu();
    menuBar.add(fileMenu);
    menuBar.add(configMenu);
    menuBar.add(taskMenu);
    setJMenuBar(menuBar);

    addWindowListener(new WindowMonitor());

  }


  // Creates menu of file-based operations

  public void createFileMenu() {
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    String[] commands = { "Load SE", "Save SE", "Exit" };
    char[] shortcuts = { 'L', 'S', 'X' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i], shortcuts[i]);
      item.addActionListener(this);
      fileMenu.add(item);
    }
  }


  // Creates configuration menu

  public void createConfigMenu() {
    configMenu = new JMenu("Configure");
    configMenu.setMnemonic('C');
    String[] commands = {
     "Set all SE pixels",
     "Clear all SE pixels",
     "Copy output to input"
    };
    char[] shortcuts = { 'S', 'C', 'O' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i]);
      item.setAccelerator(
       KeyStroke.getKeyStroke(shortcuts[i], Event.SHIFT_MASK, false));
      item.addActionListener(this);
      configMenu.add(item);
    }
  }


  // Creates menu of morphological operations

  public void createTaskMenu() {
    taskMenu = new JMenu("Task");
    taskMenu.setMnemonic('T');
    String[] commands = { "Erode", "Dilate", "Open", "Close" };
    char[] shortcuts = { 'E', 'D', 'O', 'C' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i]);
      item.setAccelerator(
       KeyStroke.getKeyStroke(shortcuts[i], Event.CTRL_MASK, false));
      item.addActionListener(this);
      taskMenu.add(item);
    }
  }


  // Handles menu selection events

  public void actionPerformed(ActionEvent event) {
    String cmd = event.getActionCommand();
    if (cmd.equals("Load SE")) {
      loadStructuringElement();
      repaint();
    }
    else if (cmd.equals("Save SE")) {
      saveStructuringElement();
      repaint();
    }
    else if (cmd.equals("Exit")) {
      setVisible(false);
      dispose();
      System.exit(0);
    }
    else if (cmd.equals("Set all SE pixels"))
      controlPane.setPixels();
    else if (cmd.equals("Clear all SE pixels"))
      controlPane.clearPixels();
    else if (cmd.equals("Copy output to input")) {
      copyOutputToInput();
      repaint();
    }
    else if (cmd.equals("Erode"))
      erode();
    else if (cmd.equals("Dilate"))
      dilate();
    else if (cmd.equals("Open"))
      open();
    else if (cmd.equals("Close"))
      close();
  }


  // Loads a new structuring element from a user-selected file

  public void loadStructuringElement() {
    fileChooser.setDialogTitle("Load structuring element");
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        BinaryStructElement element = new BinaryStructElement(
         new FileReader(fileChooser.getSelectedFile()));
        if (element.getWidth() != width || element.getHeight() != height)
          throw new IOException(
           "Error: invalid structuring element dimensions!");
        controlPane.setToStructuringElement(element);
        statusBar.setText("Structuring element loaded");
      }
      catch (FileNotFoundException e) {
        statusBar.setText("Error: cannot access structuring element!");
      }
      catch (Exception e) {
        statusBar.setText("Error: " + e.getMessage());
      }
    }
  }


  // Saves the current structuring element to a user-selected file

  public void saveStructuringElement() {
    fileChooser.setDialogTitle("Save structuring element");
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
        BinaryStructElement element = controlPane.getStructuringElement();
        element.write(new FileWriter(file));
        fileChooser.rescanCurrentDirectory();
        statusBar.setText("Structuring element saved");
      }
      catch (Exception e) {
        statusBar.setText("Error: " + e.getMessage());
      }
    }
  }


  // Replaces input image with current output image

  public void copyOutputToInput() {
    if (JOptionPane.showConfirmDialog(this,
     "Input will be lost!  Are you sure?", "Copy output to input",
     JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
      inputImage = outputImage;
      inputView.setImage(inputImage);
      inputView.repaint();
    }
  }


  // Erodes input image by current structuring element

  public void erode() {
    if (operating)
      statusBar.setText("Not ready yet!...");
    else {
      BinaryStructElement element = controlPane.getStructuringElement();
      operation = new BinaryErodeOp(element);
      statusBar.setText("Eroding image...");
      operationThread = new Thread(this);
      setPriority();
      operationThread.start();
    }
  }


  // Dilates input image by current structuring element

  public void dilate() {
    if (operating)
      statusBar.setText("Not ready yet!...");
    else {
      BinaryStructElement element = controlPane.getStructuringElement();
      operation = new BinaryDilateOp(element);
      statusBar.setText("Dilating image...");
      operationThread = new Thread(this);
      setPriority();
      operationThread.start();
    }
  }


  // Opens input image by current structuring element

  public void open() {
    if (operating)
      statusBar.setText("Not ready yet!...");
    else {
      BinaryStructElement element = controlPane.getStructuringElement();
      operation = new BinaryOpenOp(element);
      statusBar.setText("Opening image...");
      operationThread = new Thread(this);
      setPriority();
      operationThread.start();
    }
  }


  // Closes input image by current structuring element

  public void close() {
    if (operating)
      statusBar.setText("Not ready yet!...");
    else {
      BinaryStructElement element = controlPane.getStructuringElement();
      operation = new BinaryCloseOp(element);
      statusBar.setText("Closing image...");
      operationThread = new Thread(this);
      setPriority();
      operationThread.start();
    }
  }


  // Performs morphological operation in a separate thread

  public void run() {
    operating = true;
    timer.start();
    outputImage = operation.filter(inputImage, null);
    outputView.setImage(outputImage);
    outputView.repaint();
    statusBar.setText("Operation finished [" + timer.stop() + " sec]");
    operating = false;
  }


  // Lowers priority of operation thread relative to application thread

  public void setPriority() {
    int priority = Thread.currentThread().getPriority();
    operationThread.setPriority(Math.max(Thread.MIN_PRIORITY, priority-1));
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        int w = 3, h = 3;
        if (argv.length > 2) {
          w = Integer.parseInt(argv[1]);
          h = Integer.parseInt(argv[2]);
        }
        JFrame frame = new BinaryMorphologyTool(argv[0], w, h);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java BinaryMorphologyTool " +
       "<imagefile> [<width> <height>]");
      System.exit(1);
    }
  }


}
