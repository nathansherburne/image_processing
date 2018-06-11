/***************************************************************************

  RankFilterTool.java   Version 1.0 [1999/07/25]

  This program displays an image and a rank filtered version of that
  image.  The rank filter has a 3x3 neighbourhood.  Its rank can be
  selected using a group of radio buttons; the default is 5 - i.e., a
  median filter.


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
import java.io.IOException;
import javax.swing.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.op.*;



public class RankFilterTool extends JFrame {


  private BufferedImage inputImage;
  private ImageView filteredView;


  // Inner class providing a control panel for the application

  class Controls extends JPanel implements ActionListener {

    private BufferedImageOp[] op;   // operations associated with each button

    public Controls() {
      op = new BufferedImageOp[9];
      JRadioButton[] rank = new JRadioButton[9];
      final ButtonGroup group = new ButtonGroup();
      setLayout(new GridLayout(10, 1));
      add(new JLabel("Rank"));
      for (int i = 0; i < 9; ++i) {
        op[i] = new RankFilterOp(i+1, 3, 3);
        rank[i] = new JRadioButton(String.valueOf(i+1), false);
        rank[i].addActionListener(this);
        group.add(rank[i]);
        add(rank[i]);
      }
      rank[4].setSelected(true);   // median
    }

    public void actionPerformed(ActionEvent event) {
      int n = Integer.parseInt(event.getActionCommand());
      filteredView.setImage(op[n-1].filter(inputImage, null));
      filteredView.repaint();
    }

  }


  public RankFilterTool(String filename)
   throws IOException, ImageDecoderException {

    // Load image and create display components

    super("RankFilterTool: " + filename);
    ImageDecoder input = ImageFile.createImageDecoder(filename);
    inputImage = input.decodeAsBufferedImage();
    ImageView inputView = new ImageView(inputImage);
    BufferedImageOp op = new RankFilterOp(5, 3, 3);   // median filter
    filteredView = new ImageView(op.filter(inputImage, null));

    // Add display components and control panel to frame

    JPanel pane = new JPanel();
    pane.setLayout(new FlowLayout());
    pane.add(new JScrollPane(inputView));
    pane.add(new Controls());
    pane.add(new JScrollPane(filteredView));
    setContentPane(pane);
    addWindowListener(new WindowMonitor());

  }


  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
        JFrame frame = new RankFilterTool(argv[0]);
        frame.pack();
        frame.setVisible(true);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
    else {
      System.err.println("usage: java RankFilterTool <imagefile>");
      System.exit(1);
    }
  }


}
