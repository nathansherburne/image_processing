/***************************************************************************

  Histogram.java

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



import java.awt.image.*;
import java.io.*;



/**
 * A class which calculates the histogram of a BufferedImage, together
 * with associated statistics such as minimum and maximum values in all
 * bands, mean value in all bands, etc.
 *
 * @author Nick Efford
 * @version 1.3 [1999/06/30]
 *
 * @see java.awt.image.BufferedImage
 */

public final class Histogram implements Cloneable {


  /////////////////////////////// CONSTANTS ////////////////////////////////


  private static final String BAND_ERROR = "histogram band not specified";


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /**
   * Number of bands in the source image (1 for greyscale, 3 for colour).
   */

  private int bands;

  /**
   * Total number of samples processed.
   */

  private int samples;

  /**
   * 2D array holding frequency data for a greyscale or colour image.
   */

  private int[][] freq;

  /**
   * 2D array holding cumulative frequency data.
   */

  private int[][] cumFreq;

  /**
   * Array storing the minimum grey value or minimum R, G and B
   * values of the source image.
   */

  private int[] minValue;

  /**
   * Array storing the maximum grey value or maximum R, G and B
   * values of the source image.
   */

  private int[] maxValue;

  /**
   * Array storing the lowest frequencies recorded for each
   * band of the source image.
   */

  private int[] minFreq;

  /**
   * Array storing the highest frequencies recorded for each
   * band of the source image.
   */

  private int[] maxFreq;

  /**
   * Array holding the mean values for each band of the source image.
   */

  private double[] meanValue;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Default constructor.
   */

  public Histogram() {
    allocateStorage();
  }

  /**
   * Constructs a histogram using a Reader as the data source.
   * @param reader the Reader used to obtain histogram data
   * @exception IOException if the input data are not formatted correctly.
   */

  public Histogram(Reader reader) throws IOException {
    allocateStorage();
    read(reader);
  }

  /**
   * Constructs the histogram of a BufferedImage.
   * @param image the image for which a histogram is required
   * @exception HistogramException if image is of wrong type.
   */

  public Histogram(BufferedImage image) throws HistogramException {
    allocateStorage();
    computeHistogram(image);
  }


  /**
   * Clones a histogram.
   */

  public Object clone() {
    Histogram h = new Histogram();
    if (samples > 0) {
      h.bands = bands;
      h.samples = samples;
      for (int i = 0; i < 3; ++i) {
        System.arraycopy(freq[i], 0, h.freq[i], 0, 256);
        System.arraycopy(cumFreq[i], 0, h.cumFreq[i], 0, 256);
      }
      System.arraycopy(minFreq, 0, h.minFreq, 0, 3);
      System.arraycopy(maxFreq, 0, h.maxFreq, 0, 3);
      System.arraycopy(minValue, 0, h.minValue, 0, 3);
      System.arraycopy(maxValue, 0, h.maxValue, 0, 3);
      System.arraycopy(meanValue, 0, h.meanValue, 0, 3);
    }
    return h;
  }


  /**
   * Tests for equivalence of Histogram objects.
   * @return true if histograms contain the same data, false otherwise.
   */

  public boolean equals(Object obj) {

    if (obj instanceof Histogram) {

      Histogram h = (Histogram) obj;
      if (bands != h.bands || samples != h.samples)
        return false;

      for (int k = 0; k < 256; ++k)
        for (int j = 0; j < bands; ++j)
          if (freq[j][k] != h.freq[j][k])
            return false;

      // Don't need to check contents of other arrays, since
      // they are derived from frequency data...

      return true;

    }
    else
      return false;

  }


  /**
   * @return a String object giving the number of bands and samples
   *   in the histogram.
   */

  public String toString() {
    return new String(getClass().getName() + ": " +
     bands + " bands, " + samples + " samples");
  }


