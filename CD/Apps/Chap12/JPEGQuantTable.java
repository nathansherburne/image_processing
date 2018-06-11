/***************************************************************************

  JPEGQuantTable.java

  This program generates and prints the JPEG quantisation table for
  the specified quality factor.


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


import java.awt.image.BufferedImage;
import com.sun.image.codec.jpeg.*;
import com.pearsoneduc.ip.util.StringTools;



public class JPEGQuantTable {

  // row indices of zigzag path through quantisation table

  public static final int[] row = {
    0, 0, 1, 2, 1, 0, 0, 1, 2, 3, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4,
    5, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4,
    3, 2, 1, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4, 3, 4, 5, 6, 7, 7, 6,
    5, 6, 7, 7
  };

  // column indices of zigzag path through quantisation table

  public static final int[] column = {
    0, 1, 0, 0, 1, 2, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 4, 3, 2, 1,
    0, 0, 1, 2, 3, 4, 5, 6, 7, 6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4,
    5, 6, 7, 7, 6, 5, 4, 3, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4, 5, 6,
    7, 7, 6, 7
  };

  // Returns a set of JPEG parameters

  public static JPEGEncodeParam getParameters() {
    BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_GRAY);
    return JPEGCodec.getDefaultJPEGEncodeParam(img);
  }

  // Prints zigzag-ordered quantisation values as a 2D matrix

  public static void printAsMatrix(int[] data) {
    int[][] table = new int[8][8];
    for (int i = 0; i < data.length; ++i)
      table[row[i]][column[i]] = data[i];
    System.out.println();
    for (int v = 0; v < 8; ++v) {
      for (int u = 0; u < 8; ++u)
        System.out.print(StringTools.rightJustify(table[v][u], 4));
      System.out.println();
    }
    System.out.println();
  }

  public static void main(String[] argv) {
    if (argv.length > 0) {
      float quality = Math.max(0.0f, Math.min(1.0f,
        Float.valueOf(argv[0]).floatValue()));
      JPEGEncodeParam parameters = getParameters();
      parameters.setQuality(quality, true);
      printAsMatrix(parameters.getQTable(0).getTable());
      System.exit(0);
    }
    else {
      System.err.println("usage: java QuantTable <quality>");
      System.exit(1);
    }
  }

}
