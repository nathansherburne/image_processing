/***************************************************************************

  BinaryOpenOp.java

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
 * Performs the morphological operation of opening on a binary image.
 * 'Binary image' is taken here to mean a greyscale image with just
 * two values, one of which is zero.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/30]
 */

public class BinaryOpenOp extends BinaryMorphologicalOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Structuring element used to perform the opening. */
  private BinaryStructElement structElement;


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Creates a BinaryOpenOp that uses the specified structuring element.
   * @param element structuring element
   */

  public BinaryOpenOp(BinaryStructElement element) {
    structElement = element;
  }


  /**
   * Performs morphological opening of a binary image.
   * @param src source image
   * @param dest destination image, or null
   * @return opened image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {
    BinaryErodeOp erodeOp = new BinaryErodeOp(structElement);
    BufferedImage erodedImage = erodeOp.filter(src, null);
    BinaryDilateOp dilateOp = new BinaryDilateOp(structElement);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);
    return dilateOp.filter(erodedImage, dest);
  }


}
