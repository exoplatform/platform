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
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
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
    setNumberSteps(4);
    setHasWelcome(true);
  }     
  
  private void saveData() throws Exception {
    UserPortalConfigService service = getApplicationComponent(UserPortalConfigService.class);
    UIPagePreview uiPagePreview = getChild(UIPagePreview.class);
    UIPage uiPage = (UIPage)uiPagePreview.getUIComponent();
    
    UIWizardPageSetInfo uiPageInfo = getChild(UIWizardPageSetInfo.class);  
    UIPageNodeSelector uiNodeSelector = uiPageInfo.getChild(UIPageNodeSelector.class);      
    PageNode selectedNode = uiNodeSelector.getSelectedPageNode();
    PageNavigation pageNav =  uiNodeSelector.getSelectedNavigation();
    
    Page page = PortalDataModelUtil.toPageModel(uiPage);
    PageNode pageNode = uiPageInfo.getPageNode();
    pageNode.setPageReference(page.getPageId());
    if(selectedNode != null){
      List<PageNode> children = selectedNode.getChildren();
      if(children == null) children = new ArrayList<PageNode>();
      children.add(pageNode);
      selectedNode.setChildren((ArrayList<PageNode>)children);        
    } else {       
      pageNav.addNode(pageNode);      
    }
    pageNav.setModifier(RequestContext.<WebuiRequestContext>getCurrentInstance().getRemoteUser());
    uiNodeSelector.selectPageNodeByUri(pageNode.getUri());
 
    service.create(page); 
    service.update(pageNav);
    
    UIPortal uiPortal = Util.getUIPortal();
    for(PageNavigation editNav : uiNodeSelector.getNavigations()) {
      setNavigation(uiPortal.getNavigations(), editNav);
    }
    String uri = pageNode.getUri();
    PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;
    uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
  }
  
  private void setNavigation(List<PageNavigation> navs, PageNavigation nav) {
    for(int i = 0; i < navs.size(); i++) {
      if(navs.get(i).getId().equals(nav.getId())) {
        navs.set(i, nav);
        return;
      }
    }
  }
  
  static  public class ViewStep1ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);      
      uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class);      
      uiWizard.updateWizardComponent();
      uiWizard.viewStep(1);      
    }
  }

  static  public class ViewStep3ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIWizardPageSetInfo pageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      UIPageNodeSelector uiNodeSelector = pageSetInfo.getChild(UIPageNodeSelector.class);
      uiWizard.setDescriptionWizard();
      uiWizard.updateWizardComponent();
      
      PageNode pageNode = pageSetInfo.getPageNode();
      if (uiNodeSelector.findPageNodeByUri(pageNode.getUri()) != null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.NameNotSame", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        uiWizard.viewStep(2);
        return;
      }
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
        uiWizard.updateWizardComponent();
        return;
      }
      
      UIPageTemplateOptions uiPageTemplateOptions = uiWizard.findFirstComponentOfType(UIPageTemplateOptions.class);
      UIWizardPageSetInfo uiPageInfo = uiWizard.getChild(UIWizardPageSetInfo.class);      
      WebuiRequestContext context = Util.getPortalRequestContext() ;  
      
      String ownerType = PortalConfig.USER_TYPE ;
      String ownerId = context.getRemoteUser() ;
      UIPageNodeSelector uiNodeSelector = uiPageInfo.getChild(UIPageNodeSelector.class) ;
      PageNavigation pageNavi = uiNodeSelector.getSelectedNavigation() ;
      if (pageNavi != null) {
        ownerType = pageNavi.getOwnerType() ;
        ownerId = pageNavi.getOwnerId() ;
      }
      
      PageNode pageNode = uiPageInfo.getPageNode();
      Page page = uiPageTemplateOptions.getSelectedOption();
      if(page == null){
        page  = new Page();
        page.setCreator(context.getRemoteUser());
      }
      if(page.getOwnerType() == null || page.getOwnerType().trim().length() == 0) {
        page.setOwnerType(ownerType);
      }
      if(page.getOwnerId() == null || page.getOwnerId().trim().length() == 0) {
        page.setOwnerId(ownerId);
      }
      if(page.getName() == null || page.getName().trim().length() == 0 || page.getName().equals("UIPage")) {
        page.setName(pageNode.getName());
      }
      page.setModifiable(true);
      if(page.getTitle() == null || page.getTitle().trim().length() == 0) page.setTitle(pageNode.getLabel()) ;
      
      boolean isDesktopPage = Page.DESKTOP_PAGE.equals(page.getFactoryId());
      if(isDesktopPage) page.setShowMaxWindow(true);
      
      String pageId = page.getPageId();
      DataStorage service = uiWizard.getApplicationComponent(DataStorage.class);
      Page existPage = service.getPage(pageId);
      if(existPage != null) page.setName(page.getName() + String.valueOf(page.hashCode()));
      
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

  static  public class ViewStep5ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      uiWizard.saveData();
      uiWizard.updateUIPortal(uiPortalApp, event);   
    }
  }  

}
