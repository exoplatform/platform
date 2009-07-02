/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.application;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.exoplatform.commons.utils.Safe;
import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.SessionManagerContainer;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.webui.application.ConfigurationManager;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.core.UIApplication;

public class PortalStateManager extends StateManager {

  protected static Log log = ExoLogger.getLogger("portal:PortalStateManager");  
  
  private ConcurrentMap<String, PortalApplicationState> uiApplications = new ConcurrentHashMap<String, PortalApplicationState>();

  /**
   * This method is used to restore the UI component tree either the current request targets a portlet 
   * or the portal. 
   * 
   * In both cases, if the tree is not stored already it is created and then stored in a local Map 
   * 
   */
  @SuppressWarnings("unchecked")
  public UIApplication restoreUIRootComponent(WebuiRequestContext context) throws Exception {
    context.setStateManager(this) ;
    WebuiApplication app  = (WebuiApplication)context.getApplication() ;
    
    /*
     * If the request context is of type PortletRequestContext, we extract the parent context which will
     * allow to get access to the PortalApplicationState object thanks to the session id used as the key for the
     * syncronised Map uiApplications
     */
    if(context instanceof PortletRequestContext) {
      WebuiRequestContext preqContext = (WebuiRequestContext) context.getParentAppRequestContext() ;
      PortalApplicationState state = uiApplications.get(preqContext.getSessionId()) ;
      PortletRequestContext pcontext = (PortletRequestContext) context ;
      String key =  pcontext.getApplication().getApplicationId() + "/" + pcontext.getWindowId();
      UIApplication uiApplication =  state.get(key) ;
      if(uiApplication != null)  return uiApplication;
      ConfigurationManager cmanager = app.getConfigurationManager() ;
      String uirootClass = cmanager.getApplication().getUIRootComponent() ;
      Class type = Thread.currentThread().getContextClassLoader().loadClass(uirootClass) ;
      uiApplication = (UIApplication)app.createUIComponent(type, null, null, context) ;
      state.put(key, uiApplication) ;
      return uiApplication ;
    } 
    
    PortalRequestContext pcontext = (PortalRequestContext) context ;
    PortalApplicationState state = uiApplications.get(pcontext.getSessionId()) ;
    if(state != null) {
      if((!(Safe.equals(pcontext.getRemoteUser(), state.getUserName()))) ||
          (!pcontext.getPortalOwner().equals(state.getUIPortalApplication().getOwner()))) {
        clearSession(pcontext.getRequest().getSession()) ;
        state = null ;
      }
    }
    if(state == null) {
      ConfigurationManager cmanager = app.getConfigurationManager() ;
      String uirootClass = cmanager.getApplication().getUIRootComponent() ;
      Class type = Thread.currentThread().getContextClassLoader().loadClass(uirootClass) ;
      UserPortalConfig config = getUserPortalConfig(pcontext) ;
      if(config == null) {
        pcontext.getRequest().getSession().invalidate() ;
        HttpServletResponse response = pcontext.getResponse();
//        if(pcontext.getRemoteUser() == null) {
//          String portalName = pcontext.getPortalOwner() ;
//          portalName = URLEncoder.encode(portalName, "UTF-8") ;
//          String redirect = pcontext.getRequest().getContextPath() + "/private/" + portalName + "/";
//          response.sendRedirect(redirect);
//        }
//        else response.sendRedirect("/portal/portal-unavailable.jsp");
        response.sendRedirect("/portal/portal-unavailable.jsp");
        pcontext.setResponseComplete(true);
        return null;
      }
      pcontext.setAttribute(UserPortalConfig.class, config);
      UIPortalApplication uiApplication =
        (UIPortalApplication)app.createUIComponent(type, null, null, context) ;
      state = new PortalApplicationState(uiApplication, pcontext.getRemoteUser()) ;
      uiApplications.put(context.getSessionId(), state) ;
      SessionManagerContainer pcontainer = (SessionManagerContainer) app.getApplicationServiceContainer() ;
      pcontainer.createSessionContainer(context.getSessionId(), uiApplication.getOwner()) ;
    }
    return state.getUIPortalApplication() ;
  }
  
  @SuppressWarnings("unused")
  public void storeUIRootComponent(WebuiRequestContext context) {}
  
  public void expire(String sessionId, WebuiApplication app) {
    PortalApplicationState state = uiApplications.remove(sessionId) ;
    if(state != null){
      log.warn("Session expires, remove application: " + state.getUIPortalApplication());
    }
    SessionManagerContainer pcontainer = (SessionManagerContainer) app.getApplicationServiceContainer() ;
    pcontainer.removeSessionContainer(sessionId) ;
  }
  
  public UserPortalConfig getUserPortalConfig(PortalRequestContext  context) throws Exception {
    ExoContainer appContainer  =  context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService service_ = 
      (UserPortalConfigService)appContainer.getComponentInstanceOfType(UserPortalConfigService.class);
    String remoteUser = context.getRemoteUser();
    String ownerUser = context.getPortalOwner(); 
    return service_.getUserPortalConfig(ownerUser, remoteUser) ;
  }
  
  private void clearSession(HttpSession session) {
    Enumeration<?> e = session.getAttributeNames() ;
    while(e.hasMoreElements()) {
      String name =  (String)e.nextElement() ;
      session.removeAttribute(name) ;
    }
  }
  
  @SuppressWarnings("serial")
  static public class PortalApplicationState extends HashMap<String, UIApplication> {
    
    private final UIPortalApplication uiPortalApplication_ ;
    private final String userName_ ;
    
    public PortalApplicationState(UIPortalApplication uiPortalApplication, String userName) {
      uiPortalApplication_ = uiPortalApplication ;
      userName_ = userName ;
    }
    
    public String getUserName() { return userName_ ; }
    
    public UIPortalApplication  getUIPortalApplication() { return uiPortalApplication_ ; }
  }
}