/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
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

  private List<PortletCategory> portletCategories ;
  private HashMap<String, List<Portlet>> mapPortlets = new HashMap<String, List<Portlet>>();

  @SuppressWarnings("unchecked")
  public UIAddPortlet() throws Exception {
    PortletRegisteryService service = getApplicationComponent(PortletRegisteryService.class) ;
    portletCategories = service.getPortletCategories();   
    if(portletCategories == null) portletCategories = new ArrayList<PortletCategory>(0);
    for(PortletCategory category: portletCategories ) {
      mapPortlets.put(category.getId(), service.getPortlets(category.getId()));
    }
  }  

  public List<PortletCategory> getPortletCategory() { return portletCategories ;  }


  public List<Portlet> getPortlets(PortletCategory category) { return mapPortlets.get(category.getId()) ;  }  
}
