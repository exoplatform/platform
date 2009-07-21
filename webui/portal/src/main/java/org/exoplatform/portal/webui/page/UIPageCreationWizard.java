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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.UIPageNodeSelector;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComposer;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL Author : Dang Van Minh minhdv81@yahoo.com
 * Jun 23, 2006
 */
@ComponentConfigs( {
    @ComponentConfig(template = "system:/groovy/webui/core/UIWizard.gtmpl", events = {
        @EventConfig(listeners = UIPageCreationWizard.ViewStep1ActionListener.class),
        @EventConfig(listeners = UIPageCreationWizard.ViewStep2ActionListener.class),
        @EventConfig(listeners = UIPageCreationWizard.ViewStep3ActionListener.class),
        @EventConfig(listeners = UIPageCreationWizard.ViewStep4ActionListener.class),
        @EventConfig(listeners = UIPageCreationWizard.ViewStep5ActionListener.class),
        @EventConfig(listeners = UIPageWizard.AbortActionListener.class) }),
    @ComponentConfig(id = "ViewStep1", type = UIContainer.class, template = "system:/groovy/portal/webui/page/UIWizardPageWelcome.gtmpl") })
public class UIPageCreationWizard extends UIPageWizard {

  public UIPageCreationWizard() throws Exception {
    addChild(UIContainer.class, "ViewStep1", null);
    addChild(UIWizardPageSetInfo.class, null, null).setRendered(false);
    addChild(UIWizardPageSelectLayoutForm.class, null, null).setRendered(false);
    addChild(UIPagePreview.class, null, null).setRendered(false);
    setNumberSteps(4);
    setShowWelcomeComponent(true);
  }

