/***************************************************************************

  MaximumFilterOp.java

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
 * Performs maximum filtering of a BufferedImage.
 *
 * <p>Note: this can also be done with a RankFilterOp, but a
 * faster algorithm is used here.</p>
 *
 * @author Nick Efford
 * @version 1.0 [1999/07/23]
 */

public class MaximumFilterOp extends RankFilterOp {


  /**
   * Constructs a MaximumFilterOp for a 3x3 neighbourhood.
   * No special border processing will be done.
   */

  public MaximumFilterOp() {
    super(9, 3, 3);
  }

  /**
   * Constructs a MaximumFilterOp for the given neighbourhood
   * dimensions.  No special border processing will be done.
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   */

  public MaximumFilterOp(int w, int h) {
    super(w*h, w, h);
  }

  /**
   * Constructs a MaximumFilterOp for the given neighbourhood
   * dimensions and border processing strategy.
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   * @param strategy border processing strategy
   */

  public MaximumFilterOp(int w, int h, int strategy) {
    super(w*h, w, h, strategy);
  }


  /**
   * Performs maximum filtering of an image.
   * @param src source image
   * @param dest destination image, or null
   * @return processed image.
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
    int maximum, value;
    switch (borderStrategy) {

      case REFLECTED_INDEXING:
        for (int y = 0; y < h; ++y)
          for (int x = 0; x < w; ++x) {
            maximum = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j) {
                value =
                 srcRaster.getSample(refIndex(x+j, w), refIndex(y+k, h), 0);
                if (value > maximum)
                  maximum = value;
              }
            destRaster.setSample(x, y, 0, maximum);
          }
        break;

      case CIRCULAR_INDEXING:
        for (int y = 0; y < h; ++y)
          for (int x = 0; x < w; ++x) {
            maximum = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j) {
                value =
                 srcRaster.getSample(circIndex(x+j, w), circIndex(y+k, h), 0);
                if (value > maximum)
                  maximum = value;
              }
            destRaster.setSample(x, y, 0, maximum);
          }
        break;

      case COPY_BORDER_PIXELS:
        copyBorders(srcRaster, destRaster);
        // fall through...

      default:
        // NO_BORDER_OP
        for (int y = n; y < h-n; ++y)
          for (int x = m; x < w-m; ++x) {
            maximum = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j) {
                value = srcRaster.getSample(x+j, y+k, 0);
                if (value > maximum)
                  maximum = value;
              }
            destRaster.setSample(x, y, 0, maximum);
          }
        break;

    }

    return dest;

  }


}
