/***************************************************************************

  Rotate1.java  Version 1.0 [1999/08/13]

  This program reads an image from a file, rotates it and writes the
  rotated image to a new file.  The output image will contain those parts
  of the input image that are still in the +x +y quadrant after rotation.
  The input file and output file are specified on the command line, along
  with interpolation order and rotation angle.  The latter is in degrees,
  with positive values signifying clockwise rotation.  Interpolation
  order is 0, signifying zero-order interpolation, or 1, signifying
  first-order interpolation.  (Actually, any value greater than 0 will
  specify first-order interpolation.)

  Example of use:

    java Rotate1 input.png output.png 0 62.5


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


import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.util.IntervalTimer;


public class Rotate1 {

  public static void main(String[] argv) {
    if (argv.length > 3) {
      try {

        // Parse command line and load input image

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        BufferedImage image = input.decodeAsBufferedImage();
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int interpolation = (Integer.parseInt(argv[2]) > 0 ?
         AffineTransformOp.TYPE_BILINEAR :
         AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        double angle = Double.valueOf(argv[3]).doubleValue()*Math.PI/180.0;
        double x, y;
        if (argv.length > 5) {
          // rotate about specified point
          x = Double.valueOf(argv[4]).doubleValue();
          y = Double.valueOf(argv[5]).doubleValue();
        }
        else {
          // rotate about image centre
          x = image.getWidth()/2.0;
          y = image.getHeight()/2.0;
        }

        // Create transformation object

        AffineTransform rotation =
         AffineTransform.getRotateInstance(angle, x, y);
        BufferedImageOp rotationOp =
         new AffineTransformOp(rotation, interpolation);

        // Perform rotation and write rotated image to output file

        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage rotatedImage = rotationOp.filter(image, null);
        System.out.println("Rotation finished [" + timer.stop() + " sec]");
        output.encode(rotatedImage);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Rotate1 " +
       "<infile> <outfile> <interp> <angle> [<x> <y>]");
      System.exit(1);
    }
  }

}
