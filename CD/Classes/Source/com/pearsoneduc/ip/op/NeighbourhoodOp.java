/***************************************************************************

  NeighbourhoodOp.java

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
 * Adds support for neighbourhood operations to StandardGreyOp.
 *
 * <p>This class supplies instance variables to represent neighbourhood
 * dimensions and provides support for processing at the borders
 * of an image.  No actual processing is carried out.  Subclasses must
 * override the filter() method inherited from StandardGreyOp in order
 * to do anything useful.</p>
 *
 * <p>Four possible strategies are supported for border processing:</p>
 * <ul>
 *  <li> No operation (resulting in a black border of unprocessed pixels)
 *  <li> Copying of data from source image
 *  <li> Reflected indexing
 *  <li> Circular indexing
 * </ul>
 *
 * <p>Copying of data is supported by the protected
 * @link{#copyBorders(BufferedImage, BufferedImage) copyBorders()} method.
 * Also available are static methods @link{#refIndex(int, int) refIndex()}
 * and @link{#circIndex(int, int) circIndex()}.  The former reflects invalid
 * coordinates back into the image, whereas the latter forces coordinates
 * to 'wrap around'.</p>
 *
 * @author Nick Efford
 * @version 1.0 [1999/07/23]
 */

public class NeighbourhoodOp extends StandardGreyOp {


  ///////////////////////////// CLASS CONSTANTS ////////////////////////////


  /** Indicates that no processing of borders is to be done. */
  public static final int NO_BORDER_OP = 1;

  /** Indicates that border pixels are to be copied from source image. */
  public static final int COPY_BORDER_PIXELS = 2;

  /** Indicates that invalid coordinates are reflected into the image. */
  public static final int REFLECTED_INDEXING = 3;

  /** Indicates that invalid coordinates wrap around the image. */
  public static final int CIRCULAR_INDEXING = 4;


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Width of neighbourhood. */
  protected int width;

  /** Height of neighbourhood. */
  protected int height;

  /** Number of pixels in neighbourhood. */
  protected int size;

  /** Strategy to use when dealing with image borders. */
  protected int borderStrategy;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Constructs a NeighbourhoodOp with the given dimensions and
   * border processing strategy.
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   * @param strategy border processing strategy
   */

  public NeighbourhoodOp(int w, int h, int strategy) {
    if (w < 1 || h < 1 || w%2 == 0 || h%2 == 0)
      throw new ImagingOpException("invalid neighbourhood dimensions");
    width = w;
    height = h;
    size = w*h;
    borderStrategy = strategy;
  }


  /**
   * @return width of neighbourhood.
   */

  public int getWidth() {
    return width;
  }


  /**
   * @return height of neighbourhood.
   */

  public int getHeight() {
    return height;
  }


  /**
   * @return number of pixels in neighbourhood.
   */

  public int getNumPixels() {
    return size;
  }


  /**
   * @return strategy used to deal with image borders.
   */

  public int getBorderStrategy() {
    return borderStrategy;
  }


  /**
   * Reflects an invalid coordinate back into an image.
   * @param i coordinate, possibly out of bounds
   * @param n appropriate dimension of image (width or height)
   * @return coordinate, now valid if it was out of bounds
   */

  public static final int refIndex(int i, int n) {
    if (i < 0)
      return -i-1;
    else if (i >= n)
      return 2*n-i-1;
    else
      return i;
  }


  /**
   * Wraps an invalid coordinate around to the other side of an image.
   * @param i coordinate, possibly out of bounds.
   * @param n appropriate dimension of image (width or height)
   * @return coordinate, now valid if it was out of bounds
   */

  public static final int circIndex(int i, int n) {
    if (i < 0)
      return i+n;
    else if (i >= n)
      return i-n;
    else
      return i;
  }


  /////////////////////////// PROTECTED METHODS ////////////////////////////


  /**
   * Copies border pixels that cannot normally be processed by a
   * neighbourhood operation from one raster to another.
   * @param src source of pixel data
   * @param dest destination for pixel data
   */

  protected void copyBorders(Raster src, WritableRaster dest) {
    int w = src.getWidth();
    int h = src.getHeight();
    int m = width/2;
    int n = height/2;
    for (int x = 0; x < w; ++x) {    // copy top and bottom
      for (int y = 0; y < n; ++y)
        dest.setSample(x, y, 0, src.getSample(x, y, 0));
      for (int y = h-n; y < h; ++y)
        dest.setSample(x, y, 0, src.getSample(x, y, 0));
    }
    for (int y = 0; y < h; ++y) {    // copy left and right
      for (int x = 0; x < m; ++x)
        dest.setSample(x, y, 0, src.getSample(x, y, 0));
      for (int x = w-m; x < w; ++x)
        dest.setSample(x, y, 0, src.getSample(x, y, 0));
    }
  }


}
