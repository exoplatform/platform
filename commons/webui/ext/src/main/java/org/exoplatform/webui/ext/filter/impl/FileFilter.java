/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.webui.ext.filter.impl;

import java.util.List;
import java.util.Map;

import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minh.dang@exoplatform.com
 * Aug 18, 2009  
 * 7:08:32 AM
 */
public class FileFilter implements UIExtensionFilter {

  protected List<String> mimeTypes;
  
  public boolean accept(Map<String, Object> context) throws Exception {
    if (mimeTypes == null || mimeTypes.isEmpty()) {
      return true;
    }
    String mimeType = context.get("mimeType").toString();
    if(mimeTypes.contains(mimeType)) return true;
    return false;
  }

  public UIExtensionFilterType getType() {
    return UIExtensionFilterType.MANDATORY;
  }

  public void onDeny(Map<String, Object> context) throws Exception {
    
  }

}
