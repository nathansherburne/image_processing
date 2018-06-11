/***************************************************************************

  AffineTransformTool.java   Version 1.0 [1999/08/13]

  This application demonstrates interactive scaling and rotation of
  images using the AffineTransformOp class.  An image is loaded from a
  file specified on the command line and is displayed, along with a
  transformed version of that image (initially identical to the
  original).  Sliders are provided to scale the image in the x and y
  directions by a factor in the range 0.1-2.0 and to rotate it by
  an angle between -90 and +90 degrees.  Interpolation can be toggled
  between zero-order and first-order.


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
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.*;



public class AffineTransformTool extends JFrame {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private BufferedImage image;           // input image
  private ImageView transformedView;     // displays transformed image


  ///////////////////////////// INNER CLASSES //////////////////////////////


  class Controls extends JPanel implements ChangeListener, ActionListener {

    private Font labelFont = new Font("Monospaced", Font.BOLD, 12);
    private JSlider xScale = new JSlider(1, 20, 10);
    private JSlider yScale = new JSlider(1, 20, 10);
    private JSlider angle  = new JSlider(-90, 90, 0);
    private JRadioButton zeroOrder = new JRadioButton("zero-order");
    private JRadioButton firstOrder = new JRadioButton("first-order");
    private int interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;

    public Controls() {

      setLayout(new GridLayout(0,1));

      // Configure sliders for x & y scale factors and angle of rotation

      addSlider(xScale, "horizontal scale (x10) ", 0, 1, 5, 5);
      addSlider(yScale, "  vertical scale (x10) ", 0, 1, 5, 5);
      addSlider(angle, "              rotation ", 30, 5, 30, -90);

      // Set up radiobuttons for interpolation scheme selection

      ButtonGroup group = new ButtonGroup();
      group.add(zeroOrder);
      group.add(firstOrder);
      zeroOrder.setSelected(true);
      zeroOrder.addActionListener(this);
      firstOrder.addActionListener(this);

      JPanel pane = new JPanel();
      pane.add(zeroOrder);
      pane.add(firstOrder);
      add(pane);

    }

    // Configures a slider and adds it to the control panel

    public void addSlider(JSlider slider, String name, int major,
     int minor, int increment, int start) {
      JPanel pane = new JPanel();
      JLabel label = new JLabel(name);
      label.setFont(labelFont);
      pane.add(label);
      slider.setMajorTickSpacing(major);
      slider.setMinorTickSpacing(minor);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setLabelTable(slider.createStandardLabels(increment, start));
      slider.addChangeListener(this);
      pane.add(slider);
      add(pane);
    }

    // Handles adjustment of scale or rotation sliders

    public void stateChanged(ChangeEvent event) {
      JSlider slider = (JSlider) event.getSource();
      if (!slider.getValueIsAdjusting())
        transformImage();
    }

    // Handles clicking on a radiobutton to select the interpolation scheme

    public void actionPerformed(ActionEvent event) {
      String command = event.getActionCommand();
      if (command.startsWith("zero")
       && interpolation == AffineTransformOp.TYPE_BILINEAR) {
        interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        transformImage();
      }
      else if (command.startsWith("first")
       && interpolation == AffineTransformOp.TYPE_NEAREST_NEIGHBOR) {
        interpolation = AffineTransformOp.TYPE_BILINEAR;
        transformImage();
      }
    }

    // Polls the controls for the current transformation parameters,
    // transforms image using these parameters and updates view

    public void transformImage() {
      double sx = xScale.getValue() / 10.0;
      double sy = yScale.getValue() / 10.0;
      double theta = angle.getValue() * Math.PI / 180.0;
      AffineTransform transform = AffineTransform.getScaleInstance(sx, sy);
      transform.rotate(theta);
      BufferedImageOp op = new AffineTransformOp(transform, interpolation);
      transformedView.setImage(op.filter(image, null));
      transformedView.repaint();
    }

  }


  //////////////////////////////// METHODS /////////////////////////////////


  public AffineTransformTool(String file)
   throws IOException, ImageDecoderException {

    super("AffineTransformTool: " + file);

    // Load image from file and create image display components

    ImageDecoder input = ImageFile.createImageDecoder(file);
    image = input.decodeAsBufferedImage();
    ImageView inputView = new ImageView(image);
    transformedView = new ImageView(image);

    // Assemble GUI

    JPanel viewPane = new JPanel();
    viewPane.add(new JScrollPane(inputView));
    viewPane.add(new JScrollPane(transformedView));
    JPanel pane = new JPanel(new BorderLayout());
    pane.add(viewPane, BorderLayout.CENTER);
    pane.add(new Controls(), BorderLayout.SOUTH);
    setContentPane(pane);
    addWindowListener(new WindowMonitor());

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new AffineTransformTool(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java AffineTransformTool <imagefile>");
      System.exit(1);
    }
  }


}
