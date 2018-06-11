/*
 * $Id: PNGException.java,v 1.2 1997/08/13 01:05:43 lord Exp $
 *
 * $Log: PNGException.java,v $
 * Revision 1.2  1997/08/13 01:05:43  lord
 * move some classes from PNG to Cubes
 *
 * Revision 1.1  1997/08/05 17:53:31  lord
 * PNG code added. JDK-1.1.3 is now used
 *
 */

package com.visualtek.PNG;

import java.io.IOException;

public class PNGException extends IOException
{
    public  PNGException() {}
    
    public  PNGException( String what )
    {
        super(what);
    }

}

