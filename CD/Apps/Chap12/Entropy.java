/***************************************************************************

  Entropy.java   Version 1.0 [1999/09/03]

  This program reads an image from a named file, generates a histogram
  of that image and then computes entropy, redundancy and a theoretical
  compression ratio for the image.  (This compression ratio is the
  maximum achievable through removal of the coding redundancy in the
  image.)


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
import java.text.DecimalFormat;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;



public class Entropy {


  public static final double LOG_TWO = Math.log(2.0);


  public static final double logBase2(double value) {
    return Math.log(value)/LOG_TWO;
  }


  public static double calculateEntropy(Histogram histogram)
   throws HistogramException {
    int n = histogram.getNumSamples();
    double p, sum = 0.0;
    for (int i = 0; i < 256; ++i)
      if (histogram.getFrequency(i) > 0) {
        p = ((double) histogram.getFrequency(i)) / n;
        sum += p*logBase2(p);
      }
    return -sum;
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        BufferedImage image = input.decodeAsBufferedImage();
        double entropy = calculateEntropy(new Histogram(image));
        DecimalFormat number = new DecimalFormat("0.000");
        System.out.println("Entropy     = " + number.format(entropy));
        System.out.println("Redundancy  = " + number.format(8.0-entropy));
        System.out.println("Comp. ratio = " + number.format(8.0/entropy));
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Entropy <imagefile>");
      System.exit(1);
    }
  }


}
