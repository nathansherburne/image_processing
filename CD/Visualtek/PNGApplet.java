/*
 * $Header: /export/CVS/wb/libsrc/java/com/visualtek/PNG/PNGApplet.java,v 1.2 1997/08/28 23:10:55 vlad Exp $
 *
 * $Log: PNGApplet.java,v $
 * Revision 1.2  1997/08/28 23:10:55  vlad
 * DataFormatException doesn't throw up from PNG any more
 *
 * Revision 1.1  1997/08/15 02:05:52  lord
 * PNG Applet created.
 * PNG test suite downloaded.
 *
 */

package com.visualtek.PNG;

import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.net.URL;
import java.io.InputStream;
import com.visualtek.PNG.PNGProducer;

/**
  * This is simple applet class which display given
  * PNG Image
  */
public class PNGApplet extends Applet
{
    Image img=null;

    public PNGApplet()
    {
    }
    
    public void init()
    { 
	try
	{
	    String      src = getParameter("source");
	    URL         url = new URL(src);
	    InputStream inp = url.openStream();
	    PNGProducer p   = new PNGProducer(inp);
	    img=createImage(p);
	}
	catch(java.net.MalformedURLException e)    {System.err.println(e);}
	catch(java.io.IOException e)               {System.err.println(e);}
    }
    
    public void paint(Graphics g)
    {
	if(img!=null)
	    g.drawImage(img,0,0,this);
    }
}
