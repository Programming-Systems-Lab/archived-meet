/*
 * IManager.java
 *
 * Created on July 11, 2002, 12:58 PM
 */

package psl.meet.server.core;
import java.util.Iterator;

/** 
 * Subsystem managers implement this interface, which allows individual
 * implementations to be added, removed, stopped, started, etc.
 * @author phil
 */
public interface IManager extends IModule{
    
    /**
     * Tell manager where to find its configuration
     * Needed because Manager will be created from classloader with null constructor
     * @return success code
     */
    int setConfig(String confName);
    
    /** 
     * returns list of installed IModules
     * @return list of installed IModules
     *
     */   
    Iterator list() throws MEETException;
    
    /** 
     * add a new module to this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int add(IModule im) throws MEETException;
    
    /** 
     * remove a module from this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int remove(IModule im) throws MEETException;
    
    /** 
     * instruct a module to restart itself
     * @return success code
     * @throws MEETException Internal error
     *
     */   
    int reload(IModule im) throws MEETException;
    
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
    int setDefault(IModule im) throws MEETException;
        
    /** 
     * freeze setup of this subsystem
     * @return success code
     * @throws MEETException Internal error
     */   
    int lock() throws MEETException;
    
}
