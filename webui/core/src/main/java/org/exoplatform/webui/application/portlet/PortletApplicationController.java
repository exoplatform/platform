/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.LogUtil;
import org.exoplatform.web.WebAppController;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class PortletApplicationController extends GenericPortlet {
  
  private String applicationId_ ;
  
  public void init(PortletConfig config) throws PortletException {
    super.init(config) ;
    PortletContext pcontext = config.getPortletContext();
    String contextName = pcontext.getPortletContextName();
    applicationId_  = contextName + "/" + config.getPortletName() ;
  }
  
  public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
    try {
      req.setCharacterEncoding("UTF-8");
      
      getPortletApplication().processAction(req, res) ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
    }
  }
  
  public  void render(RenderRequest req,  RenderResponse res) throws PortletException, IOException {
    try {
      getPortletApplication().render(req, res) ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
    }
  }
  
  private PortletApplication getPortletApplication() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    PortletApplication application = controller.getApplication(applicationId_) ;
    if(application == null) {
      application = new PortletApplication(getPortletConfig()) ;
      application.onInit() ; 
      controller.addApplication(application) ;
    }
    return application ;
  }
  
  public void destroy() {
    RootContainer rootContainer =  RootContainer.getInstance() ;
    List<PortalContainer> containers = 
      rootContainer.getComponentInstancesOfType(PortalContainer.class) ;
    try {
      for(PortalContainer container : containers) {
        PortalContainer.setInstance(container) ;
        WebAppController controller = 
          (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
        PortletApplication application = controller.getApplication(applicationId_) ;
        if(application != null) {
          application.onDestroy() ;
          controller.removeApplication(applicationId_) ;
        }
      }
    } catch(Exception ex) {
      LogUtil.getLog(getClass()).error("Error: ", ex);
    }
  }
}