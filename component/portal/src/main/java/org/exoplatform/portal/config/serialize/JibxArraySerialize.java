/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
