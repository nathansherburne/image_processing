/***************************************************************************

  StructElementPane.java   Version 1.1 [1999/08/31]

  Extends JPanel to act as a container for a structuring element's pixels.
  See StructElementValue and BinaryMorphologyTool for further details.


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


import java.awt.*;
import javax.swing.*;
import com.pearsoneduc.ip.op.*;



public class StructElementPane extends JPanel {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private int width;                    // width of structuring element
  private int height;                   // height of structuring element
  private StructElementValue[] value;   // structuring element pixel values


  //////////////////////////////// METHODS /////////////////////////////////


  // Creates a StructElementPane with the specified dimensions

  public StructElementPane(int w, int h) {
    width = w;
    height = h;
    int n = width*height;
    value = new StructElementValue[n];
    setLayout(new GridLayout(height, width));
    for (int i = 0; i < n; ++i) {
      add(value[i] = new StructElementValue());
      value[i].setToolTipText("SE pixel value");
    }
  }


  // Copies StructElement data to the values in the pane

  public void setToStructuringElement(BinaryStructElement element) {
    int i = 0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x, ++i)
        value[i].setValue(element.getPixel(x, y));
  }


  // Sets all SE pixels to 1

  public void setPixels() {
    for (int i = 0; i < width*height; ++i)
      value[i].setValue(1);
  }


  // Clears all SE pixels (i.e. makes them all zero)

  public void clearPixels() {
    for (int i = 0; i < width*height; ++i)
      value[i].setValue(0);
  }


  // Return data from the pane in the form of a BinaryStructElement

  public BinaryStructElement getStructuringElement() {
    try {
      BinaryStructElement element = new BinaryStructElement(width, height);
      int[][] pixelData = new int[height][width];
      int i = 0;
      for (int y = 0; y < height; ++y)
        for (int x = 0; x < width; ++x, ++i)
          pixelData[y][x] = value[i].getValue();
      element.setPixels(pixelData);
      return element;
    }
    catch (StructElementException e) {
      return null;
    }
  }


}
