/***************************************************************************

  HighBoostKernel.java

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
 * A Kernel for high frequency emphasis filtering.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/29]
 */

public class HighBoostKernel extends StandardKernel {


  /**
   * Creates an array of kernel coefficients consisting of the
   * specified central coefficient surrounded by -1.
   * @param coeff central coefficient
   * @return array of coefficients.
   */

  public static float[] createKernelData(int coeff) {
    float[] data = new float[9];
    for (int i = 0; i < 9; ++i)
      data[i] = -1.0f;
    data[4] = (float) coeff;
    return data;
  }


  /**
   * Constructs a HighBoostKernel with the default central
   * coefficient of 9, surrounded by -1.
   */

  public HighBoostKernel() {
    this(9);
  }

  /**
   * Constructs a HighBoostKernel with the specified central coefficient.
   * @param coeff central coefficient of kernel
   */

  public HighBoostKernel(int coeff) {
    super(3, 3, createKernelData(coeff), 0);
  }


  /**
   * Creates a HighBoostKernel and prints it on standard output.
   */

  public static void main(String[] argv) {
    int centralCoefficient = 9;
    if (argv.length > 0)
      centralCoefficient = Integer.parseInt(argv[0]);
    StandardKernel kernel = new HighBoostKernel(centralCoefficient);
    kernel.write(new java.io.OutputStreamWriter(System.out));
  }


}
