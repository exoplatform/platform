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
package org.exoplatform.portal.config.serialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 2, 2007  
 */
public class JibxArraySerialize {
  
  public static String serializeStringArray(String[] values) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      if (i > 0) builder.append(';');
      builder.append(values[i]);
    }
    return builder.toString();
  }

  public static String[] deserializeStringArray(String text) {
    if (text == null || text.trim().length() < 1) return new String[0];
    text = text.trim();
    List<String> list = new ArrayList<String>(5);
    String [] components = text.split(";");
    for(String ele : components) {
      ele = ele.trim();
      if(ele.length() < 1) continue;
      list.add(ele);
    }
    String [] values = new String[list.size()];
    list.toArray(values);
    return values;
  }
  
}
