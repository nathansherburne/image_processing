/***************************************************************************

  BinaryDilateOp.java

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
 * Performs the morphological operation of dilation on a binary image.
 * 'Binary image' is taken here to mean a greyscale image with just
 * two values, one of which is zero.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/30]
 */

public class BinaryDilateOp extends BinaryMorphologicalOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Structuring element used to perform the dilation. */
  private BinaryStructElement structElement;


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Creates a BinaryDilateOp that uses the specified structuring element.
   * @param element structuring element
   */

  public BinaryDilateOp(BinaryStructElement element) {
    structElement = element;
  }


  /**
   * Performs morphological dilation of a binary image.
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

    // Intersect structuring element with source image

    for (int y = ymin; y <= ymax; ++y)
      for (int x = xmin; x <= xmax; ++x)
        if (structElement.hits(srcRaster, x, y))
          destRaster.setSample(x, y, 0, nonZeroValue);

    return dest;

  }


}
