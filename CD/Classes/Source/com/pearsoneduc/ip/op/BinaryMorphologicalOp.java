/***************************************************************************

  BinaryMorphologicalOp.java

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
 * Adapts StandardGreyOp for binary morphological operations.
 * The method checkImage() is overridden to check that the source
 * image is either a true binary image or a greyscale image with
 * no more than two grey levels.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/30]
 */

public class BinaryMorphologicalOp extends StandardGreyOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Non-zero value assigned to pixels in the output image. */
  protected int nonZeroValue;


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Checks that the image to be processed is of the correct type -
   * either a true binary image or a two-level 8-bit greyscale image
   * with 0 as one of the values.
   * @param image image to be processed
   * @exception ImagingOpException if the image is not of the correct type.
   */

  public void checkImage(BufferedImage image) {
    if (image.getType() == BufferedImage.TYPE_BYTE_BINARY)
      nonZeroValue = 1;
    else if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
      nonZeroValue = 255;
      Raster raster = image.getRaster();
      int[] hist = new int[256];
      for (int y = 0; y < image.getHeight(); ++y)
        for (int x = 0; x < image.getWidth(); ++x)
          ++hist[raster.getSample(x, y, 0)];
      int n = 0;
      for (int i = 1; i < 256; ++i)
        if (hist[i] > 0) {
          ++n;
          if (n > 1)
            throw new ImagingOpException(
             "image must be binary, or grey with <= one non-zero value");
        }
    }
    else
      throw new ImagingOpException("invalid image type");
  }


}
