/***************************************************************************

  PixelInfoPane.java   Version 1.1 [1999/06/30]

  This class extends JPanel to provide specialised facilities for
  displaying pixel coordinate and value information.  An instance of
  this class is utilised in the ImageViewer application.  This
  application tracks mouse movement when the cursor is over an image
  and invokes PixelInfoPane methods to display coordinates of the
  pixel under the cursor, and the value(s) at that pixel.

  See the ImageViewer class for further information.


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
import java.awt.image.*;
import javax.swing.*;



public class PixelInfoPane extends JPanel {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private BufferedImage image;
  private JLabel xCoord = new JLabel();
  private JLabel yCoord = new JLabel();
  private JLabel[] pixelValue = new JLabel[3];
  private int[] value = new int[3];


  //////////////////////////////// METHODS /////////////////////////////////


  public PixelInfoPane(BufferedImage img) {

    image = img;

    // Create a panel containing pixel coordinate information

    JPanel coordPane = new JPanel();
    coordPane.add(new JLabel("x"));
    Font fixedFont = new Font("Monospaced", Font.BOLD, 12);
    xCoord.setFont(fixedFont);
    xCoord.setForeground(Color.black);
    coordPane.add(xCoord);
    coordPane.add(new JLabel("y"));
    yCoord.setFont(fixedFont);
    yCoord.setForeground(Color.black);
    coordPane.add(yCoord);
    coordPane.setBorder(BorderFactory.createEtchedBorder());
    add(coordPane);

    // Create a panel containing pixel value information

    JPanel valuePane = new JPanel();
    if (image.getType() == BufferedImage.TYPE_BYTE_GRAY
     || image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
 
      // Greyscale or binary image, so only one value per pixel

      valuePane.add(new JLabel("grey level"));
      pixelValue[0] = new JLabel();
      pixelValue[0].setFont(fixedFont);
      pixelValue[0].setForeground(Color.black);
      valuePane.add(pixelValue[0]);

    }
    else {
 
      // Colour image, so three values needed

      valuePane.add(new JLabel("RGB"));
      for (int i = 0; i < 3; ++i) {
        pixelValue[i] = new JLabel();
        pixelValue[i].setFont(fixedFont);
        pixelValue[i].setForeground(Color.black);
        valuePane.add(pixelValue[i]);
      }

    }

    valuePane.setBorder(BorderFactory.createEtchedBorder());
    add(valuePane);

    updateInfo(new Point(0, 0));

  }


  // Updates displayed information, given new coordinates for a pixel

  public void updateInfo(Point pixel) {
    if (validPixel(pixel)) {

      // Pixel is within image, so update coordinates...

      xCoord.setText(formattedValue(pixel.x));
      yCoord.setText(formattedValue(pixel.y));

      // ...and pixel value(s) at those coordinates

      if (image.getType() == BufferedImage.TYPE_BYTE_GRAY
       || image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
 
        // Greyscale image

        pixelValue[0].setText(
         formattedValue(image.getRaster().getSample(pixel.x, pixel.y, 0)));

      }
      else {
 
        // Colour image

        image.getRaster().getPixel(pixel.x, pixel.y, value);
        for (int i = 0; i < 3; ++i)
          pixelValue[i].setText(formattedValue(value[i]));

      }

    }
  }


  // Checks whether a pixel lies within the image

  public boolean validPixel(Point pixel) {
    if (pixel.x >= 0 && pixel.x < image.getWidth() &&
     pixel.y >= 0 && pixel.y < image.getHeight())
      return true;
    else
      return false;
  }


  // Provides fixed-width formatting of integers in range 0-999

  public String formattedValue(int value) {
    if (value < 10)
      return new String("  " + value);
    else if (value < 100)
      return new String(" " + value);
    else
      return String.valueOf(value);
  }


}
