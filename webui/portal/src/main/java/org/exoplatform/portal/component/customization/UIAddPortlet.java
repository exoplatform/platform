/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Jun 23, 2006
 * 10:07:15 AM
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/portal/webui/component/customization/UIAddPortlet.gtmpl",

    events = {
//      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIAddPortlet extends UIContainer {

  private List<ApplicationCategory> portletCategories ;
  private HashMap<String, List<Application>> mapApplications = new HashMap<String, List<Application>>();

  @SuppressWarnings("unchecked")
  public UIAddPortlet() throws Exception {
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
    portletCategories = service.getApplicationCategories();  
    if(portletCategories == null) portletCategories = new ArrayList<ApplicationCategory>(0);
    for(ApplicationCategory category: portletCategories ) {
      mapApplications.put(category.getName(), service.getApplications(category));
    }
  }  

  public List<ApplicationCategory> getPortletCategory() { return portletCategories ;  }


  public List<Application> getPortlets(ApplicationCategory category) { return mapApplications.get(category.getName()) ;  }  
}
