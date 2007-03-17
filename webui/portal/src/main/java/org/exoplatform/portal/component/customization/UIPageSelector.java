/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
@ComponentConfig(template = "app:/groovy/portal/webui/component/customization/UIPageSelector.gtmpl")
public class UIPageSelector extends UIFormInputContainer {

  public Page page_; 

  public UIPageSelector() throws Exception {
    super("UIPageSelector", null) ;
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "PopupPageSelector");
    uiPopup.setWindowSize(900, 400);
    uiPopup.setRendered(false);
    UIPageBrowser uiPageBrowser = createUIComponent(UIPageBrowser.class, null, null) ;
    uiPopup.setUIComponent(uiPageBrowser);
    UIGrid uiGrid = uiPageBrowser.getChild(UIGrid.class);
    uiGrid.configure("id", UIPageBrowser.BEAN_FIELD, UIPageBrowser.SELECT_ACTIONS);
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
  
  public void processDecode(RequestContext context) throws Exception {   
    super.processDecode(context);
    UIPageBrowser uiPageBrowser = findFirstComponentOfType(UIPageBrowser.class);
    uiPageBrowser.processDecode(context);
  }
}
