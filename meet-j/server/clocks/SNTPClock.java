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
public class SNTPClock implements MEETClock {

    // print debug statements
    final boolean DEBUG = false;
    
    // multicast or unicast client, unused for now
    boolean multiCastMode;
    
    // offset from the system clock, this is updated
    // instead of actual system clock
    // (keep away from native methods for now)
    private long offset;

    // roundtrip delay in communicating with NTP server
    private long delay;
 
    // automatically update clock at certain intervals
    // unused for now
    private long updateInterval;
    
    // NTP Server to use
    private String server;
    private int port;

    // offset from UT, in hours
    private int timezone;


    /** Creates a new instance of SNTPClock */
    public SNTPClock() {
	server = "";
	port = 0;
	timezone = 0;
	updateInterval = -1;
	delay = 0;
	offset = 0;
    }

    /**
     * creates a new SNTPClock
     */
    public SNTPClock(String srvr, int p, int tz, long updateint) {
	server = srvr;
	port = p;
	timezone = tz;
	updateInterval = updateint;
	delay = 0;
	offset = 0;
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
	server = srvr;
	port = p;
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
	NTPTimeStamp now = new NTPTimeStamp(timezone);
	return now.getLong() + offset;
    }

    /**
     * get the offset from the reference clock
     * in number of milliseconds
     */
    public long getOffset() {
	return offset;
    }

    /**
     * get the calculated network delay
     */
    public long getDelay() {
	return delay;
    }

    /**
     * synchronize with NTP server
     */
    public void sync() throws Exception {
	byte[] b = this.createSNTPRequest();
	this.send(b);
    }


    /**
     * send bytes to NTP Server
     *
     */
    public void send(byte[] data) throws Exception {

        // create socket to server
        InetAddress address = InetAddress.getByName(server);

        DatagramSocket socket = new DatagramSocket();
	socket.connect(address,port);

        // create packet
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);

        
        // send packet
        if (DEBUG) System.out.println("Attempting to send packet to " + socket.getInetAddress() + " (" + socket.getPort()  + ")\n");

        socket.send(packet);

        if (DEBUG) System.out.println("Packet sent\n");
	
	// create receiving packet
	byte[] rx = new byte[48];
	packet = new DatagramPacket(rx,48);

	//socket.disconnect();
	//socket.close();

	
	// receive packet
        socket.receive(packet);

	// calculate timestamp for time packet was received
	// this is occasionally not correct, probably because
	// of java instruction reordering
        NTPTimeStamp T4 = new NTPTimeStamp(timezone);


	// parse Timestamps from received packet

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
	if (DEBUG) {
	    System.out.println("T1 = " + T1.getLong());
	    System.out.println("T2 = " + T2.getLong());
	    System.out.println("T3 = " + T3.getLong());
	    System.out.println("T4 = " + T4.getLong());
	    
	    System.out.println("delay = " + delay);
	    System.out.println("offset = " + offset);
	}

    }


    /**
     * receive NTP information (unused)
     */
    public void receive() throws Exception {

        /**
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
	*/
    }


    /**
     * create a valid NTP Request
     */
    public byte[] createSNTPRequest() {

	NTPTimeStamp ntptime = new NTPTimeStamp(timezone);
	byte[] time = ntptime.getBytes();
	
	byte[] output = new byte[48];
	
	for (int i=0; i < output.length; i++) {
	    output[i] = 0;
	}

	// just the bytes we need to fill in, hardcoded
	// every request has the same first 4 bytes
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
    

    /**
     * test program
     */
    public static void main(String args[]) throws Exception {

	if (args.length < 2) {
	    System.out.println("Usage: java SNTPClock <ntp server> <port>");
	    System.exit(0);
	}

	SNTPClock sc = new SNTPClock(args[0], Integer.parseInt(args[1]), -5, 0);

	long oldOffset = 0;

	System.out.println("offset\t\tdelta");

	while (true) {
	    sc.sync();
	    Thread.sleep(2000);

	    System.out.println(sc.getOffset() + "\t\t" + (sc.getOffset() - oldOffset));
	    oldOffset = sc.getOffset();

	}

    }
    
}
