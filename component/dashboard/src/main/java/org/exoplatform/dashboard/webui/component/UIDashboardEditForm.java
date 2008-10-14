/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.dashboard.webui.component;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

@ComponentConfig(
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    lifecycle = UIFormLifecycle.class, 
    events = { 
      @EventConfig(listeners = UIDashboardEditForm.SaveActionListener.class)
    }
) 
public class UIDashboardEditForm extends UIForm {

  public static final String TOTAL_COLUMNS = "totalColumns";

  public static final int MAX_COLUMNS = 4;

  public static final int DEFAULT_COLUMNS = 3;

  public UIDashboardEditForm() throws Exception {
    PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext
        .getCurrentInstance();
    PortletPreferences pref = pcontext.getRequest().getPreferences();
    addUIFormInput(new UIFormStringInput(TOTAL_COLUMNS, TOTAL_COLUMNS, pref.getValue(
        TOTAL_COLUMNS, "3")));
    addUIFormInput(new UIFormStringInput(DashboardParent.OWNER, DashboardParent.OWNER, pref.getValue(
        DashboardParent.OWNER, null)));
    addUIFormInput(new UIFormStringInput(DashboardParent.ISPRIVATE, DashboardParent.ISPRIVATE, pref.getValue(
        DashboardParent.ISPRIVATE, null)));
  }

  public static class SaveActionListener extends EventListener<UIDashboardEditForm> {
    public final void execute(final Event<UIDashboardEditForm> event) throws Exception {

      UIDashboardEditForm uiForm = event.getSource();
      UIFormStringInput uiInput = uiForm.getUIStringInput(TOTAL_COLUMNS);

      String label = uiForm.getLabel(uiInput.getName());
      if (label == null) {
        label = uiInput.getName();
      }
      label = label.trim();
      if (label.charAt(label.length() - 1) == ':') {
        label = label.substring(0, label.length() - 1);
      }
      Object[] args = {label, String.valueOf(1), String.valueOf(MAX_COLUMNS) };

      PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext
          .getCurrentInstance();
      PortletPreferences pref = pcontext.getRequest().getPreferences();
      String lastValue = pref.getValue(TOTAL_COLUMNS, "" + DEFAULT_COLUMNS);

      if (uiInput.getValue() == null || uiInput.getValue().length() == 0) {
        uiInput.setValue(lastValue);
        throw new MessageException(new ApplicationMessage("EmptyFieldValidator.msg.empty-input",
            args));
      }

      int totalCols = 0;
      try {
        totalCols = Integer.parseInt(uiInput.getValue());
      } catch (Exception e) {
        uiInput.setValue(lastValue);
        throw new MessageException(new ApplicationMessage(
            "NumberFormatValidator.msg.Invalid-number", args));
      }

      if (totalCols < 1 || totalCols > MAX_COLUMNS) {
        uiInput.setValue(lastValue);
        throw new MessageException(new ApplicationMessage(
            "NumberInRangeValidator.msg.Invalid-number", args));
      }

      uiInput = uiForm.getUIStringInput(DashboardParent.ISPRIVATE);
      pref.setValue(DashboardParent.ISPRIVATE, uiInput.getValue());

      uiInput = uiForm.getUIStringInput(DashboardParent.OWNER);
      pref.setValue(DashboardParent.OWNER, uiInput.getValue());

      pref.setValue(TOTAL_COLUMNS, String.valueOf(totalCols));
      pref.store();

      UIDashboardContainer uiDashboardContainer = ((UIContainer) uiForm.getParent())
          .getChild(UIDashboard.class).getChild(UIDashboardContainer.class);
      uiDashboardContainer.setColumns(totalCols);
      uiDashboardContainer.save();
      pcontext.setApplicationMode(PortletMode.VIEW);
    }
  }
}
