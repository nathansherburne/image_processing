/***************************************************************************

  MMSEFilter.java

  This program performs minimal mean squared error (MMSE) filtering on
  an image, using the specified neighbourhood dimensions and noise
  variance.

  Example of use:

    java MMSEFilter input.jpg output.jpg 3 3 100


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
import com.pearsoneduc.ip.op.MMSEFilterOp;
import com.pearsoneduc.ip.util.IntervalTimer;


public class MMSEFilter {
  public static void main(String[] argv) {
    if (argv.length > 4) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int w = Integer.parseInt(argv[2]);
        int h = Integer.parseInt(argv[3]);
        float variance = Float.valueOf(argv[4]).floatValue();
        BufferedImage inputImage = input.decodeAsBufferedImage();
        BufferedImageOp op = new MMSEFilterOp(variance, w, h);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage outputImage = op.filter(inputImage, null);
        System.out.println("MMSE filtering finished [" +
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
      System.err.println("usage: java MMSEFilter " +
       "<infile> <outfile> <width> <height> <variance>");
      System.exit(1);
    }
  }
}
