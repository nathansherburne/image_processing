/*/***************************************************************************

  ResolutionSimulator.java

  This program reads a greyscale image and simulates lower-resolution
  views of the same scene by averaging over blocks of pixels.  The
  image is displayed and lower-resolution views can be be selected from
  a list of possibilities.  The image must be square and with dimensions
  that are a 'reasonable' power of two.


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
*/


import java.awt.image.*;
import java.io.IOException;
import java.util.Vector;
import javax.swing.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.op.OperationException;



public class ResolutionSimulator extends ImageSelector {

//	imageFile ImageIO.read(new File("C:\\Users\\karan\\Desktop\\MyPic.jpg
  public ResolutionSimulator(String imageFile)
   throws IOException, ImageDecoderException, OperationException {
    super(imageFile);
  }


  // Checks that the image is suitable for simulation

  public boolean imageOK() {

    // Must be grey...

    if (getSourceImage().getType() != BufferedImage.TYPE_BYTE_GRAY)
      return false;

    // Must be square...

    int width = getSourceImage().getWidth();
    if (width != getSourceImage().getHeight())
      return false;

    // ...with dimensions a 'reasonable' power of 2

    int[] dim = { 64, 128, 256, 512 };
    for (int i = 0; i < dim.length; ++i)
      if (width == dim[i])
        return true;
    return false;
 
  }


  // Averages over blocks of pixels to simulate reduced resolution

  public BufferedImage blockAverage(int blockSize) {

    int width = getSourceImage().getWidth();
    int height = getSourceImage().getHeight();
    BufferedImage destImage =
     new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    Raster src = getSourceImage().getRaster();
    WritableRaster dest = destImage.getRaster();
    float sum;
    int average, blockArea = blockSize*blockSize;
    for (int y = 0; y < height; y += blockSize)
      for (int x = 0; x < width; x += blockSize) {
        sum = 0.0f;
        for (int j = 0; j < blockSize; ++j)
          for (int i = 0; i < blockSize; ++i)
            sum += src.getSample(x+i, y+j, 0);
        average = Math.round(sum/blockArea);
        for (int j = 0; j < blockSize; ++j)
          for (int i = 0; i < blockSize; ++i)
            dest.setSample(x+i, y+j, 0, average);
      }

    return destImage;

  }


  // Creates simulated views of an image at different resolutions

  public Vector generateImages() {

    Vector resolutions = new Vector();
    int width = getSourceImage().getWidth();
    int height = getSourceImage().getHeight();

    // Generate reduced resolution versions of source image

    for (int n = 2; n < width; n *= 2) {
      String key = Integer.toString(n) + "x" + Integer.toString(n);
      System.out.println(key + "...");
      resolutions.addElement(key);
      addImage(key, new ImageIcon(blockAverage(width/n)));
    }

    // Add full resolution image

    String key = Integer.toString(width) + "x" + Integer.toString(height);
    resolutions.addElement(key);
    addImage(key, new ImageIcon(getSourceImage()));

    return resolutions;

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new ResolutionSimulator(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
	System.err.println(e);
	System.exit(1);
      }
    }
    else {
      System.err.println("usage: java ResolutionSimulator <imagefile>");
      System.exit(1);
    }
  }


}
