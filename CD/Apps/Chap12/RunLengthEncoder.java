/***************************************************************************

  RunLengthEncoder.java   Version 1.0 [1999/09/06]

  This program performs reads an image from a file named as the first
  command line argument and applies run length encoding to the pixel
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



public class RunLengthEncoder {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private DataOutputStream outputStream;


  //////////////////////////////// METHODS /////////////////////////////////


  // Creates an encoder that sends data to the specified stream

  public RunLengthEncoder(OutputStream out) {
    outputStream = new DataOutputStream(new BufferedOutputStream(out));
  }


  // Encodes an array of bytes

  public void encode(byte[] data) throws IOException {
    int runLength = 1;
    byte value = data[0];
    for (int i = 1; i < data.length; ++i) {
      if (data[i] == value && runLength < 255)
        ++runLength;
      else {
        outputStream.write(value);
        outputStream.write(runLength);
        runLength = 1;
        value = data[i];
      }
    }
    outputStream.write(value);
    outputStream.write(runLength);
    outputStream.flush();
  }


  ///////////////////////////// STATIC METHODS /////////////////////////////


  // Reads image from a named file and accesses its data buffer

  public static byte[] getData(String filename) throws Exception {
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    BufferedImage image = input.decodeAsBufferedImage();
    int n = image.getWidth()*image.getHeight();
    System.out.println(n + " pixels read");
    DataBufferByte buf = (DataBufferByte) image.getRaster().getDataBuffer();
    return buf.getData();
  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {

        // Acquire image data

        byte[] data = getData(argv[0]);

        if (argv.length > 1) {

          // Encode to a file

          RunLengthEncoder rle =
           new RunLengthEncoder(new FileOutputStream(argv[1]));
          rle.encode(data);

        }
        else {

          // Encode to memory and calculate compression ratio

          ByteArrayOutputStream out = new ByteArrayOutputStream();
          RunLengthEncoder rle = new RunLengthEncoder(out);
          rle.encode(data);
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
      System.err.println("usage: java RunLengthEncoder <image> [<outfile>]");
      System.exit(1);
    }
  }


}
