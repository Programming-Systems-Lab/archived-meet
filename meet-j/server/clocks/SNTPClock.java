/*
 * SNTPClock.java
 *
 * Created on November 7, 2002, 8:34 PM
 */

package psl.meet.server.clocks;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.io.IOException;
import java.util.Date;

import psl.meet.server.clocks.NTPTimeStamp64;


/**
 *
 * @author  mkuba
 */
public class SNTPClock implements MEETClock {
  
  // print debug statements
  final static boolean DEBUG = true;
  
  // multicast or unicast client, unused for now
  boolean multiCastMode;
  
  // offset from the system clock, this is updated
  // instead of actual system clock
  // (keep away from native methods for now)
  private long _offset;
  
  // roundtrip delay in communicating with NTP server
  private long _delay;
  
  // automatically update clock at certain intervals
  // unused for now
  private long updateInterval;
  
  // NTP Server to use
  private String _server = "localhost";
  private int _port = 123;
  
  // offset from UT, in hours
  private int timezone = 0;
  
  private DatagramChannel dgc = null;
  
  
  /** Creates a new instance of SNTPClock */
  public SNTPClock() {    
    updateInterval = -1;
    _delay = 0;
    _offset = 0;
    try {
      dgc = DatagramChannel.open();
    } catch (IOException ioe) {ioe.printStackTrace();}
  }
  
  /**
   * creates a new SNTPClock
   */
  public SNTPClock(String srvr, int p, long updateint) {
    _server = srvr;
    _port = p;    
    updateInterval = updateint;
    _delay = 0;
    _offset = 0;
    try {
      dgc = DatagramChannel.open();
    } catch (IOException ioe) {ioe.printStackTrace();}
  }
  
  /**
   * set what timezone we are in
   */
  public void setTimezone(int tz) {
    timezone = tz;
  }
  
  /**
   * set which NTP server to use
   */
  public void setSource(String srvr, int p) {
    _server = srvr;
    _port = p;
  }
  
  /**
   * set the update interval
   */
  public void setUpdateInterval(long ui) {
    updateInterval = ui;
  }
  
  /**
   * return the clock name
   * from MEETClock interface
   */
  public String getClockName() {
    return "SNTPClock";
  }
  
  /**
   * return # of milliseconds since Jan 1 1900, 00:00:00 UT
   */
  public long getTime() {
    // NTPTimeStamp now = new NTPTimeStamp(timezone);
    // return now + offset;
    return System.currentTimeMillis() + _offset;
  }
  
  /**
   * get the offset from the reference clock
   * in number of milliseconds
   */
  public long getOffset() {
    return _offset;
  }
  
  /**
   * get the calculated network delay
   */
  public long getDelay() {
    return _delay;
  }
  
  
  /**
   * send bytes to NTP Server
   *
   */
  public void getNTP() throws Exception {
    
    // create socket to server
    InetSocketAddress remote = new InetSocketAddress(_server, _port);
    DatagramSocket s = dgc.socket();
    if (!s.isBound()) {
      s.bind(null);
    }
    if (!dgc.isConnected()) {
      dgc.connect(remote);
      dgc.configureBlocking(false);
    }
    
    // create packet
    ByteBuffer bbOut = ByteBuffer.allocate(48);
    bbOut.order(ByteOrder.BIG_ENDIAN);
    bbOut.clear();
    // mode = 3, version = 3, LI = 0
    bbOut.put(0, (byte) 35);
    // rfc2030: put our current time in Transmit field
    long now = NTPTimeStamp64.currentNTP(_offset);
    if (DEBUG) {
      NTPTimeStamp64 nowTS = new NTPTimeStamp64(now);
      System.out.println("calced current time as " + nowTS.toFull());
    }
    bbOut.putLong(40, now);
    bbOut.rewind();    
    NTPMessage ntpOut = new NTPMessage(bbOut);
    System.out.println("preparing to send NTP query:" + ntpOut);
    bbOut.rewind();
    
    
   // send packet
    if (DEBUG) System.out.println("Attempting to send packet to " + remote);
    dgc.send(bbOut, remote);
    if (DEBUG) System.out.println("Packet sent\n");
    
    // create receiving packet
    ByteBuffer bbIn = ByteBuffer.allocate(48);
    bbIn.order(ByteOrder.BIG_ENDIAN);
    bbIn.clear();
    InetSocketAddress rcvAddr = (InetSocketAddress) dgc.receive(bbIn);
    if (rcvAddr == null) {
      System.out.println("got null packet");
      return;
    }
    NTPTimeStamp64 T4 = new NTPTimeStamp64(NTPTimeStamp64.currentNTP(_offset));
    
    bbIn.rewind();
    if (DEBUG) System.out.println("Received packet from " + rcvAddr);
    System.out.println("buffer remaining:" + bbIn.remaining());
    /* 
     byte ba[] = bbIn.array();
    System.out.println("backing array is:");
    for (int i=0; i<48; i+=4) {
      for (int j=0; j<4; ++j) {
        System.out.print(ba[i+j] + "  ");
      }
      System.out.print('\n');
    }
     */
    NTPMessage ntpIn = new NTPMessage(bbIn);
    System.out.println("parsed message is:\n" + ntpIn);
    if (ntpIn.getVersion() < 3) {
      return;
    }
    
    _delay = ntpIn.calcDelay(T4);
    _offset = ntpIn.calcOffset(T4);
    // parse Timestamps from received packet
    
    // delay and offset, as defined in RFC 2030
    
    //_delay = ( (T4 - T1) - (T2 - T3) );
    
    // _offset = (long)(( (T2 - T1) + (T3 - T4) ) / 2);
    
    
    
    //DEBUG
    if (DEBUG) {
      // System.out.println("T1-4 = " + T1 +", "+T2+", "+T3+", "+T4);
      System.out.println("delay, offset = " + _delay + ", " + _offset);      
    }
    
  }
  
  
  /**
   * receive NTP information (unused)
   */
  public void receive() throws Exception {
    
    /**
     * //create empty packet
     * byte[] data = new byte[40];
     * DatagramPacket packet = new DatagramPacket(data,40);
     *
     * // create socket
     * DatagramSocket socket = new DatagramSocket(123);
     *
     * // wait for socket
     *
     * System.out.println("waiting for packet\n");
     * socket.receive(packet);
     *
     * // print out what was received
     * String msg = new String(packet.getData());
     *
     * System.out.println("received: ");
     * printByteArray(packet.getData());
     */
  }
  
  
  
  
  // DEBUG
  public static void printByteArray(byte[] b) {
    for (int i=0; i<b.length; i++) {
      System.out.print(b[i] + " ");
      if (i % 8 == 7) System.out.print("\n");
    }
  }
  
  
  /**
   * test program
   */
  public static void main(String args[]) throws Exception {
    
    if (args.length < 2) {
      System.out.println("Usage: java SNTPClock <ntp server> <port>");
      System.exit(0);
    }
    
    SNTPClock sc = new SNTPClock(args[0], Integer.parseInt(args[1]), 0);
    
    long oldOffset = 0;
    
    System.out.println("time\t\toffset\t\tdelta");
    
    while (true) {
      if (DEBUG) System.out.println("about to sc.getNTP()");
      sc.getNTP();
      
      System.out.println(new Date(sc.getTime()) + "\t\t" +
        sc.getOffset() + "\t\t" + (sc.getOffset() - oldOffset));
      oldOffset = sc.getOffset();
      
      Thread.sleep(5000);
      
    }
    
  }
  
  /** When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see     java.lang.Thread#run()
   *
   */
  public void run() {
  }
  
}
