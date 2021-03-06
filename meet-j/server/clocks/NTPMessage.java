/*
 * NTPTimeStamp.java
 *
 * Created on December 6, 2002, 1:41 AM
 */
package psl.meet.server.clocks;

import java.util.Date;
import java.util.HashMap;
import java.io.IOException;
import java.nio.*;

/**
 * Class representing an NTP timestamp as presented in
 * RFC2030 SNTPv4 for IPv4, IPv6 and OSI.
 *
 *                      1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |LI | VN  |Mode |    Stratum    |     Poll      |   Precision   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          Root Delay                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Root Dispersion                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Reference Identifier                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * |                   Reference Timestamp (64)                    |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * |                   Originate Timestamp (64)                    |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * |                    Receive Timestamp (64)                     |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * |                    Transmit Timestamp (64)                    |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                 Key Identifier (optional) (32)                |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * |                                                               |
 * |                 Message Digest (optional) (128)               |
 * |                                                               |
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 *
 * @author mkuba, phil
 *
 * Not sure which is faster - need to test:
 * - individual buffer for each item: scatter then parse
 * - one big buffer: read and parse as I go
 *
 * note: many comments quoted from RFC 2030
 */

public class NTPMessage {

  public final static boolean DEBUG = true;
  // system offset, seconds between 1970 and 1900
  // phil: or 2208902400L?
  private final long OFFSET = 2208988800L;
  
  
  private boolean initialized = false;
  
  // with associated byte array, if any
  // private boolean synchronized = false;
  
  // basic packet data structure
  
  /** bits 0-1 unsigned.
   * RFC2030: Leap Indicator (LI): This is a two-bit code warning of an
   * impending leap second to be inserted/deleted in the last minute of the
   * current day, with bit 0 and bit 1, respectively.
   */
  private byte _leapIndicator = 0;
  public static final String []  leapToString = {
    "no warning",
    "last minute has 61 seconds",
    "last minute has 59 seconds",
    "alarm condition (clock not synchronized)"
  };
  
  /** bits 2-4 unsigned.
   * RFC2030: Version Number (VN): This is a three-bit integer indicating the
   * NTP/SNTP version number. The version number is 3 for Version 3 (IPv4
   * only) and 4 for Version 4 (IPv4, IPv6 and OSI). If necessary to
   * distinguish between IPv4, IPv6 and OSI, the encapsulating context
   * must be inspected.
   */
  private byte _version = 0;
  public byte getVersion() {return _version;};
  
  /** bits 5-7  unsigned.
   * RFC2030: In unicast and anycast modes, the client sets this field to 3
   * (client) in the request and the server sets it to 4 (server) in the
   * reply. In multicast mode, the server sets this field to 5
   * (broadcast).
   */
  private byte _mode = 0;
  public static final String [] modeToString = {
    "reserved",
    "symmetric active",
    "symmetric passive",
    "client",
    "server",
    "broadcast",
    "reserved for NTP control message",
    "reserved for private use"
  };
  
  /** bits 8-15 unsigned.
   * RFC2030: Stratum: This is a eight-bit unsigned integer indicating the stratum
   * level of the local clock, with values defined as follows:
   *
   * Stratum  Meaning
   * ----------------------------------------------
   * 0        unspecified or unavailable
   * 1        primary reference (e.g., radio clock)
   * 2-15     secondary reference (via NTP or SNTP)
   * 16-255   reserved
   *
   */
  private short _stratum = 0;
  
  /** bits 16-23, signed.
   * RFC2030: Poll Interval: This is an eight-bit signed integer indicating the
   * maximum interval between successive messages, in seconds to the
   * nearest power of two. The values that can appear in this field
   * presently range from 4 (16 s) to 14 (16284 s); however, most
   * applications use only the sub-range 6 (64 s) to 10 (1024 s).
   */
  private byte _pollInterval = 0;
  
  /**
   * bits 24-31, signed.
   * RFC2030: Precision: This is an eight-bit signed integer indicating the
   * precision of the local clock, in seconds to the nearest power of two.
   * The values that normally appear in this field range from -6 for
   * mains-frequency clocks to -20 for microsecond clocks found in some
   * workstations.
   */
  private byte _precision = 0;
  
