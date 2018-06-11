/***************************************************************************

  ForwardRotation.java

  Demonstrates the flaws in performing a geometric transformation of
  an image using the forward mapping approach.  Command line arguments
  are an input image, an output image and an angle (in degrees).

  Example of use:

    java ForwardRotation input.png output.png 40.0


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


import java.awt.image.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.util.IntervalTimer;


public class ForwardRotation {


  public static BufferedImage rotate(BufferedImage input, double angle) {

    int width = input.getWidth();
    int height = input.getHeight();
    BufferedImage output = new BufferedImage(width, height, input.getType());

    double a0 = Math.cos(angle*Math.PI/180.0);
    double b0 = Math.sin(angle*Math.PI/180.0);
    double a1 = -b0, b1 = a0;

    IntervalTimer timer = new IntervalTimer();
    timer.start();
    int rx, ry, n = 0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x) {
        rx = (int) Math.round(a0*x + a1*y);
        ry = (int) Math.round(b0*x + b1*y);
        if (rx >= 0 && rx < width && ry >= 0 && ry < height)
          output.setRGB(rx, ry, input.getRGB(x, y));
        else
          ++n;
      }
    System.out.println("Rotation complete [" + timer.stop() + " sec]");
    System.out.println(n + " pixels could not be mapped");

    return output;

  }


  public static void main(String[] argv) {
    if (argv.length > 2) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        double angle = Double.valueOf(argv[2]).doubleValue();
        BufferedImage inputImage = input.decodeAsBufferedImage();
        output.encode(rotate(inputImage, angle));
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java ForwardRotation " +
       "<infile> <outfile> <angle>");
      System.exit(1);
    }
  }


}
