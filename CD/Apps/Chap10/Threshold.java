/***************************************************************************

  Threshold.java   Version 1.0 [1999/08/16]

  This program performs grey level or colour thresholding of an image.
  For the former, one or two thresholds must be supplied; for the latter,
  three or six thresholds must be supplied.  Input filename, output
  filename and thresholds are specified on the command line.

  Examples of use:

    java Threshold grey.png binary.png 150
    java Threshold grey.png binary.png 150 200
    java Threshold colour.png binary.png 128 128 128
    java Threshold colour.png binary.png 70 80 115 128 95 105


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


import java.awt.image.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;



public class Threshold {


  // Performs grey level thresholding using a ThresholdOp operator

  public static BufferedImage greyThreshold(BufferedImage image,
   int low, int high) {
    BufferedImageOp threshOp = new ThresholdOp(low, high);
    return threshOp.filter(image, null);
  }


  // Performs colour thresholding

  public static BufferedImage colourThreshold(BufferedImage image,
   int[] low, int[] high) {

    if (image.getType() == BufferedImage.TYPE_BYTE_GRAY
     || image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
      System.err.println("cannot apply colour thresholds to a grey image");
      System.exit(1);
    }

    // Create lookup table defining a volume of RGB space
    // for which output value is non-zero

    boolean[][] table = new boolean[3][256];
    for (int i = 0; i < 3; ++i)
      for (int j = low[i]; j <= high[i]; ++j)
        table[i][j] = true;

    int w = image.getWidth();
    int h = image.getHeight();
    Raster in = image.getRaster();
    BufferedImage output =
     new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster out = output.getRaster();

    // Iterate over data, thresholding values of R, G and B

    int[] data = new int[3];
    for (int y = 0; y < h; ++y)
      for (int x = 0; x < w; ++x) {
        in.getPixel(x, y, data);
        if (table[0][data[0]] && table[1][data[1]] && table[2][data[2]])
          out.setSample(x, y, 0, 255);
      }

    return output;

  }


  public static void main(String[] argv) {
    if (argv.length > 2) {
      try {

        // Parse image filename arguments and read image data

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        BufferedImage image = input.decodeAsBufferedImage();

        // Parse thresholds and perform thresholding,
        // writing results to output file

        if (argv.length > 4) {
          int[] low = new int[3];
          int[] high = new int[3];
          if (argv.length > 7) {
            int i, j;
            for (i = 0, j = 2; i < 3; ++i, j += 2) {
              low[i] = Integer.parseInt(argv[j]);
              high[i] = Integer.parseInt(argv[j+1]);
            }
          }
          else
            for (int i = 0; i < 3; ++i) {
              low[i] = Integer.parseInt(argv[2+i]);
              high[i] = 255;
            }
          output.encode(colourThreshold(image, low, high));
        }
        else if (argv.length > 2) {
          int low = Integer.parseInt(argv[2]);
          int high = (argv.length > 3 ? Integer.parseInt(argv[3]) : 255);
          output.encode(greyThreshold(image, low, high));
        }

        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java Threshold <infile> <outfile> <threshold>\n" +
       "       java Threshold <infile> <outfile> <thresh1> <thresh2>\n" +
       "       java Threshold <infile> <outfile> <red> <green> <blue>\n" +
       "       java Threshold <infile> <outfile> <r1> <r2> <g1> <g2> <b1> <b2>");
      System.exit(1);
    }
  }


}
