/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInput;
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
public class UIPageSelector extends UIFormInputContainer<String> {
  
  private Page page_;

  public UIPageSelector() throws Exception {
    super("UIPageSelector", null) ;
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "PopupPageSelector");
    uiPopup.setWindowSize(900, 400);
    uiPopup.setRendered(false);
    UIPageBrowser uiPageBrowser = createUIComponent(UIPageBrowser.class, "SelectPage", null) ;
    uiPopup.setUIComponent(uiPageBrowser);    
    UIGrid uiGrid = uiPageBrowser.getChild(UIGrid.class);
    uiGrid.configure("pageId", UIPageBrowser.BEAN_FIELD, new String[]{"SelectPage"});
  }

  public void configure(String iname, String  bfield) {
    setId(iname) ;
    setName(iname) ;
    setBindingField(bfield) ;    
  }
  
  public UIFormInput setValue(String value) throws Exception {
    PortalRequestContext pcontext = Util.getPortalRequestContext();
    UserPortalConfigService service = getApplicationComponent(UserPortalConfigService.class);
    Page page = service.getPage(value) ;

    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    if(page == null){
      uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-Preview", new String[]{value})) ;;
      pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
      return this;
    }
    
    page = service.getPage(value, pcontext.getRemoteUser()) ;
    System.out.println("\n\n\n====================>>>>UIPageSelector.setValue() =  " + value + " with user: " + pcontext.getRemoteUser());
    if(page == null){
      uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.NoPermission", new String[]{value})) ;;
      pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
      return this;
    }

    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) {
      pcontext.addUIComponentToUpdateByAjax(uiForm.getParent()); 
    } else {
      pcontext.addUIComponentToUpdateByAjax(getParent());
    }

    if(page == null) {
      uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
      pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
      return this;
    }

    UIFormPopupWindow uiPopup = getAncestorOfType(UIFormPopupWindow.class);
    if(uiPopup != null) uiPopup.setShow(false);
    page_ = page;
    super.setValue(value);    
    return this;
  }
  
  public Page getPage() { return page_; }

  public Class<String> getTypeValue() {  return String.class ; }

  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    UIPageBrowser uiPageBrowser = findFirstComponentOfType(UIPageBrowser.class);
    uiPageBrowser.processDecode(context);
  }
  
  static public class SelectPageActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource();
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      event.getRequestContext().getRequestContextPath();
      System.out.println("\n\n\n\n------------------>>>>>>>>>>>>>>>> ID= " + id);
      UIPageSelector uiPageSelector = uiPageBrowser.getAncestorOfType(UIPageSelector.class) ;
      uiPageSelector.setValue(id);
     
    }
  }
  
}
