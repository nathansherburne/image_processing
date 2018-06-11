/***************************************************************************

  RegionLabelOp.java

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
 * Performs connected component labelling of an image.  Each 4-connected
 * or 8-connected region of pixels is given its own label.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/26]
 */

public class RegionLabelOp extends StandardGreyOp {


  //////////////////////////// CLASS CONSTANTS /////////////////////////////


  /** Vectors to 4-connected neighbours of a pixel. */
  private static final Point[] FOUR_CONNECTED = {
    new Point( 1,  0),
    new Point( 0, -1),
    new Point(-1,  0),
    new Point( 0,  1)
  };

  /** Vectors to 8-connected neighbours of a pixel. */
  private static final Point[] EIGHT_CONNECTED = {
    new Point( 1,  0),
    new Point( 1, -1),
    new Point( 0, -1),
    new Point(-1, -1),
    new Point(-1,  0),
    new Point(-1,  1),
    new Point( 0,  1),
    new Point( 1,  1)
  };

  /** Maximum possible number of regions. */
  private static final int MAX_REGIONS = 255;


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Pixel connectivity - either 4 or 8. */
  private int connectivity;

  /** Array holding relative position vectors of a pixel's neighbours. */
  private Point[] delta;

  /** Width of image being labelled. */
  private int width;

  /** Height of image being labelled. */
  private int height;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Creates a RegionLabelOp with the specified connectivity.
   * @param c pixel connectivity (either 4 or 8)
   * @exception ImagingOpException if connectivity is not 4 or 8.
   */

  public RegionLabelOp(int c) {
    if (c == 4) {
      connectivity = 4;
      delta = FOUR_CONNECTED;
    }
    else if (c == 8) {
      connectivity = 8;
      delta = EIGHT_CONNECTED;
    }
    else
      throw new ImagingOpException("connectivity must be 4 or 8");
  }


  /**
   * Performs labelling of connected components in an image.
   * @param src source image
   * @param dest destination image, or null
   * @return labelled image.
   * @exception ImagingOpException if source image is not an
   *  8-bit greyscale image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);

    width = src.getWidth();
    height = src.getHeight();
    WritableRaster in = src.copyData(null);
    WritableRaster out = dest.getRaster();

    int n = 1;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x)
        if (in.getSample(x, y, 0) > 0) {
          label(in, out, x, y, n);
          ++n;
          if (n > MAX_REGIONS)
            return dest;
        }

    return dest;

  }


  //////////////////////////// PRIVATE METHODS ////////////////////////////


  /**
   * Recursively labels the 4- or 8-neighbours of a pixel.
   * @param in raster containing data to be labelled
   * @param out raster containing labelled regions
   * @param x x coordinate of a pixel
   * @param y y coordinate of a pixel
   * @param n current region number
   */

  private void label(WritableRaster in, WritableRaster out,
   int x, int y, int n) {
    in.setSample(x, y, 0, 0);
    out.setSample(x, y, 0, n);
    int j, k;
    for (int i = 0; i < connectivity; ++i) {
      j = x + delta[i].x;
      k = y + delta[i].y;
      if (inImage(j, k) && in.getSample(j, k, 0) > 0)
        label(in, out, j, k, n);
    }
  }


  /**
   * Tests whether a pixel is in the image being labelled.
   * @param x x coordinate of pixel
   * @param y y coordinate of pixel
   * @return true if pixel is inside image, false otherwise.
   */

  private final boolean inImage(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height;
  }


}
