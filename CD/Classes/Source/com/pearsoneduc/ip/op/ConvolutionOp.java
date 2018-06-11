/***************************************************************************

  ConvolutionOp.java

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
 * A class to perform convolution.  ConvolutionOp is slower than
 * ConvolveOp, but it is more flexible.  Four different strategies for
 * dealing with the image borders are supported (compared with two
 * for ConvolveOp).  Also, ConvolutionOp supports separable convolution,
 * whereby a 1D kernel is applied along the rows of an image to generate
 * an intermediate result, and then to the columns of this intermediate
 * result to produce the final image.  Finally, ConvolutionOp handles
 * convolution with kernels containing negative coefficients properly,
 * by convolving to temporary storage and then rescaling the stored
 * data to lie within a 0-255 range.
 *
 * <p>ConvolutionOp also hosts a small number of static methods that
 * act as shortcuts for common operations such as image blurring.
 * These static methods delegate responsibility for convolution to
 * ConvolutionOp or to ConvolveOp, as appropriate - ensuring that
 * convolution is done in the most efficient manner possible.</p>
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/27]
 */

public class ConvolutionOp extends NeighbourhoodOp {


  //////////////////////////// CLASS CONSTANTS /////////////////////////////


  /** Indicates the convolution is to be done in a single pass. */
  public static final int SINGLE_PASS = 1;

  /** Indicates that separable convolution is to be done. */
  public static final int SEPARABLE = 2;

  /** Indicates that no rescaling of output values should be done. */
  public static final int NO_RESCALING = 1;

  /** Indicates that maximum output value should be scaled to 255. */
  public static final int RESCALE_MAX_ONLY = 2;

  /** Indicates that range should be scaled to 0-255. */
  public static final int RESCALE_MIN_AND_MAX = 3;


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Kernel used for convolution. */
  private Kernel kernel;

  /** Calculation method (single pass or separable). */
  private int calculation;

  /** Strategy used to rescale output values. */
  private int rescaleStrategy;


  ///////////////////////// PUBLIC STATIC METHODS //////////////////////////


  /**
   * Computes a uniform blur of an image using the specified
   * neighbourhood dimensions.  No special measures will taken
   * to deal with image borders.
   * @param image source image
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   * @return blurred image.
   */

  public static BufferedImage blur(BufferedImage image, int w, int h) {
    return blur(image, w, h, NO_BORDER_OP);
  }

  /**
   * Computes a uniform blur of an image using the specified
   * neighbourhood and border handling strategy.
   * @param image source image
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   * @param border border handling strategy
   * @return blurred image.
   */

  public static BufferedImage blur(
   BufferedImage image, int w, int h, int border) {
    Kernel kernel = new MeanKernel(w, h);
    BufferedImageOp blurOp;
    if (border == CIRCULAR_INDEXING
     || border == REFLECTED_INDEXING)
      blurOp =
       new ConvolutionOp(kernel, border, SINGLE_PASS, NO_RESCALING);
    else if (border == COPY_BORDER_PIXELS)
      blurOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    else
      blurOp = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
    return blurOp.filter(image, null);
  }


  /**
   * Performs Gaussian blurring of an image using a separable kernel.
   * @param image source image
   * @param sigma standard deviation of Gaussian
   * @return blurred image.
   */

  public static BufferedImage gaussianBlur(BufferedImage image, float sigma) {
    return gaussianBlur(image, sigma, NO_BORDER_OP);
  }

  /**
   * Performs Gaussian blurring of an image using a separable kernel
   * and the specified border strategy.
   * @param image source image
   * @param sigma standard deviation of Gaussian
   * @param border border handling strategy
   * @return blurred image.
   */

  public static BufferedImage gaussianBlur(
   BufferedImage image, float sigma, int borderStrategy) {
    Kernel kernel = new SeparableGaussianKernel(sigma);
    BufferedImageOp blurOp =
     new ConvolutionOp(kernel, borderStrategy, SEPARABLE, NO_RESCALING);
    return blurOp.filter(image, null);
  }


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Constructs a ConvolutionOp for a given kernel.  No special
   * measures will be taken at the image borders.  Convolution will
   * be performed in a single pass, and no rescaling of output
   * values will take place.
   * @param kernel convolution kernel
   */

