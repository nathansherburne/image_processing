/***************************************************************************

  MapTest2.java

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
import java.util.Random;
import com.pearsoneduc.ip.util.IntervalTimer;



public class MapTest2 {


  public static void randomFill(WritableRaster raster) {
    Random randomValue = new Random();
    for (int y = 0; y < raster.getHeight(); ++y)
      for (int x = 0; x < raster.getWidth(); ++x)
        raster.setSample(x, y, 0, randomValue.nextInt(256));
  }


  public static void main(String[] argv) {

    int n = 512;
    if (argv.length > 0)
      n = Integer.parseInt(argv[0]);

    // Create image and fill it with random values

    BufferedImage image =
     new BufferedImage(n, n, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = image.getRaster();
    randomFill(raster);

    // Perform the mapping directly

    IntervalTimer timer = new IntervalTimer();
    timer.start();
    int value;
    for (int y = 0; y < n; ++y)
      for (int x = 0; x < n; ++x) {
        value = (int) Math.round(Math.sqrt(raster.getSample(x, y, 0)));
        raster.setSample(x, y, 0, value);
      }
    System.out.println("direct calculation: " + timer.stop() + " sec");

    // Perform the mapping with a lookup table

    randomFill(raster);
    timer.start();
    int[] table = new int[256];
    for (int i = 0; i < 256; ++i)
      table[i] = (int) Math.round(Math.sqrt(i));
    for (int y = 0; y < n; ++y)
      for (int x = 0; x < n; ++x) {
        value = table[raster.getSample(x, y, 0)];
        raster.setSample(x, y, 0, value);
      }
    System.out.println("lookup table: " + timer.stop() + " sec");

    System.exit(0);

  }


}
