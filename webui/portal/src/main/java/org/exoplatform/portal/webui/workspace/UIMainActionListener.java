/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.webui.page.UIPageBrowser;
import org.exoplatform.portal.webui.page.UIPageCreationWizard;
import org.exoplatform.portal.webui.page.UIPageEditWizard;
import org.exoplatform.portal.webui.page.UIWizardPageSetInfo;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalBrowser;
import org.exoplatform.portal.webui.portal.UIPortalComposer;
import org.exoplatform.portal.webui.portal.UIPortalForm;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS Author : Pham Thanh Tung
 * thanhtungty@gmail.com May 5, 2009
 */
public class UIMainActionListener {

  static public class EditPageActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      System.out.println("\n\n\nThis action is not implemented");
    }
  }

  static public class EditCurrentPageActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setEditting(true);
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
      uiWorkingWS.addChild(UIPortalComposer.class, null, null).setRendered(false);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
      uiToolPanel.setWorkingComponent(UIPageEditWizard.class, null);
      UIPageEditWizard uiWizard = (UIPageEditWizard) uiToolPanel.getUIComponent();
      uiWizard.setDescriptionWizard(1);
      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      uiPageSetInfo.setEditMode();
      uiPageSetInfo.createEvent("ChangeNode", Event.Phase.DECODE, event.getRequestContext())
                   .broadcast();
    }
  }

  static public class PageCreationWizardActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setEditting(true);
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
      uiWorkingWS.addChild(UIPortalComposer.class, null, null).setRendered(false);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      uiToolPanel.setWorkingComponent(UIPageCreationWizard.class, null);
      UIPageCreationWizard uiWizard = (UIPageCreationWizard) uiToolPanel.getUIComponent();
      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      uiPageSetInfo.setShowPublicationDate(false);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
    }
  }

  static public class EditPortalActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      if (!uiPortal.isModifiable()) {
        UIPortalApplication uiPortalApp = event.getSource()
                                               .getAncestorOfType(UIPortalApplication.class);
        uiPortalApp.addMessage(new ApplicationMessage("UIPortalManagement.msg.Invalid-editPermission",
                                                      new String[] { uiPortal.getName() }));
        ;
        return;
      }
      System.out.println("\n\n\nThis action is not implemented");
    }
  }

  public static class CreatePortalActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UserACL userACL = uiApp.getApplicationComponent(UserACL.class);
      if (!userACL.hasCreatePortalPermission()) {
        uiApp.addMessage(new ApplicationMessage("UIPortalBrowser.msg.Invalid-createPermission",
                                                null));
        ;
        return;
      }
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
      UIPortalForm uiNewPortal = uiMaskWS.createUIComponent(UIPortalForm.class,
                                                            "CreatePortal",
                                                            "UIPortalForm");
      uiMaskWS.setUIComponent(uiNewPortal);
      uiMaskWS.setShow(true);
      prContext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  static public class BrowsePortalActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setEditting(true);
      UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      UIPortalBrowser uiPortalBrowser = uiToolPanel.createUIComponent(UIPortalBrowser.class, null, null);
      uiToolPanel.setUIComponent(uiPortalBrowser);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
    }
  }

  static public class EditInlineActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      if (!uiPortal.isModifiable()) {
        uiApp.addMessage(new ApplicationMessage("UIPortalManagement.msg.Invalid-editPermission",
                                                new String[] { uiPortal.getName() }));
        return;
      }
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiApp.setEditting(true);
      uiWorkingWS.addChild(UIPortalComposer.class, null, null);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
    }

  }

  static public class ManagePortalsActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      // UISiteManagement siteManager =
      // uiToolPanel.createUIComponent(UISiteManagement.class, null, null);
      // uiToolPanel.setUIComponent(siteManager);
      // siteManager.loadPortalConfigs();
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
    }
  }

  static public class ManageGroupsActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      // NewUIGroupManagement groupsManager =
      // uiToolPanel.createUIComponent(NewUIGroupManagement.class, null, null);
      // uiToolPanel.setUIComponent(groupsManager);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
    }
  }

  static public class BrowsePageActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setEditting(true);
      UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      UIPageBrowser uiPageBrowser = uiToolPanel.createUIComponent(UIPageBrowser.class, null, null) ;
      uiToolPanel.setUIComponent(uiPageBrowser);
      uiPageBrowser.setShowAddNewPage(true);    
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
    }
  }

}
