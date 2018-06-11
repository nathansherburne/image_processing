/***************************************************************************

  GreyDilate.java

  This program performs greyscale dilation on an image read from a file
  using a structuring element read from another file.  The dilated image
  is written to a new file.  By default output values are truncated
  to lie in a 0-255 range; if the word 'rescale' is specified as a fourth
  command line parameter, output values will be rescaled to lie in this
  range.

  Examples of use:

    java GreyDilate grey.png dilated.png sphere.gse
    java GreyDilate grey.png dilated.png square.gse rescale


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
import java.io.FileReader;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.IntervalTimer;


public class GreyDilate {
  public static void main(String[] argv) {
    if (argv.length > 2) {
      try {

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        BufferedImage inputImage = input.decodeAsBufferedImage();

        GreyStructElement structElement =
         new GreyStructElement(new FileReader(argv[2]));
        boolean rescale = false;
        if (argv.length > 3)
          rescale = argv[3].equalsIgnoreCase("rescale");
        BufferedImageOp dilateOp = new GreyDilateOp(structElement, rescale);

        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage outputImage = dilateOp.filter(inputImage, null);
        System.out.println("Dilation finished [" + timer.stop() + " sec]");
        output.encode(outputImage);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java GreyDilate <infile> <outfile> <structElement> [rescale]");
      System.exit(1);
    }
  }
}
