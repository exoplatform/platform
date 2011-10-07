package org.exoplatform.setting.server.service;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.monitor.jvm.J2EEServerInfo;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WizardServiceImpl extends RemoteServiceServlet implements WizardService {

  /**
   * Return a map with interesting system properties
   */
  public Map<String, String> getSystemProperties() {
    
    Map<String, String> map = new LinkedHashMap<String, String>();
    
    // Get server name
    J2EEServerInfo j2eeServerInfo = new J2EEServerInfo();
    map.put("server.name", j2eeServerInfo.getServerName());
    map.put("server.home", j2eeServerInfo.getServerHome());
    
    // Get some system properties
    map.put("exo.conf.dir.name", System.getProperty("exo.conf.dir.name"));
    map.put("exo.product.developing", System.getProperty("exo.product.developing"));
    map.put("exo.profiles", System.getProperty("exo.profiles"));
    map.put("file.encoding", System.getProperty("file.encoding"));
    map.put("gatein.data.dir", System.getProperty("gatein.data.dir"));
    map.put("java.home", System.getProperty("java.home"));
    map.put("java.runtime.name", System.getProperty("java.runtime.name"));
    map.put("java.runtime.version", System.getProperty("java.runtime.version"));
    map.put("java.specification.version", System.getProperty("java.specification.version"));
    map.put("java.version", System.getProperty("java.version"));
    map.put("os.arch", System.getProperty("os.arch"));
    map.put("os.name", System.getProperty("os.name"));
    map.put("os.version", System.getProperty("os.version"));
    map.put("user.country", System.getProperty("user.country"));
    map.put("user.dir", System.getProperty("user.dir"));
    map.put("user.home", System.getProperty("user.home"));
    map.put("user.language", System.getProperty("user.language"));
    map.put("user.name", System.getProperty("user.name"));
    
    return map;
  }

  /**
   * Return all datasources installed into user system
   */
  public List<String> getDatasources() {
    List<String> datasources = new ArrayList<String>();
    datasources.add("Toto 1");
    datasources.add("Toto 2");
    return datasources;
  }

  /**
   * Save datas into configuration file
   */
  public String saveDatas(Map<SetupWizardData, String> datas) {
    
    //String path = "F:\\Java\\exo-project\\platform\\trunk\\packaging\\pkg\\target\\tomcat\\gatein\\conf\\configuration.properties";
    String path = "F:\\Java\\exo-project\\platform\\trunk\\packaging\\tomcat\\target\\tomcat\\gatein\\conf\\configuration.properties";

    if(datas != null && datas.size() > 0) {
      try {
        PropertiesConfiguration conf = new PropertiesConfiguration(path);

        // Fetch all properties stores by user
        for(Map.Entry<SetupWizardData, String> entry : datas.entrySet()) {
          SetupWizardData data = entry.getKey();
          String ppValue = entry.getValue();
          
          if(data.getPropertyName() != null) {
            if(conf.containsKey(data.getPropertyName())) {
              // If propoperty exists we update it
              conf.setProperty(data.getPropertyName(), ppValue);
            }
            else {
              // Else we add to file
              conf.addProperty(data.getPropertyName(), ppValue);
            }
          }
        }
        
        conf.save();
      } 
      catch (ConfigurationException e) {
        Logger.getLogger("WizardServiceImpl").log(Level.ERROR, e.getMessage());
      }
    }
    
    return null;
  }
  
  /**
   * This method start platform
   */
  public String startPlatform() {
    System.out.println("TOTO");
    try
    {
       final RootContainer rootContainer = RootContainer.getInstance();
       SecurityHelper.doPrivilegedAction(new PrivilegedAction<Void>()
       {
          public Void run()
          {
             rootContainer.createPortalContainers();
             return null;
          }
       });
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }

}
