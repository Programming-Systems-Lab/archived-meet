/*
 * SNTPClock.java
 *
 * Created on November 7, 2002, 8:34 PM
 */
package psl.meet.server.clocks;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.BitSet;


/**
 *
 * @author  mkuba
 */
public class SNTPClock {
    
    // multicast or unicast client
    boolean multiCastMode;
    
    // offset from the system clock, this is updated
    // instead of actual system clock
    long offset;
 
    // automatically update at certain intervals
    long updateInterval;
    
    /** Creates a new instance of SNTPClock */
    public SNTPClock() {
    }
    
    public long getTime() {
        return 0;
    }
    
  /*  
    public ErrorDesc getAccuracy() {
        
    }
    
    public SourceDesc getSource() {
        
    }
    
    public setSource(SourceDesc src) {
        
    }
*/
    
    public void send() throws Exception {
        // create empty packet
        byte[] data = new byte[128];
        
        // how about the ASCII table, or 7bit ASCII at least
        for (int i=0; i<128; i++) {
            data[i] = (byte)i;
        }
        
        
        // create socket to localhost?
        InetAddress address = InetAddress.getLocalHost();
        DatagramSocket socket = new DatagramSocket();

        // create packet
        DatagramPacket packet = new DatagramPacket(data,128,address,3156);

        
        // send packet
        System.out.println("Attempting to send packet\n");
        socket.send(packet);
        System.out.println("Packet sent\n");
        
    }
    
    public void receive() throws Exception {
        
        //create empty packet
        byte[] data = new byte[128];
        DatagramPacket packet = new DatagramPacket(data,128);
        
        // create socket
        DatagramSocket socket = new DatagramSocket(3156);
        
        // wait for socket

        System.out.println("waiting for packet\n");
        socket.receive(packet);
            
        // print out what was received
        String msg = new String(packet.getData());

        System.out.println("received: " + msg);
        
    }
    
    public void syncTime() {
        
    }
    
    
    public static byte[] longToNTPTimeStamp(long now) {
        
        // convert msec to sec
        int time1 = (int) now / 1000;

        // get remainder for 2nd part of timestamp
        int time2 = (int) now % 1000;
        float foo = (float) time2 /  1000;
        
        System.out.println("first 32: " + time2);
        System.out.println("second 32: " + foo);
        
        time2 = Float.floatToRawIntBits(foo);
        
        byte[] b = new byte[8];
        
        // in == long since Epoch
        // must convert to NTP TimeStamp format
        // first 32 bits = seconds since 1900 00:00:00 UT
        // second 32 bits = fixed point fraction of a second
       
        b[0] = (byte)(time1 & 0xff);
        b[1] = (byte)((time1 >> 8) & 0xff);
        b[2] = (byte)((time1 >> 16) & 0xff);
        b[3] = (byte)(time1 >>> 24);

        b[4] = (byte)(time2 & 0xff);
        b[5] = (byte)((time2 >> 8) & 0xff);
        b[6] = (byte)((time2 >> 16) & 0xff);
        b[7] = (byte)(time2 >>> 24);

        return b;
    }
    
    public static long NTPTimeStampToLong(byte[] b) {
        
        int in = (b[0] & 0xff) | ((b[1] << 8) & 0xff00) | ((b[2] << 24) >>> 8) | (b[3] << 24);
        return 0;
    }       
    
    // Returns a bitset containing the values in bytes.
    // The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
    public static BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i=0; i<bytes.length*8; i++) {
            if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

    public static void printByteArray(byte[] b) {
        for (int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
        }
    }
    

    public static void floatToFixedPoint(float foo) {

	foo = foo % 1;
	int idx = 0;

	while (idx < 32) {
	    float x = foo * 2;
	    if (x>=1) {
		System.out.print("1");
	    } else {
		System.out.print("0");
	    }
	    idx++;
	    foo = foo * 10;
	    foo = foo % 1;		
	}

    }
    
    public static void main(String args[]) throws Exception {

	float myFloat = (float).25;

        System.out.println("longToNTPTimeStamp test");
        byte[] b = longToNTPTimeStamp(256000);
        printByteArray(b);



	System.out.println("floatToFixedPoint test");
	floatToFixedPoint(myFloat);

	System.out.println("BitSet test");
        System.out.println("");
        BitSet bits = fromByteArray(b);
        System.out.println(bits.toString());        


        
    }
    
}
