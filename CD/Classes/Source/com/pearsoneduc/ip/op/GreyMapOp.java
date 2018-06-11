/***************************************************************************

  GreyMapOp.java

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
 * Performs arbitrary mapping of grey levels in a BufferedImage,
 * using a look-up table.  The image must be an 8-bit greyscale image.
 *
 * <p>This is an abstract class; concrete subclasses must implement
 * the method {@link #computeMapping(int, int) computeMapping()},
 * which generates data for the lookup table.</p>
 *
 * @author Nick Efford
 * @version 2.1 [1999/07/23]
 */

public abstract class GreyMapOp extends StandardGreyOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Lookup table data. */
  protected byte[] table = new byte[256];


  //////////////////////////////// METHODS /////////////////////////////////


  /**
   * Retrieves a lookup table entry.
   * @param i index into the lookup table
   * @return value stored at the specified index.
   */

  public int getTableEntry(int i) {
    if (table[i] < 0)
      return 256 + (int) table[i];
    else
      return (int) table[i];
  }


  /**
   * Modifies a lookup table entry.
   * @param i index into the lookup table
   * @param value value to be stored at the specified index
   *  (forced to be in the range 0-255 if necessary)
   */

  protected void setTableEntry(int i, int value) {
    if (value < 0)
      table[i] = (byte) 0;
    else if (value > 255)
      table[i] = (byte) 255;
    else
      table[i] = (byte) value;
  }


  /**
   * Computes a mapping of grey level with upper and lower limits.
   * @param low lower limit, mapping onto 0
   * @param high upper limit, mapping onto 255
   */

  public abstract void computeMapping(int low, int high);

  /**
   * Computes a mapping of grey level that maps 0 onto 0 and 255 onto 255.
   */

  public void computeMapping() {
    computeMapping(0, 255);
  }


  /**
   * Performs mapping of grey levels in an image.
   * @param src source image
   * @param dest destination image, or null
   * @return the mapped image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {
    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);
    LookupOp operation = new LookupOp(new ByteLookupTable(0, table), null);
    operation.filter(src, dest);
    return dest;
  }


}
