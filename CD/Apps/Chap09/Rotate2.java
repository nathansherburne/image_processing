/***************************************************************************

  Rotate2.java   Version 1.0 [1999/08/13]

  This application reads an image from a file, rotates it and writes the
  rotated image to a new file.  Translation and padding of the output
  image are done to ensure that all of the rotated image is visible.
  Input and output filenames are specified on the command line, along with
  interpolation order and the rotation angle (in degrees, with positive
  values signifying clockwise rotation).  An interpolation order of 0
  signifies zero-order interpolation; 1 (actually, any value greater
  than 0) signifies first-order interpolation.

  Example of use:

    java Rotate2 original.jpg rotated.jpg 1 30.0


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
import java.awt.geom.*;
import java.awt.image.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.util.*;


public class Rotate2 {

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

        // Find bounding box of the rotated image and use it to modify the
        // transformation so that all of the rotated image is visible

        AffineTransform rotation = AffineTransform.getRotateInstance(angle);
        Rectangle2D box = GeomTools.getBoundingBox(image, rotation);
        AffineTransform translation =
         AffineTransform.getTranslateInstance(
          -box.getMinX()+2, -box.getMinY()+2);
        rotation.preConcatenate(translation);

        // Use bounding box to determine size of output image

        int width = (int) Math.round(box.getWidth()) + 5;
        int height = (int) Math.round(box.getHeight()) + 5;

        // Perform rotation and write rotated image to output file

        BufferedImageOp rotationOp =
         new AffineTransformOp(rotation, interpolation);
        BufferedImage rotatedImage =
         new BufferedImage(width, height, image.getType());
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        rotationOp.filter(image, rotatedImage);
        System.out.println("Rotation finished [" + timer.stop() + " sec]");
        output.encode(rotatedImage);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Rotate2 " +
       "<infile> <outfile> <interp> <angle>");
      System.exit(1);
    }
  }

}
