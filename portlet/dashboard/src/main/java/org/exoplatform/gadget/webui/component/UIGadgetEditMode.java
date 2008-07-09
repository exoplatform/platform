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

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * June 27, 2008
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
    events = { @EventConfig(listeners = UIGadgetEditMode.SaveActionListener.class) })
public class UIGadgetEditMode extends UIForm {
  
  public static final String FIELD_URL = "gadgetUrl";

  public UIGadgetEditMode() throws Exception {
    PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext
        .getCurrentInstance();
    PortletPreferences pref = pcontext.getRequest().getPreferences();
    addUIFormInput(new UIFormStringInput(FIELD_URL, FIELD_URL, pref.getValue("url",
        "http://www.google.com/ig/modules/horoscope.xml")));
  }

  public static class SaveActionListener extends EventListener<UIGadgetEditMode> {
    public void execute(final Event<UIGadgetEditMode> event) throws Exception {
      UIGadgetEditMode uiGadgetEditMode = event.getSource();
      String url = uiGadgetEditMode.getUIStringInput(FIELD_URL).getValue();
      UIGadgetPortlet uiPortlet = uiGadgetEditMode.getParent();
      if (url == null || url.length() == 0) {
//        Object args[] = {uiGadgetEditMode.getLabel(uiGadgetEditMode.getUIStringInput(FIELD_URL).getId())};
//        uiPortlet.addMessage(new ApplicationMessage("EmptyFieldValidator.msg.empty-input", args));
        uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(uiPortlet.getUrl());
        return;
      }
      try {
        PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext
        .getCurrentInstance();
        PortletPreferences pref = pcontext.getRequest().getPreferences();
        new URL(url);
        pref.setValue("url", url);
        pref.store();
        pcontext.setApplicationMode(PortletMode.VIEW);
      } catch (Exception e) {
        uiGadgetEditMode.getUIStringInput(FIELD_URL).setValue(uiPortlet.getUrl());
//          Object[] args = { FIELD_URL, "Url"};
//          throw new MessageException(new ApplicationMessage("ExpressionValidator.msg.value-invalid",
//              args));
      }
    }
  }
}
