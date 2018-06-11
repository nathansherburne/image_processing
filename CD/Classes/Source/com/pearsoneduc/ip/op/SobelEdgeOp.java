/***************************************************************************

  SobelEdgeOp.java

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
 * Performs edge detection using gradients computed with Sobel kernels.
 * Gradient magnitude data are thresholded if a threshold was specified
 * when creating the SobelEdgeOp.
 *
 * @author Nick Efford
 * @version 1.0 [1999/07/23]
 */

public class SobelEdgeOp extends StandardGreyOp {


  ///////////////////////////// CLASS CONSTANTS ////////////////////////////


  public static final int SQRT_MAGNITUDE = 1;
  public static final int ABS_MAGNITUDE = 2;


  //////////////////////////// INSTANCE VARIABLES //////////////////////////


  /** Threshold to apply to gradient magnitude (-1 = no thresholding) */
  private int gradientThreshold = -1;

  /** Method of magnitude calculation. */
  private int magnitudeCalculation = SQRT_MAGNITUDE;


  ////////////////////////////// PUBLIC METHODS ////////////////////////////


  /**
   * Constructs a SobelEdgeOp that computes gradient magnitudes
   * without thresholding.
   */

  public SobelEdgeOp() {}

  /**
   * Constructs a SobelEdgeOp that computes gradient magnitudes
   * and performs thresholding to produce an edge map.
   * @param threshold gradient magnitude threshold
   */

  public SobelEdgeOp(int threshold) {
    gradientThreshold = threshold;
  }

  /**
   * Constructs a SobelEdgeOp that computes gradient magnitudes
   * using the specified calculation method and then performs
   * thresholding to produce an edge map.
   * @param threshold gradient magnitude threshold
   * @param magCalc gradient magnitude calculation method
   */

  public SobelEdgeOp(int threshold, int magCalc) {
    gradientThreshold = threshold;
    magnitudeCalculation = magCalc;
  }


  /**
   * @return current threshold on gradient magnitude.
   */

  public int getGradientThreshold() {
    return gradientThreshold;
  }


  /**
   * @return gradient magnitude calculation method.
   */

  public int getMagnitudeCalculation() {
    return magnitudeCalculation;
  }


  /**
   * Computes gradient magnitude for an image using the Sobel
   * kernels and optionally thresholds the data to produce
   * an edge map.
   * @param src source image
   * @param dest destination image, or null
   * @return processed image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);
    WritableRaster raster = dest.getRaster();

    // Compute gradient magnitude

    float[] gradient = gradientMagnitude(src);
    float maxGradient = gradient[0];
    for (int i = 1; i < gradient.length; ++i)
      if (gradient[i] > maxGradient)
        maxGradient = gradient[i];

    int w = src.getWidth();
    int h = src.getHeight();
    float scaleFactor = 255.0f/maxGradient;
    if (gradientThreshold >= 0) {

      // Apply threshold to scaled gradient magnitudes

      for (int y = 1; y < h-1; ++y)
        for (int x = 1; x < w-1; ++x)
          if (Math.round(scaleFactor*gradient[y*w+x]) >= gradientThreshold)
            raster.setSample(x, y, 0, 255);

    }
    else {

      // Output rescaled gradient magnitudes

      for (int y = 1; y < h-1; ++y)
        for (int x = 1; x < w-1; ++x)
          raster.setSample(x, y, 0,
           Math.round(scaleFactor*gradient[y*w+x]));

    }

    return dest;

  }


  //////////////////////////// PROTECTED METHODS ///////////////////////////


  /**
   * Computes gradient magnitudes for the specified image.
   * @param image BufferedImage to be processed
   * @return array of gradient magnitudes
   */

  protected float[] gradientMagnitude(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    float[] mag = new float[w*h];
    Raster raster = image.getRaster();
    int gx, gy;
    if (magnitudeCalculation == ABS_MAGNITUDE) {
      for (int y = 1; y < h-1; ++y)
        for (int x = 1; x < w-1; ++x) {
          gx = xGradient(raster, x, y);
          gy = yGradient(raster, x, y);
          mag[y*w+x] = (float) (Math.abs(gx) + Math.abs(gy));
        }
    }
    else {
      for (int y = 1; y < h-1; ++y)
        for (int x = 1; x < w-1; ++x) {
          gx = xGradient(raster, x, y);
          gy = yGradient(raster, x, y);
          mag[y*w+x] = (float) Math.sqrt(gx*gx + gy*gy);
        }
    }
    return mag;
  }


  /**
   * Computes the x component of the gradient vector
   * at a given point in a raster.
   * @param raster pixel data
   * @param x x coordinate of point under consideration
   * @param y y coordinate of point under consideration
   * @return gradient in the x direction.
   */

  protected final int xGradient(Raster raster, int x, int y) {
    return raster.getSample(x-1, y-1, 0) +
            2*raster.getSample(x-1, y, 0) +
             raster.getSample(x-1, y+1, 0) -
              raster.getSample(x+1, y-1, 0) -
               2*raster.getSample(x+1, y, 0) -
                raster.getSample(x+1, y+1, 0);
  }


  /**
   * Computes the y component of the gradient vector
   * at a given point in a raster.
   * @param raster pixel data
   * @param x x coordinate of point under consideration
   * @param y y coordinate of point under consideration
   * @return gradient in the y direction.
   */

  protected final int yGradient(Raster raster, int x, int y) {
    return raster.getSample(x-1, y-1, 0) +
            2*raster.getSample(x, y-1, 0) +
             raster.getSample(x+1, y-1, 0) -
              raster.getSample(x-1, y+1, 0) -
               2*raster.getSample(x, y+1, 0) -
                raster.getSample(x+1, y+1, 0);
  }


}
