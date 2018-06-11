/***************************************************************************

  QuantisationSimulator.java

  This program reads a greyscale image and simulates quantisation with
  fewer bits.  The image or any of the requantised versions of it can be
  selected for display from a list.


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
import java.util.Vector;
import javax.swing.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.op.OperationException;



public class QuantisationSimulator extends ImageSelector {


  public QuantisationSimulator(String imageFile)
   throws IOException, ImageDecoderException, OperationException {
    super(imageFile);
  }


  // Checks that we have a greyscale image

  public boolean imageOK() {
    return getSourceImage().getType() == BufferedImage.TYPE_BYTE_GRAY;
  }


  // Requantises the image

  public BufferedImage quantiseImage(int numBits) {
    int n = 8 - numBits;
    float scale = 255.0f / (255 >> n);
    byte[] tableData = new byte[256];
    for (int i = 0; i < 256; ++i)
      tableData[i] = (byte) Math.round(scale*(i >> n));
    LookupOp lookup =
     new LookupOp(new ByteLookupTable(0, tableData), null);
    BufferedImage result = lookup.filter(getSourceImage(), null);
    return result;
  }


  // Creates versions of the image simulating quantisation with fewer bits

  public Vector generateImages() {

    Vector quantisation = new Vector();
    int width = getSourceImage().getWidth();
    int height = getSourceImage().getHeight();

    int levels = 2;
    for (int n = 1; n < 8; ++n, levels *= 2) {
      String key = Integer.toString(levels) + " levels";
      quantisation.addElement(key);
      addImage(key, new ImageIcon(quantiseImage(n)));
    }

    quantisation.addElement("256 levels");
    addImage("256 levels", new ImageIcon(getSourceImage()));

    return quantisation;

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new QuantisationSimulator(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
	System.err.println(e);
	System.exit(1);
      }
    }
    else {
      System.err.println("java QuantisationSimulator <imagefile>");
      System.exit(1);
    }
  }


}
