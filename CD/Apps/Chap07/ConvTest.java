/***************************************************************************

  ConvTest.java

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


import java.awt.image.*;
import java.io.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.util.StringTools;


public class ConvTest {


  private static final float[] coeff = { -1.0f, 0.0f, 1.0f,
                                         -1.0f, 0.0f, 1.0f,
                                         -1.0f, 0.0f, 1.0f  };

  private Raster source;
  private Raster result;
  private StreamTokenizer input =
   new StreamTokenizer(new InputStreamReader(System.in));;
  private int[][] neighbourhood = new int[3][3];


  public ConvTest(String imageFile)
   throws IOException, ImageDecoderException {
    ImageDecoder input = ImageFile.createImageDecoder(imageFile);
    source = input.decodeAsBufferedImage().getRaster();
    Kernel kernel = new Kernel(3, 3, coeff);
    ConvolveOp op = new ConvolveOp(kernel);
    result = op.filter(source, null);
  }


  public void testPixels() {
    int xmax = source.getWidth()-1;
    int ymax = source.getHeight()-1;
    System.out.println(
     "Enter x and y coordinates when prompted, separated by whitespace.\n" +
     "x can range from 1 to " + xmax +
     ", y can range from 1 to " + ymax + ".\n" +
     "Non-numeric or invalid input will terminate the program.\n");
    try {
      while (true) {
        System.out.print("x y: ");
        int x = getInteger(1, xmax);
        int y = getInteger(1, ymax);
        moveNeighbourhood(x, y);
        displayNeighbourhood();
        System.out.println("ConvolveOp gives " + result.getSample(x, y, 0));
        System.out.println("Convolution gives " + convolve());
        System.out.println();
      }
    }
    catch (Exception e) {}
  }


  public int getInteger(int low, int high) throws IOException {
    input.nextToken();
    if (input.ttype == StreamTokenizer.TT_NUMBER) {
      int value = (int) input.nval;
      if (value < low || value > high)
        throw new IOException();
      return value;
    }
    else
      throw new IOException();
  }


  public void moveNeighbourhood(int x, int y) {
    for (int k = -1; k <= 1; ++k)
      for (int j = -1; j <= 1; ++j)
        neighbourhood[k+1][j+1] = source.getSample(x+j, y+k, 0);
  }


  public void displayNeighbourhood() {
    System.out.println();
    for (int k = 0; k < 3; ++k) {
      for (int j = 0; j < 3; ++j)
        System.out.print(StringTools.rightJustify(neighbourhood[k][j], 4));
        System.out.println();
      }
    System.out.println();
  }


  public int convolve() {
    float sum = 0.0f;
    int i = 0;
    for (int k = 0; k < 3; ++k)
      for (int j = 0; j < 3; ++j, ++i)
        sum += coeff[i]*neighbourhood[2-k][2-j];
    return Math.round(sum);
  }


  public void createProfiles(String filename) throws IOException {
    PrintWriter out =
     new PrintWriter(
      new BufferedWriter(
       new FileWriter(filename)));
    int y = source.getHeight()/2;
    for (int x = 1; x < source.getWidth()-1; ++x) {
      moveNeighbourhood(x, y);
      out.println(
       StringTools.rightJustify(x, 5) +
       StringTools.rightJustify(source.getSample(x, y, 0), 5) +
       StringTools.rightJustify(result.getSample(x, y, 0), 5) +
       StringTools.rightJustify(convolve(), 5));
    }
    out.flush();
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        ConvTest tester = new ConvTest(argv[0]);
        if (argv.length > 1)
          tester.createProfiles(argv[1]);
        else
          tester.testPixels();
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java ConvTest <imagefile> [<profiles>]");
      System.exit(1);
    }
  }


}
