/***************************************************************************

  GreyRescale.java

  This program remaps grey levels linearly using the supplied scale
  factor and offset.

  Example of use:

    java GreyRescale dark.png bright.png 1.5 20


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


public class GreyRescale {
  public static void main(String[] argv) {
    if (argv.length > 3) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        float scale = Float.valueOf(argv[2]).floatValue();
        float offset = Float.valueOf(argv[3]).floatValue();
        BufferedImage image = input.decodeAsBufferedImage();
        RescaleOp rescale = new RescaleOp(scale, offset, null);
        output.encode(rescale.filter(image, null));
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java GreyRescale <infile> <outfile> <scale> <offset>");
      System.exit(1);
    }
  }
}