  private void saveData() throws Exception {
    UserPortalConfigService service = getApplicationComponent(UserPortalConfigService.class);
    UIPagePreview uiPagePreview = getChild(UIPagePreview.class);
    UIPage uiPage = (UIPage) uiPagePreview.getUIComponent();
    UIPortal uiPortal = Util.getUIPortal();
    if (PortalConfig.PORTAL_TYPE.equals(uiPage.getOwnerType())) {
      uiPage.setAccessPermissions(uiPortal.getAccessPermissions());
      uiPage.setEditPermission(uiPortal.getEditPermission());
    } else if (PortalConfig.GROUP_TYPE.equals(uiPage.getOwnerType())) {
      UserACL acl = getApplicationComponent(UserACL.class);
      uiPage.setAccessPermissions(new String[] { "*:/" + uiPage.getOwnerId() });
      uiPage.setEditPermission(acl.getMakableMT() + ":/" + uiPage.getOwnerId());
    }

    UIWizardPageSetInfo uiPageInfo = getChild(UIWizardPageSetInfo.class);
    UIPageNodeSelector uiNodeSelector = uiPageInfo.getChild(UIPageNodeSelector.class);
    PageNode selectedNode = uiNodeSelector.getSelectedPageNode();
    PageNavigation pageNav = uiNodeSelector.getSelectedNavigation();

    Page page = PortalDataMapper.toPageModel(uiPage);
    PageNode pageNode = uiPageInfo.getPageNode();
    pageNode.setPageReference(page.getPageId());
    if (selectedNode != null) {
      List<PageNode> children = selectedNode.getChildren();
      if (children == null)
        children = new ArrayList<PageNode>();
      children.add(pageNode);
      selectedNode.setChildren((ArrayList<PageNode>) children);
    } else {
      pageNav.addNode(pageNode);
    }
    pageNav.setModifier(RequestContext.<WebuiRequestContext> getCurrentInstance().getRemoteUser());
    uiNodeSelector.selectPageNodeByUri(pageNode.getUri());

    service.create(page);
    service.update(pageNav);
    for (PageNavigation editNav : uiNodeSelector.getPageNavigations()) {
      setNavigation(uiPortal.getNavigations(), editNav);
    }
    String uri = pageNav.getId() + "::" + pageNode.getUri();
    PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal,
                                                                  PageNodeEvent.CHANGE_PAGE_NODE,
                                                                  uri);
    uiPortal.broadcast(pnevent, Event.Phase.PROCESS);
  }

  private void setNavigation(List<PageNavigation> navs, PageNavigation nav) {
    for (int i = 0; i < navs.size(); i++) {
      if (navs.get(i).getId() == nav.getId()) {
        navs.set(i, nav);
        return;
      }
    }
  }

  public boolean isSelectedNodeExist() throws Exception {
    UIWizardPageSetInfo uiPageSetInfo = getChild(UIWizardPageSetInfo.class);
    PageNavigation navigation = uiPageSetInfo.getChild(UIPageNodeSelector.class)
                                             .getSelectedNavigation();
    PageNode pageNode = uiPageSetInfo.getPageNode();
    PageNode selectedPageNode = uiPageSetInfo.getSelectedPageNode();
    List<PageNode> sibbling = null;
    if (selectedPageNode != null)
      sibbling = selectedPageNode.getChildren();
    else
      sibbling = navigation.getNodes();
    if (sibbling != null) {
      for (PageNode ele : sibbling) {
        if (ele.getUri().equals(pageNode.getUri())) {
          return true;
        }
      }
    }

    return false;
  }

  // TODO: Review this listener after remove UIExoStart
  static public class ViewStep1ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      uiWizard.setDescriptionWizard(1);
      uiWizard.updateWizardComponent();
      uiWizard.viewStep(1);
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.findFirstComponentOfType(UIPortalComposer.class).setRendered(false);
    }
  }

  static public class ViewStep2ActionListener extends EventListener<UIPageWizard> {
    public void execute(Event<UIPageWizard> event) throws Exception {
      UIPageWizard uiWizard = event.getSource();
      uiWizard.setDescriptionWizard(2);

      uiWizard.updateWizardComponent();
      uiWizard.viewStep(2);
      
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.findFirstComponentOfType(UIPortalComposer.class).setRendered(false);
    }
  }

  static public class ViewStep3ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.findFirstComponentOfType(UIPortalComposer.class).setRendered(false);
      uiWizard.viewStep(3);

      if (uiWizard.getSelectedStep() < 3) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.StepByStep", null));
        uiWizard.setDescriptionWizard(2);
        uiWizard.viewStep(2);
        uiWizard.updateWizardComponent();
        return;
      }

      if (uiWizard.isSelectedNodeExist()) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.NameNotSame", null));
        uiWizard.viewStep(2);
        uiWizard.updateWizardComponent();
        return;
      }

      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      UIPageNodeSelector uiNodeSelector = uiPageSetInfo.getChild(UIPageNodeSelector.class);
      uiWizard.setDescriptionWizard(3);
      uiWizard.updateWizardComponent();
      PageNavigation navigation = uiNodeSelector.getSelectedNavigation();
      if (navigation == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.notSelectedPageNavigation",
                                                      new String[] {}));
        ;
        uiWizard.viewStep(2);
        return;
      }

      if (uiPageSetInfo.getUIFormCheckBoxInput(UIWizardPageSetInfo.SHOW_PUBLICATION_DATE)
                       .isChecked()) {
        Calendar startCalendar = uiPageSetInfo.getUIFormDateTimeInput(UIWizardPageSetInfo.START_PUBLICATION_DATE)
                                              .getCalendar();
        Date startDate = startCalendar.getTime();
        Calendar endCalendar = uiPageSetInfo.getUIFormDateTimeInput(UIWizardPageSetInfo.END_PUBLICATION_DATE)
                                            .getCalendar();
        Date endDate = endCalendar.getTime();
        if (startDate.after(endDate)) {
          uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.startDateBeforeEndDate",
                                                        null));
          uiWizard.viewStep(2);
          return;
        }
      }

    }
  }

  static public class ViewStep4ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.findFirstComponentOfType(UIPortalComposer.class).setRendered(true);
      WebuiRequestContext context = Util.getPortalRequestContext();

      if (uiWizard.isSelectedNodeExist()) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.NameNotSame", null));
        uiWizard.setDescriptionWizard(2);
        uiWizard.viewStep(2);
        uiWizard.updateWizardComponent();
        return;
      }
      uiWizard.viewStep(4);

      if (uiWizard.getSelectedStep() < 4) {
        uiWizard.setDescriptionWizard(uiWizard.getSelectedStep());
        uiWizard.updateWizardComponent();
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.StepByStep", null));
        return;
      }

      UIPageTemplateOptions uiPageTemplateOptions = uiWizard.findFirstComponentOfType(UIPageTemplateOptions.class);
      UIWizardPageSetInfo uiPageInfo = uiWizard.getChild(UIWizardPageSetInfo.class);

      UIPageNodeSelector uiNodeSelector = uiPageInfo.getChild(UIPageNodeSelector.class);
      PageNavigation pageNavi = uiNodeSelector.getSelectedNavigation();
      String ownerType = pageNavi.getOwnerType();
      String ownerId = pageNavi.getOwnerId();

      PageNode pageNode = uiPageInfo.getPageNode();
      Page page = uiPageTemplateOptions.createPageFromSelectedOption(ownerType, ownerId);
      page.setCreator(context.getRemoteUser());
      page.setName("page" + page.hashCode());
      String pageId = pageNavi.getOwnerType() + "::" + pageNavi.getOwnerId() + "::"
          + page.getName();
      DataStorage storage = uiWizard.getApplicationComponent(DataStorage.class);
      if (storage.getPage(pageId) != null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.NameNotSame", null));
        uiWizard.setDescriptionWizard(2);
        uiWizard.viewStep(2);
        uiWizard.updateWizardComponent();
      }
      page.setModifiable(true);
      if (page.getTitle() == null || page.getTitle().trim().length() == 0)
        page.setTitle(pageNode.getName());

      boolean isDesktopPage = Page.DESKTOP_PAGE.equals(page.getFactoryId());
      if (isDesktopPage)
        page.setShowMaxWindow(true);

      UIPagePreview uiPagePreview = uiWizard.getChild(UIPagePreview.class);
      UIPage uiPage;
      if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        uiPage = uiWizard.createUIComponent(context, UIDesktopPage.class, null, null);
      } else {
        uiPage = uiWizard.createUIComponent(context, UIPage.class, null, null);
      }

      PortalDataMapper.toUIPage(uiPage, page);
      uiPagePreview.setUIComponent(uiPage);

      if (isDesktopPage) {
        uiWizard.saveData();
        uiWizard.updateUIPortal(uiPortalApp, event);
        return;
      }

      uiWizard.updateWizardComponent();
      uiPageTemplateOptions.setSelectedOption(null);
    }
  }

  static public class ViewStep5ActionListener extends EventListener<UIPageCreationWizard> {
    public void execute(Event<UIPageCreationWizard> event) throws Exception {
      UIPageCreationWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = uiWizard.getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.findFirstComponentOfType(UIPortalComposer.class).setRendered(false);
      if (uiWizard.isSelectedNodeExist()) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageCreationWizard.msg.NameNotSame", null));
        uiWizard.setDescriptionWizard(2);
        uiWizard.viewStep(2);
        uiWizard.updateWizardComponent();
        return;
      }
      uiPortalApp.setEditMode(UIPortalApplication.NORMAL_MODE);
      uiWizard.saveData();
      uiWizard.updateUIPortal(uiPortalApp, event);
    }
  }

}
