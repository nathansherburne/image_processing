/***************************************************************************

  GreyDilateOp.java

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



import java.awt.Point;
import java.awt.image.*;



/**
 * Performs the morphological operation of dilation on a greyscale image.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/31]
 */

public class GreyDilateOp extends StandardGreyOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Structuring element used to perform the erosion. */
  private GreyStructElement structElement;

  /** Flag to indicate whether output values should be rescaled. */
  private boolean rescaling;


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Creates a GreyDilateOp that uses the specified structuring element.
   * Output values will be truncated, rather than rescaled, to a
   * 0-255 range.
   * @param element structuring element
   */

  public GreyDilateOp(GreyStructElement element) {
    this(element, false);
  }

  /**
   * Creates a GreyDilateOp with the specified structuring element.
   * Output values will be truncated or rescaled, according to the
   * value of the boolean flag.
   * @param element structuring element
   * @param rescale flag to indicate whether rescaling is required
   */

  public GreyDilateOp(GreyStructElement element, boolean rescale) {
    structElement = element;
    rescaling = rescale;
  }


  /**
   * Performs morphological dilation of a greyscale image.
   * @param src source image
   * @param dest destination image, or null
   * @return dilated image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);

    int w = src.getWidth();
    int h = src.getHeight();
    Raster srcRaster = src.getRaster();
    WritableRaster destRaster = dest.getRaster();

    // Determine range of pixels for which operation can be performed

    Point origin = structElement.getOrigin(null);
    int xmin = Math.max(origin.x, 0);
    int ymin = Math.max(origin.y, 0);
    int xmax = origin.x + w - structElement.getWidth();
    int ymax = origin.y + h - structElement.getHeight();
    xmax = Math.min(w-1, xmax);
    ymax = Math.min(h-1, ymax);

    // Perform operation

    if (rescaling) {

      // Store output values and determine range

      int[][] result = new int[h][w];
      int minimum = Integer.MAX_VALUE;
      int maximum = Integer.MIN_VALUE;
      for (int y = ymin; y <= ymax; ++y)
        for (int x = xmin; x <= xmax; ++x) {
          result[y][x] = structElement.above(srcRaster, x, y);
          if (result[y][x] < minimum)
            minimum = result[y][x];
          if (result[y][x] > maximum)
            maximum = result[y][x];
        }

      // Write rescaled values to destination image

      double scale = 255.0/(maximum-minimum);
      for (int y = ymin; y <= ymax; ++y)
        for (int x = xmin; x <= xmax; ++x)
          destRaster.setSample(x, y, 0,
           Math.round(scale*(result[y][x]-minimum)));

    }
    else {
      int distance;
      for (int y = ymin; y <= ymax; ++y)
        for (int x = xmin; x <= xmax; ++x) {
          distance = structElement.above(srcRaster, x, y);
          destRaster.setSample(x, y, 0, Math.max(0, Math.min(255, distance)));
        }
    }

    return dest;

  }


}
