/*
 * LamportClock.java
 *
 * Created on October 31, 2002, 1:04 PM
 */

package psl.meet.server.clocks;

/**
 *
 * @author  mkuba
 */
public class LamportClock {
    
    private long clock;
    
    /** Creates a new instance of LamportClock */
    public LamportClock() {
        clock = 0;
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
    
}
