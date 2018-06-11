/***************************************************************************

  RegionGrower.java

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



import java.awt.Color;
import java.awt.Point;
import java.awt.image.*;
import java.util.*;



/**
 * A class that segments greyscale or colour images using a
 * region growing algorithm.
 *
 * @author Nick Efford
 * @version 1.1 [1999/08/25]
 */

public class RegionGrower {


  //////////////////////////// CLASS CONSTANTS /////////////////////////////


  /** Vectors to 4-connected neighbours of a pixel. */
  private static final Point[] FOUR_CONNECTED = {
    new Point( 1,  0),
    new Point( 0, -1),
    new Point(-1,  0),
    new Point( 0,  1)
  };

  /** Vectors to 8-connected neighbours of a pixel. */
  private static final Point[] EIGHT_CONNECTED = {
    new Point( 1,  0),
    new Point( 1, -1),
    new Point( 0, -1),
    new Point(-1, -1),
    new Point(-1,  0),
    new Point(-1,  1),
    new Point( 0,  1),
    new Point( 1,  1)
  };


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  /** Image to be segmented. */
  private BufferedImage sourceImage;

  /** List of seeds from which regions will be grown. */
  private List seedPixels;

  /** Connectivity of pixels (4 or 8). */
  private int connectivity;

  /** Square of threshold on difference of a value from region mean. */
  private int squaredThreshold;

  private int width;
  private int height;
  private int numBands;
  private boolean[][] assigned;
  private short[][] border;
  private BufferedImage regionImage;
  private BufferedImage statusImage;
  private Color assignedColour = Color.red;
  private Color borderColour = Color.yellow;
  private Point[] delta;
  private int numRegions;
  private int[] regionSize;
  private int[][] regionSum;
  private int[][] regionMean;
  private boolean unassignedPixels = true;
  private int unassignedLastTime;
  private int unassigned = 0;
  private int numIterations = 0;
  private boolean monitorStatus;


  ///////////////////////////// PUBLIC METHODS /////////////////////////////


  /**
   * Constructs a RegionGrower that will grow regions in an image from
   * the specified set of seed pixels.  Status monitoring is disabled.
   * @param image Image to be segmented
   * @param seeds list of seed pixels (each a Point object)
   * @param conn connectivity (4 or 8)
   * @param thresh threshold on the difference between a pixel's
   *  grey level and the mean grey level of a region
   */

  public RegionGrower(BufferedImage image, List seeds, int conn, int thresh) {
    this(image, seeds, conn, thresh, false);
  }

  /**
   * Constructs a RegionGrower that will grow regions in an image from
   * the specified set of seed pixels.
   * @param image Image to be segmented
   * @param seeds list of seed pixels (each a Point object)
   * @param conn connectivity (4 or 8)
   * @param thresh threshold on the difference between a pixel's
   *  grey level and the mean grey level of a region
   * @param monitor flag indicating whether status should be monitored
   */

  public RegionGrower(BufferedImage image, List seeds, int conn, int thresh,
   boolean monitor) {

    sourceImage = image;
    seedPixels = seeds;
    connectivity = conn;
    squaredThreshold = thresh*thresh;
    monitorStatus = monitor;

    // Determine data dimensions and allocate storage for
    // workspaces and the output image

    width = sourceImage.getWidth();
    height = sourceImage.getHeight();
    numBands = sourceImage.getRaster().getNumBands();
    assigned = new boolean[height][width];
    border = new short[height][width];
    regionImage =
     new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    if (monitorStatus)
      statusImage =
       new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    if (connectivity == 4)
      delta = FOUR_CONNECTED;
    else {
      // Force 8-connectivity
      connectivity = 8;
      delta = EIGHT_CONNECTED;
    }

    // Create arrays to hold region statistics

    numRegions = seedPixels.size();
    regionSize = new int[numRegions];
    regionSum = new int[numRegions][numBands];
    regionMean = new int[numRegions][numBands];

    initialise();

  }


  /**
   * @return number of regions that will be created.
   */

  public int getNumRegions() {
    return numRegions;
  }


  /**
   * Gives the number of pixels in the specified region.
   * @param i index of region (between 0 and N-1, where N
   *  is number of regions)
   * @return region size, or 0 if index is invalid.
   */

  public int getRegionSize(int i) {
    return (i >= 0 && i < numRegions ? regionSize[i] : 0);
  }


  /**
   * Gives an image containing the regions grown thus far.
   * Each pixel's grey level indicates the region to which it
   * belongs.  A value of 0 indicates that the pixel has not been
   * assigned to any region.
   * @return a reference to a greyscale BufferedImage.
   */

  public BufferedImage getRegionImage() {
    return regionImage;
  }


  /**
   * If status monitoring is enabled, this method gives a colour image
   * that indicates the current status of the region growing process.
   * Pixels unassigned to any region are transparent.  Assigned pixels
   * are opaque and are given one of two different colours, depending
   * on the status of the pixel (internal to a region or 'active'
   * and on the border of a region).
   * @return a reference to a colour BufferedImage (RGB with alpha),
   *  or null if status monitoring is disabled.
   */

  public BufferedImage getStatusImage() {
    return statusImage;
  }


