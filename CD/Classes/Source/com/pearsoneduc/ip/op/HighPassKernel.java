/***************************************************************************

  HighPassKernel.java

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
 * A Kernel for high pass filtering.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/29]
 */

public class HighPassKernel extends StandardKernel {


  private static final float[] data = { -1.0f, -1.0f, -1.0f,
                                        -1.0f,  8.0f, -1.0f,
                                        -1.0f, -1.0f, -1.0f  };


  /**
   * Constructs a 3x3 kernel for high pass filtering.
   */

  public HighPassKernel() {
    super(3, 3, data, 0);
  }


  /**
   * Creates a HighPassKernel and prints it to standard output.
   */

  public static void main(String[] argv) {
    StandardKernel kernel = new HighPassKernel();
    kernel.write(new java.io.OutputStreamWriter(System.out));
  }


}
