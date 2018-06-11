/*
 * $Id: PNGData.java,v 1.2 1997/08/13 01:01:49 lord Exp $
 *
 * $Log: PNGData.java,v $
 * Revision 1.2  1997/08/13 01:01:49  lord
 * Use System.arraycopy in memCopy() for speed.
 *
 * Revision 1.1  1997/08/05 17:53:30  lord
 * PNG code added. JDK-1.1.3 is now used
 *
 */

package visualtek;

import java.util.zip.CRC32;
import java.awt.Color;

// PNG Data
public class PNGData
{
    /*
     * These determine if a chunks information is present in a read operation, or
     *  if the chunk should be written in a write operation.
     */
    public final static int PNG_INFO_gAMA = 0x0001;
    public final static int PNG_INFO_sBIT = 0x0002;
    public final static int PNG_INFO_cHRM = 0x0004;
    public final static int PNG_INFO_PLTE = 0x0008;
    public final static int PNG_INFO_tRNS = 0x0010;
    public final static int PNG_INFO_bKGD = 0x0020;
    public final static int PNG_INFO_hIST = 0x0040;
    public final static int PNG_INFO_pHYs = 0x0080;
    public final static int PNG_INFO_oFFs = 0x0100;
    public final static int PNG_INFO_tIME = 0x0200;

    /* color type masks */
    public final static int PNG_COLOR_MASK_PALETTE = 1;
    public final static int PNG_COLOR_MASK_COLOR   = 2;
    public final static int PNG_COLOR_MASK_ALPHA   = 4;

    /* color types.  Note that not all combinations are legal */
    public final static int PNG_COLOR_TYPE_GRAY       = 0;
    public final static int PNG_COLOR_TYPE_PALETTE    = PNG_COLOR_MASK_COLOR | PNG_COLOR_MASK_PALETTE;
    public final static int PNG_COLOR_TYPE_RGB        = PNG_COLOR_MASK_COLOR;
    public final static int PNG_COLOR_TYPE_RGB_ALPHA  = PNG_COLOR_MASK_COLOR | PNG_COLOR_MASK_ALPHA;
    public final static int PNG_COLOR_TYPE_GRAY_ALPHA = PNG_COLOR_MASK_ALPHA;

    /* flags for the png_ptr->flags rather than declaring a bye for each one */
    public final static int PNG_FLAG_ZLIB_CUSTOM_STRATEGY    = 0x0001;
    public final static int PNG_FLAG_ZLIB_CUSTOM_LEVEL       = 0x0002;
    public final static int PNG_FLAG_ZLIB_CUSTOM_MEM_LEVEL   = 0x0004;
    public final static int PNG_FLAG_ZLIB_CUSTOM_WINDOW_BITS = 0x0008;
    public final static int PNG_FLAG_ZLIB_CUSTOM_METHOD      = 0x0010;
    public final static int PNG_FLAG_ZLIB_FINISHED           = 0x0020;
    public final static int PNG_FLAG_ROW_INIT                = 0x0040;
    public final static int PNG_FLAG_FILLER_AFTER            = 0x0080;
    public final static int PNG_FLAG_CRC_ANCILLARY_USE       = 0x0100;
    public final static int PNG_FLAG_CRC_ANCILLARY_NOWARN    = 0x0200;
    public final static int PNG_FLAG_CRC_CRITICAL_USE        = 0x0400;
    public final static int PNG_FLAG_CRC_CRITICAL_IGNORE     = 0x0800;
    public final static int PNG_FLAG_FREE_PALETTE            = 0x1000;
    public final static int PNG_FLAG_FREE_TRANS              = 0x2000;
    public final static int PNG_FLAG_FREE_HIST               = 0x4000;
    public final static int PNG_FLAG_HAVE_CHUNK_HEADER       = 0x8000;
    public final static int PNG_FLAG_WROTE_tIME              = 0x10000;

    public final static int PNG_FLAG_CRC_ANCILLARY_MASK = PNG_FLAG_CRC_ANCILLARY_USE |
                                                          PNG_FLAG_CRC_ANCILLARY_NOWARN;

    public final static int PNG_FLAG_CRC_CRITICAL_MASK  = PNG_FLAG_CRC_CRITICAL_USE |
                                                          PNG_FLAG_CRC_CRITICAL_IGNORE;

    public final static int PNG_FLAG_CRC_MASK           = PNG_FLAG_CRC_ANCILLARY_MASK |
                                                          PNG_FLAG_CRC_CRITICAL_MASK;


    public final static int PNG_BEFORE_IHDR = 0x00;
    public final static int PNG_HAVE_IHDR   = 0x01;
    public final static int PNG_HAVE_PLTE   = 0x02;
    public final static int PNG_HAVE_IDAT   = 0x04;
    public final static int PNG_AFTER_IDAT  = 0x08;
    public final static int PNG_HAVE_IEND   = 0x10;

