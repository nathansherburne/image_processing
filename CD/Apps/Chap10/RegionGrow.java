/***************************************************************************

  RegionGrow.java   Version 1.0 [1999/08/25]

  This program reads an image from a file and grows a region around a
  specified seed pixel, using either 4- or 8-connectivity and a given
  threshold on the difference between a pixel's grey level or colour and
  the mean grey level or colour of the region.  The output image has
  pixels set to 255 if they belong to the region, or 0 otherwise.
  Command line arguments are: input filename, output filename, seed
  pixel x and y coordinates, connectivity, threshold.

  Example of use:

    java RegionGrow test.jpg region.png 128 128 4 35


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


import java.awt.Point;
import java.awt.image.*;
import java.util.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.RegionGrower;
import com.pearsoneduc.ip.util.IntervalTimer;



public class RegionGrow {


  public static void makeRegionWhite(BufferedImage image) {
    WritableRaster raster = image.getRaster();
    for (int y = 0; y < raster.getHeight(); ++y)
      for (int x = 0; x < raster.getWidth(); ++x)
        if (raster.getSample(x, y, 0) > 0)
          raster.setSample(x, y, 0, 255);
  }


  public static void main(String[] argv) {
    if (argv.length > 5) {
      try {

        // Parse command line arguments

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int x = Integer.parseInt(argv[2]);
        int y = Integer.parseInt(argv[3]);
        int connectivity = Integer.parseInt(argv[4]);
        int threshold = Integer.parseInt(argv[5]);

        // Load image and create seed pixel

        BufferedImage image = input.decodeAsBufferedImage();
        List seeds = new ArrayList();
        seeds.add(new Point(x, y));

        // Grow region around seed pixel and change output image
        // so that grey levels are 0 and 255

        RegionGrower grower =
         new RegionGrower(image, seeds, connectivity, threshold);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        grower.growToCompletion();
        System.out.println("Region growing finished [" +
         grower.getNumIterations() + " iterations, " +
         timer.stop() + " sec]");
        BufferedImage outputImage = grower.getRegionImage();
        makeRegionWhite(outputImage);

        // Write output image to file

        output.encode(outputImage);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java RegionGrow " +
       "<infile> <outfile> <x> <y> <connectivity> <threshold>");
      System.exit(1);
    }
  }


}
