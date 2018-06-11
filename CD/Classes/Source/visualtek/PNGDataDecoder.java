/*
 * $Id: PNGDataDecoder.java,v 1.3 1997/08/28 23:10:55 vlad Exp $
 *
 * $Log: PNGDataDecoder.java,v $
 * Revision 1.3  1997/08/28 23:10:55  vlad
 * DataFormatException doesn't throw up from PNG any more
 *
 * Revision 1.2  1997/08/13 01:05:41  lord
 * move some classes from PNG to Cubes
 *
 * Revision 1.1  1997/08/05 17:53:30  lord
 * PNG code added. JDK-1.1.3 is now used
 *
 */

package visualtek;

import java.awt.Color;
import java.util.zip.*;
import java.io.*;

// PNG decoder
public class PNGDataDecoder extends PNGData
{
    private InputStream     png_istream = null;
    private Inflater        zstream     = null;
    
    /* Constructor */
    public  PNGDataDecoder ( InputStream i_stream )
    {
	super ();
	
	png_istream = i_stream;
	
	zstream = new Inflater ();
	
	/* initialize zbuf - compression buffer */
	zbuf_size = PNG_ZBUF_SIZE;
	zbuf = new byte [zbuf_size];
	
	/* !!!
	   zstream.next_out = zbuf;
	   zstream.avail_out = (int)zbuf_size;
	*/
    }
    
