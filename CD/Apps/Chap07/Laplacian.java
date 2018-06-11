/***************************************************************************

  Laplacian.java

  This program computes the Laplacian of an image, by convolution.  The
  output is shifted and scaled to lie in a 0-255 range, with a filter
  output of zero represented by a grey level of 128.

  Example of use:

    java Laplacian input.jpg output.jpg


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
import com.pearsoneduc.ip.util.IntervalTimer;


public class Laplacian {
  public static void main(String[] argv) {
    if (argv.length > 1) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        BufferedImage image = input.decodeAsBufferedImage();
        Kernel kernel = new LaplacianKernel();
        BufferedImageOp op = new ConvolutionOp(
         kernel, NeighbourhoodOp.NO_BORDER_OP,
         ConvolutionOp.SINGLE_PASS, ConvolutionOp.RESCALE_MAX_ONLY);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage outputImage = op.filter(image, null);
        System.out.println("Laplacian filtering finished [" +
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
      System.err.println("usage: java Laplacian <infile> <outfile>");
      System.exit(1);
    }
  }

}
