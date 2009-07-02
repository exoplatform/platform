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
package org.exoplatform.webui.application.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.WebAppController;
/**
 * Created by The eXo Platform SAS
 * May 8, 2006
 * 
 * This class is just the main entry point and act as an adapter on eXo web framework.
 * 
 * Hence every call is delegated to the PortletApplication which extends the WebuiApplication
 * 
 */
public class PortletApplicationController extends GenericPortlet {
  
  protected static Log log = ExoLogger.getLogger("portlet:PortletApplicationController"); 
  
  private String applicationId_;
  
  /**
   * This method is called when the portlet is initialised, in eXo this is a lazy loading
   * mechanism
   * 
   * the main goal of this method is to generate an application ID
   *         applicationId_  = portlet-application-name + "/" + portlet-name 
   */
  public void init(PortletConfig config) throws PortletException {
    super.init(config);
    PortletContext pcontext = config.getPortletContext();
    String contextName = pcontext.getPortletContextName();
    applicationId_  = contextName + "/" + config.getPortletName();
  }
  
  /**
   * Delegate the action to the PortletApplication object
   */
  public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
    try {
      getPortletApplication().processAction(req, res);
    } catch(Exception ex) {
      log.error("Error while processing action in the porlet", ex);
    }
  }
  
  /**
   * Delegate the action to the PortletApplication object
   */
  public void processEvent(EventRequest req, EventResponse res) {
    try {
      getPortletApplication().processEvent(req, res);
    } catch(Exception ex) {
      log.error("Error while processing event in the porlet", ex);
    }    
  }
  
  /**
   * Delegate the render to the PortletApplication object
   */  
  public  void render(RenderRequest req,  RenderResponse res) throws PortletException, IOException {
    try {
      getPortletApplication().render(req, res);
    } catch(Exception ex) {
      log.error("Error while rendering the porlet", ex);
    }
  }
  
  /**
   * try to obtain the PortletApplication from the WebAppController.
   * 
   * If it does not exist a new PortletApplication object is created, init and cached in the
   * controller
   */
  private PortletApplication getPortletApplication() throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class);
    PortletApplication application = controller.getApplication(applicationId_);
    if(application == null) {
      application = new PortletApplication(getPortletConfig());
      application.onInit(); 
      controller.addApplication(application);
    }
    return application;
  }
  
  /**
   * When the portlet is destroyed by the portlet container, the onDestroy() method of the
   * PortletApplication is called and then the PortletApplication is removed from the cache
   * inside th WebController
   */
  @SuppressWarnings("unchecked")
  public void destroy() {
    ExoContainer rootContainer = ExoContainerContext.getTopContainer();
    List<ExoContainer> containers = 
      rootContainer.getComponentInstancesOfType(ExoContainer.class);
    containers.add(rootContainer);
    try {
      for(ExoContainer container : containers) {
        ExoContainerContext.setCurrentContainer(container);
        WebAppController controller = 
          (WebAppController)container.getComponentInstanceOfType(WebAppController.class);
        PortletApplication application = controller.getApplication(applicationId_);
        if(application != null) {
          application.onDestroy();
          controller.removeApplication(applicationId_);
        }
      }
    } catch(Exception ex) {
      log.error("Error while destroying the porlet", ex);
    }
  }
}
