/*
 * Main.java
 *
 * Created on July 14, 2002, 11:33 PM
 */

package psl.meet.server;

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import psl.meet.server.core.Manager;
import psl.meet.server.core.IManager;

/**
 *
 * Copyright (c) 2002: The Trustees of Columbia University in the City of New York, 
 * Philip N. Gross. All Rights Reserved.
 * @author  phil
 */

/* TODO: 
 * with init option, set configuration.  Otherwise try to read, give error
 * if no config present
 */
public class Main {
    
    private static final String manager_class = "manager_class";
    private static final String manager_class_default = "psl.meet.server.core.Manager";
    
    static Logger logger = Logger.getLogger(Main.class);
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    public static void main(String[] args) {
        BasicConfigurator.configure();
        logger.info("Meet Main starting up");
        Preferences prefs = Preferences.systemNodeForPackage(Main.class);
        Manager mTop = new Manager("TopLevel");        
        try {
            String cnames[] = prefs.childrenNames();
            for (int i=0; i<cnames.length; ++i) {
                logger.info("Configuring package " + cnames[i]);
                Preferences modPrefs = prefs.node(cnames[i]);
                String mName = modPrefs.get(manager_class, manager_class_default);
                Class c = Class.forName(mName);
                IManager im = (IManager) c.newInstance();
                im.setConfig(cnames[i]);
                mTop.add(im);
                logger.info("resolved class " + cnames[i]);                
            }
        } catch (Exception e) {
            logger.error("Error loading packages", e);
        }
    }
}
