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
package org.exoplatform.gadget.webui.component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.URLValidator;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * June 27, 2008
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
    events = { 
      @EventConfig(listeners = UIGadgetEditMode.SaveActionListener.class),
      @EventConfig(listeners = UIGadgetEditMode.SelectTypeActionListener.class, phase = Phase.DECODE)
    }
)
public class UIGadgetEditMode extends UIForm {
  
  public static final String FIELD_URL = "gadgetUrl";
  
  public static final String TYPE_SELECTOR = "typeSelector" ;
  public static final String GADGET_SELECTOR = "gadgetSelector" ;
  public static final String REMOTE_TYPE = "remote" ;
  public static final String LOCAL_TYPE = "local" ;

  public UIGadgetEditMode() throws Exception {
    PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext
        .getCurrentInstance();
    PortletPreferences pref = pcontext.getRequest().getPreferences();
    
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>(REMOTE_TYPE, REMOTE_TYPE)) ;
    options.add(new SelectItemOption<String>(LOCAL_TYPE, LOCAL_TYPE)) ;
    
    UIFormSelectBox typeSelector = new UIFormSelectBox(TYPE_SELECTOR, null, options) ;
    typeSelector.setOnChange("SelectType") ;
    addUIFormInput(typeSelector) ;
    addUIFormInput(new UIFormStringInput(FIELD_URL, FIELD_URL, pref.getValue("url",
        "http://www.google.com/ig/modules/horoscope.xml")));
    UIFormSelectBox gadgetSelector = new UIFormSelectBox(GADGET_SELECTOR, null, 
        new ArrayList<SelectItemOption<String>>()) ;
    gadgetSelector.setRendered(false) ;
    gadgetSelector.addValidator(MandatoryValidator.class) ;
    addUIFormInput(gadgetSelector) ;
    setActions(new String[]{"Save"}) ;
  }

  public static class SaveActionListener extends EventListener<UIGadgetEditMode> {
    public void execute(final Event<UIGadgetEditMode> event) throws Exception {
      UIGadgetEditMode uiGadgetEditMode = event.getSource();
      PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext
          .getCurrentInstance();
      String url = uiGadgetEditMode.getUIStringInput(FIELD_URL).getValue();
      UIGadgetPortlet uiPortlet = uiGadgetEditMode.getParent();
      UIFormSelectBox typeSelector = uiGadgetEditMode.getUIFormSelectBox(TYPE_SELECTOR) ;
      org.exoplatform.webui.core.UIApplication uiApplication = pcontext.getUIApplication() ;
      
      
      if(typeSelector.getValue().equals(REMOTE_TYPE)) {
        String label = uiGadgetEditMode.getLabel(FIELD_URL);
        if(label.charAt(label.length() - 1) == ':') label = label.substring(0, label.length() - 1);
        Object[]  args = { label } ;
        
        if (url == null || url.length() == 0) {
          uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(uiPortlet.getUrl());
          uiApplication.addMessage(new ApplicationMessage("EmptyFieldValidator.msg.empty-input", 
                                                          args, ApplicationMessage.WARNING)) ;
          return;
        }
        url = url.trim() ;
        if(!url.matches(URLValidator.URL_REGEX)) {
          uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(uiPortlet.getUrl());
          uiApplication.addMessage(new ApplicationMessage("URLValidator.msg.invalid-url", 
                                                          args, ApplicationMessage.WARNING)) ;
          return ;
        }
        try {
          PortletPreferences pref = pcontext.getRequest().getPreferences();
          new URL(url);
          pref.setValue("url", url);
          pref.store();
          uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(url) ;
          pcontext.setApplicationMode(PortletMode.VIEW);
        } catch (Exception e) {
          uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(uiPortlet.getUrl());
        }
      } else {
        UIFormSelectBox gadgetSelector = uiGadgetEditMode.getUIFormSelectBox(GADGET_SELECTOR) ;
        String gadgetId = gadgetSelector.getValue() ;
        
        ApplicationRegistryService service = uiPortlet
            .getApplicationComponent(ApplicationRegistryService.class) ;
        Application application = service.getApplication(gadgetId) ;
        if (application == null) {
          return ;
        }
        StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE) ;
        windowId.append("#").append(pcontext.getRemoteUser()) ;
        windowId.append(":/").append(
            application.getApplicationGroup() + "/" + application.getApplicationName()).append('/') ;
        UIGadget uiGadget = event.getSource().createUIComponent(pcontext, UIGadget.class, null, null) ;
        uiGadget.setId(Integer.toString(uiGadget.hashCode()+1)) ;
        windowId.append(uiGadget.hashCode()) ;
        uiGadget.setApplicationInstanceId(windowId.toString()) ;
        PortletPreferences pref = pcontext.getRequest().getPreferences();
        pref.setValue("url", uiGadget.getUrl()) ;
        pref.store() ;
        uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(uiGadget.getUrl()) ;
        pcontext.setApplicationMode(PortletMode.VIEW) ;
        pcontext.addUIComponentToUpdateByAjax(uiPortlet) ;
      }
    }
  }
  
  static public class SelectTypeActionListener extends EventListener<UIGadgetEditMode> {
    public void execute(final Event<UIGadgetEditMode> event) throws Exception {
      UIGadgetEditMode uiGadgetEdit = event.getSource() ;
      UIFormSelectBox typeSelector = uiGadgetEdit.getUIFormSelectBox(TYPE_SELECTOR) ;
      String selectedValue = typeSelector.getValue() ;
      UIFormStringInput urlInput = uiGadgetEdit.getUIStringInput(FIELD_URL) ;
      UIFormSelectBox gadgetSelector = uiGadgetEdit.getUIFormSelectBox(GADGET_SELECTOR) ;
      if(selectedValue.equals(REMOTE_TYPE)) {
        urlInput.setRendered(true) ;
        gadgetSelector.setRendered(false) ;
      } else {
        urlInput.setRendered(false) ;
        gadgetSelector.setRendered(true) ;
        List<SelectItemOption<String>> gadgetItems = gadgetSelector.getOptions() ;
        gadgetItems.clear() ;
        
        ApplicationRegistryService service = uiGadgetEdit.getApplicationComponent(ApplicationRegistryService.class) ;
        UserACL acl = uiGadgetEdit.getApplicationComponent(UserACL.class) ;
        List<Application> appList = service.getAllApplications() ;
        String accessUser = Util.getPortalRequestContext().getRemoteUser() ;
        for (Application app : appList) {
          if(app.getApplicationType().equals(org.exoplatform.web.application.Application.EXO_GAGGET_TYPE)) {
            for (String per : app.getAccessPermissions()) {
              if(acl.hasPermission(per, accessUser)) {
                gadgetItems.add(new SelectItemOption<String>(app.getDisplayName(), app.getId())) ;
                break ;
              }
            }
          }
        }
      }  
      event.getRequestContext().addUIComponentToUpdateByAjax(uiGadgetEdit) ;
    }
  }
}
