/*
 * $Id: PNGProducer.java,v 1.7 1997/09/02 20:45:30 lord Exp $
 *
 * $Log: PNGProducer.java,v $
 * Revision 1.7  1997/09/02 20:45:30  lord
 * workaround for IE3.0. Ditry hack
 *
 * Revision 1.6  1997/08/28 23:10:56  vlad
 * DataFormatException doesn't throw up from PNG any more
 *
 * Revision 1.5  1997/08/21 02:00:13  vlad
 * Some Pointer related bugs have been fixed
 *
 * Revision 1.4  1997/08/13 01:53:56  lord
 * PNGProducer moved back to PNG package.
 *
 * Revision 1.1  1997/08/13 01:05:44  lord
 * move some classes from PNG to Cubes
 *
 * Revision 1.1  1997/08/05 17:53:31  lord
 * PNG code added. JDK-1.1.3 is now used
 *
 */

package com.visualtek.PNG;

import java.awt.image.*;
import java.io.*;
import java.util.Hashtable;
import java.util.zip.*;
import com.visualtek.PNG.PNGException;
import com.visualtek.PNG.PNGInfo;
import com.visualtek.PNG.PNGData;
import com.visualtek.PNG.PNGDataDecoder;

 /**
  *
  */
public class PNGProducer implements ImageProducer
{
    public int              width;
    public int              height;
    public int              pixels[];

    private ColorModel      colorModel = ColorModel.getRGBdefault(); 
    private ImageConsumer   theConsumer;
    private boolean initialised=false;
    
    /**
      * Constructs an ImageProducer object.
      */
    public PNGProducer () throws PNGException,
	IOException
    {
	initialised=false;
    }
    
    /**
      * Constructs an ImageProducer object from an PNG bitmap
      * to produce data for an Image object.
      * @param istream PNG input stream
      */
    public PNGProducer ( InputStream istream ) throws PNGException,
	IOException
    {
	init(istream);
	initialised=true;;
    }
    
    public void init ( InputStream istream ) throws PNGException,
	IOException
    {
	PNGDataDecoder png_decoder = new PNGDataDecoder (istream);
	PNGInfo png_info           = new PNGInfo ();
	PNGInfo png_end_info       = new PNGInfo ();
	
	png_decoder.readInfo (png_info);
	
// rows proccessing
	width  = png_info.width;
	height = png_info.height;
	pixels = new int[width * height];
	int index = 0;
	
	int channels = 3;
	if ((png_info.color_type & PNGData.PNG_COLOR_TYPE_PALETTE) == PNGData.PNG_COLOR_TYPE_PALETTE)
	    channels = 1;
	if ((png_info.color_type & PNGData.PNG_COLOR_MASK_ALPHA) != 0) channels++;
	
	byte[] row_buf = new byte [png_info.rowbytes];
	
	int red   = 0;
	int green = 0;
	int blue  = 0;
	for (int y = 0; y < height; y++)
	{
	    png_decoder.readRow  (row_buf);
	    int ibuf = 0;
	    for (int x = 0; x < width; x++, ibuf += channels)
	    {
		if (channels >= 3)   // RGB
		{
		    red   = PNGData.ubyte(row_buf[ibuf + 0]);
		    green = PNGData.ubyte(row_buf[ibuf + 1]);
		    blue  = PNGData.ubyte(row_buf[ibuf + 2]);
		}
		else
		{
		    int i = PNGData.ubyte(row_buf[ibuf]);
		    red   = png_info.palette[i].getRed();
		    green = png_info.palette[i].getGreen();
		    blue  = png_info.palette[i].getBlue();
		}
		pixels[index++] = 0xff << 24 |  red << 16 | green << 8 | blue;
	    }
	}
	png_decoder.readEnd  (png_end_info);
    }
    
    /**
      * Adds an ImageConsumer to the list of consumers interested in
      * data for this image.
      * @see ImageConsumer
      */
    public synchronized void addConsumer(ImageConsumer ic)
    {
	theConsumer = ic;
	produce();
	theConsumer = null;
    }
    
    /**
      * Determine if an ImageConsumer is on the list of consumers currently
      * interested in data for this image.
      * @return true if the ImageConsumer is on the list; false otherwise
      * @see ImageConsumer
      */
    public synchronized boolean isConsumer(ImageConsumer ic)
    {
	return (ic == theConsumer);
    }
    
    /**
      * Remove an ImageConsumer from the list of consumers interested in
      * data for this image.
      * @see ImageConsumer
      */
    public synchronized void removeConsumer(ImageConsumer ic)
    {
	if (theConsumer == ic)
	{
	    theConsumer = null;
	}
    }
    
    /**
      * Adds an ImageConsumer to the list of consumers interested in
      * data for this image, and immediately start delivery of the
      * image data through the ImageConsumer interface.
      * @see ImageConsumer
      */
    public void startProduction(ImageConsumer ic)
    {
	addConsumer(ic);
    }
    
    /**
      * Requests that a given ImageConsumer have the image data delivered
      * one more time in top-down, left-right order.
      * @see ImageConsumer
      */
    public void requestTopDownLeftRightResend(ImageConsumer ic)
    {
	// Not needed.  The data is always in TDLR format.
    }
    
    /*
     * Produce Image data.
     * I.e set the data for the Image, using the ImageConsumer interface.
     */
    private void produce()
    {
	if (theConsumer != null)
	{
	    theConsumer.setDimensions(width, height);
	}
	if (theConsumer != null)
	{
	    theConsumer.setProperties(new Hashtable());
	}
	if (theConsumer != null)
	{
	    theConsumer.setColorModel(colorModel);
	}
	if (theConsumer != null)
	{
	    theConsumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT |
				 ImageConsumer.COMPLETESCANLINES |
				 ImageConsumer.SINGLEPASS |
				 ImageConsumer.SINGLEFRAME);
	}
	if (theConsumer != null)
	{
	    theConsumer.setPixels(0, 0, width, height,
				  colorModel,
				  pixels, 0, width);
	}
	if (theConsumer != null)
	{
	    theConsumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
	}
    }

    //This is dirty hack. I am ashamed. :-(
    // lord
    public boolean equals(Object obj)
    {
	if(obj instanceof InputStream)
	{
	    if(!initialised)
	    {
		try
		{
		    init((InputStream)obj);
		    initialised=true;
		} catch(Exception e)
		{
		}
	    }
	    return false;
	} else
	{
	    return super.equals(obj);
	}
    }
}


