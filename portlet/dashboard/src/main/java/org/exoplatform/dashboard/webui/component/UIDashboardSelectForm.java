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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Dinh Tan
 *          tan.pham@exoplatform.com
 * Apr 24, 2008  
 */
@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/dashboard/webui/component/UIDashboardSelectForm.gtmpl",
      lifecycle = UIFormLifecycle.class
  )
})
public class UIDashboardSelectForm extends UIForm {
  
  private List<Application> gadgets ;
  
  public UIDashboardSelectForm() throws Exception {
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class);
    service.importExoGadgets();
    List<Application> applications = service.getAllApplications();
    List<Application> listGadgets = new ArrayList<Application>();
    for (Application app : applications) {
      if (app.getApplicationType().equals(org.exoplatform.web.application.Application.EXO_GAGGET_TYPE)) {
        listGadgets.add(app);
      }
    }
    gadgets = listGadgets;
  }

  public List<Application> getWidgets() {
    return gadgets;
  }
}
