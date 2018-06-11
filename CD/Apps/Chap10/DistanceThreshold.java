/***************************************************************************

  DistanceThreshold.java   Version 1.0 [1999/08/16]

  This program segments a colour image by 'distance thresholding',
  whereby pixels within a particular radius in RGB space from some
  reference colour are set to 255, all other pixels being set to 0.
  A single radius can be specified, defining a spherical volume
  in RGB space within which pixels are detected; alternatively, three
  radii can be given, defining an ellipsoidal volume.


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



public class DistanceThreshold {


  // Thresholds an image using distance of a pixel from a reference colour

  public static BufferedImage colourThreshold(BufferedImage image,
   int[] colour, double[] radius) {

    int w = image.getWidth();
    int h = image.getHeight();
    Raster in = image.getRaster();
    BufferedImage output =
     new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster out = output.getRaster();

    int[] pixelColour = new int[3];
    for (int y = 0; y < h; ++y)
      for (int x = 0; x < w; ++x) {
        in.getPixel(x, y, pixelColour);
        if (insideEllipsoid(pixelColour, colour, radius))
          out.setSample(x, y, 0, 255);
      }

    return output;

  }


  // Determines whether a point lies inside an ellipsoid

  public static boolean insideEllipsoid(int[] p, int[] centre, double[] r) {
    double d, sum = 0.0;
    for (int i = 0; i < 3; ++i) {
      d = p[i] - centre[i];
      sum += d*d/(r[i]*r[i]);
    }
    return sum <= 1.0;
  }


  public static void main(String[] argv) {
    if (argv.length > 5) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int[] colour = new int[3];
        for (int i = 0; i < 3; ++i)
          colour[i] = Integer.parseInt(argv[2+i]);
        double[] radius = new double[3];
        radius[0] = Double.valueOf(argv[5]).doubleValue();
        if (argv.length > 7) {
          radius[1] = Double.valueOf(argv[6]).doubleValue();
          radius[2] = Double.valueOf(argv[7]).doubleValue();
        }
        else
          radius[2] = radius[1] = radius[0];
        BufferedImage image = input.decodeAsBufferedImage();
        output.encode(colourThreshold(image, colour, radius));
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java DistanceThreshold " +
       "<infile> <outfile> <r> <g> <b> <dist>\n" +
       "       java DistanceThreshold " +
       "<infile> <outfile> <r> <g> <b> <dr> <dg> <db>");
      System.exit(1);
    }
  }


}
