/***************************************************************************

  IntervalTimer.java
  
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
 * A simple class for interval timing.
 *
 * @author Nick Efford
 * @version 1.3 [1999/02/06]
 */

public final class IntervalTimer {


  private static final boolean GC = false;


  /////////////////////////// INSTANCE VARIABLES ////////////////////////////


  /**
   * Start time (in milliseconds since 00:00:00 1 Jan 1970)
   */

  private long startTime;

  /**
   * Stop time (in milliseconds since 00:00:00 1 Jan 1970)
   */

  private long endTime;

  /**
   * Flag to indicate whether timer is active.
   */

  private boolean timing;


  ///////////////////////////////// METHODS /////////////////////////////////


  /**
   * Starts the timer.
   */

  public void start() {
    if (!timing) {

      if (GC)          // invoke garbage collector explicitly
	System.gc();   // to minimise possible effects on timing

      startTime = System.currentTimeMillis();
      endTime = 0L;
      timing = true;

    }
  }


  /**
   * Calculates elapsed time.
   * @return Current elapsed time in seconds, as a real number
   */

  public double getElapsedTime() {
    if (timing) {
      long now = System.currentTimeMillis();
      return (now - startTime) / 1000.0;
    }
    else 
      return (endTime - startTime) / 1000.0;
  }


  /**
   * Stops the timer (if it is running).
   * @return Total elapsed time in seconds, as a real number
   */

  public double stop() {
    if (timing) {
      endTime = System.currentTimeMillis();
      timing = false;
    }
    return (endTime - startTime) / 1000.0;
  }


  /**
   * Resets the timer.
   */

  public void reset() {
    startTime = endTime = 0L;
    timing = false;
  }


  /**
   * Indicates whether timer is currently active.
   * @return True if the timer is active, false otherwise
   */

  public boolean isTiming() {
    return timing;
  }


  /**
   * Indicates whether timer is currently inactive.
   * @return True if the timer is inactive, false otherwise
   */

  public boolean isStopped() {
    return (!timing);
  }


  /**
   * Creates a String representation of timer status.
   * @return Timer status, as a String
   */

  public String toString() {

    if (startTime == 0L && endTime == 0L)
      return new String(getClass().getName() + ": unused");

    if (timing)
      return new String(getClass().getName() + ": started " + startTime);
    else
      return new String(getClass().getName() + ": started " + startTime +
       ", stopped " + endTime);

  }


}
