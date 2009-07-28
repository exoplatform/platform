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
package org.exoplatform.portal.webui.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.WindowState;

import org.exoplatform.services.log.Log;
import org.exoplatform.Constants;
import org.exoplatform.commons.utils.Text;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.portlet.PortletExceptionHandleService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.portletcontainer.PortletContainerException;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.RenderInput;
import org.exoplatform.services.portletcontainer.pci.RenderOutput;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
import org.exoplatform.webui.core.lifecycle.WebuiBindingContext;
import org.exoplatform.webui.event.Event;

/**
 * Created by The eXo Platform SAS May 8, 2006
 */
public class UIPortletLifecycle extends Lifecycle {

  protected static Log log = ExoLogger.getLogger("portal:UIPortletLifecycle");

  /**
   * This processAction method associated with the portlet UI component does the
   * following work:
   * 
   * 1) If the current request is one that target the portal than an event
   * targeting a Portal level ActionListener is sent. This case happen when the
   * incoming request contains the parameter
   * PortalRequestContext.UI_COMPONENT_ACTION (portal:action). When the event is
   * broadcasted the methods is over 2) In other cases, the request targets the
   * portlet either to a) change the portlet mode b) change the window state c)
   * make a processAction() or render() call to the portlet container (Portlet
   * API methods here) In those 3 cases, dedicated events are created and
   * broadcasted and the portlet is added in the list of components to update
   * within the AJAX call
   */
  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    String action = context.getRequestParameter(PortalRequestContext.UI_COMPONENT_ACTION);
    if (action != null) {
      Event<UIComponent> event = uicomponent.createEvent(action, Event.Phase.PROCESS, context);
      if (event != null)
        event.broadcast();
      return;
    }

    boolean addUpdateComponent = false;
    String portletMode = context.getRequestParameter("portal:portletMode");
    if (portletMode != null) {
      Event<UIComponent> event = uicomponent.createEvent("ChangePortletMode",
                                                         Event.Phase.PROCESS,
                                                         context);
      if (event != null)
        event.broadcast();
      addUpdateComponent = true;
    }

    String windowState = context.getRequestParameter("portal:windowState");
    if (windowState != null) {
      Event<UIComponent> event = uicomponent.createEvent("ChangeWindowState",
                                                         Event.Phase.PROCESS,
                                                         context);
      if (event != null)
        event.broadcast();
      addUpdateComponent = true;
    }

