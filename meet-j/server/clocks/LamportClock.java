/*
 * LamportClock.java
 *
 * Created on October 31, 2002, 1:04 PM
 */

package psl.meet.server.clocks;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author  mkuba
 */
public class LamportClock {
    
    private long clock;
    
    /** Creates a new instance of LamportClock */
    public LamportClock() {
        clock = 0;

        Timer t = new Timer();
        
        // every second
        t.scheduleAtFixedRate(new updateTask(),0,1000);
        
    }
    
    public long getLamport() {
        return clock;
    }
    
    
    public void setLamport(long time) {
        if (time > clock) {
            clock = time + 1;
        } else {
        }
    }
    
    class updateTask extends TimerTask {
        public void run() {
            clock++;
            //DEBUG
            System.out.println("Clock = " + clock);
        }
    }
    
    public static void main(String args[]) {
        LamportClock l1 = new LamportClock();
        LamportClock l2 = new LamportClock();
    }
    
}
