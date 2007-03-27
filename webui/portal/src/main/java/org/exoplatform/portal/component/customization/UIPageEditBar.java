/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIToolbar;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(  
    template = "system:/groovy/webui/component/UIToolbar.gtmpl",
    events = {
        @EventConfig(listeners = UIPageEditBar.PagePreviewActionListener.class),
        @EventConfig(listeners = UIPageEditBar.EditPageActionListener.class),        
        @EventConfig(listeners = UIPageEditBar.EditContainerActionListener.class),
        @EventConfig(listeners = UIPageEditBar.EditPortletActionListener.class),
        @EventConfig(listeners = UIPageEditBar.SavePageActionListener.class)
    }
)
public class UIPageEditBar extends UIToolbar { 

  transient UIPage uiPage_;

  public UIPageEditBar() throws Exception {
    setToolbarStyle("EditToolbar") ;
    setJavascript("PagePreview","onClick='eXo.portal.UIPortal.switchModeForPage(this)';") ;
  }
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
    Util.getUIPortalToolPanel().findComponentOfType(uiPortlets, UIPortlet.class);
    for (UIPortlet uiPortlet : uiPortlets) {
      uiPortlet.setShowEditControl(b);
    }
    return super.<T>setRendered(b) ;
  } 

  public UIPage getUIPage(){ return uiPage_; }  
  public void setUIPage(UIPage uiPage){ uiPage_ = uiPage; }

  public void showUIPage(){
    if(uiPage_ == null) return;    
    UIPortalToolPanel uiToolPanel =  Util.getUIPortalToolPanel();
    uiToolPanel.setUIComponent(uiPage_);    
  }

  @SuppressWarnings("unused")
  static public class PagePreviewActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
    }
  }

  static public class EditContainerActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {      
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.showUIPage();      

      UIPageManagement uiPManagement = uiEditBar.getParent();
      Class [] childrenToRender = {UIPageEditBar.class, 
                                   UIContainerConfigOptions.class, UIPageNavigationControlBar.class}; 
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      Util.updateUIApplication(event);
    }
  }
  

  static public class EditPortletActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {     
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.showUIPage();

      UIPageManagement uiPManagement = uiEditBar.getParent();       
      Class [] childrenToRender = {UIPageEditBar.class, 
                                   UIPortletOptions.class, UIPageNavigationControlBar.class}; 
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      Util.updateUIApplication(event);
    }
  }

  static public class EditPageActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.showUIPage();

      UIPageManagement uiPManagement = uiEditBar.getParent();      
      Class [] childrenToRender ={UIPageEditBar.class, 
                                  UIPageNodeSelector.class , UIPageNavigationControlBar.class}; 
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);

      UIPageForm uiPageForm =  Util.showComponentOnWorking(event.getSource(), UIPageForm.class);
      uiPageForm.removeChild(UIPageTemplateOptions.class);
      uiPageForm.setValues(uiEditBar.getUIPage());

      Util.updateUIApplication(event);
    }
  }

  public void savePage() throws Exception {
    if(getUIPage() == null) return;
    Page page = PortalDataModelUtil.toPageModel(getUIPage(), true);      
    PortalDAO dataService = getApplicationComponent(PortalDAO.class);
    dataService.savePage(page); 
  }
  static public class SavePageActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.savePage();
    }
  }
  
}
