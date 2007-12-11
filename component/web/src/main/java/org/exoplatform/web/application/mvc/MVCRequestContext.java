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
package org.exoplatform.web.application.mvc;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.application.URLBuilder;
/**
 * Created by The eXo Platform SAS
 * Apr 23, 2007
 */
public class MVCRequestContext extends RequestContext {
  public MVCRequestContext(Application app, RequestContext parent) {
    super(app) ;
    setParentAppRequestContext(parent) ;
  }
  
  public String getRequestParameter(String arg0) {
    return null ;
  }

  public String[] getRequestParameterValues(String arg0) {
    return null ;
  }

  public URLBuilder getURLBuilder() {
    return null ;
  }

  public boolean useAjax() {
    return false;
  }
}