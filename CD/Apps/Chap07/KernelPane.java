/***************************************************************************

  KernelPane.java   Version 1.0 [1999/07/27]

  Extends JPanel to provide a facility to display and edit convolution
  kernel coefficients.  See ConvolutionTool.java for further details.


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
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import com.pearsoneduc.ip.op.*;



public class KernelPane extends JPanel {


  /////////////////////////// INSTANCE VARIABLES ///////////////////////////


  private int width;             // width of kernel
  private int height;            // height of kernel
  private JTextField[] coeff;    // kernel coefficients


  //////////////////////////////// METHODS /////////////////////////////////


  // Constructs a KernelPane with the specified dimensions

  public KernelPane(int w, int h) {

    width = w;
    height = h;
    coeff = new JTextField[w*h];

    GridBagLayout layout = new GridBagLayout();
    setLayout(layout);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridwidth = constraints.gridheight = 1;
    constraints.weightx = constraints.weighty = 0.0;
    constraints.insets = new Insets(2, 2, 2, 2);
    Font fixedFont = new Font("Monospaced", Font.PLAIN, 12);
    int i = 0;
    for (int y = 0; y < height; ++y) {
      constraints.gridy = y;
      for (int x = 0; x < width; ++x, ++i) {
        constraints.gridx = x;
        coeff[i] = new JTextField();
        coeff[i].setText("   ");
        coeff[i].setFont(fixedFont);
        coeff[i].setPreferredSize(coeff[i].getPreferredSize());
        coeff[i].setText("");
        coeff[i].setHorizontalAlignment(JTextField.RIGHT);
        layout.setConstraints(coeff[i], constraints);
        add(coeff[i]);
      }
    }

  }


  // Validates a coefficient and returns it as an integer value

  public int getCoeff(int i) {
    try {
      return Integer.parseInt(coeff[i].getText());
    }
    catch (Exception e) {
      coeff[i].setText("0");
      return 0;
    }
  }


  // Sets a coefficient field to an integer value

  public void setCoeff(int i, int value) {
    coeff[i].setText(String.valueOf(value));
  }


  // Resets kernel to the identity kernel (1 at centre, surrounded by 0)

  public void reset() {
    for (int i = 0; i < coeff.length; ++i)
      coeff[i].setText("0");
    int x = (width-1)/2;
    int y = (height-1)/2;
    int centre = y*width+x;
    coeff[centre].setText("1");
  }


}
