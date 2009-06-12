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
package org.exoplatform.navigation.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.StringLengthValidator;

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

/*
 * Created by The eXo Platform SAS
 * Author : tam.nguyen
 *          tamndrok@gmail.com
 * June 11, 2009  
 */
@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl", events = {
    @EventConfig(listeners = UIPageNavigationForm.SaveActionListener.class),
    @EventConfig(listeners = UIPageNavigationForm.CloseActionListener.class, phase = Phase.DECODE, name = "ClosePopup") })
public class UIPageNavigationForm extends UIForm {

  protected PageNavigation pageNav_;

  private String           ownerId;

  public UIPageNavigationForm() throws Exception {

  }

  public void addFormInput() throws Exception {
    PortalRequestContext pContext = Util.getPortalRequestContext();

    List<SelectItemOption<String>> priorties = new ArrayList<SelectItemOption<String>>();
    for (int i = 1; i < 11; i++) {
      priorties.add(new SelectItemOption<String>(String.valueOf(i), String.valueOf(i)));
    }
    addUIFormInput(new UIFormStringInput("ownerType", "ownerType", PortalConfig.GROUP_TYPE).setEditable(false)).addUIFormInput(new UIFormStringInput("ownerId",
                                                                                                                                                     "ownerId",
                                                                                                                                                     ownerId).setEditable(false))
                                                                                                               .addUIFormInput(new UIFormTextAreaInput("description",
                                                                                                                                                       "description",
                                                                                                                                                       null).addValidator(StringLengthValidator.class,
                                                                                                                                                                          0,
                                                                                                                                                                          255))
                                                                                                               .addUIFormInput(new UIFormSelectBox("priority",
                                                                                                                                                   null,
                                                                                                                                                   priorties));
  }

  public void setValues(PageNavigation pageNavigation) throws Exception {
    pageNav_ = pageNavigation;
    invokeGetBindingBean(pageNavigation);
    removeChildById("ownerId");
    getUIStringInput("creator").setValue(pageNavigation.getCreator());
    UIFormStringInput ownerId = new UIFormStringInput("ownerId",
                                                      "ownerId",
                                                      pageNavigation.getOwnerId());
    ownerId.setEditable(false);
    ownerId.setParent(this);
    getChildren().add(1, ownerId);
    UIFormSelectBox uiSelectBox = findComponentById("priority");
    uiSelectBox.setValue(String.valueOf(pageNavigation.getPriority()));
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  static public class SaveActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {
      UIPageNavigationForm uiForm = event.getSource();
      PageNavigation pageNav = uiForm.pageNav_;
      PortalRequestContext pcontext = Util.getPortalRequestContext();

      // if edit navigation
      if (pageNav != null) {
        uiForm.invokeSetBindingBean(pageNav);
        UIFormSelectBox uiSelectBox = uiForm.findComponentById("priority");
        int priority = Integer.parseInt(uiSelectBox.getValue());
        pageNav.setPriority(priority);
        pageNav.setModifier(pcontext.getRemoteUser());
        UIComponentDecorator uiFormParent = uiForm.getParent();
        uiFormParent.setUIComponent(null);
        pcontext.addUIComponentToUpdateByAjax(uiFormParent);
        return;
      }

      // if add navigation
      pageNav = new PageNavigation();
      // set properties for navigation
      uiForm.invokeSetBindingBean(pageNav);
      UIFormStringInput uiOwnerId = uiForm.findComponentById("ownerId");
      UIFormSelectBox uiSelectBox = uiForm.findComponentById("priority");
      int priority = Integer.parseInt(uiSelectBox.getValue());
      pageNav.setPriority(priority);
      pageNav.setModifiable(true);
      pageNav.setCreator(pcontext.getRemoteUser());
      pageNav.setOwnerId(uiOwnerId.getValue());
      UIPortalApplication uiPortalApp = uiForm.getAncestorOfType(UIPortalApplication.class);

      // ensure this navigation is not exist
      DataStorage dataService = uiForm.getApplicationComponent(DataStorage.class);
      if (dataService.getPageNavigation(pageNav.getOwnerType(), pageNav.getOwnerId()) != null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageNavigationForm.msg.existPageNavigation",
                                                      new String[] { pageNav.getOwnerId() }));
        ;
        return;
      }

      // create navigation for group
      UserPortalConfigService service = uiForm.getApplicationComponent(UserPortalConfigService.class);

      service.create(pageNav);

      // close popup window, update popup window
      UIPopupWindow uiPopup = uiForm.getParent();
      uiPopup.setShow(false);
      pcontext.addUIComponentToUpdateByAjax(uiPopup);
    }
  }

  static public class CloseActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {
      UIPageNavigationForm uiForm = event.getSource();
      uiForm.<UIComponent> getParent().broadcast(event, Phase.ANY);
    }
  }
}
