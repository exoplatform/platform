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
package org.exoplatform.portal.webui.portal;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.PortalProperties;
import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : LeBienThuy  
 *          lebienthuy@gmail.com
 * Mar 16, 2007  
 */
@ComponentConfig(
    template = "system:/groovy/webui/core/UIToolbar.gtmpl",
    events = {   
        @EventConfig(listeners = UIPortalManagementControlBar.RollbackActionListener.class),
        @EventConfig(listeners = UIPortalManagementControlBar.AbortActionListener.class),
        @EventConfig(listeners = UIPortalManagementControlBar.SaveActionListener.class),
        @EventConfig(listeners = UIPortalManagementControlBar.FinishActionListener.class)
    }
)

public class UIPortalManagementControlBar extends UIToolbar {
  
  public UIPortalManagementControlBar() throws Exception {
    super();
    setToolbarStyle("ControlToolbar") ;
    setJavascript("Preview","onclick='eXo.portal.UIPortal.switchMode(this);'") ;
  }
  
  public void save() throws Exception {
    UIPortal uiPortal = Util.getUIPortal();     
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);    
    
    PortalConfig portalConfig  = PortalDataMapper.toPortal(uiPortal);    
    UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class);     
    configService.update(portalConfig);
    uiPortalApp.getUserPortalConfig().setPortal(portalConfig) ;
    PortalRequestContext prContext = Util.getPortalRequestContext();
    String remoteUser = prContext.getRemoteUser();
    String ownerUser = prContext.getPortalOwner();   
    UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);
    if(userPortalConfig != null) {
      uiPortal.setModifiable(userPortalConfig.getPortalConfig().isModifiable());        
    } else {
      uiPortal.setModifiable(false);
    }
    LocaleConfigService localeConfigService  = uiPortalApp.getApplicationComponent(LocaleConfigService.class) ;
    LocaleConfig localeConfig = localeConfigService.getLocaleConfig(portalConfig.getLocale());
    if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
    //TODO dang.tung - change layout when portal get language from UIPortal (user and browser not support)
    //----------------------------------------------------------------------------------------------------
    String portalAppLanguage = uiPortalApp.getLocale().getLanguage();
    OrganizationService orgService = getApplicationComponent(OrganizationService.class) ;
    UserProfile userProfile = orgService.getUserProfileHandler().findUserProfileByName(remoteUser) ;
    String userLanguage = userProfile.getUserInfoMap().get("user.language");
    String browserLanguage = prContext.getRequest().getLocale().getLanguage();
    if(!portalAppLanguage.equals(userLanguage) && !portalAppLanguage.equals(browserLanguage)) {  
      uiPortalApp.setLocale(localeConfig.getLocale());
      uiPortal.refreshNavigation(localeConfig.getLocale());
    }
    //----------------------------------------------------------------------------------------------------
    uiPortalApp.setSkin(uiPortal.getSkin());
    prContext.refreshResourceBundle();
    SkinService skinService = getApplicationComponent(SkinService.class);
    skinService.invalidatePortalSkinCache(uiPortal.getName(), uiPortal.getSkin());
  }
  
  @Override
  public String event(String name) throws Exception {
    if(name.equals("Finish") || name.equals("Abort")) return super.url(name);
    return super.event(name);
  }

  /*public void abort(Event<UIPortalManagementControlBar> event) throws Exception {
    UIPortal uiPortal = Util.getUIPortal();
    uiPortal.setMode(UIPortal.COMPONENT_VIEW_MODE);
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
    pcontext.setFullRender(true);
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);  ;
    uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;
    
    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    uiWorkingWS.setRenderedChild(UIPortal.class) ; 
    pcontext.addUIComponentToUpdateByAjax(uiControl);
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;  
  }
  */
  static public class RollbackActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
      
      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);      
      configService.update(uiPortalApp.getUserPortalConfig().getPortalConfig());
      PortalRequestContext prContext = Util.getPortalRequestContext();     
      
      String remoteUser = prContext.getRemoteUser();
      String ownerUser = prContext.getPortalOwner();   
      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);
      
      UIPortal oldUIPortal =uiWorkingWS.getChild(UIPortal.class);
      uiWorkingWS.setBackupUIPortal(oldUIPortal);      
      
      if(userPortalConfig != null) {
        UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
        PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);      
        uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
      }
      
      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
    }
  }
  
  static public class SaveActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource(); 
      uiPortalManagement.save();
      Util.updateUIApplication(event);      
    }
  }  
  
  static public class FinishActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource();   
      uiPortalManagement.save();
      
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UserPortalConfigService configService = uiPortalManagement.getApplicationComponent(UserPortalConfigService.class);
      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(prContext.getPortalOwner(), prContext.getRemoteUser());
      if(userPortalConfig == null){
        HttpServletRequest request = prContext.getRequest() ;        
        String portalName = URLEncoder.encode(Util.getUIPortal().getName(),"UTF-8") ;        
        String redirect = request.getContextPath() + "/public/" + portalName + "/" ;
        prContext.getResponse().sendRedirect(redirect) ;        
      }
      
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiPortalApp = Util.getUIPortalApplication() ;
      if(PortalProperties.SESSION_ALWAYS.equals(uiPortal.getSessionAlive())) uiPortalApp.setSessionOpen(true) ;
      else uiPortalApp.setSessionOpen(false) ;
      uiPortalApp.setEditting(false) ;
      PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, 
           PageNodeEvent.CHANGE_PAGE_NODE, 
           (uiPortal.getSelectedNode() != null ? uiPortal.getSelectedNode().getUri() : null)) ;
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ; 
    }
  }
  
  static public class AbortActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      
      PortalRequestContext prContext = Util.getPortalRequestContext();  
      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);
      configService.update(uiPortalApp.getUserPortalConfig().getPortalConfig());
      uiPortalApp.setEditting(false) ;
      
      String remoteUser = prContext.getRemoteUser();
      String ownerUser = prContext.getPortalOwner();   
      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);
      
      if(userPortalConfig == null){
        HttpServletRequest request = prContext.getRequest() ;        
        String portalName = URLEncoder.encode(Util.getUIPortal().getName(),"UTF-8") ;        
        String redirect = request.getContextPath() + "/public/" + portalName + "/" ;
        prContext.getResponse().sendRedirect(redirect) ;      
      }
      
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
      PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);
      
      UIPortal oldUIPortal = uiWorkingWS.getChild(UIPortal.class);
      uiWorkingWS.setBackupUIPortal(oldUIPortal);
      uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
      PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, 
           PageNodeEvent.CHANGE_PAGE_NODE, 
           (uiPortal.getSelectedNode() != null ? uiPortal.getSelectedNode().getUri() : null)) ;
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;  
      
    }
  }
}
