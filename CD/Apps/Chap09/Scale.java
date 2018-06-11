/***************************************************************************

  Scale.java   Version 1.0 [1999/08/13]

  This program reads an image from a file, scales it in the x and y
  directions and writes the scaled image to a new file.  Input and
  output filenames are specified on the command line, along with the
  interpolation scheme and one or two scaling factors.  If one factor
  is specified, uniform scaling is carried out; if two are specified,
  the first is the scale factor in the x direction and the second the
  scale factor in the y direction.  Interpolation is an integer
  parameter.  A value of 0 specifies zero-order interpolation; any
  value greater than 0 specifies first-order interpolation.

  Examples of use:

    java Scale small.jpg large.jpg 0 2.5
    java Scale normal.png stretched.png 1 3.0 1.5


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


public class Scale {

  public static final double parseDouble(String s) {
    return Double.valueOf(s).doubleValue();
  }

  public static void main(String[] argv) {
    if (argv.length > 3) {
      try {

        // Parse command line arguments

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int interpolation = (Integer.parseInt(argv[2]) > 0 ?
         AffineTransformOp.TYPE_BILINEAR :
         AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        double sx = parseDouble(argv[3]), sy;
        if (argv.length > 4)
          sy = parseDouble(argv[4]);
        else
          sy = sx;

        // Load input image and create output image of an appropriate size

        BufferedImage inputImage = input.decodeAsBufferedImage();
        int width = (int) Math.round(sx*inputImage.getWidth());
        int height = (int) Math.round(sy*inputImage.getHeight());
        BufferedImage outputImage =
         new BufferedImage(width, height, inputImage.getType());

        // Create a transformation with the required scale
        // factors and interpolation scheme

        BufferedImageOp scaleOp = new AffineTransformOp(
         AffineTransform.getScaleInstance(sx, sy), interpolation);

        // Perform scaling and write scaled image to output file

        IntervalTimer timer = new IntervalTimer();
        timer.start();
        scaleOp.filter(inputImage, outputImage);
        System.out.println("Scaling finished [" + timer.stop() + " sec]");
        output.encode(outputImage);

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java Scale <infile> <outfile> <interp> <factor>\n" +
       "       java Scale <infile> <outfile> <interp> <xfactor> <yfactor>");
      System.exit(1);
    }
  }

}
