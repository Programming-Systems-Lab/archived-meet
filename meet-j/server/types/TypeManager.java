/*
 * TypeManager.java
 *
 * Created on August 7, 2002, 5:11 PM
 */

package psl.meet.server.types;

import java.util.HashMap;

import siena.Notification;
import siena.Notifiable;
import siena.Filter;

import psl.meet.server.core.*;


/**
 * @author phil
 */
public class TypeManager extends Object implements IEvent, IModule {
    
    // keys are longs, values are AVList
    protected HashMap avLists;
    // map string to AVLists key.  this might be better handled
    // by a real DB (id, name, AVList) with two indexes
    protected HashMap avListsNames;
    protected HashMap sienas;
    
    /** Creates new TypeManager */
    public TypeManager() {
        avLists = new HashMap();
        avListsNames = new HashMap();
        sienas = new HashMap();
    }
    
    /** Siena compatible type registration 
     * Considers a Filter to be equivalent to an event type
     * Notifications are exhaustively compared to existing subs to determine
     * "types"
     */
    public int registerType(Filter f) {
        return MEET_SUCCESS;
    }
    
    /** identify this module
     * @return title of this module
     */
    public String getName() {
        return "TypeManager";
    }
    
    /**
     * returns an integer representing the status of the module
     * @return success code
     *
     */
    public int getStatus() {
        return MEET_STATUS_OK;
    }
    
    /**
     * tell a module to become active
     * @return success code
     * @throws MEETException Internal error
     */
    public int start() throws MEETException {
        return MEET_SUCCESS;
    }
    
    /**
     * tell a module to halt processing
     * @return success code
     * @throws MEETException Internal error
     */
    public int stop() throws MEETException {
        return MEET_SUCCESS;
    }
    
    /*
    private String examplePrivateMethod() {
      return "examplePrivateMethod value";
    }
     */
    
    public static class TEST extends Object {
        
        /** Unit-test {@link TypeManager}.
         * @param args the command line arguments
         */
        public static void main(String args[]) {
            /*
            System.err.println("Test results: " + new TypeManager().examplePrivateMethod());
             */
        }
        
    }


    class AV {
        public String att;
        public int type;
    }
}
