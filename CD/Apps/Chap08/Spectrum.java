/***************************************************************************

  Spectrum.java

  This program computes the Fourier spectrum of an image, in the form
  of another image.  The spectrum is shifted such that the DC component
  is at the origin, and scaled logarithmically so that low-amplitude
  detail will be visible.  Two filenames, representing the input and
  output images, must be specified on the command line.  An optional
  third argument specifies the windowing function to be used when
  computing the spectrum:

    1 = no window
    2 = Bartlett window
    3 = Hamming window
    4 = Hanning window

  The default is no window.

  Examples of use:

    java Spectrum image.png spec.png
    java Spectrum image.png spec.png 3


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


public class Spectrum {
  public static void main(String[] argv) {
    if (argv.length > 1) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int window = 1;
        if (argv.length > 2)
          window = Integer.parseInt(argv[2]);
        BufferedImage inputImage = input.decodeAsBufferedImage();
        ImageFFT fft = new ImageFFT(inputImage, window);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        fft.transform();
        System.out.println("FFT finished [" + timer.stop() + " sec]");
        output.encode(fft.getSpectrum());
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Spectrum <infile> <outfile> [<window>]");
      System.exit(1);
    }
  }

}
