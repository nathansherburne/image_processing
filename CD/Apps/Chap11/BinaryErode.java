/***************************************************************************

  BinaryErode.java   Version 1.0 [1999/08/30]

  This program reads a binary image from one file and a structuring
  element from another file, erodes the image using that structuring
  element and writes the eroded image to a new file.  'Binary image',
  in this context, means either a true binary image or a greyscale
  image with two levels, one of which is zero.

  Example of use:

    java BinaryErode binary.png eroded.png disk.se


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
import java.io.FileReader;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;
import com.pearsoneduc.ip.util.IntervalTimer;


public class BinaryErode {
  public static void main(String[] argv) {
    if (argv.length > 2) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        ImageEncoder output = ImageFile.createImageEncoder(argv[1]);
        BufferedImage inputImage = input.decodeAsBufferedImage();
        BinaryStructElement structElement =
         new BinaryStructElement(new FileReader(argv[2]));
        BufferedImageOp erodeOp = new BinaryErodeOp(structElement);
        IntervalTimer timer = new IntervalTimer();
        timer.start();
        BufferedImage outputImage = erodeOp.filter(inputImage, null);
        System.out.println("Erosion finished [" + timer.stop() + " sec]");
        output.encode(outputImage);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java BinaryErode " +
       "<infile> <outfile> <structElement>");
      System.exit(1);
    }
  }
}