  /** bytes 4-7.
   * RFC2030: Root Delay: This is a 32-bit signed fixed-point number indicating the
   * total roundtrip delay to the primary reference source, in seconds
   * with fraction point between bits 15 and 16. Note that this variable
   * can take on both positive and negative values, depending on the
   * relative time and frequency offsets. The values that normally appear
   * in this field range from negative values of a few milliseconds to
   * positive values of several hundred milliseconds.
   */
  private NTPTimeStamp32 _rootDelay = null;
  
  /** bytes 8-11.
   * RFC2030: Root Dispersion: This is a 32-bit unsigned fixed-point number
   * indicating the nominal error relative to the primary reference
   * source, in seconds with fraction point between bits 15 and 16. The
   * values that normally appear in this field range from 0 to several
   * hundred milliseconds.
   */
  private NTPTimeStamp32 _rootDispersion = null;
  
  /** bytes 12-15.
   * RFC2030: Reference Identifier: This is a 32-bit bitstring identifying the
   * particular reference source. In the case of NTP Version 3 or Version
   * 4 stratum-0 (unspecified) or stratum-1 (primary) servers, this is a
   * four-character ASCII string, left justified and zero padded to 32
   * bits. In NTP Version 3 secondary servers, this is the 32-bit IPv4
   * address of the reference source. In NTP Version 4 secondary servers,
   * this is the low order 32 bits of the latest transmit timestamp of the
   * reference source. NTP primary (stratum 1) servers should set this
   * field to a code identifying the external reference source according
   * to the following list. If the external reference is one of those
   * listed, the associated code should be used. Codes for sources not
   * listed can be contrived as appropriate.
   */
  private long _referenceIdentifier = 0L;
  private static HashMap _referenceIdentifierToString = new HashMap();
  // ah yes, the static block
  static {
    _referenceIdentifierToString.put(
    "LOCL", "uncalibrated local clock used as a primary reference for" +
    "a subnet without external means of synchronization");
    
    _referenceIdentifierToString.put(
    "PPS", "atomic clock or other pulse-per-second source" +
    "individually calibrated to national standards");
    _referenceIdentifierToString.put(
    "ACTS", "NIST dialup modem service");
    _referenceIdentifierToString.put(
    "USNO", "USNO modem service");
    _referenceIdentifierToString.put(
    "PTB", "PTB (Germany) modem service");
    _referenceIdentifierToString.put(
    "TDF", "Allouis (France) Radio 164 kHz");
    _referenceIdentifierToString.put(
    "DCF", "Mainflingen (Germany) Radio 77.5 kHz");
    _referenceIdentifierToString.put(
    "MSF", "Rugby (UK) Radio 60 kHz");
    _referenceIdentifierToString.put(
    "WWV", "Ft. Collins (US) Radio 2.5, 5, 10, 15, 20 MHz");
    _referenceIdentifierToString.put(
    "WWVB", "Boulder (US) Radio 60 kHz");
    _referenceIdentifierToString.put(
    "WWVH", "Kaui Hawaii (US) Radio 2.5, 5, 10, 15 MHz");
    _referenceIdentifierToString.put(
    "CHU", "Ottawa (Canada) Radio 3330, 7335, 14670 kHz");
    _referenceIdentifierToString.put(
    "LORC", "LORAN-C radionavigation system");
    _referenceIdentifierToString.put(
    "OMEG", "OMEGA radionavigation system");
    _referenceIdentifierToString.put(
    "GPS", "Global Positioning Service");
    _referenceIdentifierToString.put(
    "GOES", "Geostationary Orbit Environment Satellite");
  }
  
  /** bytes 16-23.
   * RFC2030: Reference Timestamp: This is the time at which the local clock was
   * last set or corrected, in 64-bit timestamp format.
   */
  private NTPTimeStamp64 _referenceTS = null;
  
  /** bytes 24-31.
   *
   * RFC2030: Originate Timestamp: This is the time at which the request departed
   * the client for the server, in 64-bit timestamp format.
   */
  private NTPTimeStamp64 _originateTS = null;
  
  /** bytes 32-39.
   * RFC2030: Receive Timestamp: This is the time at which the request arrived at
   * the server, in 64-bit timestamp format.
   */
  private NTPTimeStamp64 _receiveTS = null;
  
