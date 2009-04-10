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

import java.io.Reader;
import java.io.IOException;

/**
 * Represents a resource.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class Resource {

  private final String contextPath;
  private final String parentPath;
  private final String fileName;

  public Resource(String path) {
    int index = path.indexOf("/", 2);
    String relativeCSSPath = path.substring(index);
    int index2 = relativeCSSPath.lastIndexOf("/") + 1;

    //
    this.contextPath = path.substring(0, index);
    this.parentPath = relativeCSSPath.substring(0, index2);
    this.fileName = relativeCSSPath.substring(index2);
  }

  public Resource(String contextPath, String parentPath, String fileName) {
    this.contextPath = contextPath;
    this.parentPath = parentPath;
    this.fileName = fileName;
  }

  public final String getPath() {
    return getContextPath() + getParentPath() + getFileName();
  }

  public final String getContextPath() {
    return contextPath;
  }

  public final String getParentPath() {
    return parentPath;
  }

  public final String getFileName() {
    return fileName;
  }

  public final String getResourcePath() {
    return getParentPath() + getFileName();
  }

  public abstract Reader read() throws IOException;
}
