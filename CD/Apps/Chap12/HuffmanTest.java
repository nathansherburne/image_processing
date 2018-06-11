/***************************************************************************

  HuffmanTest.java

  Compresses an image using a Deflater with the compression strategy
  set to HUFFMAN_ONLY.  The compressed data are written to memory; the
  program simply checks how many bytes were written and computes
  the compression ratio, writing this information to standard output.


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
import java.io.*;
import java.text.DecimalFormat;
import java.util.zip.*;
import com.pearsoneduc.ip.io.*;



public class HuffmanTest {


  public static byte[] getData(BufferedImage image) {
    DataBufferByte buf = (DataBufferByte) image.getRaster().getDataBuffer();
    return buf.getData();
  }


  public static Deflater getHuffmanCoder() {
    Deflater deflater = new Deflater();
    deflater.setStrategy(Deflater.HUFFMAN_ONLY);
    return deflater;
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {

        ImageDecoder input = ImageFile.createImageDecoder(argv[0]);
        BufferedImage image = input.decodeAsBufferedImage();
        byte[] data = getData(image);
        System.out.println(data.length + " bytes acquired");

        Deflater huffman = getHuffmanCoder();
        DeflaterOutputStream output =
         new DeflaterOutputStream(new ByteArrayOutputStream(), huffman);
        output.write(data, 0, data.length);
        output.finish();

        int n = huffman.getTotalOut();
        System.out.println(n + " bytes written");
        float ratio = 1.0f*data.length / n;
        DecimalFormat number = new DecimalFormat("0.000");
        System.out.println("Compression ratio = " + number.format(ratio));

      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java HuffmanTest <infile>");
      System.exit(1);
    }
  }


}