  /** bytes 40-47.
   * RFC2030: Transmit Timestamp: This is the time at which the reply departed the
   * server for the client, in 64-bit timestamp format.
   */
  private NTPTimeStamp64 _transmitTS = null;
  
  /** bytes 128-147 (4 for key ident, 16 for digest)
   * RFC2030: Authenticator (optional): When the NTP authentication scheme is
   * implemented, the Key Identifier and Message Digest fields contain the
   * message authentication code (MAC) information defined in Appendix C
   * of RFC-1305.
   */
  private byte[] _keyIdent = null;
  private byte[] _messageDigest = null;
  
  /**  for reading from byte array.
   * initialized on first use
   */
  private ByteBuffer _bytes = null;
  
  private NTPMessage() {}
  
  public NTPMessage(ByteBuffer input) throws IOException {
    if (input == null) {
      throw new IOException("NTPMessage(BB): null input");
    }
    if (input.remaining() < 48) {
      throw new IOException("NTPMessage(BB): invalid input length " +
      input.remaining());
    }
    
    if (_bytes != input) {
      _bytes = input;     
    } else {
      _bytes.rewind();
    }
      
      // parse the bytes to get a long
      if (_bytes == null) {
        throw new IOException("NTPTSRaw64 ctor: dis uninitialized");
      }
      
      int _header = _bytes.getInt();
      _precision = (byte)(_header & 0xFF);
      _pollInterval = (byte)((_header >> 8) & 0xFF);
       _stratum = (short)((_header >> 16) & 0xFF);
      _mode = (byte)((_header >> 24) & 0x07);
      _version = (byte)((_header >> 27) & 0x07);
      _leapIndicator = (byte)((_header >> 30) & 0x03);
     
      _rootDelay = new NTPTimeStamp32(_bytes);
      _rootDispersion = new NTPTimeStamp32(_bytes);
      _referenceIdentifier = ((long) _bytes.getInt()) & 0xFFFFFFFFL;
      
      _referenceTS = new NTPTimeStamp64(_bytes);
      _originateTS = new NTPTimeStamp64(_bytes);
      _receiveTS = new NTPTimeStamp64(_bytes);
      _transmitTS = new NTPTimeStamp64(_bytes);
      
      // ignoring auth stuff for now
    }
    
    public long calcDelay(NTPTimeStamp64 destTS) {
      long T1s = _originateTS._secs;  long T1f = _originateTS._frac;
      long T2s = _receiveTS._secs;  long T2f = _receiveTS._frac;
      long T3s = _transmitTS._secs;  long T3f = _transmitTS._frac;
      long T4s = destTS._secs;  long T4f = destTS._frac;
      long resultS = ((T4s - T1s) - (T2s - T3s)); // / 2 * 1000
      long resultF = ((T4f - T1f) - (T2f - T3f));
      //TODO: the below is wrong
      long result = (resultS * 1000L) + resultF;
      if (DEBUG) {
        System.out.println("calcDelay T1 T2 T3 T4: " + T1s + " " + T2s + " " +
          T3s + " " + T4s + " -> " + result);
      }
      return (long)(result * 1000.0);
    }
    
    public long calcOffset(NTPTimeStamp64 destTS) {
      double T1 = _originateTS.toDelta();
      double T2 = _receiveTS.toDelta();
      double T3 = _transmitTS.toDelta();
      double T4 = destTS.toDelta();
      double result = ((T2 - T1) + (T3 - T4)) / 2.0;
      if (DEBUG) {
        System.out.println("calcOffset T1 T2 T3 T4: " + T1 + " " + T2 + " " +
          T3 + " " + T4 + " -> " + result);
      }
      return (long)(result * 1000.0);
    }
    
    
    public String toString() {
      return
      "leapIndicator = " + _leapIndicator + "\n" +
      "version =       " + _version + "\n" +
      "mode =          " + _mode + "\n" +
      "stratum =       " + _stratum + "\n" +
      "pollInterval =  " + _pollInterval + "\n" +
      "precision =     " + _precision + "\n" +      
      
      "rootDelay =           " + _rootDelay + "\n" +
      "rootDispersion =      " + _rootDispersion + "\n" +
      "referenceIdentifier = " + _referenceIdentifier + "\n" +
      
      "referenceTS = " + _referenceTS.toFull() + "\n" +
      "originateTS = " + _originateTS.toFull() + "\n" +
      "receiveTS =   " + _receiveTS.toFull() + "\n" +
      "transmitTS =  " + _transmitTS.toFull();
      
    }
    
