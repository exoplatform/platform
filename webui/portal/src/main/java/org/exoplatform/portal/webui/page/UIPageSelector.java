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
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputContainer;
import org.exoplatform.webui.form.UIFormPopupWindow;
/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
                   template = "system:/groovy/portal/webui/page/UIPageSelector.gtmpl"
  ),
  @ComponentConfig(      
                   id = "SelectPage",
                   type = UIPageBrowser.class,
                   template = "system:/groovy/portal/webui/page/UIPageBrowser.gtmpl" ,      
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

  public UIFormInput<?> setValue(String value) throws Exception {
    WebuiRequestContext ctx = WebuiRequestContext.getCurrentInstance();
    UserPortalConfigService service = getApplicationComponent(UserPortalConfigService.class);
    Page page = service.getPage(value, ctx.getRemoteUser()) ;
    page_ = page;
    super.setValue(value);    
    return this;
  }

  public Page getPage() { return page_; }

  public void setPage(Page page) {
    page_ = page;
  }

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
      WebuiRequestContext ctx = event.getRequestContext();
      UIApplication uiApp = ctx.getUIApplication();
      UIPageSelector uiPageSelector = uiPageBrowser.getAncestorOfType(UIPageSelector.class) ;
      UserPortalConfigService service = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class);
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      if(!userACL.hasPermission(service.getPage(id))) {
        uiApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.NoPermission", new String[]{id})) ;; 
      }
      uiPageSelector.setValue(id);
      uiPageBrowser.defaultValue(null) ;

      UIForm uiForm = uiPageSelector.getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        ctx.addUIComponentToUpdateByAjax(uiForm.getParent()); 
      } else {
        ctx.addUIComponentToUpdateByAjax(uiPageSelector.getParent());
      }
      UIFormPopupWindow uiPopup =uiPageSelector.getChild(UIFormPopupWindow.class) ;
      uiPopup.setShow(false) ;
    }
  }

}
