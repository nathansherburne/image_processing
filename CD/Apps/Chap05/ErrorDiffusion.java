/***************************************************************************

  ErrorDiffusion.java

  This program reads an image from a file named on the command line and
  halftones it using Floyd and Steinberg's error diffusion algorithm.
  The original and halftoned images are displayed in a tabbed pane,
  allowing them to be compared easily.


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
import java.io.IOException;
import javax.swing.JFrame;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.ImageDecoderException;
import com.pearsoneduc.ip.op.OperationException;



public class ErrorDiffusion extends OperationViewer {


  public ErrorDiffusion(String imageFile)
   throws IOException, ImageDecoderException, OperationException {
    super(imageFile);
    setTitle("ErrorDiffusion: " + imageFile);
  } 


  // Checks that we have a greyscale image

  public boolean imageOK() {
    return inputImage.getType() == BufferedImage.TYPE_BYTE_GRAY;
  }


  // Performs Floyd-Steinberg error diffusion

  public void processImage() {

    // Create a binary image for the results of processing

    int w = inputImage.getWidth()-1;
    int h = inputImage.getHeight()-1;
    outputImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

    // Work on a copy of input image because it is modified by diffusion

    WritableRaster input = inputImage.copyData(null);
    WritableRaster output = outputImage.getRaster();
    final int threshold = 128;
    int value, error;

    for (int y = 0; y < h; ++y)
      for (int x = 0; x < w; ++x) {

        value = input.getSample(x, y, 0);

        // Threshold value and compute error

        if (value < threshold) {
          output.setSample(x, y, 0, 0);
          error = value;
        }
        else {
          output.setSample(x, y, 0, 1);
          error = value - 255;
        }

        // Spread error amongst neighbouring pixels

        value = input.getSample(x+1, y, 0);
        input.setSample(x+1, y, 0, clamp(value + 0.4375f * error));
        value = input.getSample(x-1, y+1, 0);
        input.setSample(x-1, y+1, 0, clamp(value + 0.1875f * error));
        value = input.getSample(x, y+1, 0);
        input.setSample(x, y+1, 0, clamp(value + 0.3125f * error));
        value = input.getSample(x+1, y+1, 0);
        input.setSample(x+1, y+1, 0, clamp(value + 0.0625f * error));

      }

  }


  // Forces a value to a 0-255 integer range

  public static int clamp(float value) {
    return Math.min(Math.max(Math.round(value), 0), 255);
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new ErrorDiffusion(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java ErrorDiffusion <imagefile>");
      System.exit(1);
    }
  }


}
