/***************************************************************************

  LaplacianKernel.java

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
 * A Kernel to compute the Laplacian of an image.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/29]
 */

public class LaplacianKernel extends StandardKernel {


  private static final float[] data = {  0.0f, -1.0f,  0.0f,
                                        -1.0f,  4.0f, -1.0f,
                                         0.0f, -1.0f,  0.0f  };


  public LaplacianKernel() {
    super(3, 3, data, 0);
  }


  /**
   * Creates a LaplacianKernel and prints it on standard output.
   */

  public static void main(String[] argv) {
    StandardKernel kernel = new LaplacianKernel();
    kernel.write(new java.io.OutputStreamWriter(System.out));
  }


}
