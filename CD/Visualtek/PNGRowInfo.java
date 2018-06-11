/*
 * $Id: PNGRowInfo.java,v 1.2 1997/08/13 01:05:43 lord Exp $
 *
 * $Log: PNGRowInfo.java,v $
 * Revision 1.2  1997/08/13 01:05:43  lord
 * move some classes from PNG to Cubes
 *
 * Revision 1.1  1997/08/05 17:53:31  lord
 * PNG code added. JDK-1.1.3 is now used
 *
 */

package com.visualtek.PNG;

class PNGRowInfo
{
    public  PNGRowInfo () {}

    public int      width       = 0;    /* width of row */
    public int      rowbytes    = 0;    /* number of bytes in row */
    public byte     color_type  = 0;    /* color type of row */
    public byte     bit_depth   = 0;    /* bit depth of row */
    public byte     channels    = 0;    /* number of channels (1, 2, 3, or 4) */
    public byte     pixel_depth = 0;    /* bits per pixel (depth * channels) */
    
}

