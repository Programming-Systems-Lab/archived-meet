/*
 * SNTPClock.java
 *
 * Created on November 7, 2002, 8:34 PM
 */



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
    
    // multicast or unicast client, unused for now
    boolean multiCastMode;
    
    // offset from the system clock, this is updated
    // instead of actual system clock
    // (keep away from native methods for now)
    private long offset;

    // roundtrip delay in communicating with NTP server
    private long delay;
 
    // automatically update clock at certain intervals
    private long updateInterval;
    
    // NTP Server to use
    private String server;
    private int port;

    private int timezone = -5;

    /** Creates a new instance of SNTPClock */
    public SNTPClock() {
	server = "";
	port = 0;
	timezone = 0;
	updateInterval = -1;
	delay = 0;
	offset = 0;
    }

    public SNTPClock(String srvr, int p, int tz, long updateint) {
	server = srvr;
	port = p;
	timezone = tz;
	updateInterval = updateint;
	delay = 0;
	offset = 0;
    }

    public void setTimezone(int tz) {
	timezone = tz;
    }

    public void setSource(String srvr, int p) {
	server = srvr;
	port = p;
    }

    public void setUpdateInterval(long ui) {
	updateInterval = ui;
    }

    public long getTime() {
	NTPTimeStamp now = new NTPTimeStamp(timezone);
	return now.getLong() + offset;
    }

    public long getOffset() {
	return offset;
    }

    public long getDelay() {
	return delay;
    }


    public void sync() throws Exception {
	byte[] b = this.createSNTPRequest();
	this.send(b);
    }
    
    public void send(byte[] data) throws Exception {

        // create socket to server
        InetAddress address = InetAddress.getByName(server);

        DatagramSocket socket = new DatagramSocket();
	socket.connect(address,port);

        // create packet
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);

        
        // send packet
        System.out.println("Attempting to send packet to " + socket.getInetAddress() + " (" + socket.getPort()  + ")\n");
        socket.send(packet);
        System.out.println("Packet sent\n");
        


	
	byte[] rx = new byte[48];
	packet = new DatagramPacket(rx,48);

	//socket.disconnect();
	//socket.close();

	
        //System.out.println("waiting for packet\n");
        socket.receive(packet);

        NTPTimeStamp T4 = new NTPTimeStamp(timezone);

	byte[] t1 = new byte[8];
	for (int i=0; i<8; i++) {
	    t1[i] = rx[24+i];
	}
	NTPTimeStamp T1 = new NTPTimeStamp(t1);

	byte[] t2 = new byte[8];
	for (int i=0; i<8; i++) {
	    t2[i] = rx[32+i];
	}
	NTPTimeStamp T2 = new NTPTimeStamp(t2);

	byte[] t3 = new byte[8];
	for (int i=0; i<8; i++) {
	    t3[i] = rx[40+i];
	}
	NTPTimeStamp T3 = new NTPTimeStamp(t3);

	// delay and offset, as defined in RFC 2030

	delay = ( (T4.getLong() - T1.getLong()) - (T2.getLong() - T3.getLong()) );
	
	offset = (long)(( (T2.getLong() - T1.getLong()) + (T3.getLong() - T4.getLong()) ) / 2);



	//DEBUG
	System.out.println("T1 = " + T1.getLong());
	System.out.println("T2 = " + T2.getLong());
	System.out.println("T3 = " + T3.getLong());
	System.out.println("T4 = " + T4.getLong());

	System.out.println("delay = " + delay);
	System.out.println("offset = " + offset);
    
    }


    /// currently unused, could be useful for multi/anycast    
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

    
    public byte[] createSNTPRequest() {

	NTPTimeStamp ntptime = new NTPTimeStamp(timezone);
	byte[] time = ntptime.getBytes();
	
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

    
    // DEBUG
    public static void printByteArray(byte[] b) {
        for (int i=0; i<b.length; i++) {
            System.out.print(b[i] + " ");
	    if (i % 8 == 7) System.out.print("\n");
        }
    }
    

    
    public static void main(String args[]) throws Exception {

	SNTPClock sc = new SNTPClock(args[0], Integer.parseInt(args[1]), -5, 0);

	while (true) {
	    sc.sync();
	    Thread.sleep(1000);
	}

    }
    
}
