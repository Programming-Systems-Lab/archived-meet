/*
 * Event.java
 *
 * Created on August 5, 2002, 11:41 AM
 */

package psl.meet.server.types;

import java.nio.ByteBuffer;
import org.apache.log4j.Logger;


/**
 * Events have the following structure
 * byte offset     contents
 * 0               magic byte containing 0xEE
 * 1               type (byte) (see IEvent)
 * 2-3             subtype (short)
 * 4-11            timestamp (4 bytes secs since epoch, 4 bytes fraction)
 * 12-15           length of following data block in bytes (int)
 * 16-             data (byte [])
 *
 * @author phil
 */
public class Event extends Object implements IEvent {
    
    protected byte type;
    protected short subtype;
    protected int tsSecs;
    protected int tsFrac;
    protected byte [] data;
    static Logger logger = Logger.getLogger(Event.class);
    
    /** Creates new Event */
    public Event(byte type, short subtype, byte [] data, long time) {
        if (0 == time) {
            time = System.currentTimeMillis();
        }
        this.type = type;
        this.subtype = subtype;
        this.data = data;
        double curr = time / 1000.0;
        double secs = Math.floor(curr);
        double frac = curr - secs;
        tsSecs = (int) secs;
        tsFrac = (int) frac;
    }
    
    public Event(ByteBuffer bb) {
        this();
        byte b = bb.get();
        if (b != MEET_MAGIC) {
            logger.warn("Buffer has bad magic: " + b);
            // TODO: check to see if raw XML for CBR?
            return;
        }
        type = bb.get();
        subtype = bb.getShort();
        tsSecs = bb.getInt();
        tsFrac = bb.getInt();
        int length = bb.getInt();
        data = new byte[length];
        bb.get(data, 0, length);
    }
    
    protected Event() {
        this.type = 0;
        this.subtype = 0;
        this.data = null;
    }
    
    /** create a NIO buffer containing the event */
    public ByteBuffer toBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(data.length + 16);
        bb.put(MEET_MAGIC);
        bb.put(type);
        bb.putShort(subtype);
        bb.putInt(data.length);
        bb.put(data);
        return bb;
    }
    
    /*
    private String examplePrivateMethod() {
      
    }
     */
    
    public static class TEST extends Object {
        
        /** Unit-test {@link Event}.
         * @param args the command line arguments
         */
        public static void main(String args[]) {
            /*
            System.err.println("Test results: " + new Event().examplePrivateMethod());
             */
        }
        
    }
    
}
