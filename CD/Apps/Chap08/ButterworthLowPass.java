/***************************************************************************

  ButterworthLowPass.java

  This program performs Butterworth low pass filtering of an image in
  the frequency domain.  The filter order and a filter radius between
  0.05 and 0.95 must be specified.

  Example of use:

    java ButterworthLowPass sharp.jpg blurred.jpg 1 0.4


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


public class ButterworthLowPass {
  public static void main(String[] argv) {
    if (argv.length > 3) {
      try {

        ImageDecoder input = ImageFile.createImageDecoder("C:\\Users\\karan\\Desktop\\Blonde2.jpg");
        ImageEncoder output = ImageFile.createImageEncoder("C:\\Users\\karan\\Desktop\\out.jpg");
        int n = Integer.parseInt(argv[2]);
        float r =
         Math.max(0.05f, Math.min(0.95f,
          Float.valueOf(argv[3]).floatValue()));

        BufferedImage inputImage = input.decodeAsBufferedImage();
        ImageFFT fft = new ImageFFT(inputImage);
        System.out.println("Order " + n + " filter, radius = " + r + "...");
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        fft.transform();
        fft.butterworthLowPassFilter(n, r);
        fft.transform();
        System.out.println("Filtering finished [" + timer.stop() + " sec]");

        BufferedImage outputImage = fft.toImage(null);
        output.encode(outputImage);
        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java ButterworthLowPass " +
       "<infile> <outfile> <order> <radius>");
      System.exit(1);
    }
  }

}
