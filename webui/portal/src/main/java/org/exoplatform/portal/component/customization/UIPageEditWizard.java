/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIExoStart;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.event.PageNodeEvent;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 23, 2006
 */
@ComponentConfig(
  template = "app:/groovy/webui/component/UIWizard.gtmpl" ,
  events = {
    @EventConfig(listeners = UIPageEditWizard.ViewStep1ActionListener.class),
    @EventConfig(listeners = UIPageEditWizard.ViewStep2ActionListener.class),
    @EventConfig(listeners = UIPageEditWizard.ViewStep3ActionListener.class),
    @EventConfig(listeners = UIPageEditWizard.ViewStep4ActionListener.class),
    @EventConfig(listeners = UIPageEditWizard.AbortActionListener.class)
  }
)
public class UIPageEditWizard extends UIPageWizard {
  
  public UIPageEditWizard() throws Exception {
    addChild(UIWizardPageSetInfo.class, null, null).setEditPageNode(true);    
    addChild(UIWizardPageSelectLayoutForm.class, null, null).setRendered(false);
    addChild(UIPagePreview.class, null, null).setRendered(false); 
    setNumberSteps(3);
    setHasWelcome(false);
  }
  
  private void saveData() throws Exception {
    DataStorage daoService = getApplicationComponent(DataStorage.class);
    
    UIPagePreview uiPagePreview = getChild(UIPagePreview.class);
    UIPage uiPage = (UIPage)uiPagePreview.getUIComponent();
    Page page = PortalDataModelUtil.toPageModel(uiPage);
    daoService.save(page); 
    
    UIWizardPageSetInfo uiPageInfo = getChild(UIWizardPageSetInfo.class);  
    UIPageNodeSelector uiNodeSelector = uiPageInfo.getChild(UIPageNodeSelector.class);      
    PageNavigation pageNav =  uiNodeSelector.getSelectedNavigation();
    daoService.save(pageNav);
    
    UIPortal uiPortal = Util.getUIPortal();
    uiPortal.setNavigation(uiNodeSelector.getNavigations());
    String uri = uiPageInfo.getPageNode().getUri();
    PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;
    uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
  }
  
  static  public class ViewStep1ActionListener extends EventListener<UIPageWizard> {
    public void execute(Event<UIPageWizard> event) throws Exception { 
      UIPageWizard uiWizard = event.getSource();
      uiWizard.setDescriptionWizard();
      
      uiWizard.updateWizardComponent();
      uiWizard.viewStep(1);   
    }
  }

  static  public class ViewStep3ActionListener extends EventListener<UIPageEditWizard> {
    public void execute(Event<UIPageEditWizard> event) throws Exception {
      UIPageEditWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);

      UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);      
      uiExoStart.setUIControlWSWorkingComponent(UIWizardPageCreationBar.class);
      UIWizardPageCreationBar uiCreationBar = uiExoStart.getUIControlWSWorkingComponent();
      
      UIPageEditBar uiPageEditBar = uiCreationBar.getChild(UIPageEditBar.class);
      UIWizardPageCreationBar uiParent = uiPageEditBar.getParent();

      uiWizard.viewStep(3);      
      if(uiWizard.getSelectedStep() < 3){
        uiWizard.updateWizardComponent();
        return;
      }
      
      UIPageTemplateOptions uiPageTemplateOptions = uiWizard.findFirstComponentOfType(UIPageTemplateOptions.class);
      UIWizardPageSetInfo uiPageInfo = uiWizard.getChild(UIWizardPageSetInfo.class);      
      PageNode pageNode = uiPageInfo.getPageNode();
      
      Page page = null;
      Page templatePage = uiPageTemplateOptions.getSelectedOption();
      DataStorage configService = uiWizard.getApplicationComponent(DataStorage.class);
      page = configService.getPage(pageNode.getPageReference());
      
      boolean isDesktopPage = false;
      if(templatePage != null) {
        templatePage.setName(page.getName());
        templatePage.setOwnerId(page.getOwnerId());
        page  = templatePage;
        isDesktopPage = "Desktop".equals(page.getFactoryId());
        if(isDesktopPage) {
          page.setChildren(new ArrayList<Object>());
          page.setShowMaxWindow(true);
        }
      } else {
        isDesktopPage = "Desktop".equals(page.getFactoryId());
      }
      WebuiRequestContext context = Util.getPortalRequestContext() ;
      
      if(page == null) page  = new Page();
//      if(page.getOwnerId() == null) page.setOwnerId(pageNode.getCreator());
//      if(page.getName() == null || page.getName().equals("UIPage")) page.setName(pageNode.getName());
      if(page.getOwnerId() == null) page.setOwnerId(context.getRemoteUser());
      
      UIPagePreview uiPagePreview = uiWizard.getChild(UIPagePreview.class);
      UIPage uiPage = uiPagePreview.createUIComponent(context, UIPage.class, page.getFactoryId(), null);
      PortalDataModelUtil.toUIPage(uiPage, page);
      uiPagePreview.setUIComponent(uiPage);
      
      if(isDesktopPage){
        uiWizard.saveData();
        uiWizard.updateUIPortal(uiPortalApp, event);
        return;
      }
      uiWizard.updateWizardComponent();
      
      Class [] childrenToRender = {UIPageEditBar.class, UIPortletOptions.class}; 
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
      
      uiPageEditBar.setUIPage(uiPage);      
      uiPageTemplateOptions.setSelectedOption(null);
    }
  }

  static  public class ViewStep4ActionListener extends EventListener<UIPageEditWizard> {
    public void execute(Event<UIPageEditWizard> event) throws Exception {
      UIPageEditWizard uiWizard = event.getSource();
      uiWizard.saveData();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      uiWizard.updateUIPortal(uiPortalApp, event);
    }
  }  
  
  static public class AbortActionListener extends EventListener<UIPageEditWizard> {
    public void execute(Event<UIPageEditWizard> event) throws Exception {
      UIPageEditWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      uiWizard.updateUIPortal(uiPortalApp, event);    
    }
  }

}
