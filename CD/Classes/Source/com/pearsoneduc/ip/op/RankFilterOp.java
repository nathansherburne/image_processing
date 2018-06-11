/***************************************************************************

  RankFilterOp.java

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
import java.util.Arrays;



/**
 * Performs general rank filtering of a BufferedImage.
 *
 * @author Nick Efford
 * @version 1.0 [1999/07/23]
 */

public class RankFilterOp extends NeighbourhoodOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Rank of filter. */
  protected int rank;

  /** Array holding values from neighbourhood. */
  protected int[] neighbourhood;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Constructs a RankFilterOp with the specified rank.  The filter will
   * operate in a 3x3 neighbourhood and will do no special border
   * processing.
   * @param n rank of filter
   */

  public RankFilterOp(int n) {
    this(n, 3, 3, NO_BORDER_OP);
  }

  /**
   * Constructs a RankFilterOp with the specified rank and neighbourhood
   * dimensions.  The filter will do no special border processing.
   * @param n rank of filter
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   */

  public RankFilterOp(int n, int w, int h) {
    this(n, w, h, NO_BORDER_OP);
  }

  /**
   * Constructs a RankFilterOp with the specified rank, neighbourhood
   * dimensions and border processing strategy.
   * @param n rank of filter
   * @param w width of neighbourhood
   * @param h height of neighbourhood
   * @param strategy border processing strategy
   */

  public RankFilterOp(int n, int w, int h, int strategy) {
    super(w, h, strategy);
    neighbourhood = new int[size];
    if (n < 1 || n > size)
      throw new ImagingOpException("rank must lie between 1 and " + size);
    rank = n;
  }


  /**
   * @return rank of filter.
   */

  public int getRank() {
    return rank;
  }


  /**
   * Performs a rank filtering operation on an image.
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
    switch (borderStrategy) {

      case REFLECTED_INDEXING:
        for (int y = 0; y < h; ++y)
          for (int x = 0; x < w; ++x) {
            int i = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j, ++i)
                neighbourhood[i] =
                 srcRaster.getSample(refIndex(x+j, w), refIndex(y+k, h), 0);
            Arrays.sort(neighbourhood);
            destRaster.setSample(x, y, 0, neighbourhood[rank-1]);
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
            Arrays.sort(neighbourhood);
            destRaster.setSample(x, y, 0, neighbourhood[rank-1]);
          }
        break;

      case COPY_BORDER_PIXELS:
        copyBorders(srcRaster, destRaster);
        // fall through...

      default:
        // NO_BORDER_OP
        for (int y = n; y < h-n; ++y)
          for (int x = m; x < w-m; ++x) {
            int i = 0;
            for (int k = -n; k <= n; ++k)
              for (int j = -m; j <= m; ++j, ++i)
                neighbourhood[i] = srcRaster.getSample(x+j, y+k, 0);
            Arrays.sort(neighbourhood);
            destRaster.setSample(x, y, 0, neighbourhood[rank-1]);
          }
        break;

    }

    return dest;

  }


}