    /* Reading info */
    public  void    readInfo    ( PNGInfo info ) throws PNGException, IOException
    {
	readData(info.signature, 8);

	// check signature
	if (memCompare(info.signature, png_sig, 8) != 0)
	    throw new PNGException("Not a PNG file");

	byte[] chunk_length = new byte[4];

	while (true)
        {
	    int length;

	    readData(chunk_length, 4);
	    length = getInt32(chunk_length);

	    resetCRC();
	    crcRead(chunk_name, 4);

	    if (memCompare(chunk_name, png_IHDR, 4) == 0)
		handleIHDR(info, length);
	    else if (memCompare(chunk_name, png_PLTE, 4) == 0)
		handlePLTE(info, length);
	    else if (memCompare(chunk_name, png_IEND, 4) == 0)
		handleIEND(info, length);
	    else if (memCompare(chunk_name, png_IDAT, 4) == 0)
            {
		if ((mode & PNG_HAVE_IHDR) == 0)
		    throw new PNGException("Missing IHDR before IDAT");
		else if (color_type == PNG_COLOR_TYPE_PALETTE &&
			 (mode & PNG_HAVE_PLTE) == 0)
		    throw new PNGException("Missing PLTE before IDAT");

		idat_size = length;
		mode |= PNG_HAVE_IDAT;
		break;
            }
	    else
		handleUnknown(info, length);
        }
    }

/* Reading next row */
    public  void    readRow     ( byte[] row )  throws PNGException, IOException
    {
	int zavail_in;

	if ((flags & PNG_FLAG_ROW_INIT) == 0)
	    readStartRow();

	if ((mode & PNG_HAVE_IDAT) == 0)
	    throw new PNGException("Invalid attempt to read row data");

	int zavail_out = irowbytes;
	int buf_off    = 0;
	do
        {
	    if (zstream.needsInput())
            {
		byte[] chunk_length = new byte [4];

		while (idat_size == 0)
                {
		    crcFinish(0);

		    readData(chunk_length, 4);
		    idat_size = getInt32(chunk_length);

		    resetCRC();
		    crcRead(chunk_name, 4);
		    if (memCompare(chunk_name, png_IDAT, 4) != 0)
			throw new PNGException("Not enough image data");
                }

		zavail_in = zbuf_size;
		if (zbuf_size > idat_size)
		    zavail_in = idat_size;
		crcRead(zbuf, zavail_in);
		idat_size -= zavail_in;
		zstream.setInput (zbuf, 0, zavail_in);
            }
		
	    int inf_bytes;
	    try
	    {
		inf_bytes = zstream.inflate  (row_buf, buf_off, zavail_out);
	    }
	    catch( DataFormatException e)
	    {
		throw new PNGException("Zip error: " + e.getMessage());
	    }
	    buf_off    += inf_bytes;
	    zavail_out -= inf_bytes;
	    if (zstream.finished())
            {
		if (zstream.getRemaining() != 0 || idat_size != 0)
		    throw new PNGException("Extra compressed data");
		mode  |= PNG_AFTER_IDAT;
		flags |= PNG_FLAG_ZLIB_FINISHED;
		break;
            }
        }
	while (zavail_out > 0);

	row_info.color_type = color_type;
	row_info.width = iwidth;
	row_info.channels = channels;
	row_info.bit_depth = bit_depth;
	row_info.pixel_depth = pixel_depth;
	row_info.rowbytes = ((row_info.width * (int)row_info.pixel_depth + 7) >> 3);

	readFilterRow(row_info, row_buf, 1, prev_row, 1, ubyte(row_buf[0]));

	memCopy(prev_row, row_buf, rowbytes + 1);

	/*!!! not implemented now
	  if (transformations)
	  doReadTransformations();
	*/

	if (row != null) combineRow(row, 0xff);
	readFinishRow();
    }

/* End reading */
    public  void    readEnd     ( PNGInfo info )  throws PNGException, IOException
    {
	byte[] chunk_length = new byte [4];
	int length;

	crcFinish(0);

	do
        {
	    readData(chunk_length, 4);
	    length = getInt32(chunk_length);

	    resetCRC();
	    crcRead(chunk_name, 4);

	    if (memCompare(chunk_name, png_IHDR, 4) == 0)
		handleIHDR(info, length);
	    else if (memCompare(chunk_name, png_PLTE, 4) == 0)
		handlePLTE(info, length);
	    else if (memCompare(chunk_name, png_IDAT, 4) == 0)
            {
		if (length > 0 || (mode & PNG_AFTER_IDAT) != 0)
		    throw new PNGException("Too many IDAT's found");
            }
	    else if (memCompare(chunk_name, png_IEND, 4) == 0)
		handleIEND(info, length);
	    else
		handleUnknown(info, length);
        } while ((mode & PNG_HAVE_IEND) == 0);
    }

/* handle method's */
    private void  handleIHDR  ( PNGInfo info, int length )  throws PNGException, IOException
    {
	byte[] buf = new byte [13];
	int compression_type, filter_type, interlace_type;

	if (mode != PNG_BEFORE_IHDR)
	    throw new PNGException("Out of place IHDR");

	/* check the length */
	if (length != 13)
	    throw new PNGException("Invalid IHDR chunk");

	mode |= PNG_HAVE_IHDR;

	crcRead(buf, 13);
	crcFinish(0);

	width = getInt32(buf);
	height = getInt32(buf, 4);
	bit_depth = (byte)buf[8];
	color_type = (byte)buf[9];
	compression_type = buf[10];
	filter_type = buf[11];
	interlace_type = buf[12];

	/* check for width and height valid values */
	if (width == 0 || width > 2147483647 || height == 0 || height > 2147483647)
	    throw new PNGException("Invalid image size in IHDR");

	/* check other values */
	if (bit_depth != 1 && bit_depth != 2 &&
	    bit_depth != 4 && bit_depth != 8 &&
	    bit_depth != 16)
	    throw new PNGException("Invalid bit depth in IHDR");

	if (color_type < 0 || color_type == 1 ||
	    color_type == 5 || color_type > 6)
	    throw new PNGException("Invalid color type in IHDR");

	if (((color_type == (byte)PNG_COLOR_TYPE_PALETTE) && bit_depth > 8) ||
	    ((color_type == (byte)PNG_COLOR_TYPE_RGB ||
	      color_type == (byte)PNG_COLOR_TYPE_GRAY_ALPHA ||
	      color_type == (byte)PNG_COLOR_TYPE_RGB_ALPHA) && bit_depth < 8))
	    throw new PNGException("Invalid color type/bit depth combination in IHDR");

	if (interlace_type > 1)
	    throw new PNGException("Unknown interlace method in IHDR");

	if (compression_type > 0)
	    throw new PNGException("Unknown compression method in IHDR");

	if (filter_type > 0)
	    throw new PNGException("Unknown filter method in IHDR");

	/* find number of channels */
	switch (color_type)
	{
	case 0:
	case 3:
	    channels = 1;
	    break;
	case 2:
	    channels = 3;
	    break;
	case 4:
	    channels = 2;
	    break;
	case 6:
	    channels = 4;
	    break;
	}

	/* set up other useful info */
	pixel_depth = (byte)(bit_depth * channels);
	rowbytes = ((width * (int)pixel_depth + 7) >> 3);
	readIHDR(info, width, height, bit_depth,
		 color_type, compression_type, filter_type, interlace_type);
    }

/* handle method's */
    private void  handlePLTE  ( PNGInfo info, int length )  throws PNGException, IOException
    {
	int num, i;

	if ((mode & PNG_HAVE_IHDR) == 0)
	    throw new PNGException("Missing IHDR before PLTE");
	else if ((mode & PNG_HAVE_IDAT) != 0)
	{
	    warning("Invalid PLTE after IDAT");
	    crcFinish(length);
	    return;
	}
	else if ((mode & PNG_HAVE_PLTE) != 0)
	    throw new PNGException("Duplicate PLTE chunk");

	mode |= PNG_HAVE_PLTE;

	if ((length % 3) != 0)
	{
	    if (color_type != PNG_COLOR_TYPE_PALETTE)
	    {
		warning("Invalid palette chunk");
		crcFinish(length);
		return;
	    }
	    else
	    {
		throw new PNGException("Invalid palette chunk");
	    }
	}

	num = (int)length / 3;
	palette = new Color [num];
	flags |= PNG_FLAG_FREE_PALETTE;

	byte[] buf = new byte[3];
	for (i = 0; i < num; i++)
	{
	    crcRead(buf, 3);
	    /* don't depend upon png_color being any order */
	    palette[i] = new Color (ubyte(buf[0]), ubyte(buf[1]), ubyte(buf[2]));
	}

	/* If we actually NEED the PLTE chunk (ie for a paletted image), we do
	   whatever the normal CRC configuration tells us.  However, if we
	   have an RGB image, the PLTE can be considered ancillary, so
	   we will act as though it is. */
	if (color_type == PNG_COLOR_TYPE_PALETTE)
	{
	    if (crcFinish(0) != 0)
		return;
	}
	else if (crcError() != 0)  /* Only if we have a CRC error */
	{
	    String msg = "CRC error in " + chunk_name;

	    /* If we don't want to use the data from an ancillary chunk,
	       we have two options: an error abort, or a warning and we
	       ignore the data in this chunk (which should be OK, since
	       it's considered ancillary for a RGB or RGBA image). */
	    if ((flags & PNG_FLAG_CRC_ANCILLARY_USE) == 0)
	    {
		if ((flags & PNG_FLAG_CRC_ANCILLARY_NOWARN) != 0)
		{
		    throw new PNGException(msg);
		}
		else
		{
		    warning(msg);
		    return;
		}
	    }
	    else if ((flags & PNG_FLAG_CRC_ANCILLARY_NOWARN) == 0)
	    {
		warning(msg);
	    }
	}

	num_palette = (short)num;
	readPLTE(info, palette, num);
    }

/* handle method's */
    private void  handleIEND  ( PNGInfo info, int length )  throws PNGException, IOException
    {
	if ((mode & PNG_HAVE_IHDR) == 0 || (mode & PNG_HAVE_IDAT) == 0)
	{
	    throw new PNGException("No image in file");
	}

	mode |= PNG_AFTER_IDAT | PNG_HAVE_IEND;

	if (length != 0)
	{
	    warning("Incorrect IEND chunk length");
	    crcFinish(length);
	}
    }

/* handle method's */
    private void  handleUnknown ( PNGInfo info, int length )  throws PNGException, IOException
    {
	/* In the future we can have code here that calls user-supplied
	 * callback functions for unknown chunks before they are ignored or
	 * cause an error.
	 */
	checkChunkName(chunk_name);

	if ((chunk_name[0] & 0x20) == 0)
	{
	    String msg = "Unknown critical chunk " + chunk_name;
	    throw new PNGException(msg);
	}

	if ((mode & PNG_HAVE_IDAT) != 0)
	    mode |= PNG_AFTER_IDAT;

	crcFinish(length);
    }

/* handle method's */
    private void  checkChunkName ( byte[] name )  throws PNGException
    {
	if (name[0] < 41 || name[0] > 122  ||
	    (name[0] > 90 && name[0] < 97) ||
	    name[1] < 41 || name[1] > 122  ||
	    (name[1] > 90 && name[1] < 97) ||
	    name[2] < 41 || name[2] > 122  ||
	    (name[2] > 90 && name[2] < 97) ||
	    name[3] < 41 || name[3] > 122  ||
	    (name[3] > 90 && name[3] < 97))
	{
	    throw new PNGException("Invalid chunk type");
	}
    }

/* handle method's */
    private void readIHDR ( PNGInfo info,
			    int vwidth, int vheight, int vbit_depth,
			    int vcolor_type, int vcompression_type,
			    int vfilter_type, int vinterlace_type )
    {
	info.width = vwidth;
	info.height = vheight;
	info.bit_depth = (byte)vbit_depth;
	info.color_type =(byte)vcolor_type;
	info.compression_type = (byte)vcompression_type;
	info.filter_type = (byte)vfilter_type;
	info.interlace_type = (byte)vinterlace_type;
	if (info.color_type == PNG_COLOR_TYPE_PALETTE)
	    info.channels = 1;
	else if ((info.color_type & PNG_COLOR_MASK_COLOR) != 0)
	    info.channels = 3;
	else
	    info.channels = 1;
	if ((info.color_type & PNG_COLOR_MASK_ALPHA) != 0)
	    info.channels++;
	info.pixel_depth = (byte)(info.channels * info.bit_depth);
	info.rowbytes = ((info.width * info.pixel_depth + 7) >> 3);
    }


/* handle method's */
    private void readPLTE ( PNGInfo info, Color[] vpalette, int num )
    {
	info.palette = vpalette;
	info.num_palette = (short)num;
	info.valid |= PNG_INFO_PLTE;
    }

/* read row methods */
    private void readStartRow()
    {
	int max_pixel_depth;
	int vrowbytes;

	// !!! zstream.avail_in = 0;

	/*!!! Not implemented
	  initReadTransformations();
	*/
	if (interlaced != 0)
	{
	    if ((transformations & PNG_INTERLACE) == 0)
		num_rows = (height + png_pass_yinc[0] - 1 -
			    png_pass_ystart[0]) / png_pass_yinc[0];
	    else
		num_rows = height;

	    iwidth = (width +
		      png_pass_inc[pass] - 1 -
		      png_pass_start[pass]) /  png_pass_inc[pass];
	    irowbytes = ((iwidth * pixel_depth + 7) >> 3) + 1;
	}
	else
	{
	    num_rows  = height;
	    iwidth    = width;
	    irowbytes = rowbytes + 1;
	}

	max_pixel_depth = pixel_depth;

	/* align the width on the next larger 8 pixels.  Mainly used
	   for interlacing */
	vrowbytes = ((width + 7) & ~((int)7));
	/* calculate the maximum bytes needed, adding a byte and a pixel
	   for safety sake */
	vrowbytes = ((vrowbytes * (int)max_pixel_depth + 7) >> 3) +
	    1 + ((max_pixel_depth + 7) >> 3);
	row_buf = new byte [vrowbytes];

	prev_row = new byte [rowbytes + 1];

	memSet(prev_row, 0, rowbytes + 1);

	flags |= PNG_FLAG_ROW_INIT;
    }

/* read row methods */
    private void readFilterRow ( PNGRowInfo row_info,
				 byte[] row,       int row_off,
				 byte[] vprev_row, int prev_row_off,
				 int vfilter )  throws PNGException
    {
	switch (vfilter)
	{
	case 0:
	    break;
	case 1:
	{
	    int i;
	    int bpp;
	    int rp;
	    int lp;

	    bpp = (pixel_depth + 7) / 8;
	    for (i = bpp, rp = bpp, lp = 0;
		 i < rowbytes; i++, rp++, lp++)
	    {
		int val = (ubyte(row[rp+row_off]) + ubyte(row[lp+row_off])) & 0xff;
		row[rp+row_off] = (byte) val;
	    }
	    break;
	}
	case 2:
	{
	    int i;
	    int rp;
	    int pp;

	    for (i = 0, rp = 0, pp = 0;
		 i < rowbytes; i++, rp++, pp++)
	    {
		int val = (ubyte(row[rp+row_off]) +
			   ubyte(vprev_row[pp+prev_row_off])) & 0xff;
		row[rp+row_off] = (byte) val;
	    }
	    break;
	}
	case 3:
	{
	    int i;
	    int bpp;
	    int rp;
	    int pp;
	    int lp;

	    bpp = (pixel_depth + 7) / 8;
	    for (i = 0, rp = 0, pp = 0;
		 i < bpp; i++, rp++, pp++)
	    {
		int val = (ubyte(row[rp+row_off]) +
			   (ubyte(vprev_row[pp+prev_row_off]) / 2)) & 0xff;
		row[rp+row_off] = (byte) val;
	    }
	    for (lp = 0; i < rowbytes; i++, rp++, lp++, pp++)
	    {
		int val = (ubyte(row[rp+row_off]) +
			   (ubyte(vprev_row[pp+prev_row_off]) + ubyte(row[lp+row_off])) / 2) & 0xff;
		row[rp+row_off] = (byte) val;
	    }
	    break;
	}
	case 4:
	{
	    int bpp;
	    int i;
	    int rp;
	    int pp;
	    int lp;
	    int cp;

	    bpp = (pixel_depth + 7) / 8;
	    for (i = 0, rp = 0, pp = 0,
		     lp = - bpp, cp = - bpp;
		 i < rowbytes; i++, rp++, pp++, lp++, cp++)
	    {
		int a, b, c, pa, pb, pc, p;

		b = ubyte (vprev_row[pp+prev_row_off]);
		if (i >= bpp)
		{
		    c = ubyte (vprev_row[cp+prev_row_off]);
		    a = ubyte (row[lp+row_off]);
		}
		else
		{
		    a = c = 0;
		}
		p = a + b - c;
		pa = iAbs(p - a);
		pb = iAbs(p - b);
		pc = iAbs(p - c);

		if (pa <= pb && pa <= pc)
		    p = a;
		else if (pb <= pc)
		    p = b;
		else
		    p = c;

		int val = (ubyte(row[rp+row_off]) + p) & 0xff;
		row[rp+row_off] = (byte) val;
	    }
	    break;
	}
	default:
	    throw new PNGException("Bad adaptive filter type");
	}
    }
/* read row methods */
    private void readFinishRow ()  throws PNGException, IOException
    {
	row_number++;
	if (row_number < num_rows)
	    return;

	if (interlaced != 0)
	{
	    row_number = 0;
	    memSet(prev_row, 0, rowbytes + 1);
	    do
	    {
		pass++;
		if (pass >= 7)
		    break;
		iwidth = (width + png_pass_inc[pass] - 1 -
			  png_pass_start[pass]) / png_pass_inc[pass];
		irowbytes = ((iwidth * pixel_depth + 7) >> 3) + 1;
		if ((transformations & PNG_INTERLACE) == 0)
		{
		    num_rows = (height + png_pass_yinc[pass] - 1 -
				png_pass_ystart[pass]) / png_pass_yinc[pass];
		    if (num_rows == 0)
			continue;
		}
		if ((transformations & PNG_INTERLACE) != 0)
		    break;
	    } while (iwidth == 0);

	    if (pass < 7)
		return;
	}

	if ((flags & PNG_FLAG_ZLIB_FINISHED) == 0)
	{
	    byte[] extra = new byte [1];
	    int zavail_in;

	    /*
	      zstream.next_out = (byte)extra;
	      zstream.avail_out = (int)1;
	    */
	    do
	    {
		if (zstream.needsInput())
		{
		    byte[] chunk_length = new byte [4];

		    while (idat_size == 0)
		    {

			crcFinish(0);

			readData(chunk_length, 4);
			idat_size = getInt32(chunk_length);

			resetCRC();
			crcRead(chunk_name, 4);
			if (memCompare(chunk_name, png_IDAT, 4) != 0)
			    throw new PNGException("Not enough image data");

		    }

		    zavail_in = zbuf_size;
		    if (zbuf_size > idat_size)
			zavail_in = idat_size;
		    crcRead(zbuf, zavail_in);
		    idat_size -= zavail_in;
		    zstream.setInput (zbuf, 0, zavail_in);
		}
		try
		{    
		    zstream.inflate  (extra, 0, 1);
		}
		catch(DataFormatException e)
		{
		    throw new PNGException("Zip error: " + e.getMessage());
		}
		if (zstream.finished())
		{
		    if (zstream.getRemaining() != 0 || idat_size != 0)
			throw new PNGException("Extra compressed data");
		    mode |= PNG_AFTER_IDAT;
		    flags |= PNG_FLAG_ZLIB_FINISHED;
		    break;
		}
		if (zstream.getRemaining() == 0)
		    throw new PNGException("Extra compressed data");

	    } while (true);
	    //!!! zstream.avail_out = 0;
	}

	if (idat_size != 0 || zstream.getRemaining() != 0)
	    throw new PNGException("Extra compression data");

	zstream.reset();

	mode |= PNG_AFTER_IDAT;
    }

/* combine row */
    private void combineRow  ( byte[] row, int mask )
    {
	if (mask == 0xff)
	{
	    memCopy(row, 0, row_buf, 1, (width * row_info.pixel_depth + 7) >> 3);
	}
	else
	{
	    switch (row_info.pixel_depth)
	    {
	    case 1:
	    {
		int sp;
		int dp;
		int m;
		int shift;
		int i;
		int value;

		sp = 1;
		dp = 0;
		shift = 7;
		m = 0x80;
		for (i = 0; i < width; i++)
		{
		    if ((m & mask) != 0)
		    {
			value = (row_buf[sp] >> shift) & 0x1;
			row[dp] &= (byte)((0x7f7f >> (7 - shift)) & 0xff);
			row[dp] |= (byte)(value << shift);
		    }

		    if (shift == 0)
		    {
			shift = 7;
			sp++;
			dp++;
		    }
		    else
			shift--;

		    if (m == 1)
			m = 0x80;
		    else
			m >>= 1;
		}
		break;
	    }
	    case 2:
	    {
		int sp;
		int dp;
		int m;
		int shift;
		int i;
		int value;

		sp = 1;
		dp = 0;
		shift = 6;
		m = 0x80;
		for (i = 0; i < width; i++)
		{
		    if ((m & mask) != 0)
		    {
			value = (row_buf[sp] >> shift) & 0x3;
			row[dp] &= (byte)((0x3f3f >> (6 - shift)) & 0xff);
			row[dp] |= (byte)(value << shift);
		    }

		    if (shift == 0)
		    {
			shift = 6;
			sp++;
			dp++;
		    }
		    else
			shift -= 2;
		    if (m == 1)
			m = 0x80;
		    else
			m >>= 1;
		}
		break;
	    }
	    case 4:
	    {
		int sp;
		int dp;
		int m;
		int shift;
		int i;
		int value;

		sp = 1;
		dp = 0;
		shift = 4;
		m = 0x80;
		for (i = 0; i < width; i++)
		{
		    if ((m & mask) != 0)
		    {
			value = (row_buf[sp] >> shift) & 0xf;
			row[dp] &= (byte)((0xf0f >> (4 - shift)) & 0xff);
			row[dp] |= (byte)(value << shift);
		    }

		    if (shift == 0)
		    {
			shift = 4;
			sp++;
			dp++;
		    }
		    else
			shift -= 4;
		    if (m == 1)
			m = 0x80;
		    else
			m >>= 1;
		}
		break;
	    }
	    default:
	    {
		int sp;
		int dp;
		int i, pixel_bytes;
		byte m;

		pixel_bytes = (row_info.pixel_depth >> 3);

		sp = 1;
		dp = 0;
		m = (byte)0x80;
		for (i = 0; i < width; i++)
		{
		    if ((m & mask) != 0)
		    {
			memCopy(row, dp, row_buf, sp, pixel_bytes);
		    }

		    sp += pixel_bytes;
		    dp += pixel_bytes;

		    if (m == 1)
			m = (byte)0x80;
		    else
			m >>= 1;
		}
		break;
	    }
	    }
	}
    }

/* crc methods */
    private void crcRead ( byte[] buf, int length ) throws IOException
    {
	int need_crc = 1;

	if ((chunk_name[0] & 0x20) != 0)                    /* ancillary */
	{
	    if ((flags & PNG_FLAG_CRC_ANCILLARY_MASK) ==
		(PNG_FLAG_CRC_ANCILLARY_USE | PNG_FLAG_CRC_ANCILLARY_NOWARN))
		need_crc = 0;
	}
	else                                                   /* critical */
	{
	    if ((flags & PNG_FLAG_CRC_CRITICAL_IGNORE) != 0)
		need_crc = 0;
	}

	readData(buf, length);

	if (need_crc != 0)
	    calculateCRC(buf, length);
    }

/* crc methods */
    private int crcFinish ( int skip )  throws PNGException, IOException
    {
	int need_crc = 1;
	int crc_error;
	int i;

	if ((chunk_name[0] & 0x20) != 0)                    /* ancillary */
	{
	    if ((flags & PNG_FLAG_CRC_ANCILLARY_MASK) ==
		(PNG_FLAG_CRC_ANCILLARY_USE | PNG_FLAG_CRC_ANCILLARY_NOWARN))
		need_crc = 0;
	}
	else                                                    /* critical */
	{
	    if ((flags & PNG_FLAG_CRC_CRITICAL_IGNORE) != 0)
		need_crc = 0;
	}

	for (i = skip; i > zbuf_size; i -= zbuf_size)
	{
	    readData(zbuf, zbuf_size);
	    if (need_crc != 0)
		calculateCRC(zbuf, zbuf_size);
	}
	if (i != 0)
	{
	    readData(zbuf, i);
	    if (need_crc != 0)
		calculateCRC(zbuf, i);
	}

	crc_error = crcError();

	if (need_crc != 0 && crc_error != 0)
	{
	    String msg = "CRC error in " + chunk_name;

	    if (((chunk_name[0] & 0x20) != 0 &&                /* Ancillary */
		 (flags & PNG_FLAG_CRC_ANCILLARY_NOWARN) == 0) ||
		((chunk_name[0] & 0x20) == 0 &&             /* Critical  */
		 (flags & PNG_FLAG_CRC_CRITICAL_USE) != 0))
	    {
		warning(msg);
	    }
	    else
	    {
		throw new PNGException(msg);
	    }
	    return 1;
	}

	return 0;

    }

/* crc methods */
    private int crcError () throws IOException
    {
	byte[] crc_bytes = new byte [4];
	int  local_crc;

	readData(crc_bytes, 4);

	local_crc = getInt32(crc_bytes);

	return (local_crc != crc) ? 1 : 0;
    }

/* read data */
    protected void readData  ( byte[] data, int length )  throws IOException
    {
	int count = png_istream.read (data,0,length);
	if (count != length ) throw new PNGException("Read Error");
    }

}
