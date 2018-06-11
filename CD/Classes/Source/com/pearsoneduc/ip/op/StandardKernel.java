/***************************************************************************

  StandardKernel.java

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



import java.awt.image.Kernel;
import java.io.*;
import java.text.*;
import java.util.Vector;
import com.pearsoneduc.ip.util.StringTools;



/**
 * Extends the Kernel class, adding I/O capabilities
 * and a {@link #toString()} method.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/29]
 */

public class StandardKernel extends Kernel {


  private int numDigits;


  /**
   * Creates a StandardKernel by reading data.
   * @param reader source of kernel data
   * @return the kernel.
   */

  public static StandardKernel createKernel(Reader reader)
   throws IOException {
    return createKernel(reader, false);
  }

  /**
   * Creates a StandardKernel by reading data.
   * @param reader source of kernel data
   * @param normalise flag to indicate whether coefficients should be
   *  normalised on input
   * @return the kernel.
   */

  public static StandardKernel createKernel(Reader reader, boolean normalise)
   throws IOException {
    StreamTokenizer parser = new StreamTokenizer(new BufferedReader(reader));
    parser.commentChar('#');
    int w = getInteger(parser);
    int h = getInteger(parser);
    if (w < 1 || h < 1)
      throw new IOException("invalid kernel dimensions");
    int n = getInteger(parser);
    int size = w*h;
    float[] data = new float[size];
    for (int i = 0; i < size; ++i)
      data[i] = getFloat(parser);
    if (normalise)
      normaliseCoefficients(data);
    return new StandardKernel(w, h, data, n);
  }


  /**
   * @return an integer extracted from a token stream.
   */

  private static int getInteger(StreamTokenizer input) throws IOException {
    input.nextToken();
    if (input.ttype == StreamTokenizer.TT_NUMBER)
      return (int) input.nval;
    else if (input.ttype == StreamTokenizer.TT_EOF)
      throw new EOFException("kernel appears to be truncated");
    else
      throw new IOException("invalid kernel data");
  }


  /**
   * @return a float value extracted from a token stream.
   */

  private static float getFloat(StreamTokenizer input) throws IOException {
    input.nextToken();
    if (input.ttype == StreamTokenizer.TT_NUMBER)
      return (float) input.nval;
    else if (input.ttype == StreamTokenizer.TT_EOF)
      throw new EOFException("kernel appears to be truncated");
    else
      throw new IOException("invalid kernel data");
  }


  /**
   * Normalises an array of coefficients if their sum exceeds 1.
   * @param coeff array of coefficients
   */

  private static void normaliseCoefficients(float[] coeff) {
    float sum = 0.0f;
    for (int i = 0; i < coeff.length; ++i)
      sum += coeff[i];
    if (sum > 1.001f)
      for (int i = 0; i < coeff.length; ++i)
        coeff[i] /= sum;
  }


  /**
   * Constructs a StandardKernel with the specified
   * dimensions and coefficients.
   * @param w width of kernel
   * @param h height of kernel
   * @param data array of coefficients, in row-major order
   */

  public StandardKernel(int w, int h, float[] data) {
    this(w, h, data, 4);
  }

  /**
   * Constructs a StandardKernel with the specified dimensions,
   * coefficients and coefficient formatting.
   * @param w width of kernel
   * @param h height of kernel
   * @param data array of coefficients, in row-major order
   * @param n number of fraction digits used when writing coefficients
   */

  public StandardKernel(int w, int h, float[] data, int n) {
    super(w, h, data);
    setFractionDigits(n);
  }


  public String toString() {
    return new String(
     getClass().getName() + ": " + getWidth() + "x" + getHeight());
  }


  /**
   * Sets number of digits used to format fractional part of a coefficient.
   */

  public void setFractionDigits(int n) {
    numDigits = Math.max(n, 0);
  }


  /**
   * @return number of digits used format fractional part of a coefficient.
   */

  public int getFractionDigits() {
    return numDigits;
  }


  /**
   * Writes kernel data.
   * @param writer destination for kernel data
   */

  public void write(Writer writer) {

    PrintWriter out = new PrintWriter(new BufferedWriter(writer));
    out.println("# convolution kernel");
    out.println(getWidth() + " " + getHeight() + " " + numDigits);

    float[] data = getKernelData(null);
    NumberFormat nf = getFormat();
    Vector coefficients = new Vector();

    // Format each coefficient into a string

    int i, maxLength = 0;
    for (i = 0; i < data.length; ++i) {
      String string = nf.format(data[i]);
      coefficients.addElement(string);
      if (string.length() > maxLength)
        maxLength = string.length();
    }

    // Print coefficient strings

    i = 0;
    for (int k = 0; k < getHeight(); ++k) {
      for (int j = 0; j < getWidth(); ++j, ++i)
        out.print(StringTools.rightJustify(
         (String) coefficients.elementAt(i), maxLength+1));
      out.println();
    }
    out.flush();

  }


  /**
   * @return a NumberFormat that can be used to format coefficients
   * when printing them.
   */

  private NumberFormat getFormat() {
    StringBuffer patternBuffer = new StringBuffer("0");
    if (numDigits > 0) {
      patternBuffer.append('.');
      for (int i = 0; i < numDigits; ++i)
        patternBuffer.append('0');
    }
    return new DecimalFormat(patternBuffer.toString());
  }


}
