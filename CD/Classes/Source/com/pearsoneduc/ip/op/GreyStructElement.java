/***************************************************************************

  GreyStructElement.java

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
import java.util.StringTokenizer;
import com.pearsoneduc.ip.util.StringTools;



/**
 * A structuring element for the morphological processing
 * of greyscale images.
 *
 * @author Nick Efford
 * @version 1.0 [1999/08/31]
 */

public class GreyStructElement extends StructElement implements Cloneable {


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Creates a 3x3 square SE with pixels set to the specified value.
   * @param value SE pixel value
   */

  public GreyStructElement(int value) throws StructElementException {
    super(3, 3);
    setPixels(value);
  }

  /**
   * Creates a rectangular GreyStructElement with the specified
   * dimensions and pixel value.
   * @param w desired SE width (greater than 0)
   * @param h desired SE height (greater than 0)
   * @param value SE pixel value
   * @exception StructElementException if dimensions are invalid.
   */

  public GreyStructElement(int w, int h, int value)
   throws StructElementException {
    super(w, h);
    setPixels(value);
  }

  /**
   * Creates an instance of a GreyStructElement with the specified
   * dimensions, origin and pixel value.
   * @param w desired SE width (greater than 0)
   * @param h desired SE height (greater than 0)
   * @param p java.awt.Point object representing origin of SE
   * @param value SE pixel value
   * @exception StructElementException if dimensions are invalid.
   */

  public GreyStructElement(int w, int h, Point p, int value)
   throws StructElementException {
    super(w, h, p);
    setPixels(value);
  }

  /**
   * Creates an instance of a GreyStructElement by reading data.
   * @param source Reader representing source of SE data
   * @exception java.io.IOException if there was an I/O error.
   * @exception StructElementException if the data do not conform to
   *  the required format or the dimensions, origin or SE values
   *  are not valid.
   */

  public GreyStructElement(Reader source)
   throws IOException, StructElementException {

    // Read first line and check for a valid SE header

    BufferedReader input = new BufferedReader(source);
    String line = input.readLine();
    if (!line.startsWith("# grey structuring element"))
      throw new StructElementException(
       "no grey structuring element data available");

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
      StringTokenizer parser = new StringTokenizer(line);
      if (parser.countTokens() < width)
        throw new StructElementException("truncated structuring element?");
      for (x = 0; x < width; x++)
        pixel[y][x] = Integer.parseInt(parser.nextToken());
    }

  }


  /**
   * @return a copy of this GreyStructElement.
   */

  public Object clone() {
    try {
      GreyStructElement element =
       new GreyStructElement(width, height, origin, 0);
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
   * Tests for equality with another GreyStructElement.
   * @param obj a GreyStructElement object
   * @return true if the SEs are equal, false otherwise.
   */

  public boolean equals(Object obj) {
    if ((obj instanceof GreyStructElement) && super.equals(obj))
      return true;
    else
      return false;
  }


  /**
   * Sets a pixel in the structuring element to the specified value.
   * @param x x coordinate of SE pixel
   * @param y y coordinate of SE pixel
   * @param value new value for SE pixel
   */

  public void setPixel(int x, int y, int value) {
    pixel[y][x] = value;
  }


  /**
   * Sets all pixels of the SE to the specified value.
   * @param value new value for SE pixels
   */

  public void setPixels(int value) {
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        pixel[y][x] = value;
  }

  /**
   * Copies values from a one-dimensional array into the SE.
   * @param value array of values to be copied into the SE
   */

  public void setPixels(int[] value) throws StructElementException {
    if (value.length < width*height)
      throw new StructElementException(
       "fewer values than structuring element pixels");
    int i = 0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x, ++i)
        pixel[y][x] = value[i];
  }

  /**
   * Copies values from a two-dimensional array into the SE.
   * @param value array of values to be copied into SE
   */

  public void setPixels(int[][] value) throws StructElementException {
    if (value.length < height || value[0].length < width)
      throw new StructElementException(
       "fewer values than structuring element pixels");
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        pixel[y][x] = value[y][x];
  }

  /**
   * Copies values from a String into the SE.
   * @param valueString String of values to be copied into SE
   * @exception StructElementException if string does not contain
   *  enough values.
   */

  public void setPixels(String valueString) throws StructElementException {
    StringTokenizer parser = new StringTokenizer(valueString);
    if (parser.countTokens() != width*height)
      throw new StructElementException("invalid string of values");
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        pixel[y][x] = Integer.parseInt(parser.nextToken());
  }


  /**
   * Writes SE pixel values to the specified destination.
   * @param destination Writer acting as destination for the SE data
   */

  public void writePixels(Writer destination) {
    PrintWriter out = new PrintWriter(new BufferedWriter(destination));
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
        out.print(StringTools.rightJustify(pixel[y][x], 5));
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
    out.println("# grey structuring element");
    out.println("# width=" + width);
    out.println("# height=" + height);
    out.println("# xorigin=" + origin.x);
    out.println("# yorigin=" + origin.y);
    if (out.checkError())
      throw new IOException("error writing SE header");
    writePixels(destination);
  }


  /**
   * Calculates the maximum distance that the SE can be pushed up whilst
   * remaining beneath an image.  This is equivalent to the minimum
   * distance between image pixel values and SE pixel values over the
   * domain of the SE.
   * <p>Note: this method does <em>not</em> check whether SE pixels lie
   * beyond the bounds of the image!</p>
   * @param raster Raster of the image on which the SE will operate
   * @param x x coordinate of pixel where SE will be placed
   * @param y y coordinate of pixel where SE will be placed
   * @return maximum distance that the SE can be pushed up.
   */

  public int below(Raster raster, int x, int y) {
    int distance, maxDistance = Integer.MAX_VALUE;
    int j, k, m, n;
    for (k = 0, n = y-origin.y; k < height; k++, n++)
      for (j = 0, m = x-origin.x; j < width; j++, m++) {
        distance = raster.getSample(m, n, 0) - pixel[k][j];
        maxDistance = Math.min(distance, maxDistance);
      }
    return maxDistance;
  }


  /**
   * Calculates the minimum distance that a rotated SE would need
   * to be pushed up so as to be above the image. 
   * <p>Note: this method does <em>not</em> check whether SE pixels lie
   * beyond the bounds of the image!</p>
   * @param raster Raster of the image on which the SE will operate
   * @param x x coordinate of pixel where SE will be placed
   * @param y y coordinate of pixel where SE will be placed
   * @return minimum 'push-up' distance.
   */

  public int above(Raster raster, int x, int y) {
    int distance, minDistance = Integer.MIN_VALUE;
    int j, k, m, n;
    for (k = height-1, n = y-origin.y; k >= 0; k--, n++)
      for (j = width-1, m = x-origin.x; j >= 0; j--, m++) {
        distance = raster.getSample(m, n, 0) + pixel[k][j];
        minDistance = Math.max(distance, minDistance);
      }
    return minDistance;
  }


  /**
   * @return a version of this GreyStructElement that has been
   *  rotated through 180 degrees.
   */

  public GreyStructElement getRotatedVersion() {
    try {
      GreyStructElement element =
       new GreyStructElement(width, height, origin, 0);
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
