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
    // (keep away from native methods for now)
    long offset;
 
    // automatically update clock at certain intervals
    long updateInterval;
    
    // NTP Server to use
    public String server;
    public int port;


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
    
    public void send(byte[] data) throws Exception {

        // create empty packet
        //byte[] data = new byte[40];

        
        // create socket to server
        InetAddress address = InetAddress.getByName(server);
	//printByteArray(address.getAddress());
	//InetAddress address = InetAddress.getLocalHost();

        DatagramSocket socket = new DatagramSocket();
	socket.connect(address,port);

        // create packet
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);

        
        // send packet
        System.out.println("Attempting to send packet to " + socket.getInetAddress() + " (" + socket.getPort()  + ")\n");
        socket.send(packet);
        System.out.println("Packet sent\n");
        


	
	byte[] rx = new byte[128];
	packet = new DatagramPacket(rx,128);

	//socket.disconnect();
	//socket.close();

	
        System.out.println("waiting for packet\n");
        socket.receive(packet);
            
        // print out what was received
        //String msg = new String(packet.getData());

        System.out.println("received: ");
        printByteArray(packet.getData());
		
	
	
    }
    
    public void receive() throws Exception {
        
        //create empty packet
        byte[] data = new byte[40];
        DatagramPacket packet = new DatagramPacket(data,40);
        
        // create socket
        DatagramSocket socket = new DatagramSocket(123);
        
        // wait for socket

        System.out.println("waiting for packet\n");
        socket.receive(packet);

        // print out what was received
        String msg = new String(packet.getData());

        System.out.println("received: ");
        printByteArray(packet.getData());

    }
    
    public void syncTime() {
        
    }
    
    
    public static byte[] longToNTPTimeStamp(long now) {
        
        // convert msec to sec
        int time1 = (int) now / 1000;

        // get remainder for 2nd part of timestamp
        long time2 = now % 1000;
        float foo = (float) time2 /  1000;
        
	//DEBUG
        //System.out.println("first 32: " + time1);
        //System.out.println("second 32: " + foo);

        BitSet bs = floatToFixedPoint(foo);
        byte[] b2 = toByteArray(bs);
        
        byte[] b = new byte[8];
        
        // in == long since Epoch
        // must convert to NTP TimeStamp format
        // first 32 bits = seconds since 1900 00:00:00 UT
        // second 32 bits = fixed point fraction of a second
       
        b[0] = (byte)(time1 & 0xff);
        b[1] = (byte)((time1 >> 8) & 0xff);
        b[2] = (byte)((time1 >> 16) & 0xff);
        b[3] = (byte)(time1 >>> 24);

	b[4] = b2[0];
	b[5] = b2[1];
	b[6] = b2[2];
	b[7] = b2[3];

        return b;
    }


    
    public static long NTPTimeStampToLong(byte[] b) {
        
        int in = (b[0] & 0xff) | ((b[1] << 8) & 0xff00) | ((b[2] << 24) >>> 8) | (b[3] << 24);
        return 0;
    }       



    public static byte[] createSNTPRequest(byte[] time) {
	
	byte[] output = new byte[48];

	
	for (int i=0; i < output.length; i++) {
	    output[i] = 0;
	}

	output[0] = 35;
	output[1] = 3;
	output[2] = 10;
	output[3] = -15;

	for (int i=0; i < 8; i++) {
	    output[40+i] = time[i];
	}

	return output;

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




    public static byte[] toByteArray(BitSet bits) {
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



    public static void printByteArray(byte[] b) {
        for (int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
	    if (i % 8 == 7) System.out.print("\n");
        }
    }
    

    public static BitSet floatToFixedPoint(float foo) {

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


    
    public static void main(String args[]) throws Exception {

	Date myDate = new Date();
	long now = myDate.getTime();
        byte[] b = longToNTPTimeStamp(now);
        printByteArray(b);

	System.out.println("");

	byte[] b2 = createSNTPRequest(b);
	printByteArray(b2);

	SNTPClock sc = new SNTPClock();

	sc.server = args[0];
	sc.port = Integer.parseInt(args[1]);

	sc.send(b2);

	//sc.receive();

    }
    
}
