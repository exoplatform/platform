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

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;

public class PortletStatisticLifecycle  implements  ApplicationLifecycle<WebuiRequestContext> {
  
  private static final String ATTRIBUTE_NAME = "AppStatistic";
  @SuppressWarnings("unused")
  public void onInit(Application app) {
    
  }
  
  @SuppressWarnings("unused")
  public void onStartRequest(Application app, WebuiRequestContext context) throws Exception {
  	app.setAttribute(ATTRIBUTE_NAME, System.currentTimeMillis());
  }
  
  @SuppressWarnings("unused")
  public void onEndRequest(Application app, WebuiRequestContext context) throws Exception {
    ApplicationStatisticService service = (ApplicationStatisticService) app.getApplicationServiceContainer().getComponentInstanceOfType(ApplicationStatisticService.class);
    ApplicationStatistic appStatistic = service.getApplicationStatistic(app.getApplicationId());
    long startTime = Long.valueOf(app.getAttribute(ATTRIBUTE_NAME).toString());
    appStatistic.logTime(System.currentTimeMillis() - startTime);
  }
  
  @SuppressWarnings("unused")
  public void onDestroy(Application app) {
    
  }

}