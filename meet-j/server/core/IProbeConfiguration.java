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
public interface IProbeConfiguration {
    
    /** 
     *   The Probe-Configuration-Module defining the named Probe-Configuration-Name
     *   becomes a probe configuration on the named Host. The module contains
     *   all the code and declarations needed to construct instances of the
     *   probe configuration. The Adaptor is responsible for performing that
     *   construction when the probe configuration is Installed (see below)
     *   on a running system.
     */    
    void deploy(String confName, String host, String confModule);
    
    
    /** 
     *   Probe-Configuration-Name on Host is incorporated into the named
     *   running System. The probes defined in the probe configuration are
     *   initialized to their deactivated state (i.e.  not sensing any
     *   behavior in the running system). If System is not running at the
     *   time the Install event is received, then Probe-Configuration-Name
     *   is incorporated into that System when it is started on Host (this
     *   enables probes to sense startup behavior and corresponds to how
     *   statically placed probes would be deployed and installed).
     */    
    void install(String confName, String host, String system);
    
    
    /** 
     *   The already installed
     *   Probe-Configuration-Name on System on Host is activated so that it
     *   senses behavior in the running system. In this activated state, the
     *   probes may issue Sense events for some subset of the behavior they
     *   observe. If an Activation event, as well as an Install event, is
     *   received before the named System is running, then
     *   Probe-Configuration-Name is both incorporated into that System when
     *   it is started on Host and immediately activated.
     */    
    void activate(String confName, String host, String system);
    
    /**
     *   The named Probe-Configuration-Name is no longer a defined probe
     *   configuration on the named Host and can no longer be referred to
     *   by that name on the named Host.
     */
    void undeploy(String confName, String host);
    
    /**
     *   The Probe-Configuration-Name on Host is removed from the running
     *   System. It can no longer be Activated or Deactivated on that system
     *   before it is again Installed on that system.
     */
    void uninstall(String confName, String host, String system);
    
    /**
     *   The already installed Probe-Configuration-Name on System on Host
     *   is deactivated so that it stops sensing behavior in the running
     *   system. In this deactivated state, the probes may not issue any Sense
     *   events.
     */
    void deactivate(String confName, String host, String system);    
    
    /**
     *   Requests a list of all of the Event-Names that the named
     *   Probe-Configuration-Name can generate while it is activated. This
     *   request is answered through a Generate-Sensed event.
     */
    void querySensed(String confName, String host);
    
    /**
     *   Parameter1 through ParameterN are passed to the already installed 
     *   Probe-Configuration-Name on System on Host to "focus" its sensors
     *   as specified by the parameters. How it interprets these parameters
     *   is entirely probe configuration specific.
     */
    void focus(String confName, String host, String params[]);
    
}
