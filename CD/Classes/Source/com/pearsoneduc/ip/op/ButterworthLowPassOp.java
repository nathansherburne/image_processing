/***************************************************************************

  ButterworthLowPassOp.java

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
 * Performs Butterworth low pass filtering of a BufferedImage.
 *
 * @author Nick Efford
 * @version 1.1 [1999/08/10]
 */

public class ButterworthLowPassOp extends StandardGreyOp {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Order of filter. */
  protected int order;

  /** Radius of filter. */
  protected double radius;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Constructs a ButterworthLowPassOp with the specified filter radius.
   * The filter will have an order of 1.
   * @param r radius of filter
   */

  public ButterworthLowPassOp(double r) {
    this(1, r);
  }

  /**
   * Constructs a ButterworthLowPassOp with the specified order
   * and filter radius.
   * @param n order of filter
   * @param r radius of filter
   */

  public ButterworthLowPassOp(int n, double r) {
    if (n < 1 || r < 0.0)
      throw new ImagingOpException("invalid filter parameters");
    order = n;
    radius = r;
  }


  /**
   * @return order of filter.
   */

  public int getOrder() {
    return order;
  }


  /**
   * @return radius of filter.
   */

  public double getRadius() {
    return radius;
  }


  /**
   * Performs Butterworth low pass filtering on an image.
   * @param src source image
   * @param dest destination image, or null
   * @return processed image.
   */

  public BufferedImage filter(BufferedImage src, BufferedImage dest) {

    checkImage(src);
    if (dest == null)
      dest = createCompatibleDestImage(src, null);

    try {
      ImageFFT fft = new ImageFFT(src);
      fft.transform();
      fft.butterworthLowPassFilter(order, radius);
      fft.transform();
      fft.toImage(dest);
    }
    catch (FFTException e) {
      throw new ImagingOpException("cannot filter image");
    }

    return dest;

  }


}
