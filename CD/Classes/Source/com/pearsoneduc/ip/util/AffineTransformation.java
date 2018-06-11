/***************************************************************************

  AffineTransformation.java

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


import java.awt.geom.*;


/**
 * Extends AffineTransform, adding a constructor that computes the
 * transformation mapping one set of three points onto another set of
 * three points.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/13]
 * @see java.awt.geom.AffineTransform
 */

public class AffineTransformation extends AffineTransform {

  /**
   * @see AffineTransform
   */

  public AffineTransformation() {
    super();
  }

  /**
   * @see AffineTransform
   */

  public AffineTransformation(AffineTransform t) {
    super(t);
  }

  /**
   * @see AffineTransform
   */

  public AffineTransformation(float[] matrix) {
    super(matrix);
  }

  /**
   * @see AffineTransform
   */

  public AffineTransformation(double[] matrix) {
    super(matrix);
  }

  /**
   * @see AffineTransform
   */

  public AffineTransformation(float a0, float b0, float a1, float b1,
   float a2, float b2) {
    super(a0, b0, a1, b1, a2, b2);
  }

  /**
   * @see AffineTransform
   */

  public AffineTransformation(double a0, double b0, double a1, double b1,
   double a2, double b2) {
    super(a0, b0, a1, b1, a2, b2);
  }

  /**
   * Creates an AffineTransform that maps one set of three points
   * onto another set of three points.  No checking is done for
   * degenerate cases.
   * @param in array of input points (first three points are used)
   * @param out array of corresponding output points (first three
   *  points are used)
   */

  public AffineTransformation(Point2D[] in, Point2D[] out) {
    super();
    double x0 = in[0].getX(), y0 = in[0].getY();
    double x1 = in[1].getX(), y1 = in[1].getY();
    double x2 = in[2].getX(), y2 = in[2].getY();
    double u0 = out[0].getX(), v0 = out[0].getY();
    double u1 = out[1].getX(), v1 = out[1].getY();
    double u2 = out[2].getX(), v2 = out[2].getY();
    double det = x0*(y1-y2) - y0*(x1-x2) + (x1*y1-x2*y1);
    double a0 = (u0*(y1-y2) + u1*(y2-y0) + u2*(y0-y1)) / det;
    double b0 = (v0*(y1-y2) + v1*(y2-y0) + v2*(y0-y1)) / det;
    double a1 = (u0*(x2-x1) + u1*(x0-x2) + u2*(x1-x0)) / det;
    double b1 = (v0*(x2-x1) + v1*(x0-x2) + v2*(x1-x0)) / det;
    double a2 = (u0*(x1*y2-x2*y1) + u1*(x2*y0-x0*y2) + u2*(x0*y1-x1*y0)) / det;
    double b2 = (v0*(x1*y2-x2*y1) + v1*(x2*y0-x0*y2) + v2*(x0*y1-x1*y0)) / det;
    setTransform(a0, b0, a1, b1, a2, b2);
  }

}
