/***************************************************************************

  StringTools.java
  
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


package com.pearsoneduc.ip.util;


/**
 * A class containing various static utility methods for
 * handling strings.
 *
 * @author Nick Efford
 * @version 1.1 [1999/07/23]
 * @see java.lang.String
 * @see java.lang.StringBuffer
 */

public final class StringTools {

  public static String rightJustify(int value, int fieldWidth) {
    return rightJustify(String.valueOf(value), fieldWidth);
  }

  public static String rightJustify(float value, int fieldWidth) {
    return rightJustify(String.valueOf(value), fieldWidth);
  }

  public static String rightJustify(String string, int fieldWidth) {
    StringBuffer field = new StringBuffer();
    field.setLength(fieldWidth);
    for (int i = 0; i < fieldWidth; ++i)
      field.setCharAt(i, ' ');
    field.replace(fieldWidth-string.length(), fieldWidth, string);
    return field.toString();
  }

}
