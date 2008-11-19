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

import org.exoplatform.services.resources.Orientation;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface SkinURL {

  /**
   * Sets the orientation on the skin URL.
   *
   * @param orientation the orientation
   */
  void setOrientation(Orientation orientation);

  /**
  * This method is used to compute the virtual path of a CSS, which is the
  * actual CSS path in the war file, augmented with an orientation suffix.
  * (e.g : "/portal/templates/skin/webui/component/UIHomePagePortlet/DefaultStylesheet-lt.css")
  * This virtual path with be used by the browser to retrieve the CSS
  * corresponding to the appopriate orientation.
  *
  * @return the augmented CSS path, containing the orientation suffix.
  */
  String toString();
}
