/***************************************************************************

  Limits.java

  Prints on the console the minimum and maximum values that can be
  represented by the various primitive data types.

 
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


public class Limits {
  public static void main(String[] argv) {
    java.io.PrintStream s = System.out;
    s.println("Min byte value   = " + Byte.MIN_VALUE);
    s.println("Max byte value   = " + Byte.MAX_VALUE);
    s.println("Min short value  = " + Short.MIN_VALUE);
    s.println("Max short value  = " + Short.MAX_VALUE);
    s.println("Min int value    = " + Integer.MIN_VALUE);
    s.println("Max int value    = " + Integer.MAX_VALUE);
    s.println("Min float value  = " + Float.MIN_VALUE);
    s.println("Max float value  = " + Float.MAX_VALUE);
    s.println("Min double value = " + Double.MIN_VALUE);
    s.println("Max double value = " + Double.MAX_VALUE);
  }
}

