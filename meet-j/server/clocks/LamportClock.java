/*
 * LamportClock.java
 *
 * Created on October 31, 2002, 1:04 PM
 */

import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

/**
 *
 * @author  mkuba
 */
public class LamportClock implements MEETClock {
    
    private long clock;
    private int INTERVAL;
    
    /**
     * Creates a new instance of LamportClock 
     */
    public LamportClock() {
        clock = 0;
	INTERVAL = 5;

        Timer t = new Timer();
        
        // update the Lamport clock by default every 5 ms
        t.scheduleAtFixedRate(new updateTask(),0,INTERVAL);
        
    }

    /**
     * constructor with user-defined interval, in ms
     */
    public LamportClock(int i) {
	clock = 0;
	INTERVAL = i;
	
	Timer t = new Timer();
	t.scheduleAtFixedRate(new updateTask(),0,INTERVAL);
    }

    /** 
     * return Lamport timestamp
     * this is from the MEETClock interface
     */
    public long getTime() {
	return getLamport();
    }

    /**
     * return clock name
     * this is from the MEETClock interface
     */
    public String getClockName() {
	return "LamportClock";
    }
    
    /**
     * return Lamport timestamp
     */
    public long getLamport() {
        return clock;
    }
    

    /**
     * synchronize the Lamport clock
     */
    public void setLamport(long time) {
        if (time > clock) {
            clock = time + 1;
        } else {
        }
    }

    /**
     * for the Timer class
     */
    class updateTask extends TimerTask {
        public void run() {
            clock++;
            //DEBUG
            //System.out.println("Clock = " + clock);
        }
    }


    /**
     * test program
     */
    public static void main(String args[]) throws Exception {

	// if invalid # of arguments
	if (args.length < 2) {
	    System.out.println("Usage: java LamportClock <tick size> <network multiplier>");
	    System.out.println("\tWhere <tick size> is the tick size of the Lamport Clock");
	    System.out.println("\tand <network multiplier> is how slow the network is compared to the tick size (i.e. 5 times slower, etc)");
	    System.exit(0);
	}

	// set up the interval and network delay
	int interval = Integer.parseInt(args[0]);
	float multiplier = Float.parseFloat(args[1]);

	// clock 1
        LamportClock l1 = new LamportClock(interval);
	l1.setLamport(2000);

	// clock 2
        LamportClock l2 = new LamportClock(interval);
	l2.setLamport(1000);

	// clock 3
        LamportClock l3 = new LamportClock(interval);

	System.out.println("Larry\tCurly\tMoe");
	System.out.println(l1.getLamport() + "\t" + l2.getLamport() + "\t" + l3.getLamport());

	// keep updating the clocks, simulating a network
	while (true) {
	    Thread.sleep(Math.min(interval*50,1000));

	    long x = l2.getLamport();
	    Thread.sleep((int)(multiplier*interval));
	    l1.setLamport(x);
	    l2.setLamport(x);

	    long y = l1.getLamport();
	    Thread.sleep((int)(multiplier*interval));
	    l2.setLamport(y);
	    l3.setLamport(y);

	    long z = l3.getLamport();
	    Thread.sleep((int)(multiplier * interval));
	    l1.setLamport(z);
	    l2.setLamport(z);

	    // print clock information
	    System.out.println(l1.getLamport() + "\t" + l2.getLamport() + "\t" + l3.getLamport());
	    
	}
    }
    
}
