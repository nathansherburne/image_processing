/***************************************************************************

  HorizontalPrewittKernel.java

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
 * A Kernel to compute gradient in the x direction.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/29]
 */

public class HorizontalPrewittKernel extends StandardKernel {


  private static final float[] data = { -1.0f,  0.0f,  1.0f,
                                        -1.0f,  0.0f,  1.0f,
                                        -1.0f,  0.0f,  1.0f  };


  public HorizontalPrewittKernel() {
    super(3, 3, data, 0);
  }


  /**
   * Creates a HorizontalPrewittKernel and prints it on standard output.
   */

  public static void main(String[] argv) {
    StandardKernel kernel = new HorizontalPrewittKernel();
    kernel.write(new java.io.OutputStreamWriter(System.out));
  }


}
