/*
 * IPv4Channel.java
 *
 * Created on July 22, 2002, 5:03 PM
 */

package psl.meet.server.channels;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

import psl.meet.server.core.*;
/**
 *
 * @author  phil
 */
public class IPv4Channel implements IModule {
    
    private static final String myClass = IPv4Channel.class.toString();
    static Logger logger = Logger.getLogger(IPv4Channel.class);
    
    Selector selector = null;
    
    /** Creates a new instance of IPv4Channel */
    public IPv4Channel() {
        try {
            selector = SelectorProvider.provider().openSelector();
        } catch (IOException ioe) {
            logger.error(myClass + ": couldn't get default selector", ioe);
        }
    }
    
    
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */
    public int getStatus() {
        return MEET_STATUS_OK;
    }
    
    /** identify this module
     * @return title of this module
     */
    public String getName() {
        return "IPv4Channel";
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
    
}
