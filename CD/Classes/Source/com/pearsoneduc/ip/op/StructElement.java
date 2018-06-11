/***************************************************************************

  StructElement.java

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



import java.awt.Point;
import java.io.*;



/**
 * Abstract base class for structuring elements used in
 * morphological image processing.
 *
 * @author Nick Efford
 * @version 1.2 [1999/08/31]
 */

public abstract class StructElement {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Width of structuring element. */
  protected int width;

  /** Height of structuring element. */
  protected int height;

  /** Origin of structuring element, relative to upper-left corner. */
  protected Point origin;

  /**
   * Structuring element data (1 or 0 for binary SE,
   * arbitrary integers for grey SE).
   */

  protected int[][] pixel;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  public StructElement() {}

  /**
   * Creates an SE with the specified dimensions.
   * @param w desired width of SE 
   * @param h desired height of SE
   * @exception StructElementException if dimensions are invalid.
   */

  public StructElement(int w, int h) throws StructElementException {
    if (w < 1 || h < 1)
      throw new StructElementException(
       "invalid structuring element dimensions");
    width = w;
    height = h;
    origin = new Point(w/2, h/2);
    pixel = new int[h][w];
  }

  /**
   * Creates an SE with the specified dimensions and origin.
   * @param w desired width of SE
   * @param h desired height of SE
   * @param p position of SE's origin
   * @exception StructElementException if dimensions are invalid.
   */

  public StructElement(int w, int h, Point p) throws StructElementException {
    this(w, h);
    setOrigin(p);
  }


  /**
   * Tests for equality with another StructElement object.
   * @return true if this StructElement is equal to the specified
   *  StructElement, false otherwise.
   */

  public boolean equals(Object obj) {

    if (obj instanceof StructElement) {

      StructElement element = (StructElement) obj;
      if (width != element.width
       || height != element.height
       || (!origin.equals(element.origin)))
        return false;

      for (int y = 0; y < height; y++)
        for (int x = 0; x < width; x++)
          if (pixel[y][x] != element.pixel[y][x])
            return false;

      return true;

    }
    else
      return false;

  }


  /**
   * Creates a string indicating the type of structuring element,
   * its dimensions and its origin.
   * @return a String containing information on the SE.
   */

  public String toString() {
    return new String(
     getClass().getName() + ": size " + width + "x" + height +
     ", origin (" + origin.x + "," + origin.y + ")");
  }


  /**
   * @return the width of the SE.
   */

  public int getWidth() {
    return width;
  }


  /**
   * @return the height of the SE.
   */

  public int getHeight() {
    return height;
  }


  /**
   * @return the SE's origin.
   */

  public Point getOrigin(Point p) {
    if (p != null) {
      p.x = origin.x;
      p.y = origin.y;
      return p;
    }
    else
      return new Point(origin.x, origin.y);
  }


  /**
   * Changes the origin of the SE.
   * @param p a java.awt.Point object representing the new origin.
   */

  public void setOrigin(Point p) {
    origin.x = p.x;
    origin.y = p.y;
  }


  /**
   * Retrieves a value from the specified pixel in the SE.
   * @param x x coordinate of SE pixel
   * @param y y coordinate of SE pixel
   * @return value at specified SE pixel.
   */

  public int getPixel(int x, int y) {
    return pixel[y][x];
  }


  /**
   * Initialises SE pixels using values in a one-dimensional array.
   * @param value array of SE pixel values
   * @exception StructElementException.
   */

  public abstract void setPixels(int[] value)
   throws StructElementException;

  /**
   * Initialises SE pixels using values in a two-dimensional array.
   * @param value array of SE pixel values
   * @exception StructElementException.
   */

  public abstract void setPixels(int[][] value)
   throws StructElementException;

  /**
   * Initialises SE pixels using values in a String.
   * @param valueString a string of values for SE pixels, given row-by-row
   * @exception StructElementException.
   */

  public abstract void setPixels(String valueString)
   throws StructElementException;


  /**
   * Writes SE pixel values to the specified destination.
   * @param destination Writer acting as destination for SE data
   */

  public abstract void writePixels(Writer destination);


  /**
   * Writes SE data, including a header, to the specified destination.
   * @param destination Writer acting as destination for SE data
   */

  public abstract void write(Writer destination) throws IOException;


  /////////////////////////// NON-PUBLIC METHODS ///////////////////////////


  /**
   * Reads the value of an SE parameter from the specified source.
   * @param source Reader acting as the source of SE data
   * @param key String of the form "# param=", to match with what is
   *  read from the input
   * @return integer value for the named parameter.
   * @exception java.io.IOException if an I/O error occurs.
   * @exception StructElementException if the string read from the input
   *  does not begin with the specified key.
   */

  protected int readInt(BufferedReader source, String key)
   throws IOException, StructElementException {
    String line = source.readLine();
    if (!line.startsWith(key))
      throw new StructElementException("cannot parse structuring element data");
    int i = line.indexOf('=');
    return Integer.parseInt(line.substring(i+1));
  }


}
