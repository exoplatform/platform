/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.html.refs;

import java.util.Comparator;

import org.exoplatform.services.parser.container.ThreadSoftRef;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
class DecodeService {

  static ThreadSoftRef<CharRefs> DECODE_CHARS_REF = new ThreadSoftRef<CharRefs>(CharRefs.class);
  
  static Comparator<CharRef> comparator = new Comparator<CharRef>(){
    public int compare(CharRef o1, CharRef o2){
      return o1.getName().compareTo(o2.getName());
    }
  };
  
}
