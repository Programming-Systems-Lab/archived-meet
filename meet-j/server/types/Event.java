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
 * byte offset             contents
 * 0                        magic byte containing 0xEE
 * 1                        type byte (see IEvent)
 * 2-3                      subtype
 * 4-7                      length of following data block in bytes
 * 8-                       data
 *
 * @author phil
 */
public class Event extends Object implements IEvent {
    
    private byte type;
    private short subtype;
    private byte [] data;
    static Logger logger = Logger.getLogger(Event.class);
    
    /** Creates new Event */
    public Event(byte type, short subtype, byte [] data) {
        this.type = type;
        this.subtype = subtype;
        this.data = data;
    }
    
    public Event(ByteBuffer bb) {
        this();
        byte b = bb.get();
        if (b != MEET_MAGIC) {
            logger.warn("Buffer has bad magic: " + b);
            return;
        }
        type = bb.get();
        subtype = bb.getShort();
        int length = bb.getInt();
        data = new byte[length];
        bb.get(data, 0, length);
    }
    
    protected Event() {
        this.type = 0;
        this.subtype = 0;
        this.data = null;
    }

    /** Get a NIO buffer containing the event */
    public ByteBuffer toBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(data.length + 8);
        bb.put(MEET_MAGIC);
        bb.put(type);
        bb.putShort(subtype);
        bb.putInt(data.length);
        bb.put(data);
        return bb;
    }
    
    /*
    private String examplePrivateMethod() {
      return "examplePrivateMethod value";
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
