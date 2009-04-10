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

/**
 * An interface defining the renderer contract for a resource.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface ResourceRenderer {

  /**
   * Returns an appendable for the performing the rendering of the resource.
   *
   * @return the appendable
   */
  Appendable getAppendable();

  /**
   * Instruct the renderer about the expiration time in seconds. A non positive value
   * means that no caching should be performed. The expiration value is relative to the
   * date of the request.
   *
   * @param seconds the value in seconds
   */
  void setExpiration(long seconds);

}
