/*
 * Manager.java
 *
 * Created on July 11, 2002, 1:15 PM
 */

package psl.meet.server.core;

import org.apache.log4j.Logger;
import java.util.LinkedHashMap;

/**
 *
 * @author  phil
 */
public class Manager implements IProbe, IManager {

    static Logger logger = Logger.getLogger(Manager.class);
    
    protected LinkedHashMap modules;
    protected String myName;
    
    /** Creates a new instance of Manager */
    public Manager() {
        this(null);
    }
    
    public Manager (String name) {
        modules = new LinkedHashMap();
        myName = name;
        logger.debug("Manager " + name + ": created");
    }
    
    
    /** 
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */    
    int getStatus();
    
    /** 
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */   
    String[] list();
    
     /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int add() throws MEETException {
    }
    
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int delete() throws MEETException {
    }
    
    
    
    /** Return a string uniquely identifying this instance of the probe type.
     * Ideally globabally unique, but at least unique in combination with location
     */
    public String getInstance() {
    }
    
    /** return a string representing the location of this probe, generally based
     * on the location/identity of the "probee"
     */
    public String getLocation() {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */
    public int getStatus() {
    }
    
    /** return a string indicating the class of this probe
     */
    public String getType() {
    }
    
    
    public void install(String confName, String host, String system) {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */
    public String[] list() {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int lock() throws MEETException {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     *
     */
    public int reload() throws MEETException {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int start() throws MEETException {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int startAll() throws MEETException {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int stop() throws MEETException {
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     * @throws MEETException Internal error
     */
    public int stopAll() throws MEETException {
    }
    
    
    /**
     *   The probe is activated so that it
     *   senses behavior in the running system. In this activated state, the
     *   probes may issue Sense events for some subset of the behavior they
     *   observe.
     */
    public void activate() {
    }
    
    /**   The probe
     *   is deactivated so that it stops sensing behavior in the running
     *   system. In this deactivated state, the probe may not issue any Sense
     *   events.
     */
    public void deactivate() {
    }
    
    /**   Parameter1 through ParameterN are passed to the probe to "focus" its
     *   sensors
     *   as specified by the parameters. How it interprets these parameters
     *   is entirely probe configuration specific.
     */
    public void focus(String[] params) {
    }
    
    /**   Requests a list of all of the Event-Names that the
     *   probe can generate while it is activated. This
     *   request is answered through a Generate-Sensed event.
     */
    public void querySensed() {
    }
    
    /**
     * start all modules in this subsystem
     * @return success code
     * @throws MEETException Internal error
     */
    public int setDefault(String module) throws MEETException {
    }
    
}
