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
package org.exoplatform.services.html.refs;

import java.util.Comparator;

import org.exoplatform.services.common.ThreadSoftRef;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
class EncodeService {

  static ThreadSoftRef<CharRefs> ENCODE_CHARS_REF = new ThreadSoftRef<CharRefs>(CharRefs.class);
  
  static Comparator<CharRef> comparator = new Comparator<CharRef>(){
    public int compare(CharRef o1, CharRef o2){
      if(o1.getValue() == o2.getValue()) return 0;
      if(o1.getValue() > o2.getValue()) return 1;
      return -1;
    }
  };

}
