package org.exoplatform.portal.component.customization;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  template = "app:/groovy/portal/webui/component/customization/UIPortalBrowser.gtmpl",
  events = @EventConfig(listeners = UIPortalBrowser.SelectPortalActionListener.class)  
)
public class UIPortalBrowser extends UIContainer {

  public static String[] BEAN_FIELD = {"owner", "viewPermission", "editPermission"} ;  
  public static String[] SELECT_ACTIONS = {"SelectPortal"} ; 
  
  public UIPortalBrowser() throws Exception {
    UIGrid uiGrid = addChild(UIGrid.class, null, null) ;
    uiGrid.configure("owner", BEAN_FIELD, SELECT_ACTIONS) ;
    loadPortalConfigs();
  }

  public void loadPortalConfigs() throws Exception {    
    PortalDAO service = getApplicationComponent(PortalDAO.class) ;
    PageList pagelist = service.getPortalConfigs() ;
    UIGrid uiGrid = findFirstComponentOfType(UIGrid.class) ;
    uiGrid.setUseAjax(false);
    uiGrid.getUIPageIterator().setPageList(pagelist);
  } 

  static public class SelectPortalActionListener extends EventListener<UIPortalBrowser> {
    public void execute(Event<UIPortalBrowser> event) throws Exception {
      UIPortalBrowser uiPageBrowser = event.getSource() ;
      String ownerUser = event.getRequestContext().getRequestParameter(OBJECTID) ;
      if(ownerUser == null) return;
      
      UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      
      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);     
      PortalRequestContext prContext = Util.getPortalRequestContext();  
      String remoteUser = prContext.getRemoteUser();
      
      UserPortalConfig userPortalConfig = configService.computeUserPortalConfig(ownerUser, remoteUser);      
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
      PortalDataModelUtil.toUIPortal(uiPortal, userPortalConfig);
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      if(userACL.hasPermission(uiPortal.getOwner(), remoteUser, uiPortal.getViewPermission())){
        UIPortal oldUIPortal = uiWorkingWS.getChild(UIPortal.class);      
        
        uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
        uiWorkingWS.setRenderedChild(UIPortal.class) ;
        UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
        UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
        uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
        prContext.addUIComponentToUpdateByAjax(uiControl);
        
        prContext.addUIComponentToUpdateByAjax(uiWorkingWS) ;      
        prContext.setFullRender(true);
        return;
      } 
      
      uiPortalApp.addMessage(new ApplicationMessage("UIPortalBrowser.msg.Invalid-viewPermission", new String[]{uiPortal.getName()})) ;;
      prContext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
    }
  } 
}
