/***************************************************************************

  Complex.java

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


/**
 * A simple complex number class.
 *
 * @author Nick Efford
 * @version 1.1 [1999/08/02]
 */

public class Complex {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Real part of number. */
  public float re;

  /** Imaginary part of number. */
  public float im;


  //////////////////////////////// METHODS /////////////////////////////////


  public Complex() {}


  public Complex(float real, float imaginary) {
    re = real;
    im = imaginary;
  }


  public float getMagnitude() {
    return (float) Math.sqrt(re*re + im*im);
  }


  public float getPhase() {
    return (float) Math.atan2(im, re);
  }


  public void setPolar(double r, double theta) {
    re = (float)(r*Math.cos(theta));
    im = (float)(r*Math.sin(theta));
  }


  public String toString() {
    return new String(re + " + " + im + "i");
  }


  public void swapWith(Complex value) {
    float temp = re;
    re = value.re;
    value.re = temp;
    temp = im;
    im = value.im;
    value.im = temp;
  }


}
