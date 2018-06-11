/***************************************************************************

  StructElementValue.java   Version 1.0 [1999/08/30]

  Extends JPanel to act as a container for the value of a single pixel
  in a BinaryStructElement.  See StructElementPane for further details.


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
import javax.swing.*;
import javax.swing.border.*;



public class StructElementValue extends JPanel {


  private static Font font = new Font("Monospaced", Font.BOLD, 12);
  private JLabel value = new JLabel("1", JLabel.CENTER);


  // Creates a StructElementValue representing a
  // single pixel of a structuring element

  public StructElementValue() {
    value.setVerticalAlignment(JLabel.CENTER);
    value.setFont(font);
    value.setForeground(Color.black);
    value.setBorder(BorderFactory.createCompoundBorder(
     BorderFactory.createEtchedBorder(),
     BorderFactory.createEmptyBorder(1, 3, 1, 3)));
    add(value);
    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent event) { toggleValue(); }
    });
  }


  // Toggles value between 0 and 1

  public void toggleValue() {
    if (value.getText().equals("1"))
      value.setText("0");
    else
      value.setText("1");
  }


  // Retrieves current value and returns it in integer form

  public int getValue() {
    return Integer.parseInt(value.getText());
  }


  // Sets current value to 0 or 1

  public void setValue(int n) {
    value.setText(n != 0 ? "1" : "0");
  }


}
