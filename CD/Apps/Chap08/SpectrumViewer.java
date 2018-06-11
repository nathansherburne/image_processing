/***************************************************************************

  SpectrumViewer.java   Version 1.1 [1999/08/05]

  This program reads an image from a file named on the command line and
  performs an FFT to compute its spectrum.  The logarithmically scaled
  spectrum is displayed alongside the image.  Radiobuttons are provided
  to allow the view to be toggled between a shifted and an unshifted
  spectrum.  An information panel displays horizontal and vertical
  spatial frequency, magnitude and phase for the point under the cursor,
  when the cursor is positioned over the spectrum.


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
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.StringTools;



public class SpectrumViewer extends JFrame {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private int width;
  private int height;
  private ImageFFT fft;
  private BufferedImage shiftedSpectrum;
  private BufferedImage unshiftedSpectrum;
  private boolean shifted;
  private ImageView spectrumView;
  private Info infoPane;


  ////////////////////////////// INNER CLASSES /////////////////////////////


  // Wraps scrollbars around an image (if necessary) and provides
  // feedback to information pane on cursor position

  class Scroller extends JScrollPane {

    // Creates a scroll pane wrapped around the displayed image

    public Scroller(ImageView theView) {
      super(theView);
      addMouseMotionListener(
       new MouseMotionAdapter() {
         public void mouseMoved(MouseEvent event) {
           infoPane.updateInfo(getPixelCoordinates(event.getPoint()));
         }
       });
    }

    // Maps cursor position into image coordinate system

    public Point getPixelCoordinates(Point cursorPosition) {
      Point viewOrigin = getViewport().getViewPosition();
      Point pixelPosition = new Point(viewOrigin.x + cursorPosition.x,
       viewOrigin.y + cursorPosition.y);
      return pixelPosition;
    }

  }


  // Provides an information panel for the application

  class Info extends JPanel {

    private int w2, h2;
    private NumberFormat magFormat;
    private NumberFormat phaseFormat;
    private JLabel uCoord = new JLabel();
    private JLabel vCoord = new JLabel();
    private JLabel magnitude = new JLabel();
    private JLabel phase = new JLabel();

    // Set up formatting and labels

    public Info() {

      w2 = width/2;
      h2 = height/2;
      magFormat = new DecimalFormat("0.000000E0");
      phaseFormat = new DecimalFormat("0.000");

      JPanel coordPane = new JPanel();
      coordPane.add(new JLabel("u"));
      Font fixedFont = new Font("Monospaced", Font.BOLD, 12);
      uCoord.setFont(fixedFont);
      uCoord.setForeground(Color.black);
      coordPane.add(uCoord);
      coordPane.add(new JLabel(" v"));
      vCoord.setFont(fixedFont);
      vCoord.setForeground(Color.black);
      coordPane.add(vCoord);
      coordPane.setBorder(BorderFactory.createEtchedBorder());
      add(coordPane);

      JPanel valuePane = new JPanel();
      valuePane.add(new JLabel("magnitude"));
      magnitude.setFont(fixedFont);
      magnitude.setForeground(Color.black);
      valuePane.add(magnitude);
      valuePane.add(new JLabel(" phase"));
      phase.setFont(fixedFont);
      phase.setForeground(Color.black);
      valuePane.add(phase);
      valuePane.setBorder(BorderFactory.createEtchedBorder());
      add(valuePane);

      updateInfo(new Point(0, 0));

    }

    // Updates information, given coordinates of a point in the spectrum

    public void updateInfo(Point p) {
      if (validPoint(p)) {
        try {
          int u, v;
          if (shifted) {
            u = (p.x >= w2 ? p.x-w2 : p.x+w2);
            v = (p.y >= h2 ? p.y-h2 : p.y+h2);
            uCoord.setText(StringTools.rightJustify(p.x-w2, 4));
            vCoord.setText(StringTools.rightJustify(p.y-h2, 4));
          }
          else {
            u = p.x;
            v = p.y;
            uCoord.setText(StringTools.rightJustify(u, 4));
            vCoord.setText(StringTools.rightJustify(v, 4));
          }
          magnitude.setText(StringTools.rightJustify(
           magFormat.format(fft.getMagnitude(u, v)), 10));
          phase.setText(StringTools.rightJustify(
           phaseFormat.format(fft.getPhase(u, v)), 6));
        }
        catch (Exception e) {}
      }
    }

    // Checks that a point lies in the spectrum

    public boolean validPoint(Point p) {
      if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height)
        return true;
      else
        return false;
    }

  }


  // Provides controls to switch between two different views of the spectrum

  class Buttons extends JPanel implements ActionListener {

    public Buttons() {
      ButtonGroup group = new ButtonGroup();
      JRadioButton button = new JRadioButton("shifted");
      button.addActionListener(this);
      group.add(button);
      add(button);
      button = new JRadioButton("unshifted");
      button.setSelected(true);
      button.addActionListener(this);
      group.add(button);
      add(button);
    }

    public void actionPerformed(ActionEvent event) {
      String command = event.getActionCommand();
      if (!shifted && command.equals("shifted")) {
        spectrumView.setImage(shiftedSpectrum);
        spectrumView.repaint();
        shifted = true;
      }
      else if (shifted && command.equals("unshifted")) {
        spectrumView.setImage(unshiftedSpectrum);
        spectrumView.repaint();
        shifted = false;
      }
    }

  }


  ///////////////////////////////// METHODS ////////////////////////////////


  public SpectrumViewer(String filename, int window)
   throws IOException, ImageDecoderException, FFTException {

    // Load image and perform FFT

    super("SpectrumViewer");
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    BufferedImage inputImage = input.decodeAsBufferedImage();
    System.out.println("Computing FFT and spectra...");
    fft = new ImageFFT(inputImage, window);
    width = fft.getWidth();
    height = fft.getHeight();
    fft.transform();
    shiftedSpectrum = fft.getSpectrum();
    unshiftedSpectrum = fft.getUnshiftedSpectrum();

    // Add display components and information panel to frame

    JPanel viewPane = new JPanel();
    viewPane.setLayout(new FlowLayout());
    viewPane.add(new JScrollPane(new ImageView(inputImage)));
    spectrumView = new ImageView(unshiftedSpectrum);
    shifted = false;
    viewPane.add(new Scroller(spectrumView));
    infoPane = new Info();
    JPanel controlPane = new JPanel();
    controlPane.add(new Buttons());
    controlPane.add(infoPane);

    Container pane = getContentPane();
    pane.add(viewPane, BorderLayout.CENTER);
    pane.add(controlPane, BorderLayout.SOUTH);
    addWindowListener(new WindowMonitor());

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        int window = 1;
        if (argv.length > 1)
          window = Integer.parseInt(argv[1]);
        JFrame frame = new SpectrumViewer(argv[0], window);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java SpectrumViewer <imagefile> [<window>]");
      System.exit(1);
    }
  }


}
