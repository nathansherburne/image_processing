/***************************************************************************

  StandardGreyOp.java

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



import java.awt.RenderingHints;
import java.awt.geom.*;
import java.awt.image.*;



/**
 * Implements a BufferedImageOp for standard operations on 8-bit
 * greyscale images.  Subclasses should override the
 * {@link #filter(BufferedImage, RenderingHints) filter()} method
 * to carry out processing.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/23]
 */

public class StandardGreyOp implements BufferedImageOp {


  /**
   * Performs an operation on an image.  Here, the operation is a
   * simple copy of source to destination.  Subclasses must override
   * this method to produce more interesting behaviour.
   * @param src source image
   * @param dest destination image, or null
   * @return destination image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {
    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);
    WritableRaster raster = dest.getRaster();
    src.copyData(raster);
    return dest;
  }


  /**
   * Creates a zeroed destination image with the same dimensions
   * and number of bands as the source image.
   * @param src source image
   * @param destModel ColorModel of the destination image
   *  (if null, ColorModel of the source image will be used)
   */

  public BufferedImage createCompatibleDestImage(BufferedImage src,
   ColorModel destModel) {
    if (destModel == null)
      destModel = src.getColorModel();
    int width = src.getWidth();
    int height = src.getHeight();
    BufferedImage image = new BufferedImage(destModel,
     destModel.createCompatibleWritableRaster(width, height),
     destModel.isAlphaPremultiplied(), null);
    return image;
  }


  /**
   * Determines bounding box of the destination image.  Here, it is
   * assumed that this is identical to that of the source image.
   * @param src source image
   * @return bounding box of the destination image.
   */

  public Rectangle2D getBounds2D(BufferedImage src) {
    return src.getRaster().getBounds();
  }


  /**
   * Given a point in the source image, determines the corresponding
   * point in the destination image.  Here, it is assumed that this
   * will be the same as the point in the source image.
   * @param srcPoint a point from the source image
   * @param destPoint a point in the destination image, or null
   * @return point in the destination image.
   */

  public Point2D getPoint2D(Point2D srcPoint, Point2D destPoint) {
    if (destPoint == null)
      destPoint = new Point2D.Float();
    destPoint.setLocation(srcPoint.getX(), srcPoint.getY());
    return destPoint;
  }


  /**
   * @return rendering hints for this operation.
   */

  public RenderingHints getRenderingHints() {
    return null;
  }


  /**
   * Tests that source image is suitable for processing.
   * @param src source image
   * @exception ImagingOpException if the source image is not an
   *  8-bit greyscale image.
   */

  public void checkImage(BufferedImage src) {
    if (src.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new ImagingOpException("operation requires an 8-bit grey image");
  }


}
