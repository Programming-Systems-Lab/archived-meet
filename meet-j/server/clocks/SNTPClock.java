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

    // built in offset
    // most systems count from 1 Jan 1970
    long sys_offset;

 
    // automatically update clock at certain intervals
    long updateInterval;
    
    // NTP Server to use
    public String server;
    public int port;


    /** Creates a new instance of SNTPClock */
    public SNTPClock() {
	// number of milliseconds from 1970 to 1970
	sys_offset = 2208970800000;
    }


    public SNTPClock(long s_offset) {
	sys_offset = s_offset;
    }

    
    public long getTime() {
	Date now = new Date();
        return now.getTime() + offset + sys_offset;
    }


    // get time relative to the system time
    // i.e. do not add the offset from 1970 -> 1900
    public long getTimeRelSysTime() {
	return now.getTime() + offset;
    }


    public long getOffset() {
	return offset;
    }
    


    public setSource(SourceDesc src) {
        
    }

    
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

    
    public static void printByteArray(byte[] b) {
        for (int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
	    if (i % 8 == 7) System.out.print("\n");
        }
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
