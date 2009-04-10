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
package org.exoplatform.portal.skin;

import javax.servlet.ServletContext;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class SimpleResourceContext {

  private final String contextPath;

  private final ServletContext context;

  public SimpleResourceContext(String contextPath, ServletContext context) {
    this.contextPath = contextPath;
    this.context = context;
  }

  public Resource getResource(String path) {
    int i2 = path.lastIndexOf("/") + 1;
    String targetedParentPath = path.substring(0, i2);
    String targetedFileName = path.substring(i2);
    try {
      final URL url = context.getResource(path);
      if (url != null) {
        return new Resource(contextPath, targetedParentPath, targetedFileName) {
          @Override
          public Reader read() throws IOException {
            return new InputStreamReader(url.openStream());
          }
        };
      }
    }
    catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getContextPath() {
    return contextPath;
  }
}
