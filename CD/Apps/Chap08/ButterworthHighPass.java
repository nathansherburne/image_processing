/***************************************************************************

  ButterworthHighPass.java

  This program performs Butterworth high pass filtering of an image in
  the frequency domain.  The order of the filter and its radius must
  be specifed on the command line, along with a DC bias.

  Example of use:

    java ButterworthHighPass sharp.jpg blurred.jpg 1 0.4 127


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


public class ButterworthHighPass {
  public static void main(String[] argv) {
    if (argv.length > 4) {
      try {

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int n = Integer.parseInt(argv[2]);
        float r =
         Math.max(0.05f, Math.min(0.95f,
          Float.valueOf(argv[3]).floatValue()));
        int bias = Integer.parseInt(argv[4]);

        BufferedImage inputImage = input.decodeAsBufferedImage();
        ImageFFT fft = new ImageFFT(inputImage);
        System.out.println("Order " + n + " filter, radius = " + r + "...");
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        fft.transform();
        fft.butterworthHighPassFilter(n, r);
        fft.transform();
        System.out.println("Filtering finished [" + timer.stop() + " sec]");

        BufferedImage outputImage = fft.toImage(null, bias);
        output.encode(outputImage);
        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java ButterworthHighPass " +
       "<infile> <outfile> <order> <radius> <bias>");
      System.exit(1);
    }
  }

}
