/***************************************************************************

  LogOp.java

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


/**
 * Performs logarithmic mapping of grey levels in a BufferedImage.
 *
 * @author Nick Efford
 * @version 1.2 [1999/07/08]
 */

public class LogOp extends GreyMapOp {


  /**
   * Constructs a LogOp that will map 0 to 0 and 255 to 255.
   */

  public LogOp() {
    computeMapping();
  }

  /**
   * Constructs a LogOp that will map the specified limits onto 0 and 255.
   * @param low lower limit, mapping to 0
   * @param high upper limit, mapping to 255
   */

  public LogOp(int low, int high) {
    computeMapping(low, high);
  }


  /**
   * Computes a mapping that maps the specified limits onto 0 and 255.
   * @param low lower limit, mapping onto 0
   * @param high upper limit, mapping onto 255
   */

  public void computeMapping(int low, int high) {
    if (low < 0 || high > 255 || low >= high)
      throw new java.awt.image.ImagingOpException("invalid mapping limits");
    double a = Math.log(low + 1.0);
    double b = Math.log(high + 1.0);
    double scaling = 255.0 / (b - a);
    for (int i = 0; i < 256; ++i) {
      int value = (int) Math.round(scaling*(Math.log(i + 1.0) - a));
      setTableEntry(i, value);
    }
  }


}
