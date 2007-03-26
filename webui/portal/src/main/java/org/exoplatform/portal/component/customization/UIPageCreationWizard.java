/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIExoStart;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.event.PageNodeEvent;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 23, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
    template = "app:/groovy/webui/component/UIWizard.gtmpl" ,
    events = {
      @EventConfig(listeners = UIPageCreationWizard.ViewStep1ActionListener.class),
      @EventConfig(listeners = UIPageCreationWizard.ViewStep2ActionListener.class),
      @EventConfig(listeners = UIPageCreationWizard.ViewStep3ActionListener.class),
      @EventConfig(listeners = UIPageCreationWizard.ViewStep4ActionListener.class),
      @EventConfig(listeners = UIPageCreationWizard.ViewStep5ActionListener.class),
      @EventConfig(listeners = UIPageWizard.AbortActionListener.class)
    }
  ),
  @ComponentConfig(
    id = "ViewStep1",
    type = UIContainer.class,
    template = "app:/groovy/portal/webui/component/customization/UIWizardPageWelcome.gtmpl" 
  )
})
public class UIPageCreationWizard extends UIPageWizard {
  
  public UIPageCreationWizard() throws Exception {    
    addChild(UIContainer.class, "ViewStep1", null);
    addChild(UIWizardPageSetInfo.class, null, null).setRendered(false);    
    addChild(UIWizardPageSelectLayoutForm.class, null, null).setRendered(false);
    addChild(UIPagePreview.class, null, null).setRendered(false); 
  }     
  
  
  private void saveData() throws Exception {
    PortalDAO daoService = getApplicationComponent(PortalDAO.class);
    
    UIPagePreview uiPagePreview = getChild(UIPagePreview.class);
    UIPage uiPage = (UIPage)uiPagePreview.getUIComponent();
    Page page = PortalDataModelUtil.toPageModel(uiPage, true);
    daoService.savePage(page); 
    
    UIWizardPageSetInfo uiPageInfo = getChild(UIWizardPageSetInfo.class);  
    UIPageNodeSelector uiNodeSelector = uiPageInfo.getChild(UIPageNodeSelector.class);      
    PageNode selectedNode = uiNodeSelector.getSelectedPageNode();
    PageNavigation pageNav =  uiNodeSelector.getSelectedNavigation();
    
    PageNode pageNode = uiPageInfo.getPageNode();
    pageNode.setPageReference(page.getPageId());
    if(selectedNode != null){
      List<PageNode> children = selectedNode.getChildren();
      if(children == null) children = new ArrayList<PageNode>();
      children.add(pageNode);
      selectedNode.setChildren((ArrayList<PageNode>)children);        
      pageNode.setUri(selectedNode.getUri()+"/"+pageNode.getName());
    } else {        
      if(pageNav == null){
        pageNav = new PageNavigation();
        pageNav.setOwner(Util.getPortalRequestContext().getRemoteUser());
        List<PageNavigation> navs = Util.getUIPortal().getNavigations();
        if(navs == null) navs = new ArrayList<PageNavigation>();         
        navs.add(pageNav);
        Util.getUIPortal().setNavigation(navs);
      }
      pageNav.addNode(pageNode);
      pageNode.setUri(pageNode.getName());        
    }
    uiNodeSelector.selectPageNodeByUri(pageNode.getUri());
    daoService.savePageNavigation(pageNav);
    
    UIPortal uiPortal = Util.getUIPortal();
    String uri = pageNode.getUri();
    PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;
    uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
  }
  
  static  public class ViewStep1ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);      
      uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class);      
      uiWizard.updateAjax();
      uiWizard.viewStep(1);
    }
  }

  static  public class ViewStep3ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      uiWizard.setDescriptionWizard();
      
      uiWizard.updateAjax();
      uiWizard.viewStep(3);
    }
  }

  static  public class ViewStep4ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      
      UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);      
      uiExoStart.setUIControlWSWorkingComponent(UIWizardPageCreationBar.class);
      UIWizardPageCreationBar uiCreationBar = uiExoStart.getUIControlWSWorkingComponent();

      UIPageEditBar uiPageEditBar = uiCreationBar.getChild(UIPageEditBar.class);
      UIWizardPageCreationBar uiParent = uiPageEditBar.getParent();

      uiWizard.viewStep(4);      
      if(uiWizard.getSelectedStep() < 4){
        uiWizard.updateAjax();
        return;
      }
      
      UIPageTemplateOptions uiPageTemplateOptions = uiWizard.findFirstComponentOfType(UIPageTemplateOptions.class);
      UIWizardPageSetInfo uiPageInfo = uiWizard.getChild(UIWizardPageSetInfo.class);      
      
      PageNode pageNode = uiPageInfo.getPageNode();
      
      Page page = uiPageTemplateOptions.getSelectedOption();
      if(page == null) page  = new Page();
      if(page.getOwner() == null) page.setOwner(pageNode.getCreator());
      if(page.getName() == null) page.setName(pageNode.getName());
      
      boolean isDesktopPage = "Desktop".equals(page.getFactoryId());
      if(isDesktopPage) page.setShowMaxWindow(true);
      
      UIPagePreview uiPagePreview = uiWizard.getChild(UIPagePreview.class);
      WebuiRequestContext context = Util.getPortalRequestContext() ;  
      UIPage uiPage = uiPagePreview.createUIComponent(context, UIPage.class, page.getFactoryId(), null);
      PortalDataModelUtil.toUIPage(uiPage, page, true);
      uiPagePreview.setUIComponent(uiPage);
      
      if(isDesktopPage){
        uiWizard.saveData();
        uiWizard.updateUIPortal(uiPortalApp, event);
        return;
      }
      
      uiWizard.updateAjax();
      Class [] childrenToRender = {UIPageEditBar.class, UIPortletOptions.class}; 
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
      uiPageEditBar.setUIPage(uiPage);      
      uiPageTemplateOptions.setSelectedOption(null);
    }
  }

  static  public class ViewStep5ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      uiWizard.saveData();
      uiWizard.updateUIPortal(uiPortalApp, event);
    }
  }  

}
