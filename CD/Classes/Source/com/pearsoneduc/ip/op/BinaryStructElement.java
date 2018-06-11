/***************************************************************************

  BinaryStructElement.java

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
import java.awt.image.Raster;
import java.io.*;



/**
 * A structuring element for the morphological processing
 * of binary images.
 *
 * @author Nick Efford
 * @version 1.1 [1999/08/30]
 */

public class BinaryStructElement
 extends StructElement implements Cloneable, StructElementTypes {


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Creates a 3x3 square SE.
   */

  public BinaryStructElement() throws StructElementException {
    super(3, 3);
    setPixels();
  }

  /**
   * Creates an instance of a BinaryStructElement with the specified
   * dimensions.  All its pixels will be set to 1.
   * @param w desired SE width (greater than 0)
   * @param h desired SE height (greater than 0)
   * @exception StructElementException if dimensions are invalid.
   */

  public BinaryStructElement(int w, int h) throws StructElementException {
    super(w, h);
    setPixels();
  }

  /**
   * Creates an instance of a BinaryStructElement with the specified
   * dimensions and origin.  All its pixels will be set to 1.
   * @param w desired SE width (greater than 0)
   * @param h desired SE height (greater than 0)
   * @param p java.awt.Point object representing origin of SE
   * @exception StructElementException if dimensions are invalid.
   */

  public BinaryStructElement(int w, int h, Point p)
   throws StructElementException {
    super(w, h, p);
    setPixels();
  }

  /**
   * Creates an instance of a standard BinaryStructElement, one of the
   * types defined in the MorphologicalConstants interface.
   * @param type integer specifying SE type
   * @exception StructElementException if an invalid type parameter
   *  has been specified.
   * @see StructElementTypes
   */

  public BinaryStructElement(int type) throws StructElementException {

    // Set up SE dimensions

    switch (type) {

      case CROSS_3x3:
        width = height = 3;
        origin = new Point(1, 1);
        pixel = new int[3][3];
        break;

      case CROSS_5x5:
      case DIAMOND_5x5:
      case DISK_5x5:
        width = height = 5;
        origin = new Point(2, 2);
        pixel = new int[5][5];
        break;

      case DIAMOND_7x7:
      case DISK_7x7:
        width = height = 7;
        origin = new Point(3, 3);
        pixel = new int[7][7];
        break;

      default:
        throw new StructElementException("invalid structuring element type");

    }

    // Define SE values

    switch (type) {
      case CROSS_3x3:
        setPixels("010111010");
        break;
      case CROSS_5x5:
        setPixels("0010000100111110010000100");
        break;
      case DIAMOND_5x5:
        setPixels("0010001110111110111000100");
        break;
      case DIAMOND_7x7:
        setPixels("0001000001110001111101111111011111000111000001000");
        break;
      case DISK_5x5:
        setPixels("0111011111111111111101110");
        break;
      case DISK_7x7:
        setPixels("0011100011111011111111111111111111101111100011100");
        break;
    }

  }

  /**
   * Creates an instance of a BinaryStructElement by reading data.
   * @param source Reader representing source of SE data
   * @exception java.io.IOException if there was an I/O error.
   * @exception StructElementException if the data do not conform to
   *  the required format or the dimensions, origin or SE values
   *  are not valid.
   */

  public BinaryStructElement(Reader source)
   throws IOException, StructElementException {

    // Read first line and check for a valid SE header

    BufferedReader input = new BufferedReader(source);
    String line = input.readLine();
    if (!line.startsWith("# binary structuring element"))
      throw new StructElementException(
       "no binary structuring element data available");

    // Parse width and height parameters

    width = readInt(input, "# width=");
    height = readInt(input, "# height=");
    if (width < 1 || height < 1)
      throw new StructElementException(
       "invalid structuring element dimensions");
    pixel = new int[height][width];

    // Parse SE origin

    origin = new Point(0, 0);
    origin.x = readInt(input, "# xorigin=");
    origin.y = readInt(input, "# yorigin=");

    // Read SE values into array

    int x, y, value;
    for (y = 0; y < height; y++) {
      line = input.readLine();
      if (line.length() < width)
        throw new StructElementException("truncated structuring element?");
      for (x = 0; x < width; x++) {
        value = Character.digit(line.charAt(x), 2);
        if (value < 0 || value > 1)
          throw new StructElementException("invalid structuring element value");
        pixel[y][x] = value;
      }
    }

  }


  /**
   * @return a copy of this BinaryStructElement.
   */

  public Object clone() {
    try {
      BinaryStructElement element =
       new BinaryStructElement(width, height, origin);
      for (int y = 0; y < height; y++)
        for (int x = 0; x < width; x++)
          element.pixel[y][x] = pixel[y][x];
      return element;
    }
    catch (StructElementException e) {
      return null;
    }
  }


  /**
   * Tests for equality with another BinaryStructElement.
   * @param obj a BinaryStructElement object
   * @return true if the SEs are equal, false otherwise.
   */

  public boolean equals(Object obj) {
    if ((obj instanceof BinaryStructElement) && super.equals(obj))
      return true;
    else
      return false;
  }


  /**
   * Sets a pixel in the structuring element to 1.
   * @param x x coordinate of SE pixel
   * @param y y coordinate of SE pixel
   */

  public void setPixel(int x, int y) {
    pixel[y][x] = 1;
  }


  /**
   * Clears a pixel in the structuring element (i.e., gives it a value of 0).
   * @param x x coordinate of SE pixel
   * @param y y coordinate of SE pixel
   */

  public void clearPixel(int x, int y) {
    pixel[y][x] = 0;
  }


  /**
   * Sets all pixels of the SE to 1.
   */

  public void setPixels() {
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        pixel[y][x] = 1;
  }

  /**
   * Copies values from a one-dimensional array into the SE.
   * @param value array of values to be copied into SE
   * @exception StructElementException if array is too small or
   *  if any of the values is neither 1 nor 0.
   */

  public void setPixels(int[] value) throws StructElementException {
    if (value.length < width*height)
      throw new StructElementException(
       "fewer values than structuring element pixels");
    int i = 0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x, ++i) {
        if (value[i] < 0 || value[i] > 1)
          throw new StructElementException("invalid structuring element value");
        pixel[y][x] = value[i];
      }
  }

  /**
   * Copies values from a two-dimensional array into the SE.
   * @param value array of values (1 or 0) to be copied into SE
   * @exception StructElementException if array dimensions are too
   *  small or if any of the values is neither 1 nor 0.
   */

  public void setPixels(int[][] value) throws StructElementException {
    if (value.length < height || value[0].length < width)
      throw new StructElementException(
       "fewer values than structuring element pixels");
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        if (value[y][x] < 0 || value[y][x] > 1)
          throw new StructElementException("invalid structuring element value");
        pixel[y][x] = value[y][x];
      }
  }

  /**
   * Copies values from a String into the SE.
   * @param valueString String of ones and zeroes to be copied into SE
   * @exception StructElementException if any of the values is neither 1
   *  nor 0.
   */

  public void setPixels(String valueString) throws StructElementException {
    if (valueString.length() != width*height)
      throw new StructElementException("invalid string of values");
    int i = 0, value;
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        value = Character.digit(valueString.charAt(i++), 2);
        if (value < 0 || value > 1)
          throw new StructElementException("invalid structuring element value");
        pixel[y][x] = value;
      }
  }


  /**
   * Writes SE pixel values to the specified destination.
   * @param destination Writer acting as destination for the SE data
   */

  public void writePixels(Writer destination) {
    PrintWriter out = new PrintWriter(new BufferedWriter(destination));
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
        out.print(pixel[y][x]);
      out.println();
    }
    out.flush();
  }


  /**
   * Writes SE data, including a header, to the specified destination.
   * @param destination Writer acting as a destination for the SE
   * @exception java.io.IOException if there was some kind of error when
   *  writing the header.
   */

  public void write(Writer destination) throws IOException {
    PrintWriter out = new PrintWriter(new BufferedWriter(destination));
    out.println("# binary structuring element");
    out.println("# width=" + width);
    out.println("# height=" + height);
    out.println("# xorigin=" + origin.x);
    out.println("# yorigin=" + origin.y);
    if (out.checkError())
      throw new IOException("error writing SE header");
    writePixels(destination);
  }


  /**
   * Tests whether the SE fits within an image at the specified coordinates.
   * The SE is positioned so that its origin lies at these coordinates.
   * If, for each SE pixel set to 1, the corresponding image pixel is
   * non-zero, the SE is said to fit at that point.
   * <p>Note: this method does <em>not</em> check whether SE pixels lie
   * beyond the bounds of the image!</p>
   * @param raster Raster of the image into which the SE will be fitted
   * @param x x coordinate of pixel where SE will be placed
   * @param y y coordinate of pixel where SE will be placed
   * @return true if the SE fits, false otherwise.
   */

  public boolean fits(Raster raster, int x, int y) {
    int j, k, m, n;
    for (k = 0, n = y-origin.y; k < height; k++, n++)
      for (j = 0, m = x-origin.x; j < width; j++, m++)
        if (pixel[k][j] == 1 && raster.getSample(m, n, 0) == 0)
          return false;
    return true;
  }


  /**
   * Tests whether the SE fits within the complement of an image at the
   * specified coordinates.  The SE is positioned so that its origin
   * lies at these coordinates.  If, for all SE pixels set to 1,
   * the corresponding image pixel is zero, the SE is said to fit the
   * complement of the image at that point.
   * <p>Note: this method does <em>not</em> check whether SE pixels lie
   * beyond the bounds of the image!</p>
   * @param raster Raster of the image into which the SE will be fitted
   * @param x x coordinate of pixel where SE will be placed
   * @param y y coordinate of pixel where SE will be placed
   * @return true if the SE fits the complement of the image, false otherwise.
   */

  public boolean fitsComplement(Raster raster, int x, int y) {
    int j, k, m, n;
    for (k = 0, n = y-origin.y; k < height; k++, n++)
      for (j = 0, m = x-origin.x; j < width; j++, m++)
        if (pixel[k][j] == 1 && raster.getSample(m, n, 0) != 0)
          return false;
    return true;
  }


  /**
   * Tests whether the SE hits an image when it is placed at the
   * specified coordinates.  The SE is positioned so that its origin
   * lies at these coordinates.  If, for any of the SE pixels set to 1,
   * the corresponding image pixel is non-zero, the SE is said to hit
   * the image at that point.
   * <p>Note: this method does <em>not</em> check whether SE pixels lie
   * beyond the bounds of the image!</p>
   * @param raster Raster of the image with which the SE will be intersected
   * @param x x coordinate of pixel where SE will be placed
   * @param y y coordinate of pixel where SE will be placed
   * @return true if the SE hits, false otherwise.
   */

  public boolean hits(Raster raster, int x, int y) {
    int j, k, m, n;
    for (k = 0, n = y-origin.y; k < height; k++, n++)
      for (j = 0, m = x-origin.x; j < width; j++, m++)
        if (pixel[k][j] == 1 && raster.getSample(m, n, 0) != 0)
          return true;
    return false;
  }


  /**
   * @return a version of this BinaryStructElement that has been
   *  rotated through 180 degrees.
   */

  public BinaryStructElement getRotatedVersion() {
    try {
      BinaryStructElement element =
       new BinaryStructElement(width, height, origin);
      int[][] rotated = new int[height][width];
      for (int y = 0; y < height; ++y)
        for (int x = 0; x < width; ++x)
          rotated[y][x] = pixel[height-y-1][width-x-1];
      element.setPixels(rotated);
      return element;
    }
    catch (StructElementException e) {
      return null;
    }
  }


}
