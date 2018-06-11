/***************************************************************************

  MeanInfo.java

  Creates and maintains an information panel used by a program that
  displays mean grey level within interactively defined regions of
  interest - see MeanROI.java for further details.


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


import java.text.DecimalFormat;
import java.awt.*;
import javax.swing.*;


public class MeanInfo extends JPanel {


  private JLabel imageLabel = new JLabel();
  private JLabel regionLabel = new JLabel();
  private DecimalFormat realValue = new DecimalFormat("000.000");


  public MeanInfo(double imageMean) {

    JPanel imagePane = new JPanel();
    imagePane.add(new JLabel("image: "));
    Font fixedFont = new Font("Monospaced", Font.BOLD, 12);
    imageLabel.setFont(fixedFont);
    imageLabel.setForeground(Color.black);
    imageLabel.setText(realValue.format(imageMean));
    imagePane.add(imageLabel);
    imagePane.setBorder(BorderFactory.createEtchedBorder());
    add(imagePane);

    JPanel regionPane = new JPanel();
    regionPane.add(new JLabel("ROI: "));
    regionLabel.setFont(fixedFont);
    regionLabel.setForeground(Color.black);
    regionPane.add(regionLabel);
    regionPane.setBorder(BorderFactory.createEtchedBorder());
    add(regionPane);

    clearDisplay();

  }


  public void display(double mean) {
    regionLabel.setText(realValue.format(mean));
  }


  public void clearDisplay() {
    regionLabel.setText("---.---");
  }


}
