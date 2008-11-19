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
package org.exoplatform.portal.webui.skin;

import java.io.Reader;

/**
 * Represents a resource.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Resource {

  private final ResourceResolver resolver;
  private final String contextPath;
  private final String parentPath;
  private final String fileName;

  public Resource(ResourceResolver resolver, String path) {
    int index = path.indexOf("/", 2);
    String relativeCSSPath = path.substring(index);
    int index2 = relativeCSSPath.lastIndexOf("/") + 1;

    //
    this.resolver = resolver;
    this.contextPath = path.substring(0, index);
    this.parentPath = relativeCSSPath.substring(0, index2);
    this.fileName = relativeCSSPath.substring(index2);
  }

  public Resource(ResourceResolver resolver, String contextPath, String parentPath, String fileName) {
    this.resolver = resolver;
    this.contextPath = contextPath;
    this.parentPath = parentPath;
    this.fileName = fileName;
  }

  public String getContextPath() {
    return contextPath;
  }

  public String getParentPath() {
    return parentPath;
  }

  public String getFileName() {
    return fileName;
  }

  public String getResourcePath() {
    return getParentPath() + getFileName();
  }

  public Reader read() {
    String resourcePath = getResourcePath();
    return resolver.resolve(resourcePath);
  }
}
