/***************************************************************************

  GreyOpenOp.java

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
 * Performs the morphological operation of opening on a greyscale image.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/31]
 */

public class GreyOpenOp extends StandardGreyOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Structuring element used to perform the opening. */
  private GreyStructElement structElement;

  /** Flag to indicate whether output values will be rescaled. */
  private boolean rescaling;


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Creates a GreyOpenOp that uses the specified structuring element.
   * Output values will be truncated to a 0-255 range.
   * @param element structuring element
   */

  public GreyOpenOp(GreyStructElement element) {
    this(element, false);
  }

  /**
   * Creates a GreyOpenOp that uses the specified structuring element.
   * Output values will be truncated or rescaled, according to the value
   * of the boolean parameter.
   * @param element structuring element
   * @param rescale flag to indicated whether values will be rescaled
   */

  public GreyOpenOp(GreyStructElement element, boolean rescale) {
    structElement = element;
    rescaling = rescale;
  }


  /**
   * Performs morphological opening of a greyscale image.
   * @param src source image
   * @param dest destination image, or null
   * @return opened image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {
    GreyErodeOp erodeOp = new GreyErodeOp(structElement, rescaling);
    BufferedImage erodedImage = erodeOp.filter(src, null);
    GreyDilateOp dilateOp = new GreyDilateOp(structElement, rescaling);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);
    return dilateOp.filter(erodedImage, dest);
  }


}
