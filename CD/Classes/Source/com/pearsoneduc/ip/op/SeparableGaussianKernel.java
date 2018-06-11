/***************************************************************************

  SeparableGaussianKernel.java

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
 * A Kernel for Gaussian blurring.  The kernel is one-dimensional,
 * suitable for separable convolution with an image.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/29]
 */

public class SeparableGaussianKernel extends StandardKernel {


  /**
   * Creates a separable Gaussian kernel with a default
   * standard deviation of 1.0.
   */

  public SeparableGaussianKernel() {
    this(1.0f);
  }

  /**
   * Creates a separable Gaussian kernel with the
   * specified standard deviation.
   * @param sigma standard deviation
   */

  public SeparableGaussianKernel(float sigma) {
    super(getSize(sigma), 1, createKernelData(sigma));
  }


  /**
   * Computes kernel size for a given standard deviation.
   * @param sigma standard deviation
   * @return kernel size, in pixels.
   */

  public static int getSize(float sigma) {
    int radius = (int) Math.ceil(4.0f*sigma);
    return 2*radius+1;
  }


  /**
   * Creates an array of samples from a 1D Gaussian function
   * with the given standard deviation.
   * @param sigma standard deviation
   * @return array of samples.
   */

  public static float[] createKernelData(float sigma) {
 
    int n = (int) Math.ceil(4.0f*sigma);
    int size = 2*n+1;
    float[] data = new float[size];

    double s = 2.0*sigma*sigma;
    float norm = 0.0f;
    int i = 0;
    for (int x = -n; x <= n; ++x, ++i) {
      data[i] = (float) Math.exp(-x*x/s);
      norm += data[i];
    }

    for (i = 0; i < size; ++i) 
      data[i] /= norm;

    return data;

  }


  /**
   * Creates a SeparableGaussianKernel and writes its coefficients
   * to standard output.
   */

  public static void main(String[] argv) {
    float sigma = 1.0f;
    if (argv.length > 0)
      sigma = Float.valueOf(argv[0]).floatValue();
    StandardKernel kernel = new SeparableGaussianKernel(sigma);
    kernel.write(new java.io.OutputStreamWriter(System.out));
  }


}
