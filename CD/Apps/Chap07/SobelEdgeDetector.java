/***************************************************************************

  SobelEdgeDetector.java

  This program detects edges by computing gradient magnitude using
  the Sobel kernels and then (optionally) thresholding the result.

  Examples of use:

    java SobelEdgeDetector input.png output.png
    java SobelEdgeDetector input.png output.png 150


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
import com.pearsoneduc.ip.op.SobelEdgeOp;


public class SobelEdgeDetector {
  public static void main(String[] argv) {
    if (argv.length > 1) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        int threshold = -1;
        if (argv.length > 2)
          threshold = Integer.parseInt(argv[2]);
        BufferedImage inputImage = input.decodeAsBufferedImage();
        SobelEdgeOp edgeOp = new SobelEdgeOp(threshold);
        output.encode(edgeOp.filter(inputImage, null));
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java SobelEdgeDetector <infile> <outfile> [<threshold>]");
      System.exit(1);
    }
  }
}
