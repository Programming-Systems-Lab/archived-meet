/*
 * GUID.java
 *
 * Created on August 14, 2002, 6:22 PM
 */

package psl.meet.server.core;

import java.util.Random;

/** not really a GUID, at least not the
 * http://www.webdav.org/specs/draft-leach-uuids-guids-01.txt
 * type.  Java gives no way to get at the MAC, and IP is a poor
 * substitute.
 *
 * @author  phil
 */
public class GUID {
    
    private static Random rand = new Random();
    private long nodeID;
    private long localID;
    
    /** Creates a new instance of GUID */
    public GUID() {
       nodeID = Manager.getNodeID();
       localID = rand.nextLong();
    }
    
}
