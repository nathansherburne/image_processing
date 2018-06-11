/***************************************************************************

  CalcHist.java   Version 1.0 [1999/07/11]

  This program computes the histogram of an image loaded from a file
  specified on the command line and writes the histogram to a new file
  (also specified on the command line).  If a third filename is
  specified, then the cumulative histogram is written to that file.


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


import java.awt.image.BufferedImage;
import java.io.FileWriter;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.Histogram;


public class CalcHist {
  public static void main(String[] argv) {
    if (argv.length > 1) {
      try {
        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        BufferedImage image = input.decodeAsBufferedImage();
        Histogram histogram = new Histogram(image);
        FileWriter histFile = new FileWriter(argv[1]);
        histogram.write(histFile);
        if (argv.length > 2) {
          FileWriter cumHistFile = new FileWriter(argv[2]);
          histogram.writeCumulative(cumHistFile);
        }
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println(
       "usage: java CalcHist <imageFile> <histFile> [<cumHistFile>]");
      System.exit(1);
    }
  }
}
