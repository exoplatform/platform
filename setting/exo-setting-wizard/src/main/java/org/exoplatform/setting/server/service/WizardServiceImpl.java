package org.exoplatform.setting.server.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.exoplatform.setting.client.service.WizardService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WizardServiceImpl extends RemoteServiceServlet implements WizardService {

  public Integer storeDatas(Map<String, String> datas, Integer toStep) {
    
    // Store datas in session
    Logger.getLogger("WizardServiceImpl").log(Level.INFO, "Côté serveur: store datas !");
    
    return toStep;
  }

  public Map<String, String> getSystemProperties() {
    
    Map<String, String> map = new HashMap<String, String>();
    
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

}