    /**
     * take the seconds, return
     * 8 bytes in NTP Timestamp format
     * for use in Tx and Rx of NTP info
     */
  /*
  public byte[] getBytes() {
   
    BigInteger bar = BigInteger.valueOf(secs);
    byte[] b1 = bar.toByteArray();
   
   
    if (b1.length > 4) {
      for (int i=0; i<4; i++) {
        b1[i] = b1[i+1];
      }
    }
   
    BitSet bs = doubleToFixedPoint(fracsecs);
    byte[] b2 = toByteArray(bs);
   
    byte[] output = new byte[8];
   
    for (int i=0; i< 4; i++) {
      output[i] = b1[i];
      output[i+4] = b2[i];
    }
   
    return output;
   
  }
   */
    
    /**
     * Returns a bitset containing the values in bytes.
     * The byte-ordering of bytes must be big-endian which means
     * the most significant bit is in element 0.
     */
  /*
  public static BitSet fromByteArray(byte[] bytes) {
    BitSet bits = new BitSet(32);
   
    for (int i=0; i<4; i++) {
      for (int j=0; j<8; j++) {
        if ((bytes[4-i-1] & (1 << j)) > 0) {
          bits.set(8*i + j);
        }
      }
    }
   
    return bits;
  }
   */
    
    /**
     * BitSet -> ByteArray
     */
  /*
  private static byte[] toByteArray(BitSet bits) {
    byte[] bytes = new byte[4];
   
    for (int i=0; i<4; i++) {
      for (int j=0; j<8; j++) {
        if (bits.get(8*i + j)) {
          bytes[4-(i)-1] |= 1 << j;
        }
      }
    }
   
    return bytes;
  }
   */
    
    /**
     * DEBUG method
     */
    private static void printByteArray(byte[] b) {
      for (int i=0; i<b.length; i++) {
        System.out.print(b[i] + " ");
        if (i % 8 == 7) System.out.print("\n");
      }
    }
    
    
    /**
     * bytes to a floating point
     */
  /*
  private static float bytesToFloat(byte[] b) {
   
    float output = 0;
   
    BitSet bs = fromByteArray(b);
   
    for (int i=0; i<32; i++) {
      if (bs.get(i)) {
        output += Math.pow(2,i-32);
      }
    }
   
    return output;
   
  }
   */
    
    /**
     * floating point to fixed point
     */
  /*
  private static BitSet doubleToFixedPoint(double foo) {
   
    BitSet output = new BitSet(32);
   
    foo = foo % 1;
    int idx = 0;
   
    while (idx < 32) {
      if (foo >= 0.5) {
        output.set(31-idx);
      }
      idx++;
      foo = foo * 10;
      foo = foo % 1;
    }
   
    return output;
   
  }
   */
    
    /**
     * test program
     */
    public static void main(String args[]) throws Exception {
    /*
    Date nowDate = new Date();
    System.out.println("Date:");
    System.out.println(nowDate.getTime());
     
    System.out.println("Test default constructor");
    NTPTimeStamp now = new NTPTimeStamp();
    printByteArray(now.getBytes());
    System.out.println(now.getLong());
     
    System.out.println("Test byte[] constructor");
     
    byte[] b = new byte[8];
    b[0] = -125;
    b[1] = -86;
    b[2] = 56;
    b[3] = 48;
    b[4] = -128;
    b[5] = 0;
    b[6] = 0;
    b[7] = 0;
    NTPTimeStamp byteTest = new NTPTimeStamp(b);
    printByteArray(byteTest.getBytes());
    System.out.println(byteTest.getLong());
     
    System.out.println("Test long constructor");
     
    long BigOffset = 2208970800000L;
    NTPTimeStamp longTest = new NTPTimeStamp(BigOffset,-5);
    printByteArray(longTest.getBytes());
    System.out.println(longTest.getLong());
     */
    }
    
    
  }
