/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.component;

import javax.portlet.MimeResponse;
import javax.portlet.RenderResponse;

import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.w3c.dom.Element;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class)
public class UIAdminToolbarPortlet extends UIPortletApplication {
  public UIAdminToolbarPortlet() throws Exception {
    addChild(UIAdminToolbarContainer.class, null, null);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.exoplatform.webui.core.UIPortletApplication#processRender(org.
   * exoplatform .webui.application.WebuiApplication,
   * org.exoplatform.webui.application.WebuiRequestContext)
   */
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    RenderResponse response = context.getResponse();
    Element elementS = response.createElement("script");
    elementS.setAttribute("type", "text/javascript");
    elementS.setAttribute("src", "/eXoWCMResources/javascript/eXo/wcm/frontoffice/private/QuickEdit.js");
    response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elementS);

    elementS = response.createElement("script");
    elementS.setAttribute("type", "text/javascript");
    elementS.setAttribute("src", "/eXoWCMResources/javascript/eXo/wcm/frontoffice/private/InlineEditing.js");
    response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elementS);

    super.processRender(app, context);
  }
}