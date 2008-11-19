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
 * Created by The eXo Platform SAS
 * Jan 19, 2007  
 */
public interface SkinConfig {

  /**
   * Returns the skin id.
   *
   * @return the skin id
   */
  String getId();

  String getName();

  /**
   * Returns the skin module.
   *
   * @return the module
   */
  String getModule();

  /**
   * Returns the css path.
   *
   * @return the css path
   */
  String getCSSPath();

  /**
   * Creates and return a skin URL.
   * 
   * @return the skin URL
   */
  SkinURL createURL();

}