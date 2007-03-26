package org.exoplatform.portal.application;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.ConfigurationManager;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.component.UIApplication;

public class PortalStateManager extends StateManager {
  
  private Map<String, PortalApplicationState> uiApplications = new HashMap<String, PortalApplicationState>(); 
  
  @SuppressWarnings("unchecked")
  public UIApplication restoreUIRootComponent(WebuiRequestContext context) throws Exception {
    context.setStateManager(this) ;
    WebuiApplication app  = context.getApplication() ;
    if(context instanceof PortletRequestContext) {
      PortalApplicationState state = uiApplications.get(context.getParentAppRequestContext().getSessionId()) ;
      PortletRequestContext pcontext = (PortletRequestContext) context ;
      String key =  pcontext.getApplication().getApplicationId() ;
      UIApplication uiApplication =  state.get(key) ;
      if(uiApplication != null)  return uiApplication;
      synchronized(uiApplications) {
        ConfigurationManager cmanager = app.getConfigurationManager() ;
        String uirootClass = cmanager.getApplication().getUIRootComponent() ;
        ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
        Class type = cl.loadClass(uirootClass) ;
        uiApplication = (UIApplication)app.createUIComponent(type, null, null, context) ;     
        state.put(key, uiApplication) ;
      }
      return uiApplication ;
    } 
    
    PortalRequestContext  pcontext = (PortalRequestContext) context ;
    PortalApplicationState state = uiApplications.get(pcontext.getSessionId()) ;
    if(state != null) {
      if(state.getAccessPath() != pcontext.getAccessPath()) {
        state = null ;
      } else if(!pcontext.getPortalOwner().equals(state.getUIPortalApplication().getOwner())) {
        state = null ;
      }
    }
    if(state == null) {
      synchronized(uiApplications) {
        ConfigurationManager cmanager = app.getConfigurationManager() ;
        String uirootClass = cmanager.getApplication().getUIRootComponent() ;
        ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
        Class type = cl.loadClass(uirootClass) ;
        UserPortalConfig config = getUserPortalConfig(pcontext) ;
        pcontext.setAttribute(UserPortalConfig.class, config);
        UIPortalApplication uiApplication = 
          (UIPortalApplication)app.createUIComponent(type, config.getPortalConfig().getFactoryId(), null, context) ;
        state = new PortalApplicationState(uiApplication, pcontext.getAccessPath()) ;
        uiApplications.put(context.getSessionId(), state) ;
        PortalContainer pcontainer = (PortalContainer) app.getApplicationServiceContainer() ;
        pcontainer.createSessionContainer(context.getSessionId(), uiApplication.getOwner()) ;
      }
    }
    return state.getUIPortalApplication() ;
  }

  @SuppressWarnings("unused")
  public void storeUIRootComponent(WebuiRequestContext context) {
    
  }

  public void expire(String sessionId, WebuiApplication app) {
    PortalApplicationState state = uiApplications.remove(sessionId) ;
    if(state != null){
      System.out.println("SESSION EXPIRE, REMOVE APPLICATION STATE: " + state.getUIPortalApplication());
    }
    PortalContainer pcontainer =  (PortalContainer) app.getApplicationServiceContainer() ;
    pcontainer.removeSessionContainer(sessionId) ;
  }
  
  public UserPortalConfig getUserPortalConfig(PortalRequestContext  context) throws Exception {
    ExoContainer appContainer  =  context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService service_ = 
      (UserPortalConfigService)appContainer.getComponentInstanceOfType(UserPortalConfigService.class);
    String remoteUser = context.getRemoteUser();
    String ownerUser = context.getPortalOwner();  
    UserPortalConfig userPortalConfig = service_.computeUserPortalConfig(ownerUser, remoteUser);
    return userPortalConfig  ;
  }
  
  @SuppressWarnings("serial")
  static  public  class PortalApplicationState extends HashMap<String, UIApplication> {
    private UIPortalApplication uiPortalApplication_ ;
    private int accessPath_ ;
    
    public PortalApplicationState(UIPortalApplication uiPortalApplication, int accessPath) {
      uiPortalApplication_ = uiPortalApplication ;
      accessPath_ = accessPath ;
    }
    
    public int getAccessPath() { return accessPath_ ; }  
    
    public UIPortalApplication  getUIPortalApplication() { return uiPortalApplication_ ; }
  }
}