package org.exoplatform.setting.server.service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

}
