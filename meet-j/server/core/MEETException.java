/*
 * MEETException.java
 *
 * Created on July 10, 2002, 1:15 PM
 */

package psl.meet.server.core;

/**
 *
 * @author  phil
 */
public class MEETException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>MEETException</code> without detail message.
     */
    public MEETException() {
    }
    
    
    /**
     * Constructs an instance of <code>MEETException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public MEETException(String msg) {
        super(msg);
    }
}
