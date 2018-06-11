/***************************************************************************

  Magnifier.java   Version 1.0 [1999/01/30]

  This class extends JDialog to provide a pop-up magnifying glass for
  the ImageViewer application.  A view of fixed dimensions is created;
  the corresponding area of the original image varies, depending on the
  selected magnification factor.  Magnification factors in the range
  2-8 are available.

  See the ImageViewer class for further details.


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
import javax.swing.*;
import javax.swing.event.*;



public class Magnifier extends JDialog implements ItemListener {


  /////////////////////////////// CONSTANTS ////////////////////////////////


  private static final int VIEW_WIDTH = 128;
  private static final int VIEW_HEIGHT = 128;
  private static final int MAG_FACTOR = 2;
  private static final String[] MAG_OPTIONS = {
    " x2", " x3", " x4", " x5", " x6", " x7", " x8"
  };


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////

  
  private ImageViewer viewer;          // magnifier's owner
  private BufferedImage sourceImage;   // image being magnified
  private ImageIcon viewedImage;       // magnified portion of that image
  private JLabel view;                 // component used for image display
  private Point centrePixel;           // position of magnified view
  private int magFactor = MAG_FACTOR;  // magnification factor


  //////////////////////////////// METHODS /////////////////////////////////


  public Magnifier(ImageViewer theViewer, BufferedImage image) {

    super(theViewer, "Magnifier", false);   // this is a non-modal dialog
    viewer = theViewer;

    // Trap case where user dismisses the dialog - so that
    // we can unselect the JToggleButton that made the dialog
    // visible in the first place...

    addWindowListener(
     new WindowAdapter() {
       public void windowClosing(WindowEvent event) {
         viewer.resetMagnifyButton();
       }
     }
    );

    // Create default magnified view of a region surrounding
    // the source image's central pixel...

    sourceImage = image;
    viewedImage = new ImageIcon();
    centrePixel = new Point(image.getWidth()/2, image.getHeight()/2);
    updateView();

    // Create a component to hold the magnified view

    JLabel view = new JLabel(viewedImage);

    // Create:
    //   A control to vary magnification
    //   A label for the control
    //   A container for the control and its label

    JComboBox magSelector = new JComboBox(MAG_OPTIONS);
    magSelector.addItemListener(this);
    JPanel controlPane = new JPanel();
    controlPane.add(new JLabel("Magnification"));
    controlPane.add(magSelector);

    // Add magnified view and control to the dialog

    JPanel magnifyPane = new JPanel(new BorderLayout());
    magnifyPane.add(view, BorderLayout.CENTER);
    magnifyPane.add(controlPane, BorderLayout.SOUTH);

    setContentPane(magnifyPane);
    pack();

  }


  // Updates the view in response to a change in magnification factor

  public void updateView() {
    try {
      int width = VIEW_WIDTH / magFactor;
      int height = VIEW_HEIGHT / magFactor;
      int x = centrePixel.x - width / 2;
      int y = centrePixel.y - height / 2;
      viewedImage.setImage(
       sourceImage.getSubimage(x, y, width, height).getScaledInstance(
        VIEW_WIDTH, VIEW_HEIGHT, Image.SCALE_FAST));
    }
    catch (RasterFormatException e) {
      Toolkit.getDefaultToolkit().beep();
    }
    repaint();
  }

  // Updates the view, given a new view position

  public void updateView(Point pixel) {
    centrePixel.setLocation(pixel);
    updateView();
  }


  // Event handler for magnification factor selection

  public void itemStateChanged(ItemEvent event) {
    String choice = (String) event.getItem();
    magFactor = Integer.parseInt(choice.substring(2));  // skip the " x"
    updateView();
  }


}
