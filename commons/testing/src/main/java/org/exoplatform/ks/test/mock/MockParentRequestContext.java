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
package org.exoplatform.ks.test.mock;

import java.util.Locale;

import org.exoplatform.services.resources.Orientation;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.application.URLBuilder;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public class MockParentRequestContext extends RequestContext {

  public MockParentRequestContext(Application app) {
    super(app);
    
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
  
  public Locale getLocale()
  {
     return null;
  }

  @Override
  public boolean useAjax() {

    return false;
  }

}
