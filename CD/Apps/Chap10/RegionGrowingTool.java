/***************************************************************************

  RegionGrowingTool.java   Version 1.1 [1999/09/06]

  This program displays an image read from a file and allows the user
  to mark seed pixels.  Regions can then be grown from these seeds.
  The growing regions are overlaid on the image, allowing progress to
  be monitored easily.  Once regions have been grown, they can be saved
  to a file as an image.  The user also has the option of starting
  again from the same seeds (possibly adding more to the image) or of
  starting again with a new set of seeds.  This is useful when
  experimenting with different connectivities or thresholds.  Menu
  options permit switching between 4- and 8-connectivity, and a
  threshold can be specified in the text field beneath the image.

  Example of use:

    java RegionGrowingTool foo.png


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
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.RegionGrower;
import com.pearsoneduc.ip.util.IntervalTimer;



public class RegionGrowingTool
 extends JFrame implements Runnable, ActionListener {


  //////////////////////////// CLASS CONSTANTS /////////////////////////////


  private static final Color SEED_COLOUR = Color.green;
  private static final int DEFAULT_THRESHOLD = 10;
  private static final int SEEDING = 1;
  private static final int GROWING = 2;
  private static final int GROWN = 3;


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private JMenu fileMenu;
  private JMenu regionMenu;
  private View view;
  private Threshold threshold = new Threshold();
  private JLabel statusBar
   = new JLabel(" Click on image to define seed pixels");
  private JFileChooser fileChooser
   = new JFileChooser(System.getProperty("user.dir"));
  private BufferedImage image;
  private RegionGrower regionGrower;
  private Thread growthThread;
  private java.util.List seedPixels = new ArrayList();
  private int connectivity = 8;
  private int status = SEEDING;


  ///////////////////////////// INNER CLASSES //////////////////////////////


  // Component displaying an image overlaid with seed pixels or regions

  class View extends ImageView {

    private BufferedImage overlay;

    public View(BufferedImage inputImage, BufferedImage statusImage) {
      super(inputImage);
      overlay = statusImage;
      addMouseListener(new MouseAdapter() {
        public void mouseReleased(MouseEvent event) {
          if (status == SEEDING) {
            Point pixel = event.getPoint();
            if (!seedPixels.contains(pixel)) {
              seedPixels.add(pixel);
              statusBar.setText(" Seed defined at (" +
               pixel.x + "," + pixel.y + ")");
              repaint();
            }
          }
        }
      });
    }

    public void setOverlay(BufferedImage image) {
      overlay = image;
      repaint();
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (overlay != null)
        g.drawImage(overlay, 0, 0, this);
      if (status == SEEDING) {
        g.setColor(SEED_COLOUR);
        Iterator iterator = seedPixels.iterator();
        while (iterator.hasNext()) {
          Point pixel = (Point) iterator.next();
          g.fillRect(pixel.x-1, pixel.y-1, 3, 3);
        }
      }
    }

  }


  // Component used to specify threshold on region uniformity

  class Threshold extends JPanel {

    private JTextField value =
     new JTextField(String.valueOf(DEFAULT_THRESHOLD), 3);

    public Threshold() {
      setLayout(new FlowLayout());
      add(new JLabel("Threshold"));
      add(value);
    }

    public int getValue() {
      try {
        int n = Integer.parseInt(value.getText());
        return Math.max(0, Math.min(255, n));
      }
      catch (Exception e) {
        value.setText(String.valueOf(DEFAULT_THRESHOLD));
        return DEFAULT_THRESHOLD;
      }
    }

  }


  //////////////////////////////// METHODS /////////////////////////////////


  public RegionGrowingTool(String filename)
   throws IOException, ImageDecoderException {

    super("RegionGrowingTool: " + filename);

    // Load image from file

    ImageDecoder input = ImageFile.createImageDecoder(filename);
    image = input.decodeAsBufferedImage();

    // Create components and add them to the frame

    view = new View(image, null);
    statusBar.setBorder(BorderFactory.createEtchedBorder());
    JPanel mainPane = new JPanel(new BorderLayout());
    mainPane.add(view, BorderLayout.CENTER);
    mainPane.add(threshold, BorderLayout.SOUTH);
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(mainPane, BorderLayout.CENTER);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    setContentPane(contentPane);

    // Create menus and menu bar

    JMenuBar menuBar = new JMenuBar();
    createFileMenu();
    createRegionMenu();
    menuBar.add(fileMenu);
    menuBar.add(regionMenu);
    setJMenuBar(menuBar);
    fileChooser.setDialogTitle("Save regions");

    addWindowListener(new WindowMonitor());

  }


  // Creates menu of file operations

  public void createFileMenu() {
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    String[] commands = { "Save regions", "Exit" };
    char[] shortcuts = { 'S', 'X' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i], shortcuts[i]);
      item.addActionListener(this);
      fileMenu.add(item);
    }
  }


  // Creates menu of region growing operations and options

  public void createRegionMenu() {

    regionMenu = new JMenu("Region Growing");
    regionMenu.setMnemonic('R');

    // Operations

    String[] commands = { "Grow regions", "Back to seeds", "New seeds" };
    char[] shortcuts = { 'G', 'B', 'N' };
    for (int i = 0; i < commands.length; ++i) {
      JMenuItem item = new JMenuItem(commands[i], shortcuts[i]);
      item.addActionListener(this);
      regionMenu.add(item);
    }

    // Connectivity options

    JMenu optionsMenu = new JMenu("Options");
    optionsMenu.setMnemonic('O');
    ButtonGroup group = new ButtonGroup();
    JMenuItem item;
    optionsMenu.add(item = new JRadioButtonMenuItem("4-connectivity"));
    item.addActionListener(this);
    group.add(item);
    optionsMenu.add(item = new JRadioButtonMenuItem("8-connectivity"));
    item.addActionListener(this);
    item.setSelected(true);
    group.add(item);
    regionMenu.add(optionsMenu);

  }


  // Handles menu selection events

  public void actionPerformed(ActionEvent event) {
    String cmd = event.getActionCommand();
    if (cmd.equals("Save regions")) {
      if (status == GROWN) {
        saveRegions();
        repaint();
      }
      else
        statusBar.setText(" No regions to save!");
    }
    else if (cmd.equals("Exit")) {
      setVisible(false);
      dispose();
      System.exit(0);
    }
    else if (cmd.equals("Grow regions")) {
      if (status == SEEDING) {
        growthThread = new Thread(this);
        growthThread.start();
      }
      else
        statusBar.setText(" Regions already grown/growing!");
    }
    else if (cmd.equals("Back to seeds")) {
      if (status == GROWN) {
        view.setOverlay(null);
        status = SEEDING;
        statusBar.setText(" Seeds reset");
      }
    }
    else if (cmd.equals("New seeds")) {
      if (status != GROWING) {
        seedPixels.clear();
        view.setOverlay(null);
        status = SEEDING;
        statusBar.setText(" Click on image to define seed pixels");
      }
    }
    else if (cmd.equals("4-connectivity")) {
      connectivity = 4;
    }
    else if (cmd.equals("8-connectivity")) {
      connectivity = 8;
    }
  }


  // Grows regions in a separate thread

  public void run() {
    status = GROWING;
    statusBar.setText(" Growing regions...");
    regionGrower = new RegionGrower(image, seedPixels, connectivity,
     threshold.getValue(), true);
    IntervalTimer timer = new IntervalTimer();
    timer.start();
    while (regionGrower.isNotFinished()) {
      try { growthThread.sleep(100); }
       catch (InterruptedException e) {}
      regionGrower.grow();
      view.setOverlay(regionGrower.getStatusImage());
    }
    status = GROWN;
    statusBar.setText(" Region growing finished [" + timer.stop() + " sec]");
  }


  // Saves current regions to an image file

  public void saveRegions() {
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
        output.encode(regionGrower.getRegionImage());
        fileChooser.rescanCurrentDirectory();
      }
      catch (Exception e) {
        statusBar.setText(" Error: " + e.getMessage());
      }
    }
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new RegionGrowingTool(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java RegionGrowingTool <imagefile>");
      System.exit(1);
    }
  }


}
