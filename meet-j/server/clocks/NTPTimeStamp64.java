/*
 * NTPTimeStamp64.java
 *
 * Created on December 24, 2002, 5:37 AM
 */

package psl.meet.server.clocks;

import java.nio.*;
import java.io.IOException;
import java.text.*;
import java.text.DecimalFormat.*;
import java.text.NumberFormat.Field;
import java.util.Date;

/**
 *
 * @author  phil
 */
public class NTPTimeStamp64 {
  

  private final static boolean DEBUG = true;
  private final static double SCALE = (double) (4294967295.0);
  /** seconds since 1-1-1900 minus seconds since 1-1-1970, in ms */
  private final static long EPOCH_DIFF = 2208988800L ;
  private final static long OFFSET = EPOCH_DIFF * 1000L;
  private final static long DEFLATOR= (System.currentTimeMillis() / 1000L) + EPOCH_DIFF;
  private final static DecimalFormat fmt = new DecimalFormat(".0#############");
  private final static FieldPosition fpDecSep =
  new FieldPosition(java.text.NumberFormat.Field.DECIMAL_SEPARATOR);
  // allow direct access within package
   long _secs = 0;
   long _frac = 0;
  private double _fracDouble = 0.0;
  
  /** get current time as long in NTP format. */
  static long currentNTP(long localOffset) {
    long curr = System.currentTimeMillis() + OFFSET + localOffset;
    // this integer represents milliseconds.  Need to move the
    // decimal point between seconds and fraction to pos 31/32
    long secs = curr / 1000L;
    double dFrac = (curr % 1000L) / 1000.0;
    long lfrac = (long) (dFrac * SCALE);
    return (secs << 32) | (lfrac & 0xFFFFFFFFL);
    //TODO: standard recommends filling low-end bits with random data.
    
  }
  
  Date toDate() {
    long millisecs = (long)((_secs * 1000) + (_fracDouble * 1000) - OFFSET);
    return new Date(millisecs);
  }
  
  public String toFull(){
    return toDate().toString() + " (" + toString() + ")"; 
  }
    
  /** create an NTPTimeStamp64 object from an appropriately formatted long. */
  public NTPTimeStamp64(long raw) {
    _secs = (raw >> 32)& 0xFFFFFFFFL;  // force unsigned
    
    _frac = raw & 0xFFFFFFFFL;  // force unsigned
    _fracDouble = _frac / SCALE;
    System.out.println("raw ctor: _secs, _frac = " + Long.toHexString(_secs) + 
    ", " + Long.toHexString(_frac));
  }
  
  /**
   * create an NTPTimeStamp given a DataInputStream
   *positioned right before the NTP packet bytes
   */
  public NTPTimeStamp64(ByteBuffer bb) throws IOException {
        
    if (bb == null) {
      throw new IOException("NTPTimeStamp64 ctor: bb uninitialized");
    }
    if (bb.remaining() < 8) {
      throw new IOException("NTPTimeStamp64 ctor: bb remain of " + 
        bb.remaining() + " < 8");
    }
    
    _secs = ((long) bb.getInt()) & 0xFFFFFFFFL;  // force unsigned
    
    // max of unsigned long
    
    _frac = ((long) bb.getInt()) & 0xFFFFFFFFL;  // force unsigned
    _fracDouble = _frac / SCALE;
    System.out.println("Buffer ctor: _secs, _frac = " + Long.toHexString(_secs) + 
    ", " + Long.toHexString(_frac));
    
  }
  
  public String toString() {
    
    StringBuffer sb = new StringBuffer(10);
    fmt.format(_fracDouble, sb, fpDecSep);
    // return ("" + _secs + "--" + sb);
    return ("" + _secs + sb);
  }
  
  public long toLong() {
    return ( (_secs << 32) | (_frac & 0xFFFFFFFFL) );
  }
  
  public double toDelta() {   
    /* 
     if (DEBUG) System.out.println("toDelta: _secs: " + 
      _secs + ", DEFLATOR: " +
      DEFLATOR + ", _frac:" + _fracDouble);
     */
    return ((double)(_secs)) + _fracDouble;
  }
  
  public static long spanToMillis(long src) {
    NTPTimeStamp64 ts = new NTPTimeStamp64(src);
    return (long) ( (ts._secs * 1000) + (ts._fracDouble * 1000.0) );
  }
  
  public static void main(String args[]) {
    byte [] test1 = {
      0x00, 0x00, 0x00, 0x01,
      0x00, 0x00, 0x00, 0x01
    };
    byte [] test2 = {
      0x0F, 0x00, 0x00, 0x00,
      0x0F, 0x00, 0x00, 0x00
    };
    byte [] test3 = {
      0x7F, 0x00, 0x00, 0x01,
      0x7F, 0x00, 0x00, 0x00
    };
    ByteBuffer bb1 = ByteBuffer.wrap(test1);
    ByteBuffer bb2 = ByteBuffer.wrap(test2);
    ByteBuffer bb3 = ByteBuffer.wrap(test3);
    
    NTPTimeStamp64 ntp1 = null;
    NTPTimeStamp64 ntp2 = null;
    NTPTimeStamp64 ntp3 = null;
    
    try {
      ntp1 = new NTPTimeStamp64(bb1);
      ntp2 = new NTPTimeStamp64(bb2);
      ntp3 = new NTPTimeStamp64(bb3);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    System.out.println("test1 = " + ntp1);
    System.out.println("test2 = " + ntp2);
    System.out.println("test3 = " + ntp3);
  }
}
