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

/**
 * @author phil
 */
public class TypeManager extends Object implements IEvent {
    
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