  /**
   * Calculates histogram data for a BufferedImage.
   * @param image BufferedImage for which a histogram is required
   * @exception HistogramException if the image is of an unsupported type.
   */

  public void computeHistogram(BufferedImage image)
   throws HistogramException {

    initialize();   // zero arrays before accumulating frequency data

    if (image.getType() == BufferedImage.TYPE_BYTE_GRAY)
      bands = 1;
    else
      bands = 3;

    samples = image.getWidth()*image.getHeight();
    if (samples > 0) {
      accumulateFrequencies(image);
      computeStatistics();
    }

  }


  /**
   * Reads histogram data from the specified source.
   * @param source Reader used to obtain histogram data
   * @exception IOException if the input data are not formatted correctly.
   */

  public void read(Reader source) throws IOException {

    BufferedReader in = new BufferedReader(source);
    String line = in.readLine();

    initialize();
    if (line.equals("# grey histogram")) {       // one value per line
      bands = 1; 
      samples = 0;
      for (int i = 0; i < 256; ++i) {
        freq[0][i] = Integer.parseInt(in.readLine());
        samples += freq[0][i];
      }
      if (samples > 0)
        computeStatistics();
    }
    else if (line.equals("# RGB histogram")) {   // three values per line
      bands = 3;
      samples = 0;
      StreamTokenizer tok = new StreamTokenizer(in);
      for (int k = 0; k < 256; ++k) {
        for (int j = 0; j < 3; ++j) {
          tok.nextToken();
          if (tok.ttype == StreamTokenizer.TT_EOF)
            throw new EOFException("histogram appears to be truncated");
          if (tok.ttype == StreamTokenizer.TT_NUMBER)
            freq[j][k] = (int) tok.nval;
        }
        samples += freq[0][k];
      }
      if (samples > 0)
        computeStatistics();
    }
    else
      throw new IOException("invalid histogram file");

  }


  /**
   * Writes histogram data to the specified destination.
   * @param destination Writer used to output histogram data
   */

  public void write(Writer destination) {
    PrintWriter out =
     new PrintWriter(new BufferedWriter(destination));
    if (sourceIsGrey()) {
      out.println("# grey histogram");
      for (int i = 0; i < 256; ++i)
        out.println(freq[0][i]);         // no special formatting
    }
    else {
      out.println("# RGB histograms");
      for (int k = 0; k < 256; ++k) {
        for (int j = 0; j < 3; ++j)
          writeValue(freq[j][k], out);   // right-justified frequencies,
        out.println();                   // three values per line
      }
    }
    out.flush();
  }


  /**
   * Writes cumulative histogram data to the specified destination.
   * @param destination Writer used to output data
   */

  public void writeCumulative(Writer destination) {
    PrintWriter out =
     new PrintWriter(new BufferedWriter(destination));
    if (sourceIsGrey()) {
      out.println("# grey cumulative histogram");
      for (int i = 0; i < 256; ++i)
        out.println(cumFreq[0][i]);         // no special formatting
    }
    else {
      out.println("# RGB cumulative histograms");
      for (int k = 0; k < 256; ++k) {
        for (int j = 0; j < 3; ++j)
          writeValue(cumFreq[j][k], out);   // right-justified frequencies,
        out.println();                      // three values per line
      }
    }
    out.flush();
  }


  /**
   * Indicates whether histogram was computed from a greyscale image or not.
   * @return true if source is a greyscale image, false otherwise.
   */

  public boolean sourceIsGrey() {
    return bands == 1;
  }


  /**
   * Gives number of histogram bands.
   * @return number of bands, as an integer (1 if the source of the
   *   histogram is a greyscale image, 3 if the source is a colour image).
   */

  public int getNumBands() {
    return bands;
  }


  /**
   * Gives number of samples taken from source image.
   * @return number of samples, as an integer.
   */

  public int getNumSamples() {
    return samples;
  }


