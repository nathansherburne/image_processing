/***************************************************************************

  InvertOp.java

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
 * Inverts grey levels in a BufferedImage.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/08]
 */

public class InvertOp extends GreyMapOp {

  public InvertOp() {
    computeMapping();
  }

  public InvertOp(int low, int high) {
    computeMapping(low, high);
  }

  public void computeMapping(int low, int high) {
    if (low < 0 || high > 255 || low >= high)
      throw new java.awt.image.ImagingOpException("invalid mapping limits");
    float scaling = -255.0f / (high - low);
    for (int i = 0; i < 256; ++i)
      setTableEntry(i, Math.round(scaling*(i - high)));
  }

}
