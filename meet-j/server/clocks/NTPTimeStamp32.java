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


/**
 *
 * @author  phil
 */
public class NTPTimeStamp32 {
  
  
  private final static double SCALE = (double) (65535.0);
  private final static DecimalFormat fmt = new DecimalFormat(".00000");
  private final static FieldPosition fpDecSep =
  new FieldPosition(java.text.NumberFormat.Field.DECIMAL_SEPARATOR);
  private int _secs = 0;
  private int _frac = 0;
  private double _fracDouble = 0.0;
  
  
  /**
   * create an NTPTimeStamp given a DataInputStream
   *positioned right before the NTP packet bytes
   */
  public NTPTimeStamp32(ByteBuffer bb) throws IOException {
    
    // parse the bytes to get a long
    if (bb == null) {
      throw new IOException("NTPTimeStamp32 ctor: bb uninitialized");
    }
    if (bb.remaining() < 4) {
      throw new IOException("NTPTimeStamp32 ctor: bb remain < 4");
    }
    
    _secs = ((int) bb.getShort()) & 0xFFFF;  // force unsigned
    
    
    _frac = ((int) bb.getShort()) & 0xFFFF;  // force unsigned
    _fracDouble = _frac / SCALE;
    System.out.println("_secs, _frac = " + Integer.toHexString(_secs) + 
    ", " + Integer.toHexString(_frac));
    
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer(10);
    fmt.format(_fracDouble, sb, fpDecSep);
    // return ("" + _secs + "--" + sb);
    return ("" + _secs + sb);
  }
  
  public static void main(String args[]) {
    byte [] test1 = {
      0x01, 0x00, 0x00, 0x01,
    };
    byte [] test2 = {
      0x7F, 0x00, 0x7F, 0x00,
    };
    byte [] test3 = {
      0x01, 0x00, 0x7F, 0x00,
    };
    ByteBuffer bb1 = ByteBuffer.wrap(test1);
    ByteBuffer bb2 = ByteBuffer.wrap(test2);
    ByteBuffer bb3 = ByteBuffer.wrap(test3);
    
    NTPTimeStamp32 ntp1 = null;
    NTPTimeStamp32 ntp2 = null;
    NTPTimeStamp32 ntp3 = null;
    
    try {
      ntp1 = new NTPTimeStamp32(bb1);
      ntp2 = new NTPTimeStamp32(bb2);
      ntp3 = new NTPTimeStamp32(bb3);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    System.out.println("test1 = " + ntp1);
    System.out.println("test2 = " + ntp2);
    System.out.println("test3 = " + ntp3);
  }
}
