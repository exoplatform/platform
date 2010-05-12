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
package org.exoplatform.webui.ext.filter;

import java.util.Map;

/**
 * This class is used to add custom filters on an UI Extension in order to force the
 * UIExtensionManager to hide the extension if the filter 
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 04, 2009  
 */
public interface UIExtensionFilter {

  /**
   * Indicates whether the given context is accepted by this filter
   * @param context the context to check
   * @return <code>true</code> if the context is accepted <code>false</code> otherwise
   * @throws Exception if an error occurs
   */
  public boolean accept(Map<String, Object> context) throws Exception;
  
  /**
   * Allows to execute some code when the filter rejects the given context
   * @param context the context
   * @throws Exception if an error occurs
   */
  public void onDeny(Map<String, Object> context) throws Exception;
  
  /**
   * Indicates the type of the current filter
   * @return the type of the filter
   */
  public UIExtensionFilterType getType();
}
