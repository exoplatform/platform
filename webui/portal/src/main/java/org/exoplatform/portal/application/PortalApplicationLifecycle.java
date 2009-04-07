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
package org.exoplatform.portal.application;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.SessionManagerContainer;
import org.exoplatform.container.SessionContainer;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;

public class PortalApplicationLifecycle  implements  ApplicationLifecycle<WebuiRequestContext> {
  
  @SuppressWarnings("unused")
  public void onInit(Application app) {
  }
 
  @SuppressWarnings("unused")
  public void onStartRequest(Application app, WebuiRequestContext rcontext) throws Exception {
    ExoContainer pcontainer = ExoContainerContext.getCurrentContainer() ;
    SessionContainer.setInstance(((SessionManagerContainer) pcontainer).getSessionManager().getSessionContainer(rcontext.getSessionId()));
  }

  @SuppressWarnings("unused")
  public void onEndRequest(Application app, WebuiRequestContext rcontext) throws Exception {
  	SessionContainer.setInstance(null) ;
    ExoContainerContext.setCurrentContainer(null);
  }
  
  @SuppressWarnings("unused")
  public void onDestroy(Application app) {
  }

}