    /*
     * Check the type of the incoming request, can be either an ActionURL or a
     * RenderURL one
     * 
     * In case of a RenderURL, the parameter state map must be invalidated and
     * ths is done in the associated ActionListener
     */
    String portletActionType = context.getRequestParameter(Constants.TYPE_PARAMETER);
    if (portletActionType != null) {
      if (portletActionType.equals(Constants.PORTAL_PROCESS_ACTION)) {
        Event<UIComponent> event = uicomponent.createEvent("ProcessAction",
                                                           Event.Phase.PROCESS,
                                                           context);
        if (event != null)
          event.broadcast();
        addUpdateComponent = true;
      } else if (portletActionType.equals(Constants.PORTAL_SERVE_RESOURCE)) {
        Event<UIComponent> event = uicomponent.createEvent("ServeResource",
                                                           Event.Phase.PROCESS,
                                                           context);
        if (event != null)
          event.broadcast();
      } else {
        Event<UIComponent> event = uicomponent.createEvent("Render", Event.Phase.PROCESS, context);
        if (event != null)
          event.broadcast();
        addUpdateComponent = true;
      }
    }
    if (addUpdateComponent)
      context.addUIComponentToUpdateByAjax(uicomponent);
  }

  /**
   * This methods of the Lifecycle writes into the output writer the content of
   * the portlet
   * 
   * 1) Create a RenderInput object and fill it with all the Request information
   * 2) Call the portletContainer.render() method of the Portlet Container to
   * get the HTML generated fragment 3) Then if the current request is an AJAX
   * one, just write in the buffer the content returned by the portlet container
   * 4) If not AJAX, then merge the content with the UIPortlet.gtmpl
   */
  public void processRender(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    UIPortlet uiPortlet = (UIPortlet) uicomponent;
    PortalRequestContext prcontext = (PortalRequestContext) context;
    ExoContainer container = prcontext.getApplication().getApplicationServiceContainer();
    // UIPortal uiPortal = Util.getUIPortal();
    PortletContainerService portletContainer = (PortletContainerService) container.getComponentInstanceOfType(PortletContainerService.class);
    // OrganizationService service =
    // uiPortlet.getApplicationComponent(OrganizationService.class);
    // UserProfile userProfile =
    // service.getUserProfileHandler().findUserProfileByName(uiPortal.getOwner());
    RenderInput input = new RenderInput();
    String baseUrl = new StringBuilder(prcontext.getRequestURI()).append("?"
        + PortalRequestContext.UI_COMPONENT_ID).append("=").append(uiPortlet.getId()).toString();
    input.setBaseURL(baseUrl);
    input.setEscapeXml(true);
    // if (userProfile != null)
    // input.setUserAttributes(userProfile.getUserInfoMap());
    // else input.setUserAttributes(new HashMap<String, String>());
    Map<String, String> emptyMap = Collections.emptyMap();
    input.setUserAttributes(emptyMap);

    input.setPortletMode(uiPortlet.getCurrentPortletMode());
    input.setWindowState(uiPortlet.getCurrentWindowState());
    input.setMarkup("text/html");
    input.setTitle(uiPortlet.getTitle());
    input.setInternalWindowID(uiPortlet.getExoWindowID());
    input.setRenderParameters(getRenderParameterMap(uiPortlet));
    input.setPublicParamNames(uiPortlet.getPublicRenderParamNames());
    RenderOutput output = null;
    Text markup = null;
    String portletTitle = null;
    try {
      if (uiPortlet.getCurrentWindowState() != WindowState.MINIMIZED) {
        String appStatus = uiPortlet.getProperties().get("appStatus");
        if ("Window".equals(uiPortlet.getPortletStyle())
            && !("SHOW".equals(appStatus) || "HIDE".equals(appStatus))) {
          markup = Text.create("<span></span>");
        } else {
          int portalMode = Util.getUIPortalApplication().getModeState();
          if(portalMode % 2 == 0) {
            output = portletContainer.render(prcontext.getRequest(), prcontext.getResponse(), input);
            markup = output.getMarkup();
          }
        }
      }
    } catch (PortletContainerException ex) {
      // TODO tam.nguyen fix bug PORTAL-2660
      PortletExceptionHandleService portletExceptionService = (PortletExceptionHandleService) container.getComponentInstanceOfType(PortletExceptionHandleService.class);
      portletExceptionService.handle(ex);
      markup = Text.create("This portlet encountered an error and could not be displayed.");
//      log.error("The portlet " + uiPortlet.getName()
//          + " could not be loaded. Check if properly deployed.", ExceptionUtil.getRootCause(ex));
    }
    if (output != null) {
      portletTitle = output.getTitle();
      prcontext.setHeaders(output.getHeaderProperties());
    }
    if (portletTitle == null)
      portletTitle = "Portlet";

    if (context.useAjax() && !prcontext.getFullRender()) {
      if (markup != null) {
        markup.writeTo(prcontext.getWriter());
      }
    } else {
      WebuiApplication app = (WebuiApplication) prcontext.getApplication();
      ApplicationResourceResolver resolver = app.getResourceResolver();
      WebuiBindingContext bcontext = new WebuiBindingContext(resolver,
                                                             context.getWriter(),
                                                             uiPortlet,
                                                             prcontext);
      bcontext.put("uicomponent", uiPortlet);
      bcontext.put("portletContent", markup);
      bcontext.put("portletTitle", portletTitle);
      try {
        renderTemplate(uiPortlet.getTemplate(), bcontext);
      } catch (Throwable ex) {
      }
    }
    try {
      prcontext.getResponse().flushBuffer();
    } catch (Throwable ex) {
    }
  }

  /**
   * This method returns all the parameters supported by the targeted portlets,
   * both the private and public ones
   */
  private Map<String, String[]> getRenderParameterMap(UIPortlet uiPortlet) {
    Map<String, String[]> renderParams = uiPortlet.getRenderParametersMap();

    if (renderParams == null) {
      renderParams = new HashMap<String, String[]>();
      uiPortlet.setRenderParametersMap(renderParams);
    }

    /*
     * handle public params to only get the one supported by the targeted
     * portlet
     */
    Map<String, String[]> allParams = new HashMap<String, String[]>(renderParams);
    allParams.putAll(uiPortlet.getPublicParameters());

    return allParams;
  }

}