  public ConvolutionOp(Kernel kernel) {
    this(kernel, NO_BORDER_OP, SINGLE_PASS, NO_RESCALING);
  }

  /**
   * Constructs a ConvolutionOp with a specified kernel, border
   * handling strategy, calculation method and rescaling strategy.
   * @param kernel convolution kernel
   * @param border border handling strategy
   * @param calc calculation method
   * @param rescale rescaling strategy
   */

  public ConvolutionOp(Kernel kernel, int border, int calc, int rescale) {
    super(kernel.getWidth(), kernel.getHeight(), border);
    this.kernel = kernel;
    calculation = calc;
    rescaleStrategy = rescale;
  }


  /**
   * Performs convolution on an image.
   * @param src source image
   * @param dest destination image, or null
   * @return convolved image
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);

    float[] rawData;
    if (calculation == SEPARABLE)
      rawData = separableConvolve(src);
    else
      rawData = convolve(src);

    DataBufferByte buffer = (DataBufferByte) dest.getRaster().getDataBuffer();
    convertToBytes(rawData, buffer.getData());
    return dest;

  }


  /**
   * Performs convolution on an image.
   * @param image source image
   * @return array of convolved data
   */

  public float[] convolve(BufferedImage image) {

    int w = image.getWidth();
    int h = image.getHeight();
    Raster raster = image.getRaster();
    float[] result = new float[w*h];
    float[] coeff = kernel.getKernelData(null);
    int m = width/2, n = height/2;

    float sum;
    int i, j, k, x, y;
    switch (borderStrategy) {

      case REFLECTED_INDEXING:
        for (y = 0; y < h; ++y)
          for (x = 0; x < w; ++x) {
            for (sum = 0.0f, i = 0, k = -n; k <= n; ++k)
              for (j = -m; j <= m; ++j, ++i)
                sum += coeff[i] * raster.getSample(
                 refIndex(x-j, w), refIndex(y-k, h), 0);
            result[y*w+x] = sum;
          }
        break;

      case CIRCULAR_INDEXING:
        for (y = 0; y < h; ++y)
          for (x = 0; x < w; ++x) {
            for (sum = 0.0f, i = 0, k = -n; k <= n; ++k)
              for (j = -m; j <= m; ++j, ++i)
                sum += coeff[i] * raster.getSample(
                 circIndex(x-j, w), circIndex(y-k, h), 0);
            result[y*w+x] = sum;
          }
        break;

      case COPY_BORDER_PIXELS:
        copyBorders(raster, result);
        // fall through and let the NO_BORDER_OP
        // case do the convolution...

      default:
        // NO_BORDER_OP
        for (y = n; y < h-n; ++y)
          for (x = m; x < w-m; ++x) {
            for (sum = 0.0f, i = 0, k = -n; k <= n; ++k)
              for (j = -m; j <= m; ++j, ++i)
                sum += coeff[i] * raster.getSample(x-j, y-k, 0);
            result[y*w+x] = sum;
          }
        break;

    }

    return result;
 
  }


  /**
   * Performs separable convolution on an image.
   * @param image source image
   * @return array of convolved data
   */

