/***************************************************************************

  HitAndMiss.java   Version 1.0 [1999/08/30]

  This program reads a binary image and a pair of structuring elements
  from files, and uses these structuring elements to perform a hit
  and miss transform on the image.  The results are written to a new
  file, as a list of coordinates where a perfect match was found by
  the transform.

  Example of use:

    java HitAndMiss binary.png inner.se outer.se results.dat


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
import java.io.*;
import java.util.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.IntervalTimer;



public class HitAndMiss {


  // Performs hit and miss transform on an image

  public static Collection hitAndMissTransform(BufferedImage image,
   BinaryStructElement inner, BinaryStructElement outer) {

    // Determine range of pixels over which transform is possible

    int w = image.getWidth();
    int h = image.getHeight();
    Point origin = outer.getOrigin(null);
    int xmin = Math.max(origin.x, 0);
    int ymin = Math.max(origin.y, 0);
    int xmax = origin.x + w - outer.getWidth();
    int ymax = origin.y + h - outer.getHeight();
    xmax = Math.min(w-1, xmax);
    ymax = Math.min(h-1, ymax);

    // Fit structuring elements inside and outside the image

    Raster raster = image.getRaster();
    Collection matches = new ArrayList();
    for (int y = ymin; y <= ymax; ++y)
      for (int x = xmin; x <= xmax; ++x)
        if (inner.fits(raster, x, y) && outer.fitsComplement(raster, x, y))
          matches.add(new Point(x, y));

    return matches;

  }


  // Outputs results of hit and miss transform

  public static void writeResults(Collection data, Writer destination) {
    PrintWriter output = new PrintWriter(new BufferedWriter(destination));
    Iterator iterator = data.iterator();
    while(iterator.hasNext()) {
      Point p = (Point) iterator.next();
      output.println(p.x + " " + p.y);
    }
    output.flush();
  }


  public static void main(String[] argv) {
    if (argv.length > 3) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        BufferedImage image = input.decodeAsBufferedImage();
        BinaryStructElement inner =
         new BinaryStructElement(new FileReader(argv[1]));
        BinaryStructElement outer =
         new BinaryStructElement(new FileReader(argv[2]));
        IntervalTimer timer = new IntervalTimer();
        timer.stop();
        Collection data = hitAndMissTransform(image, inner, outer);
        System.out.println(data.size() + " matches found [" +
         timer.stop() + " sec]");
        writeResults(data, new FileWriter(argv[3]));
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java HitAndMiss " +
       "<image> <innerElement> <outerElement> <outfile>");
      System.exit(1);
    }
  }


}
