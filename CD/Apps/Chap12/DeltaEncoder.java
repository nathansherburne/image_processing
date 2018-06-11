/***************************************************************************

  DeltaEncoder.java   Version 1.0 [1999/09/03]

  This program performs reads an image from a file named as the first
  command line argument and applies delta compression to the pixel
  values of that image.  If a second filename is supplied on the command
  line, the compressed datastream is written to that file; otherwise,
  the datastream is written to memory and the compression ratio is
  calculated.


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
import java.text.*;
import com.pearsoneduc.ip.io.*;



public class DeltaEncoder {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private BufferedOutputStream outputStream;  // destination for encoded data
  private int previous;                       // stores previous four bits
  private boolean stored;                     // flags storage of bits


  //////////////////////////////// METHODS /////////////////////////////////


  // Creates an encoder that sends data to the specified stream

  public DeltaEncoder(OutputStream out) {
    outputStream = new BufferedOutputStream(out);
  }


  // Encodes an array of integer values

  public void encode(int[] data) throws IOException {
    int last = data[0];
    outputStream.write(last);
    int delta;
    for (int i = 1; i < data.length; ++i) {
      delta = data[i] - last;
      if (delta < -7 || delta > 7) {  // delta too large, so...
        writeBits(8);                 // signal that value is not a delta...
        outputStream.write(data[i]);  // and write full 8 bits to stream
      }
      else if (delta >= 0)
        writeBits(delta);
      else
        writeBits(8 | -delta);        // use 4th bit to flag negative delta
      last = data[i];
    }
    flushBits();
  }


  // Writes the four low-order bits of the given value to the output stream

  private void writeBits(int value) throws IOException {
    if (stored) {
      outputStream.write(previous | (value & 0xf));
      stored = false;
    }
    else {
      previous = value << 4;
      stored = true;
    }
  }


  // Writes any outstanding data to output stream and flushes it

  private void flushBits() throws IOException {
    if (stored)
      outputStream.write(previous);
    outputStream.flush();
  }


  ///////////////////////////// STATIC METHODS /////////////////////////////


  // Reads image data from a named file into an array of ints

  public static int[] getData(String filename) throws Exception {
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    BufferedImage image = input.decodeAsBufferedImage();
    Raster raster = image.getRaster();
    int w = image.getWidth();
    int h = image.getHeight();
    int n = w*h;
    System.out.println(n + " pixels read");
    int[] data = new int[n];
    int i = 0;
    for (int y = 0; y < h; ++y)
      for (int x = 0; x < w; ++x, ++i)
        data[i] = raster.getSample(x, y, 0);
    return data;
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {

        // Acquire image data

        int[] data = getData(argv[0]);

        if (argv.length > 1) {

          // Encode to a file

          DeltaEncoder delta =
           new DeltaEncoder(new FileOutputStream(argv[1]));
          delta.encode(data);

        }
        else {

          // Encode to memory and calculate compression ratio

          ByteArrayOutputStream out = new ByteArrayOutputStream();
          DeltaEncoder delta = new DeltaEncoder(out);
          delta.encode(data);
          float ratio = (float) data.length / out.size();
          System.out.println(out.size() + " bytes written");
          NumberFormat number = new DecimalFormat("0.000");
          System.out.println("Compression ratio = " + number.format(ratio));

        }
        System.exit(0);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java DeltaEncoder <image> [<outfile>]");
      System.exit(1);
    }
  }


}
