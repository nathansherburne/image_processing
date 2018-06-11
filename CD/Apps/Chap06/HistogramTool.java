/***************************************************************************

  HistogramTool.java   Version 1.2 [1999/09/06]

  This Java2 application reads an image from a file named on the
  command line, then computes and displays its histogram.  A tabbed
  display is created for colour images, in which the view can be
  switched between histograms of the red, green and blue bands.

  The user can query values and their corresponding frequencies of
  occurrence by moving the cursor over the histogram.

  A menu is provided, from which the user may load a new image from
  a file, save a histogram to a file or quit the application.


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
import com.pearsoneduc.ip.gui.WindowMonitor;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;



public class HistogramTool extends JFrame implements ActionListener {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private Histogram histogram;          // histogram data to be displayed
  private HistogramView[] view;         // plot of the histogram
  private HistogramInfoPane infoPane;   // displays value and frequency
  private JPanel mainPane;              // contains histogram and info panel
  private JMenu menu;                   // input/output menu
  private JFileChooser fileChooser =    // handles selection of files
   new JFileChooser(System.getProperty("user.dir"));


  //////////////////////////////// METHODS /////////////////////////////////


  public HistogramTool(Histogram theHistogram, String description) {

    super(description);   // labels the frame

    // Create components to display histogram and information

    histogram = theHistogram;
    infoPane = new HistogramInfoPane(histogram);
    mainPane = new JPanel(new BorderLayout());
    if (histogram.getNumBands() == 3)
      createMultipleViews();   // three views (R, G, B) in a tabbed pane
    else
      createSingleView();
    mainPane.add(infoPane, BorderLayout.SOUTH);
    setContentPane(mainPane);

    // Add a menu bar to support image input and histogram output

    JMenuBar menuBar = new JMenuBar();
    menuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
    createFileMenu();
    menuBar.add(menu);
    setJMenuBar(menuBar);

    addWindowListener(new WindowMonitor());

  }


  // Creates a single HistogramView object to display a
  // greyscale histogram and adds it to the GUI

  public void createSingleView() {
    view = new HistogramView[1];
    view[0] = new HistogramView(histogram, infoPane);
    mainPane.add(view[0], BorderLayout.CENTER);
  }


  // Creates three HistogramView objects for the red, green
  // and blue bands of a colour histogram, places these in a
  // tabbed pane and adds the tabbed pane to the GUI

  public void createMultipleViews() {
    view = new HistogramView[3];
    Color[] bandColor = { Color.red, Color.green, Color.blue };
    String[] tabLabel = { "Red", "Green", "Blue" };
    JTabbedPane views = new JTabbedPane(JTabbedPane.BOTTOM);
    for (int i = 0; i < 3; ++i) {
      view[i] = new HistogramView(histogram, i, infoPane);
      view[i].setColor(bandColor[i]);
      views.add(tabLabel[i], view[i]);
    }
    mainPane.add(views, BorderLayout.CENTER);
  }


  // Creates a menu to support image input, histogram output
  // and termination of the application

  public void createFileMenu() {
    menu = new JMenu("File");
    menu.setMnemonic('F');
    String[] itemName = { "Load image", "Save histogram", "Exit" };
    char[] shortcut = { 'L', 'S', 'X' };
    for (int i = 0; i < 3; ++i) {
      JMenuItem item = new JMenuItem(itemName[i], shortcut[i]);
      item.addActionListener(this);
      menu.add(item);
    }
  }


  // Handles Action events triggered by menu selections

  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    if (command.startsWith("Load")) {
      loadImage();
      repaint();
    }
    else if (command.startsWith("Save")) {
      saveHistogram();
      repaint();
    }
    else if (command.equals("Exit")) {
      setVisible(false);
      dispose();
      System.exit(0);
    }
  }


  // Loads a new image, computes its histogram and updates the GUI

  public void loadImage() {

    fileChooser.setDialogTitle("Load image");
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

      // Load image and compute its histogram

      try {
        File file = fileChooser.getSelectedFile();
        ImageDecoder input =
         ImageFile.createImageDecoder(file.getAbsolutePath());
        BufferedImage image = input.decodeAsBufferedImage();
        histogram.computeHistogram(image);
        setTitle(file.getName());
      }
      catch (FileNotFoundException e) {
        error("File not found.");
        return;
      }
      catch (ImageDecoderException e) {
        error("Cannot read this image format.");
        return;
      }
      catch (IOException e) {
        error("Failed to read image data.");
        return;
      }
      catch (HistogramException e) {
        error("Cannot compute histogram for this image type.");
        return;
      }

      // Rebuild GUI

      mainPane.removeAll();
      if (histogram.getNumBands() == 3)
        createMultipleViews();
      else
        createSingleView();
      mainPane.add(infoPane, BorderLayout.SOUTH);
      mainPane.invalidate();
      validate();
      pack();

    }

  }


  // Saves current histogram to a file selected by user

  public void saveHistogram() {

    if (histogram.getNumBands() == 0) {
      error("No histogram data to save!");
      return;
    }
    else {
      fileChooser.setDialogTitle("Save histogram");
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
          histogram.write(new FileWriter(file));
          fileChooser.rescanCurrentDirectory();
        }
        catch (IOException e) {
          error("Cannot open output file.");
        }
      }
    }

  }


  // Displays an error message in a dialog box

  public void error(String message) {
    JOptionPane.showMessageDialog(this, message, "Error",
     JOptionPane.ERROR_MESSAGE);
  }


  public static void main(String[] argv) {

    if (argv.length > 0) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        BufferedImage image = input.decodeAsBufferedImage();
        Histogram hist = new Histogram(image);
        HistogramTool histTool = new HistogramTool(hist, argv[0]);
        histTool.pack();
        histTool.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      Histogram hist = new Histogram();
      HistogramTool histTool = new HistogramTool(hist, "HistogramTool");
      histTool.pack();
      histTool.setVisible(true);
    }

  }


}
