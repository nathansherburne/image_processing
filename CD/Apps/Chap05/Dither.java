/***************************************************************************

  Dither.java

  Given an image filename as a command line argument, this program reads
  the image stored in that file and halftones it using 2x2 and 4x4
  dither matrices.  The original image and the two halftoned images are
  displayed in a tabbed pane, so it is easy to switch back and forth
  between them.  Colour images are converted to greyscale images before
  dithering.

  Example of use:

    java Dither greyimg.jpg


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



import java.io.*;
import java.awt.image.*;
import java.awt.color.ColorSpace;
import javax.swing.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.gui.*;



public class Dither extends JFrame {


  // first dither matrix (2x2)
  private static final int[][] d1 = {{  0, 128},
                                     {192,  64}};

  // second dither matrix (4x4)
  private static final int[][] d2 = {{  0, 128,  32, 160},
                                     {192,  64, 224,  96},
                                     { 48, 176,  16, 144},
                                     {240, 112, 208,  80}};

  private BufferedImage sourceImage;   // image to be dithered
  private ImageView[] views;           // image display components


  public Dither(String imageFile)
   throws IOException, ImageDecoderException {

    super("Dither: " + imageFile);
    readImage(imageFile);

    views = new ImageView[3];
    views[0] = new ImageView(sourceImage);
    views[1] = new ImageView(ditherByMatrix(d1));
    views[2] = new ImageView(ditherByMatrix(d2));
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add(new JScrollPane(views[0]), "input");
    tabbedPane.add(new JScrollPane(views[1]), "2x2 dither");
    tabbedPane.add(new JScrollPane(views[2]), "4x4 dither");

    getContentPane().add(tabbedPane);
    addWindowListener(new WindowMonitor());

  }


  /*
   * Read an image from a named file, converting
   * to greyscale if necessary.
   */

  public void readImage(String filename)
   throws IOException, ImageDecoderException {
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    sourceImage = input.decodeAsBufferedImage();
    if (sourceImage.getType() != BufferedImage.TYPE_BYTE_GRAY) {
      System.err.println("Converting colour image to greyscale image...");
      ColorConvertOp op = new ColorConvertOp(
       ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
      BufferedImage greyImage = op.filter(sourceImage, null);
      sourceImage = greyImage;
    }
  }


  /*
   * Compute and return a dithered version of the source image,
   * using the supplied dither matrix.
   */

  public BufferedImage ditherByMatrix(int[][] matrix) {

    // Create binary image to hold result

    int w = sourceImage.getWidth();
    int h = sourceImage.getHeight();
    BufferedImage ditheredImage =
     new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

    Raster input = sourceImage.getRaster();
    WritableRaster output = ditheredImage.getRaster();
    int n = matrix.length;
    for (int y = 0; y < h; ++y)
      for (int x = 0; x < w; ++x) {
        int threshold = matrix[y%n][x%n];  // tiles image with the matrix
        if (input.getSample(x, y, 0) > threshold)
          output.setSample(x, y, 0, 1);
      }

    return ditheredImage;

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new Dither(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Dither <imagefile>");
      System.exit(1);
    }
  }


}
