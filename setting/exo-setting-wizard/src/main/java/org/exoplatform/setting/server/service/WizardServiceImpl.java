package org.exoplatform.setting.server.service;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.container.RootContainer;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.server.WizardProperties;
import org.exoplatform.setting.server.WizardUtility;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WizardServiceImpl extends RemoteServiceServlet implements WizardService {
  
  private static Logger logger = Logger.getLogger(WizardServiceImpl.class);

  /**
   * Return a map with interesting system properties
   */
  public Map<String, String> getSystemProperties() {
    
    logger.debug("Client call server method: getSystemProperties");
    
    Map<String, String> map = new LinkedHashMap<String, String>();
    
    // Get server name
    map.put("server.name", WizardUtility.getCurrentServerName());
    map.put("server.home", WizardUtility.getCurrentServerHome());
    
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
   * <p>
   * If datasource context is not configured, we return an empty list
   */
  public List<String> getDatasources() {
    
    logger.debug("Client call server method: getDatasources");
    
    // Get JNDI Name according to the sever name
    String DATASOURCE_CONTEXT = WizardUtility.getDatasourceJndiName(WizardUtility.getCurrentServerName());

    List<String> datasources = new ArrayList<String>();
    
    if(DATASOURCE_CONTEXT != null) {
      try {
        Context initialContext = new InitialContext();
        Context namingContext = (Context) initialContext.lookup(DATASOURCE_CONTEXT);
        if (namingContext != null) {
          NamingEnumeration<Binding> nenum = namingContext.listBindings("");
          while(nenum.hasMore()) {
            Binding binding = (Binding) nenum.next();
            Object ds = namingContext.lookup(binding.getName());
            if(ds instanceof DataSource) {
              datasources.add(binding.getName());
            }
          }
        }
        else {
          logger.error("Failed to lookup datasource.");
        }
      }
      catch (NamingException ex) {
        logger.error("Cannot get connection: " + ex.getMessage(), ex);
      }
    }
    
    return datasources;
  }

  /**
   * Save datas into configuration file
   */
  public String saveDatas(Map<SetupWizardData, String> datas) {
    
    logger.debug("Client call server method: saveDatas");
    
    // Get configuration path
    String path = WizardUtility.getExoConfigurationPropertiesPath(WizardUtility.getCurrentServerName());

    if(datas != null && datas.size() > 0) {
      try {
        PropertiesConfiguration conf = new PropertiesConfiguration(path);

        // Fetch all properties stores by user
        for(Map.Entry<SetupWizardData, String> entry : datas.entrySet()) {
          SetupWizardData data = entry.getKey();
          String ppValue = entry.getValue();
          
          if(data.getPropertyName() != null) {
            if(conf.containsKey(data.getPropertyName())) {
              // If property exists we update it
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
        logger.error("Cannot get Properties configuration: " + e.getMessage(), e);
      }
    }
    
    return null;
  }
  
  /**
   * This method start platform
   */
  public String startPlatform() {
    
    logger.debug("Client call server method: startPlatform");
    
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
  
  /**
   * Debug mode activated or not
   */
  public Boolean getDebugActivation() {
    
    logger.debug("Client call server method: getDebugActivation");
    
    return WizardProperties.getDebug();
  }
  
  /**
   * Debug mode activated or not
   */
  public Integer getFirstScreenNumber() {
    
    logger.debug("Client call server method: getFirstScreenNumber");
    
    return WizardProperties.getFirstScreenNumber();
  }

}
