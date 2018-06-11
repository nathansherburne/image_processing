/***************************************************************************

  MakeSteps.java

  Creates a synthetic image consisting of a 'staircase' of increasing
  grey level.


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



public class MakeSteps {


  // Creates stepped greyscale image

  public static BufferedImage createStepImage(int width, int height, int n) {

    // Allocate storage

    BufferedImage image =
     new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = image.getRaster();

    // Determine step size and scaling factor to ensure that
    // final step has a grey level of 255

    int stepWidth = width/n;
    float scale = 255.0f/((n-1)*stepWidth);

    // Compute stepped grey levels in first row of image

    int value;
    for (int x = 0; x < width; x += stepWidth) {
      value = Math.round(scale*x);
      for (int i = 0; i < stepWidth; ++i)
        raster.setSample(x+i, 0, 0, value);
    }

    // Copy first row of pixels to all remaining rows

    for (int y = 1; y < height; ++y)
      for (int x = 0; x < width; ++x)
        raster.setSample(x, y, 0, raster.getSample(x, 0, 0));

    return image;

  }


  // Checks values of command line parameters

  public static void checkParams(int w, int h, int n) {
    if (w < 8 || h < 8) {
      System.err.println("error: invalid image dimensions");
      System.exit(1);
    }
    if (n < 2 || n > w) {
      System.err.println("error: invalid number of steps");
      System.exit(1);
    }
    if (w%n != 0) {
      System.err.println("error: width must be a multiple of number of steps");
      System.exit(1);
    }
  }


  public static void main(String[] argv) {
    if (argv.length > 3) {
      try {
        ImageEncoder output = ImageFile.createImageEncoder(argv[0]);
        int width = Integer.parseInt(argv[1]);
        int height = Integer.parseInt(argv[2]);
        int numSteps = Integer.parseInt(argv[3]);
        checkParams(width, height, numSteps);
        BufferedImage image = createStepImage(width, height, numSteps);
        output.encode(image);
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java MakeSteps <outfile> <w> <h> <nsteps>");
      System.exit(1);
    }
  }


}
