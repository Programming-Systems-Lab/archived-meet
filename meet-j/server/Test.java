/*
 * Main.java
 *
 * Created on July 14, 2002, 11:33 PM
 */

package psl.meet.server;

import java.io.FileOutputStream;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import psl.meet.server.core.Manager;

/**
 *
 * @author  phil
 */
public class Test {
    
    // private static final String
    static Logger logger = Logger.getLogger(Test.class);
    
    /** Creates a new instance of Main */
    public Test() {
    }
    
    public static void main(String[] args) {
        BasicConfigurator.configure();
        logger.info("Meet Test starting up");
        Preferences prefs = Preferences.systemNodeForPackage(Test.class);
        try {
            prefs.clear();
            String basePath = prefs.absolutePath() + '/';
            
            prefs.putBoolean(basePath + "caches", true);
            prefs.putBoolean(basePath + "channels", true);
            prefs.putBoolean(basePath + "clocks", true);
            prefs.putBoolean(basePath + "connections", true);
            prefs.putBoolean(basePath + "core", true);
            prefs.putBoolean(basePath + "executives", true);
            prefs.putBoolean(basePath + "monitors", true);
            prefs.putBoolean(basePath + "routers", true);
            prefs.putBoolean(basePath + "schedulers", true);
            prefs.putBoolean(basePath + "stores", true);
            prefs.putBoolean(basePath + "types", true);
            
            
            
            
            prefs.flush();
            logger.info("Prefs flushed");
        } catch (Exception e) {
            logger.debug("Prefs failed to flush", e);
        }
        
        try {
            FileOutputStream fos = new FileOutputStream("meet-prefs.xml");
            prefs.exportSubtree(fos);
            logger.info("Prefs exported to:" + fos);
        } catch (Exception e) {
            logger.debug("Prefs failed to export", e);
        }
        
    }
}