  public float[] separableConvolve(BufferedImage image) {

    if (height != 1)
      throw new ImagingOpException(
       "1D kernel required for separable convolution");

    int w = image.getWidth();
    int h = image.getHeight();
    Raster raster = image.getRaster();
    float[] tmp = new float[w*h];
    float[] result = new float[w*h];
    float[] coeff = kernel.getKernelData(null);
    int n = width/2;

    float sum;
    int i, x, y;
    switch (borderStrategy) {

      case REFLECTED_INDEXING:
        for (y = 0; y < h; ++y)     // horizontal pass
          for (x = 0; x < w; ++x) {
            for (sum = 0.0f, i = -n; i <= n; ++i)
              sum += coeff[i+n] * raster.getSample(refIndex(x-i, w), y, 0);
            tmp[y*w+x] = sum;
          }
        for (y = 0; y < h; ++y)     // vertical pass
          for (x = 0; x < w; ++x) {
            for (sum = 0.0f, i = -n; i <= n; ++i)
              sum += coeff[i+n] * tmp[w*refIndex(y-i, h) + x];
            result[y*w+x] = sum;
          }
        break;

      case CIRCULAR_INDEXING:
        for (y = 0; y < h; ++y)     // horizontal pass
          for (x = 0; x < w; ++x) {
            for (sum = 0.0f, i = -n; i <= n; ++i)
              sum += coeff[i+n] * raster.getSample(circIndex(x-i, w), y, 0);
            tmp[y*w+x] = sum;
          }
        for (y = 0; y < h; ++y)     // vertical pass
          for (x = 0; x < w; ++x) {
            for (sum = 0.0f, i = -n; i <= n; ++i)
              sum += coeff[i+n] * tmp[w*circIndex(y-i, h) + x];
            result[y*w+x] = sum;
          }
        break;

      case COPY_BORDER_PIXELS:
        copyBorders(raster, result);
        // fall through and let the NO_BORDER_OP
        // case do the convolution...

      default:
        // NO_BORDER_OP
        copyBorders(raster, tmp);
        for (y = n; y < h-n; ++y)   // horizontal pass
          for (x = n; x < w-n; ++x) {
            for (sum = 0.0f, i = -n; i <= n; ++i)
              sum += coeff[i+n] * raster.getSample(x-i, y, 0);
            tmp[y*w+x] = sum;
          }
        for (y = n; y < h-n; ++y)   // vertical pass
          for (x = n; x < w-n; ++x) {
            for (sum = 0.0f, i = -n; i <= n; ++i)
              sum += coeff[i+n] * tmp[(y-i)*w + x];
            result[y*w+x] = sum;
          }
        break;

    }

    return result;

  }


  /////////////////////////// PROTECTED METHODS ////////////////////////////


  /**
   * Copies border pixels that cannot be convolved by normal means
   * from a raster to an array.
   * @param src source raster
   * @param dest destination array
   */

  protected void copyBorders(Raster src, float[] dest) {

    int w = src.getWidth();
    int h = src.getHeight();
    int m = width/2, n;
    if (calculation == SEPARABLE)
      n = m;
    else
      n = height/2;

    for (int x = 0; x < w; ++x) {     // copy top and bottom
      for (int y = 0; y < n; ++y)
        dest[y*w+x] = src.getSample(x, y, 0);
      for (int y = h-n; y < h; ++y)
        dest[y*w+x] = src.getSample(x, y, 0);
    }
    for (int y = 0; y < h; ++y) {     // copy left and right
      for (int x = 0; x < m; ++x)
        dest[y*w+x] = src.getSample(x, y, 0);
      for (int x = w-m; x < w; ++x)
        dest[y*w+x] = src.getSample(x, y, 0);
    }

  }


  /**
   * Converts raw convolved values to bytes, rescaling if necessary.
   * @param in array of raw floating-point values
   * @param out array of bytes
   */

  protected void convertToBytes(float[] in, byte[] out) {

    if (rescaleStrategy == NO_RESCALING) {
      for (int i = 0; i < in.length; ++i) {
        int value = Math.round(in[i]);
        if (value < 0)
          out[i] = (byte) 0;
        else if (value > 255)
          out[i] = (byte) 255;
        else
          out[i] = (byte) value;
      }
    }
    else {

      // Determine range of input data

      float minimum = in[0];
      float maximum = in[0];
      for (int i = 1; i < in.length; ++i)
        if (in[i] < minimum)
          minimum = in[i];
        else if (in[i] > maximum)
          maximum = in[i];

      if (rescaleStrategy == RESCALE_MIN_AND_MAX) {
        float scale = 255.0f / (maximum - minimum);
        for (int i = 0; i < in.length; ++i)
          out[i] = (byte) Math.round(scale*(in[i]-minimum));
      }
      else {  // RESCALE_MAX_ONLY
        if (minimum < 0.0f && maximum > 0.0f) {
          float max = Math.max(Math.abs(minimum), maximum);
          float scale = 127.5f / max;
          for (int i = 0; i < in.length; ++i)
            out[i] = (byte) Math.round(scale*(in[i]+max));
        }
        else {
          float scale = 255.0f / maximum;
          for (int i = 0; i < in.length; ++i)
            out[i] = (byte) Math.round(scale*in[i]);
        }
      }

    }

  }


}
