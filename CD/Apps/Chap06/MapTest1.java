/***************************************************************************

  MapTest1.java

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



import java.util.Random;
import com.pearsoneduc.ip.util.IntervalTimer;



public class MapTest1 {


  public static void randomFill(short[] array) {
    Random random = new Random();
    for (int i = 0; i < array.length; ++i)
      array[i] = (short) random.nextInt(256);
  }


  public static void main(String[] argv) {

    int n = 512;
    if (argv.length > 0)
      n = Integer.parseInt(argv[0]);

    // Create image and fill it with random values

    int numPixels = n*n;
    short[] image = new short[numPixels];
    randomFill(image);

    // Perform the mapping directly

    IntervalTimer timer = new IntervalTimer();
    timer.start();
    for (int i = 0; i < numPixels; ++i)
      image[i] = (short) Math.round(Math.sqrt(image[i]));
    System.out.println("direct calculation: " + timer.stop() + " sec");

    // Perform the mapping with a lookup table

    randomFill(image);
    timer.start();
    short[] table = new short[256];
    for (int i = 0; i < 256; ++i)
      table[i] = (short) Math.round(Math.sqrt(i));
    for (int i = 0; i < numPixels; ++i)
      image[i] = table[image[i]];
    System.out.println("lookup table: " + timer.stop() + " sec");

    System.exit(0);

  }


}
