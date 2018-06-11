/***************************************************************************

  GaussianBlur.java

  This program blurs an image by convolution with a 2D Gaussian kernel.
  Kernel width can specified by a standard deviation parameter supplied
  on the command line.  (If omitted, it defaults to 1.0.)

  Examples of use:

    java GaussianBlur sharp.jpg blurred.jpg
    java GaussianBlur input.pgm output.pgm 2.0


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
import com.pearsoneduc.ip.op.GaussianKernel;
import com.pearsoneduc.ip.util.IntervalTimer;


public class GaussianBlur {
  public static void main(String[] argv) {
    if (argv.length > 1) {
      try {

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        float sigma = 1.0f;
        if (argv.length > 2)
          sigma = Float.valueOf(argv[2]).floatValue();

        BufferedImage inputImage = input.decodeAsBufferedImage();
        Kernel kernel = new GaussianKernel(sigma);
        System.out.println("Convolving with a " + kernel.getWidth() +
         "x" + kernel.getHeight() + " kernel...");

        ConvolveOp blurOp = new ConvolveOp(kernel);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage outputImage = blurOp.filter(inputImage, null);
        System.out.println("Gaussian blur finished [" +
         timer.stop() + " sec]");

        output.encode(outputImage);
        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java GaussianBlur " +
       "<infile> <outfile> [<sigma>]");
      System.exit(1);
    }
  }
}
