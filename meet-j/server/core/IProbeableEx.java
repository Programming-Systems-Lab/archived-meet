/*
 * IProbeableEx.java
 *
 * Created on July 11, 2002, 12:41 PM
 */

package psl.meet.server.core;

/**
 *
 * @author  Phil Gross
 */
public interface IProbeableEx extends IProbeable {
    
    /**
     * return a string indicating the class of this probe
     */
    String getType();
    
    /**
     * Return a string uniquely identifying this instance of the probe type.
     * Ideally globabally unique, but at least unique in combination with location
     */
    String getInstance();
    
    /**
     * return a string representing the location of this probe, generally based 
     * on the location/identity of the "probee"
     */
    String getLocation();

}
