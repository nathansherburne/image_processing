/***************************************************************************

  GaussianNoise.java

  This program adds Gaussian random noise with zero mean and a specified
  standard deviation to an image.  A random number seed can also be
  specified, if desired.

  Examples of use:

    java GaussianNoise clean.png noisy.png 20.0
    java GaussianNoise clean.png noisy.png 20.0 11573


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
import java.util.Random;
import com.pearsoneduc.ip.io.*;


public class GaussianNoise {
  public static void main(String[] argv) {
    if (argv.length > 2) {
      try {

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        double stdDev = Double.valueOf(argv[2]).doubleValue();
        Random random = new Random();
        if (argv.length > 3) {
          long seed = Long.parseLong(argv[3]);
          random.setSeed(seed);
        }

        BufferedImage image = input.decodeAsBufferedImage();
        WritableRaster raster = image.getRaster();
        for (int y = 0; y < image.getHeight(); ++y)
          for (int x = 0; x < image.getWidth(); ++x) {
            int value = (int) Math.round(raster.getSample(x, y, 0) +
             stdDev*random.nextGaussian());
            raster.setSample(x, y, 0, Math.max(0, Math.min(255, value)));
          }

        output.encode(image);
        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java GaussianNoise " +
       "<infile> <outfile> <standardDeviation> [seed]");
      System.exit(1);
    }
  }
}
