/***************************************************************************

  MMSEFilterOp.java

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


package com.pearsoneduc.ip.op;


import java.awt.image.*;


/**
 * A class to perform minimal mean squared error filtering on 8-bit
 * greyscale images.
 *
 * @author Nick Efford
 * @version 1.0 [1999/07/31]
 */

public class MMSEFilterOp extends NeighbourhoodOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private float noiseVariance;
  private float[] neighbourhood;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Constructs an MMSEFilterOp with the specified noise variance, to
   * operate in a 3x3 neighbourhood with no special border processing.
   * @param variance noise variance
   */

  public MMSEFilterOp(float variance) {
    this(variance, 3, 3, NO_BORDER_OP);
  }

  /**
   * Constructs an MMSEFilterOp with the specified noise variance
   * and neighbourhood dimensions.  No special processing will
   * be done at the image borders.
   * @param variance noise variance
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   */

  public MMSEFilterOp(float variance, int w, int h) {
    this(variance, w, h, NO_BORDER_OP);
  }

  /**
   * Constructs an MMSEFilterOp with the specified noise variance,
   * neighbourhood dimensions and border processing strategy.
   * @param variance noise variance
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   * @param strategy border handling strategy
   */

  public MMSEFilterOp(float variance, int w, int h, int strategy) {
    super(w, h, strategy);
    if (variance <= 0.0f)
      throw new ImagingOpException("noise variance must be greater than zero");
    noiseVariance = variance;
    neighbourhood = new float[size];
  }


  /**
   * Performs minimal mean squared error filtering on an image.
   * @param src source image
   * @param dest destination image, or null
   * @return filtered image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);

    int w = src.getWidth();
    int h = src.getHeight();
    Raster srcRaster = src.getRaster();
    WritableRaster destRaster = dest.getRaster();

    int m = width/2;
    int n = height/2;
    switch (borderStrategy) {

      case REFLECTED_INDEXING:
        for (int y = 0; y < h; ++y)
          for (int x = 0; x < w; ++x) {
            int i = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j, ++i)
                neighbourhood[i] =
                 srcRaster.getSample(refIndex(x+j, w), refIndex(y+k, h), 0);
            destRaster.setSample(x, y, 0, filterValue());
          }
        break;

      case CIRCULAR_INDEXING:
        for (int y = 0; y < h; ++y)
          for (int x = 0; x < w; ++x) {
            int i = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j, ++i)
                neighbourhood[i] =
                 srcRaster.getSample(circIndex(x+j, w), circIndex(y+k, h), 0);
            destRaster.setSample(x, y, 0, filterValue());
          }
        break;

      case COPY_BORDER_PIXELS:
        copyBorders(srcRaster, destRaster);
        // fall through

      default:
        for (int y = n; y < h-n; ++y)
          for (int x = m; x < w-m; ++x) {
            int i = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j, ++i)
                neighbourhood[i] = srcRaster.getSample(x+j, y+k, 0);
            destRaster.setSample(x, y, 0, filterValue());
          }
        break;

    }

    return dest;

  }


  //////////////////////////// PRIVATE METHODS /////////////////////////////


  /**
   * @return value of minimal mean squared error filter at the
   *  current pixel location.
   */

  private int filterValue() {

    // Compute local mean

    float sum = 0.0f;
    for (int i = 0; i < size; ++i)
      sum += neighbourhood[i];
    float mean = sum/size;

    // Compute local variance

    sum = 0.0f;
    for (int i = 0; i < size; ++i)
      sum += (neighbourhood[i]-mean)*(neighbourhood[i]-mean);
    float variance = sum/size;

    // Compute filter value

    try {
      float ratio = noiseVariance / variance;
      if (ratio > 1.0f)
        return Math.round(mean);
      else {
        int result =
         Math.round((1.0f-ratio)*neighbourhood[size/2] + ratio*mean);
        return Math.max(0, Math.min(255, result));
      }
    }
    catch (ArithmeticException e) {
      return Math.round(mean);
    }

  }


}
