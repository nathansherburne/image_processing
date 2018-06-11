/***************************************************************************

  LogPolarInfo.java

  Creates and maintains an information panel used by the log-polar
  simulator application - see LogPolar.java for further information.


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



public class LogPolarInfo extends JPanel {


  private JLabel rLabel = new JLabel();
  private JLabel thetaLabel = new JLabel();
  private JLabel xLabel = new JLabel();
  private JLabel yLabel = new JLabel();

  private DecimalFormat realValue = new DecimalFormat("000.000");
  private DecimalFormat intValue = new DecimalFormat("000");


  public LogPolarInfo() {

    JPanel polarPane = new JPanel();
    polarPane.add(new JLabel("r"));
    Font fixedFont = new Font("Monospaced", Font.BOLD, 12);
    rLabel.setFont(fixedFont);
    rLabel.setForeground(Color.black);
    polarPane.add(rLabel);
    polarPane.add(new JLabel("theta"));
    thetaLabel.setFont(fixedFont);
    thetaLabel.setForeground(Color.black);
    polarPane.add(thetaLabel);
    polarPane.setBorder(BorderFactory.createEtchedBorder());
    add(polarPane);

    JPanel cartesianPane = new JPanel();
    cartesianPane.add(new JLabel("x"));
    xLabel.setFont(fixedFont);
    xLabel.setForeground(Color.black);
    cartesianPane.add(xLabel);
    cartesianPane.add(new JLabel("y"));
    yLabel.setFont(fixedFont);
    yLabel.setForeground(Color.black);
    cartesianPane.add(yLabel);
    cartesianPane.setBorder(BorderFactory.createEtchedBorder());
    add(cartesianPane);

    clearDisplay();

  }


  public void display(float r, float theta, int x, int y) {
    rLabel.setText(realValue.format(r));
    thetaLabel.setText(realValue.format(theta));
    xLabel.setText(intValue.format(x));
    yLabel.setText(intValue.format(y));
  }


  public void clearDisplay() {
    rLabel.setText("---.---");
    thetaLabel.setText("---.---");
    xLabel.setText("---");
    yLabel.setText("---");
  }


}