  /**
   * Sets the colour used to indicate pixels assigned to a region
   * in the image returned by getStatusImage().
   * @param colour new colour for assigned pixels
   */

  public void setAssignedColour(Color colour) {
    assignedColour = colour;
  }


  /**
   * Sets the colour used to indicate pixels on the border of a region
   * in the image returned by getStatusImage().
   * @param colour new colour for border pixels
   */

  public void setBorderColour(Color colour) {
    borderColour = colour;
  }


  /**
   * @return number of iterations performed thus far.
   */

  public int getNumIterations() {
    return numIterations;
  }


  /**
   * Indicates whether region growing is complete.
   * @return true if regions have grown to the maximum extent possible,
   *  false otherwise.
   */

  public boolean isFinished() {
    return !unassignedPixels;
  }


  /**
   * Indicates whether region growing is complete.
   * @return true if regions have not yet grown to the maximum extent
   *  possible, false otherwise.
   */

  public boolean isNotFinished() {
    return unassignedPixels;
  }


  /**
   * Performs a single iteration of the region growing algorithm.
   */

  public void grow() {

    int n, nx, ny;
    int[] value = new int[3];
    Raster in = sourceImage.getRaster();
    WritableRaster out = regionImage.getRaster();
    short[][] newBorder = new short[height][width];

    // Iterate over entire image

    for (int y = 1; y < height-1; ++y)
      for (int x = 1; x < width-1; ++x) {
        n = border[y][x];
        if (n != 0) {
          // This pixel is on the border of a region
          for (int j = 0; j < connectivity; ++j) {
            // Look at its neighbours
            nx = x+delta[j].x;
            ny = y+delta[j].y;
            if (!assigned[ny][nx]) {
              // Can this unassigned neighbour be added to the region?...
              in.getPixel(nx, ny, value);
              if (distanceFromMean(n, value) < squaredThreshold) {
                // ...Yes!
                updateRegionStatistics(n, value);
                out.setSample(nx, ny, 0, n);
                assigned[ny][nx] = true;
                newBorder[ny][nx] = (short) n;
                border[y][x] = 0;
              }
            }
          }
        }
      }

    border = newBorder;   // Update array showing active pixels at borders

    if (monitorStatus)
      updateStatusImage();

    // Check whether this iteration has grown any regions

    unassignedLastTime = unassigned;
    unassigned = countUnassignedPixels();
    unassignedPixels = (unassignedLastTime - unassigned != 0);
    ++numIterations;

  }


  /**
   * Grows regions to their maximum extent.
   */

  public void growToCompletion() {
    while (unassignedPixels)
      grow();
  }


  //////////////////////////// PRIVATE METHODS /////////////////////////////


  /**
   * Initialises region growing algorithm by copying seed pixels
   * into region image and updating region statistics.
   */

  private void initialise() {

    Iterator iterator = seedPixels.iterator();
    Raster in = sourceImage.getRaster();
    WritableRaster out = regionImage.getRaster();

    if (monitorStatus)
      for (int y = 0; y < height; ++y)
        for (int x = 0; x < width; ++x)
          statusImage.setRGB(x, y, 0);

    int[] data = new int[3];
    short n = 1;
    while (iterator.hasNext()) {
      Point pixel = (Point) iterator.next();
      out.setSample(pixel.x, pixel.y, 0, n);
      if (monitorStatus)
        statusImage.setRGB(pixel.x, pixel.y, borderColour.getRGB());
      border[pixel.y][pixel.x] = n;
      assigned[pixel.y][pixel.x] = true;
      ++regionSize[n-1];
      in.getPixel(pixel.x, pixel.y, data);
      for (int i = 0; i < numBands; ++i)
        regionMean[n-1][i] = regionSum[n-1][i] = data[i];
      ++n;
    }

  }


  /**
   * Computes (squared) distance of a grey level or colour from
   * the mean grey level or colour of the specified region.
   * @param n index of region
   * @param value array of samples from a pixel
   * @return squared distance.
   */

  private int distanceFromMean(int n, int[] value) {
    int d, sum = 0;
    for (int i = 0; i < numBands; ++i) {
      d = value[i] - regionMean[n-1][i];
      sum += d*d;
    }
    return sum;
  }


  /**
   * Updates region statistics with the specified pixel data.
   * @param n index of region
   * @param value array of samples from a pixel
   */

  private void updateRegionStatistics(int n, int[] value) {
    ++regionSize[n-1];
    for (int i = 0; i < numBands; ++i) {
      regionSum[n-1][i] += value[i];
      regionMean[n-1][i] = regionSum[n-1][i]/regionSize[n-1];
    }
  }


  /**
   * Updates status image to reflect current status of
   * region growing process.
   */

  private void updateStatusImage() {
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x)
        if (border[y][x] > 0)
          statusImage.setRGB(x, y, borderColour.getRGB());
        else if (assigned[y][x])
          statusImage.setRGB(x, y, assignedColour.getRGB());
  }


  /**
   * @return number of pixels not yet assigned to a region.
   */

  private int countUnassignedPixels() {
    int n = 0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x)
        if (!assigned[y][x])
          ++n;
    return n;
  }


}
