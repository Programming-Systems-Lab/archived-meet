/*
 * IManager.java
 *
 * Created on July 11, 2002, 12:58 PM
 */

package psl.meet.server.core;

/** 
 * Subsystem managers implement this interface, which allows individual
 * implementations to be added, removed, stopped, started, etc.
 * @author phil
 */
public interface IManager {
    
    public static final int MEET_SUCCESS = 0;
    
    /** 
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */    
    int getStatus();
    
    /** 
     * returns an array of strings naming the installed modules
     * @return success code
     *
     */   
    String[] list();
    
    /** 
     * add a new module to this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int add() throws MEETException;
    
    /** 
     * remove a module from this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int delete() throws MEETException;
    
    /** 
     * instruct a module to restart itself
     * @return success code
     * @throws MEETException Internal error
     *
     */   
    int reload() throws MEETException;
    
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
    
    /** 
     * stop all modules in this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int stopAll() throws MEETException;
    
    /** 
     * start all modules in this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int startAll() throws MEETException;
    
    /** 
     * distinguish one installed module as the default
     * @return success code
     * @throws MEETException Internal error
     */   
    int setDefault(String module) throws MEETException;
        
    /** 
     * freeze setup of this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int lock() throws MEETException;
    
}
