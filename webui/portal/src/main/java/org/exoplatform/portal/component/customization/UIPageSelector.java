/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/portal/webui/component/customization/UIPageSelector.gtmpl"
  ),
  @ComponentConfig(      
      id = "SelectPage",
      type = UIPageBrowser.class,
      template = "app:/groovy/portal/webui/component/customization/UIPageBrowser.gtmpl" ,      
      events = @EventConfig(listeners = UIPageSelector.SelectPageActionListener.class) 
  )
})
public class UIPageSelector extends UIFormInputContainer {

  public Page page_; 

  public UIPageSelector() throws Exception {
    super("UIPageSelector", null) ;
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "PopupPageSelector");
    uiPopup.setWindowSize(900, 400);
    uiPopup.setRendered(false);
    UIPageBrowser uiPageBrowser = createUIComponent(UIPageBrowser.class, "SelectPage", null) ;
    uiPopup.setUIComponent(uiPageBrowser);    
    UIGrid uiGrid = uiPageBrowser.getChild(UIGrid.class);
    uiGrid.configure("id", UIPageBrowser.BEAN_FIELD, new String[]{"SelectPage"});
  }

  public void configure(String iname, String  bfield) {
    setId(iname) ;
    setName(iname) ;
    setBindingField(bfield) ;    
  }

  public Object getUIInputValue() { 
    if(page_ != null) return page_.getPageId() ;
    return null ;
  }  
  
  public void setUIInputValue(Object input) throws Exception { 
    String id =  (String)input ; 
    PortalDAO service = getApplicationComponent(PortalDAO.class) ;
    page_ = service.getPage(id) ;
  }

  public Class getUIInputValueType() {  return String.class ; }

  public void setPage(Page page) {   page_ = page ; }  
  public Page getPage(){ return page_; }
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    UIPageBrowser uiPageBrowser = findFirstComponentOfType(UIPageBrowser.class);
    uiPageBrowser.processDecode(context);
  }
  
  static public class SelectPageActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {     
      UIPageBrowser uiPageBrowser = event.getSource() ;
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      PortalDAO service = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = service.getPage(id) ;
      
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = pcontext.getRemoteUser();
      
      if(!userACL.hasPermission(page.getOwner(), accessUser, page.getViewPermission())){
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-Preview", new String[]{page.getName()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      UIPageSelector uiPageSelector = uiPageBrowser.getAncestorOfType(UIPageSelector.class) ;
      uiPageSelector.setPage(page) ;
      
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        pcontext.addUIComponentToUpdateByAjax(uiForm.getParent()); 
        return ;
      }
      pcontext.addUIComponentToUpdateByAjax(uiPageSelector.getParent());
    }
  }
}