    public final static int PNG_ZBUF_SIZE   = 8192;

    /* defines for the transformations the PNG library does on the image data */
    public final static int PNG_BGR              = 0x0001;
    public final static int PNG_INTERLACE        = 0x0002;
    public final static int PNG_PACK             = 0x0004;
    public final static int PNG_SHIFT            = 0x0008;
    public final static int PNG_SWAP_BYTES       = 0x0010;
    public final static int PNG_INVERT_MONO      = 0x0020;
    public final static int PNG_DITHER           = 0x0040;
    public final static int PNG_BACKGROUND       = 0x0080;
    public final static int PNG_BACKGROUND_EXPAND= 0x0100;
    public final static int PNG_RGB_TO_GRAY      = 0x0200;
    public final static int PNG_16_TO_8          = 0x0400;
    public final static int PNG_RGBA             = 0x0800;
    public final static int PNG_EXPAND           = 0x1000;
    public final static int PNG_GAMMA            = 0x2000;
    public final static int PNG_GRAY_TO_RGB      = 0x4000;
    public final static int PNG_FILLER           = 0x8000;

    /* version information */
    public final static String  png_libpng_ver = "0.90";

    /* place to hold the signiture string for a png file. */
    public final static byte[]  png_sig = {(byte)137, 80, 78, 71, 13, 10, 26, 10};
    //public final static byte[]  png_sig = {0x89, 0x50, 0x4e, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    /* constant strings for known chunk types. */
    public final static byte[]  png_IHDR = { 73,  72,  68,  82};
    public final static byte[]  png_IDAT = { 73,  68,  65,  84};
    public final static byte[]  png_IEND = { 73,  69,  78,  68};
    public final static byte[]  png_PLTE = { 80,  76,  84,  69};
    public final static byte[]  png_gAMA = {103,  65,  77,  65};
    public final static byte[]  png_sBIT = {115,  66,  73,  84};
    public final static byte[]  png_cHRM = { 99,  72,  82,  77};
    public final static byte[]  png_tRNS = {116,  82,  78,  83};
    public final static byte[]  png_bKGD = { 98,  75,  71,  68};
    public final static byte[]  png_hIST = {104,  73,  83,  84};
    public final static byte[]  png_tEXt = {116,  69,  88, 116};
    public final static byte[]  png_zTXt = {122,  84,  88, 116};
    public final static byte[]  png_pHYs = {112,  72,  89, 115};
    public final static byte[]  png_oFFs = {111,  70,  70, 115};
    public final static byte[]  png_tIME = {116,  73,  77,  69};

    /* start of interlace block */
    public final static int[]   png_pass_start  = {0, 4, 0, 2, 0, 1, 0};
    /* offset to next interlace block */
    public final static int[]   png_pass_inc    = {8, 8, 4, 4, 2, 2, 1};
    /* start of interlace block in the y direction */
    public final static int[]   png_pass_ystart = {0, 0, 4, 0, 2, 0, 1};
    /* offset to next interlace block in the y direction */
    public final static int[]   png_pass_yinc   = {8, 8, 8, 4, 4, 2, 2};

    /* mask to determine which pixels are valid in a pass */
    public final static int[]   png_pass_mask    ={0x80, 0x08, 0x88, 0x22, 0xaa, 0x55, 0xff};
    /* mask to determine which pixels to overwrite while displaying */
    public final static int[]   png_pass_dsp_mask={0xff, 0x0f, 0xff, 0x33, 0xff, 0x55, 0xff};

    /* members */
    public int      mode            = 0;        /* used to determine where we are in the png file */
    public int      flags           = 0;        /* flags indicating various things to libpng */
    public int      transformations = 0;        /* which transformations to perform */

    public byte[]   zbuf             = null;    /* buffer for zlib */
    public int      zbuf_size        = 0;       /* size of zbuf */
    public int      zlib_level       = 0;       /* holds zlib compression level */
    public int      zlib_method      = 0;       /* holds zlib compression method */
    public int      zlib_window_bits = 0;       /* holds zlib compression window bits */
    public int      zlib_mem_level   = 0;       /* holds zlib compression memory level */
    public int      zlib_strategy    = 0;       /* holds zlib compression strategy */

    public int      width            = 0;       /* width of file */
    public int      height           = 0;       /* height of file */
    public int      num_rows         = 0;       /* number of rows in current pass */
    public int      rowbytes         = 0;       /* size of row in bytes */
    public int      usr_width        = 0;       /* width of row at start of write */
    public int      iwidth           = 0;       /* interlaced width */
    public int      irowbytes        = 0;       /* interlaced rowbytes */
    public int      row_number       = 0;       /* current row in pass */
    public byte[]   prev_row         = null;    /* place to save previous (unfiltered) row */
    public byte[]   row_buf          = null;    /* place to save current (unfiltered) row */
    public byte[]   sub_row          = null;    /* place to save "sub" row when filtering */
    public byte[]   up_row           = null;    /* place to save "up" row when filtering */
    public byte[]   avg_row          = null;    /* place to save "avg" row when filtering */
    public byte[]   paeth_row        = null;    /* place to save "Paeth" row when filtering */
    public PNGRowInfo row_info       = null;    /* used for transformation routines */

