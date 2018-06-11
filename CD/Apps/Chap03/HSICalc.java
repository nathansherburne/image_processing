/***************************************************************************

  HSICalc.java

  Given a triplet of RGB values, each in the range 0-255, passed in
  via the command line, this program computes and prints the values of
  hue, saturation and intensity that correspond to that triplet of
  values.

  Example of usage:

    java HSICalc 34 192 127

  
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


import java.awt.Color; 
import java.text.DecimalFormat;


public class HSICalc {

  public static void main(String[] argv) {

    if (argv.length > 2) {

      int[] rgb = new int[3];
      for (int i = 0; i < 3; ++i)
        rgb[i] = Integer.parseInt(argv[i]);

      float[] values = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
      String[] labels = { "H=", "S=", "I=" };
      DecimalFormat floatValue = new DecimalFormat("0.000");
      for (int i = 0; i < 3; ++i)
        System.out.println(labels[i] + floatValue.format(values[i]));

    }
    else {
      System.err.println("usage: java HSICalc <r> <g> <b>");
      System.exit(1);
    }

  }

}
