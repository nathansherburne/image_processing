/***************************************************************************

  Convolve.java

  This program convolves an image with a kernel read from a file.  In
  addition to filenames for the input image, output image and kernel,
  three integer parameters must be specified.  The first is 0 if no
  normalisation of the kernel is required, or a non-zero value if
  kernel coefficients should be normalised on input.

  The next parameter specifies border processing behaviour:

    1 = no processing of border pixels (resulting in a black border)
    2 = copying of border pixels from the input image
    3 = reflected indexing
    4 = circular indexing

  The final parameter specifies how convolution output is rescaled:

    1 = no rescaling (data truncated to a 0-255 range)
    2 = rescale maximum only (symmetrically about zero if necessary)
    3 = rescale such that minimum becomes 0 and maximum becomes 255


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
import java.io.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.IntervalTimer;


public class Convolve {
  public static void main(String[] argv) {
    if (argv.length > 5) {
      try {

        // Parse command line arguments

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        Reader kernelInput = new FileReader(argv[2]);
        boolean normaliseKernel = (Integer.parseInt(argv[3]) != 0);
        int borderStrategy =
         Math.max(1, Math.min(4, Integer.parseInt(argv[4])));
        int rescaleStrategy =
         Math.max(1, Math.min(3, Integer.parseInt(argv[5])));

        // Load image and kernel

        BufferedImage inputImage = input.decodeAsBufferedImage();
        Kernel kernel =
         StandardKernel.createKernel(kernelInput, normaliseKernel);

        // Create convolution operator and convolve image

        ConvolutionOp convOp = new ConvolutionOp(kernel,
         borderStrategy, ConvolutionOp.SINGLE_PASS, rescaleStrategy);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage outputImage = convOp.filter(inputImage, null);
        System.out.println("Convolution finished [" + timer.stop() + " sec]");

        // Write results to output file

        output.encode(outputImage);
        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Convolve " +
       "<infile> <outfile> <kernel> <norm> <border> <rescale>");
      System.exit(1);
    }
  }
}
