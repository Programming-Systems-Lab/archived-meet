/*
 * IEvent.java
 *
 * Created on August 5, 2002, 11:42 AM
 */

package psl.meet.server.types;

/**
 *
 * @author  phil
 */
public interface IEvent {
    
    // first byte is filter.  Should always have 0xEE
    // weirdly, cast is needed for hex byte constants, but not decimal
    public static final byte MEET_MAGIC = (byte) 0xEE;
    
    // second byte gives top level event type
    // 0 means content based routing (CBR)
    public static final byte MEET_ETYPE_CBR         = 0;  
    
    // 1-63 reserved for system
    // name refers to the manager that will probably be interested
    public static final byte MEET_ETYPE_CORE        = 1;  
    public static final byte MEET_ETYPE_CACHE       = 2; 
    public static final byte MEET_ETYPE_CHANNEL     = 3;  
    public static final byte MEET_ETYPE_CLOCK       = 4; 
    public static final byte MEET_ETYPE_CONNECTION  = 5; 
    public static final byte MEET_ETYPE_EXECUTIVE   = 6; 
    public static final byte MEET_ETYPE_MONITOR     = 7;  
    public static final byte MEET_ETYPE_ROUTER      = 8;  
    public static final byte MEET_ETYPE_SCHEDULER   = 9; 
    public static final byte MEET_ETYPE_STORE       = 10; 
    public static final byte MEET_ETYPE_TYPE        = 11;   
    public static final byte MEET_ETYPE_USER        = 127;
    
    public static final int MEET_TYPE_ATTVAL_SET    = 0;
    public static final int MEET_TYPE_ATTVAL_LIST   = 10;
    public static final int MEET_TYPE_XML_SCHEMA    = 20;
    public static final int MEET_TYPE_JAVA          = 30;
    public static final int MEET_TYPE_CLR           = 40;
    
    public static final int MEET_PRIMITIVE_INT8     = 0;
    public static final int MEET_PRIMITIVE_INT16    = 1;
    public static final int MEET_PRIMITIVE_INT32    = 2;
    public static final int MEET_PRIMITIVE_INT64    = 3;
    
    public static final int MEET_PRIMITIVE_UINT8    = 10;
    public static final int MEET_PRIMITIVE_UINT16   = 11;
    public static final int MEET_PRIMITIVE_UINT32   = 12;
    public static final int MEET_PRIMITIVE_UINT64   = 13;
    
    
    public static final int MEET_PRIMITIVE_FLOAT32  = 20;
    public static final int MEET_PRIMITIVE_FLOAT64  = 21;
    
    public static final int MEET_PRIMITIVE_CHAR     = 100;
    public static final int MEET_PRIMITIVE_CHAR16   = 101;
    public static final int MEET_PRIMITIVE_CHAR32   = 102;
    
    // 4 byte secs since 1 Jan 1970 GMT, 4 bytes fraction
    public static final int MEET_PRIMITIVE_TSTAMP   = 200;  

}
