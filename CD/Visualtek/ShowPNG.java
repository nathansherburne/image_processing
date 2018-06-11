/*
 * $Header: /export/CVS/wb/libsrc/java/com/visualtek/PNG/ShowPNG.java,v 1.2 1997/08/28 23:10:56 vlad Exp $
 *
 * $Log: ShowPNG.java,v $
 * Revision 1.2  1997/08/28 23:10:56  vlad
 * DataFormatException doesn't throw up from PNG any more
 *
 * Revision 1.1  1997/08/15 02:42:28  lord
 * ShowPNG implemented.
 *
 */

package com.visualtek.PNG;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Panel;
import java.io.FileInputStream;
import com.visualtek.PNG.PNGProducer;

/**
  * Simple PNG file viewer
  */
public class ShowPNG
{
    static public void main(String[] args)
    {
	if(args.length < 1)
        {
            System.err.println("Usage: ShowPNG <file.cfg>");
            return;
        }

	Frame main = new Frame();
	PNGPanel p=new PNGPanel(args[0]);
	main.add(p);
	main.resize(p.width,p.height);
	main.show();
    }
}

class PNGPanel extends Panel
{
    Image img=null;
    public int              width;
    public int              height;
    
    PNGPanel(String filename)
    { 
	try
	{
	    FileInputStream inp = new FileInputStream(filename);
	    PNGProducer p   = new PNGProducer(inp);
	    width  = p.width  ;
	    height = p.height ;
	    img=createImage(p);
	}
	catch(java.io.IOException e)               {System.err.println(e);}
    }
    
    public void paint(Graphics g)
    {
	if(img!=null)
	    g.drawImage(img,0,0,this);
    }
}
 
