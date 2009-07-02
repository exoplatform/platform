/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.sample.cometd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import java.util.*;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.frameworks.cometd.ContinuationService;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.application.PortalRequestContext;


public class Cometd extends GenericPortlet {
	private static final Log LOGGER = ExoLogger.getLogger("CometdDemo");

  protected void doView(RenderRequest renderRequest, RenderResponse renderResponse)
      throws PortletException, IOException {

    renderResponse.setContentType("text/html; charset=UTF-8");

    PortletContext context = getPortletContext();
    PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/script.jsp");
    rd.include(renderRequest, renderResponse);

  }

  protected void doEdit(RenderRequest renderRequest, RenderResponse renderResponse)
      throws PortletException, IOException {

  }

  protected void doHelp(RenderRequest renderRequest, RenderResponse renderResponse)
      throws PortletException, IOException {

  }

 protected ContinuationService getContinuationService() {
   ExoContainer container = RootContainer.getInstance();
   container = ((RootContainer)container).getPortalContainer("portal");

   ContinuationService continuation = (ContinuationService) container.getComponentInstanceOfType(ContinuationService.class);
   return continuation;

 }

  public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)
      throws PortletException, IOException {
	
	String message = actionRequest.getParameter("message");

    ContinuationService continuation = getContinuationService();
    if (continuation == null)
      return;

    Map msg = new HashMap();
    msg.put("topic", "/eXo/portal/notification");
    msg.put("sender", "demo cometd");
	msg.put("message", message);
	
	PortalRequestContext pContext = Util.getPortalRequestContext();
    String userName = pContext.getRemoteUser();
    continuation.sendMessage(userName, "/eXo/topics", msg);	
  }

  public void render(RenderRequest renderRequest, RenderResponse renderResponse)
      throws PortletException, IOException {
    super.render(renderRequest, renderResponse);
  }

  public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

  }

}