    public int      idat_size        = 0;       /* current idat size for read */
    public int      crc              = 0;       /* current crc value */
    public Color[]  palette          = null;    /* files palette */
    public short    num_palette      = 0;       /* number of entries in palette */
    public short    num_trans        = 0;       /* number of transparency values */
    public byte[]   chunk_name       = null;    /* name of current chunk being processed + '\0' */
    public byte     compression      = 0;       /* file compression type (currently only '0' used) */
    public byte     filter           = 0;       /* file filter type (currently only '0' used) */
    public byte     interlaced       = 0;       /* file interlace type (currently only '0' and '1') */
    public byte     pass             = 0;       /* current interlace pass (0 - 6) */
    public int      do_filter        = 0;       /* zero if not row filtering, non-zero if filtering */
    public byte     color_type       = 0;       /* color type of file */
    public byte     bit_depth        = 0;       /* bit depth of file */
    public byte     usr_bit_depth    = 0;       /* bit depth of users row */
    public byte     pixel_depth      = 0;       /* number of bits per pixel */
    public byte     channels         = 0;       /* number of channels in file */
    public byte     usr_channels     = 0;       /* channels at start of write */

    protected CRC32 crc_obj          = null;
   /*!!! optional information it absent now */

    /* Constructor */
    public  PNGData ()
    {
	row_info   = new PNGRowInfo();
	chunk_name = new byte[5];
	crc_obj    = new CRC32();
    }
    
    /* crc methods */
    protected void  resetCRC ()
    {
	crc_obj.reset ();
	crc = (int)crc_obj.getValue();
    }

    protected void  calculateCRC ( byte[] buf, int length )
    {
	crc_obj.update (buf,0,length);
	crc = (int)crc_obj.getValue();
    }

    /* grab an int 32 bit number from a buffer */
    protected int   getInt32  ( byte[] buf )
    {
	int i = (ubyte(buf[0]) << 24) +
	    (ubyte(buf[1]) << 16) +
	    (ubyte(buf[2]) << 8)  +
	    ubyte(buf[3]);
	return i;
    }
    
    protected int   getInt32  ( byte[] buf, int off )
    {
	int i = (ubyte(buf[0+off]) << 24) +
	    (ubyte(buf[1+off]) << 16) +
	    (ubyte(buf[2+off]) << 8)  +
	    ubyte(buf[3+off]);
	return i;
    }
    
    /* place a 32 bit number into a buffer in png byte order */
    protected void  saveInt32 ( byte[] buf, int i )
    {
	buf[0] = (byte)((i >> 24) & 0xff);
	buf[1] = (byte)((i >> 16) & 0xff);
	buf[2] = (byte)((i >> 8)  & 0xff);
	buf[3] = (byte) (i & 0xff);
    }
    
    protected void  saveInt32 ( byte[] buf, int off, int i )
    {
	buf[0+off] = (byte)((i >> 24) & 0xff);
	buf[1+off] = (byte)((i >> 16) & 0xff);
	buf[2+off] = (byte)((i >> 8)  & 0xff);
	buf[3+off] = (byte) (i & 0xff);
    }
    
    /* byte to unsigned int*/
    public static int ubyte ( byte b )
    {
	return (b < 0) ? 256 + (int)b : b;
    }
    
    /* internal methods */
    protected void  warning ( String msg )
    {
	System.err.println (msg);
    }
    
    protected int   memCompare ( byte[] buf1, byte[] buf2, int count )
    {
	for (int i = 0; i < count; i++)
	{
	    if (buf1 [i] < buf2 [i])      return -1;
	    else if (buf1 [i] > buf2 [i]) return  1;
	}
	return 0;
    }
    
    protected void  memCopy ( byte[] dest, byte[] src,  int count )
    {
	System.arraycopy(src,0,dest,0,count);
	// 	for (int i = 0; i < count; i++)
	// 	    dest [i] = src [i];
    }
    
    protected void  memCopy ( byte[] dest, int dst_off,
			      byte[] src,  int src_off, int count )
    {
	System.arraycopy(src,src_off,dest,dst_off,count);
	// 	for (int i = 0; i < count; i++)
	// 	    dest [i+dst_off] = src [i+src_off];
    }
    
    protected void  memSet ( byte[] dest, int c, int count )
    {
	for (int i = 0; i < count; i++)
	    dest [i] = (byte)c;
    }
    
    protected int   iAbs ( int v )
    {
	return ( v >= 0) ? v : - v;
    }
    
}
