/*
 * IManager.java
 *
 * Created on July 11, 2002, 12:58 PM
 */

package psl.meet.server.core;

/** 
 * Functions of an individual module
 * @author phil
 */
public interface IModule {
    
    // result codes
    public static final int MEET_SUCCESS = 0;
    
    // status codes
    public static final int MEET_STATUS_OK = 0;
    
    /** 
     * returns an integer representing the status of the module
     * @return success code
     *
     */    
    int getStatus();
        
    /**
     * identify this module
     * @return title of this module
     */
    String getName();

    /** 
     * tell a module to halt processing
     * @return success code
     * @throws MEETException Internal error
     */   
    int stop() throws MEETException;
    
    /** 
     * tell a module to become active
     * @return success code
     * @throws MEETException Internal error
     */   
    int start() throws MEETException;
    
    
}
