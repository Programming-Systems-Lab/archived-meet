/*
 * TypeManager.java
 *
 * Created on August 7, 2002, 5:11 PM
 */

package psl.meet.server.types;

import java.util.HashMap;

/**
 * @author phil
 */
public class TypeManager extends Object implements IEvent {
    
    // keys are longs, values are av_list
    protected HashMap av_lists;
    
    /** Creates new TypeManager */
    public TypeManager() {
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

    class AVList {
    }
}
