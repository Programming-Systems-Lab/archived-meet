/*
 * UDPChannel.java
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
public class UDPChannel implements psl.meet.server.core.IModule {
    
    private static final int DEFAULT_PORT = 0xEEEE;
    private static final String myClass = "UDPChannel";
    static Logger logger = Logger.getLogger(UDPChannel.class);
    
    Selector acceptSelector = null;
    
    /** Creates a new instance of UDPChannel */
    public UDPChannel() {
        try {
            acceptSelector = SelectorProvider.provider().openSelector();
        } catch (IOException ioe) {
            logger.error(myClass + ": couldn't get default selector", ioe);
        }
    }
    
    public int setListenPort(int port) {
        if (port == 0) {
            port = DEFAULT_PORT;
        }
        
        return MEET_SUCCESS;
    }
    
    /**
     * returns an integer representing the status of the manager itself
     * @return success code
     *
     */
    public int getStatus() {
        return MEET_STATUS_OK;
    }
    
    
    
}
