/***************************************************************************

  GeomTools.java

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


package com.pearsoneduc.ip.util;


import java.awt.geom.*;
import java.awt.image.BufferedImage;


/**
 * Various tools to support geometric operations.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/13]
 */

public class GeomTools {


  /**
   * Determines the bounding box that an image would have
   * following an affine transformation.
   * @param image image to be transformed
   * @param transformation affine transformation
   * @return bounding box.
   */

  public static Rectangle2D getBoundingBox(
   BufferedImage image, AffineTransform transformation) {

    // Subject corners of the image to the transformation

    int xmax = image.getWidth()-1;
    int ymax = image.getHeight()-1;
    Point2D[] corners = new Point2D.Double[4];
    corners[0] = new Point2D.Double(0, 0);
    corners[1] = new Point2D.Double(xmax, 0);
    corners[2] = new Point2D.Double(xmax, ymax);
    corners[3] = new Point2D.Double(0, ymax);
    transformation.transform(corners, 0, corners, 0, 4);

    // Calculate bounding box of transformed corner points

    Rectangle2D boundingBox = new Rectangle2D.Double();
    for (int i = 0; i < 4; ++i)
      boundingBox.add(corners[i]);

    return boundingBox;

  }


}
