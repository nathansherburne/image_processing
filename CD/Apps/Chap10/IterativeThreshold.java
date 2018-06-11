/***************************************************************************

  IterativeThreshold.java   Version 1.1 [1999/08/26]


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



public class IterativeThreshold {


  // Determines an appropriate threshold

  public static int findThreshold(BufferedImage image)
   throws HistogramException {

    Histogram histogram = new Histogram(image);
    float mean1 = meanOfCorners(image);
    float mean2 = (float) histogram.getMeanValue();
    int oldThreshold = 0;
    int threshold = Math.round((mean1+mean2)/2.0f);
    System.out.println("T=" + threshold);

    float sum1;
    while (threshold != oldThreshold) {
      oldThreshold = threshold;
      sum1 = sum(histogram, 0, threshold-1);
      mean1 = weightedSum(histogram, 0, threshold-1) / sum1;
      mean2 = weightedSum(histogram, threshold, 255) /
       (histogram.getNumSamples() - sum1);
      threshold = Math.round((mean1+mean2)/2.0f);
      System.out.println("T=" + threshold);
    }

    return threshold;

  }


  // Computes mean grey level of image corners

  public static float meanOfCorners(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    Raster raster = image.getRaster();
    float sum = raster.getSample(0, 0, 0) + raster.getSample(w-1, 0, 0) +
     raster.getSample(0, h-1, 0) + raster.getSample(w-1, h-1, 0);
    return sum/4.0f;
  }


  // Sums histogram frequencies over the specified range of grey levels

  public static float sum(Histogram histogram, int low, int high)
   throws HistogramException {
    int sum = 0;
    for (int i = low; i <= high; ++i)
      sum += histogram.getFrequency(i);
    return (float) sum;
  }


  // Sums the product of grey level and frequency over
  // the specified range of grey levels

  public static float weightedSum(Histogram histogram, int low, int high)
   throws HistogramException {
    int sum = 0;
    for (int i = low; i <= high; ++i)
      sum += i*histogram.getFrequency(i);
    return (float) sum;
  }


  public static void main(String[] argv) {
    if (argv.length > 1) {
      try {

        // Parse image filename arguments and read image data

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        BufferedImage image = input.decodeAsBufferedImage();
        if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
          System.err.println("error: 8-bit greyscale image required");
          System.exit(1);
        }

        // Determine and apply threshold, writing results to output file

        int threshold = findThreshold(image);
        BufferedImageOp threshOp = new ThresholdOp(threshold);
        output.encode(threshOp.filter(image, null));
        System.exit(0);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java IterativeThreshold <infile> <outfile>");
      System.exit(1);
    }
  }


}
