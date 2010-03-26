/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.commons.testing.mock;

import org.exoplatform.services.resources.Orientation;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public class MockWebUIRequestContext extends WebuiRequestContext {

  public MockWebUIRequestContext(Application app) {
    super(app);
  }

  @Override
  public <T> T getRequest() throws Exception {
    
    return null;
  }

  public String getPortalContextPath(){
    return null ;
  }
  
  @Override
  public String getRequestContextPath() {
    
    return null;
  }

  @Override
  public <T> T getResponse() throws Exception {
    
    return null;
  }

  @Override
  public void sendRedirect(String url) throws Exception {
  }

  @Override
  public Orientation getOrientation() {
    
    return null;
  }

  @Override
  public String getRequestParameter(String name) {
    
    return null;
  }

  @Override
  public String[] getRequestParameterValues(String name) {
    return new String[0];
  }

  @Override
  public URLBuilder getURLBuilder() {
    
    return null;
  }

  @Override
  public boolean useAjax() {
    
    return false;
  }

}
