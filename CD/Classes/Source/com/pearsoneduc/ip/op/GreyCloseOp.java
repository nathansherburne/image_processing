/***************************************************************************

  GreyCloseOp.java

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
 * Performs the morphological operation of closing on a greyscale image.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/31]
 */

public class GreyCloseOp extends StandardGreyOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Structuring element used to perform the closing. */
  private GreyStructElement structElement;

  /** Flag to indicate whether output values are rescaled. */
  private boolean rescaling;


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Creates a GreyCloseOp that uses the specified structuring element.
   * Output values will be truncated to a 0-255 range.
   * @param element structuring element
   */

  public GreyCloseOp(GreyStructElement element) {
    this(element, false);
  }

  /**
   * Creates a GreyCloseOp that uses the specified structuring element.
   * Output values will be truncated or rescaled, according to the
   * value of the boolean parameter.
   * @param element structuring element to be used
   * @param rescale flag to indicate whether values will be rescaled
   */

  public GreyCloseOp(GreyStructElement element, boolean rescale) {
    structElement = element;
    rescaling = rescale;
  }


  /**
   * Performs morphological closing of a greyscale image.
   * @param src source image
   * @param dest destination image, or null
   * @return closed image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {
    GreyDilateOp dilateOp = new GreyDilateOp(structElement, rescaling);
    BufferedImage dilatedImage = dilateOp.filter(src, null);
    GreyErodeOp erodeOp = new GreyErodeOp(structElement, rescaling);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);
    return erodeOp.filter(dilatedImage, dest);
  }


}
