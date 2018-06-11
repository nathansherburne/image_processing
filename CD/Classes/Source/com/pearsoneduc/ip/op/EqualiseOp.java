/***************************************************************************

  EqualiseOp.java

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
 * Performs histogram equalisation on a BufferedImage.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/08]
 */

public class EqualiseOp extends GreyMapOp {

  /**
   * Constructs an EqualiseOp object using histogram data.
   * @param hist Histogram of the image to be equalised
   * @exception HistogramException if the histogram is from a colour image.
   */

  public EqualiseOp(Histogram hist) throws HistogramException {
    float scale = 255.0f / hist.getNumSamples();
    for (int i = 0; i < 256; ++i)
      table[i] = (byte) Math.round(scale*hist.getCumulativeFrequency(i));
  }

  public void computeMapping(int low, int high) {
    // Does nothing - limits are meaningless in histogram equalisation
  }

}