  /**
   * Retrieves the frequency of occurrence of a specified grey level.
   * @param value pixel value for which a frequency is required
   * @return frequency as an integer (0 or greater).
   * @exception HistogramException if source is not a greyscale image.
   */

  public int getFrequency(int value) throws HistogramException {
    if (sourceIsGrey())
      return freq[0][value];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Retrieves the frequency of occurrence of a particular value
   *  in a given band.
   * @param band band for which a frequency is required (0, 1 or 2)
   * @param value pixel value for which a frequency is required
   * @return frequency as an integer (0 or greater).
   */

  public int getFrequency(int band, int value) {
    return freq[band][value];
  }


  /**
   * Retrieves the frequency of occurrence of a grey level less than
   *  or equal to the specified value.
   * @param value pixel value for which a cumulative frequency is required
   * @return cumulative frequency as an integer (0 or greater).
   * @exception HistogramException if source is not a greyscale image.
   */

  public int getCumulativeFrequency(int value) throws HistogramException {
    if (sourceIsGrey())
      return cumFreq[0][value];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Retrieves the frequency of occurrence of values less than or
   *  equal to the specified value in a given band.
   * @param band band for which a cumulative frequency is required
   * @param value pixel value for which a cumulative frequency is required
   * @return cumulative frequency as an integer (0 or greater).
   */

  public int getCumulativeFrequency(int band, int value) {
    return cumFreq[band][value];
  }


  /**
   * Gives the smallest frequency recorded in the histogram.
   * @return minimum frequency as an integer (0 or greater).
   * @exception HistogramException if source is not a greyscale image.
   */

  public int getMinFrequency() throws HistogramException {
    if (sourceIsGrey())
      return minFreq[0];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Gives the smallest frequency recorded in the histogram.
   * @param band band for which a minimum frequency is required (0, 1 or 2)
   * @return minimum frequency in that band, as an integer (0 or greater).
   */

  public int getMinFrequency(int band) {
    return minFreq[band];
  }


  /**
   * Gives the largest frequency recorded in the histogram.
   * @return maximum frequency as an integer (0 or greater).
   * @exception HistogramException if source is not a greyscale image.
   */

  public int getMaxFrequency() throws HistogramException {
    if (sourceIsGrey())
      return maxFreq[0];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Gives the largest frequency recorded in the histogram.
   * @param band band from which maximum frequency is required (0, 1 or 2)
   * @return maximum frequency in that band, as an integer (0 or greater).
   */

  public int getMaxFrequency(int band) {
    return maxFreq[band];
  }


  /**
   * Gives minimum value for which counts have been recorded.
   * @return minimum value as an integer (0 or greater).
   * @exception HistogramException if source is not a greyscale image.
   */

  public int getMinValue() throws HistogramException {
    if (sourceIsGrey())
      return minValue[0];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Gives minimum value for which counts have been recorded
   * in the specified band.
   * @param band band for which a minimum value is required (0, 1 or 2)
   * @return minimum value in that band, as an integer (0 or greater).
   */

  public int getMinValue(int band) {
    return minValue[band];
  }


  /**
   * Gives maximum value for which counts have been recorded.
   * @return maximum value, as an integer (0 or greater).
   * @exception HistogramException if source is not a greyscale image.
   */

  public int getMaxValue() throws HistogramException {
    if (sourceIsGrey())
      return maxValue[0];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Gives maximum value for which counts have been recorded
   * in the specified band.
   * @param band the band for which maximum value is required (0, 1 or 2)
   * @return maximum value in that band, as an integer (0 or greater).
   */

  public int getMaxValue(int band) {
    return maxValue[band];
  }


  /**
   * Gives mean value of a greyscale histogram.
   * @return mean value, as a double-precision real number.
   * @exception HistogramException if source is not a greyscale image.
   */

  public double getMeanValue() throws HistogramException {
    if (sourceIsGrey())
      return meanValue[0];
    else
      throw new HistogramException(BAND_ERROR);
  }

  /**
   * Gives mean value in one band of a colour histogram.
   * @param band the band for which mean value is required (0, 1 or 2)
   * @return mean value in the specified band, as a double-precision
   *   real number.
   */

  public double getMeanValue(int band) {
    return meanValue[band];
  }


  /////////////////////////// NON-PUBLIC METHODS ////////////////////////////


  /**
   * Allocates storage for arrays.
   */

  private void allocateStorage() {
    freq = new int[3][256];
    cumFreq = new int[3][256];
    minValue = new int[3];
    maxValue = new int[3];
    minFreq = new int[3];
    maxFreq = new int[3];
    meanValue = new double[3];
  }


  /**
   * Initializes arrays and other instance variables properly.
   */

  private void initialize() {
    bands = samples = 0;
    for (int i = 0; i < 3; ++i)
      for (int j = 0; j < 256; ++j)
        freq[i][j] = cumFreq[i][j] = 0;
    for (int i = 0; i < 3; ++i) {
      minValue[i] = maxValue[i] = 0;
      minFreq[i] = maxFreq[i] = 0;
      meanValue[i] = 0.0;
    }
  }


  /**
   * Accumulates histogram data by retrieving pixel values
   * from the specified image.
   * @param image BufferedImage for which a histogram must be calculated
   * @exception HistogramException if the image type is not supported.
   */

  private void accumulateFrequencies(BufferedImage image)
   throws HistogramException {

    if (image.getType() == BufferedImage.TYPE_BYTE_BINARY
     || image.getType() == BufferedImage.TYPE_USHORT_GRAY)
      throw new HistogramException("invalid image type");

    Raster raster = image.getRaster();
    if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
      for (int y = 0; y < image.getHeight(); ++y)
        for (int x = 0; x < image.getWidth(); ++x)
          ++freq[0][raster.getSample(x, y, 0)];
    }
    else {
      int[] value = new int[3];
      for (int y = 0; y < image.getHeight(); ++y)
        for (int x = 0; x < image.getWidth(); ++x) {
          raster.getPixel(x, y, value);
          ++freq[0][value[0]];
          ++freq[1][value[1]];
          ++freq[2][value[2]];
        }
    }

  }


  /**
   * Computes statistics from histogram data.
   */

  private void computeStatistics() {

    int i, j;

    for (i = 0; i < bands; ++i) {

      // Compute cumulative histogram

      cumFreq[i][0] = freq[i][0];
      for (j = 1; j < 256; ++j)
        cumFreq[i][j] = cumFreq[i][j-1] + freq[i][j];
 
      // Find minimum value

      for (j = 0; j < 256; ++j) {
        if (freq[i][j] > 0) {
          minValue[i] = j;
          break;
        }
      }

      // Find maximum value

      for (j = 255; j >= 0; --j) {
        if (freq[i][j] > 0) {
          maxValue[i] = j;
          break;
        }
      }

      // Find lowest and highest frequencies, and determine mean

      minFreq[i] = Integer.MAX_VALUE;
      for (j = 0; j < 256; ++j) {
        if (freq[i][j] < minFreq[i])
          minFreq[i] = freq[i][j];
        else if (freq[i][j] > maxFreq[i])
          maxFreq[i] = freq[i][j];
        meanValue[i] += (double) (j*freq[i][j]);
      }
      meanValue[i] /= (double) samples;

    }

  }


  /**
   * Outputs an integer in right-justified form.
   * @param value integer value to be formatted
   * @param out destination for the formatted value
   */

  private void writeValue(int value, PrintWriter out) {
    if (value > 99999)
      out.print(" " + value);
    else if (value > 9999)
      out.print("  " + value);
    else if (value > 999)
      out.print("   " + value);
    else if (value > 99)
      out.print("    " + value);
    else if (value > 9)
      out.print("     " + value);
    else
      out.print("      " + value);
  }


}
