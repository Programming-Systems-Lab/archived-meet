/*
 * IProbeable.java
 *
 * Created on July 10, 2002, 1:14 PM
 */

package psl.meet.server.core;

/** 
 * DASADA Probe interface standard.  Definitely in need of revision.
 *
 * Descriptions from e-mail from Bob Balzer 2-Jan-01.
 * @author  phil
 */
public interface IProbe {
    
    /** 
     *   The probe is activated so that it
     *   senses behavior in the running system. In this activated state, the
     *   probes may issue Sense events for some subset of the behavior they
     *   observe. 
     */    
    void activate();
    
    /**
     *   The probe
     *   is deactivated so that it stops sensing behavior in the running
     *   system. In this deactivated state, the probe may not issue any Sense
     *   events.
     */
    void deactivate();    
    
    /**
     *   Requests a list of all of the Event-Names that the 
     *   probe can generate while it is activated. This
     *   request is answered through a Generate-Sensed event.
     */
    void querySensed();
    
    /**
     *   Parameter1 through ParameterN are passed to the probe to "focus" its 
     *   sensors
     *   as specified by the parameters. How it interprets these parameters
     *   is entirely probe configuration specific.
     */
    void focus(String params[]);
    
}
