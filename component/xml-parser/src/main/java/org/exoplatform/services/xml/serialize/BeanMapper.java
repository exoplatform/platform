/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.xml.serialize;

import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 21, 2007  
 */
public interface BeanMapper {

  public <T> T toBean(Class<T> clazz, XMLDocument document) throws Exception;
  
  public <T> T toBean(Class<T> clazz, XMLNode node) throws Exception ;
  
  public <T> void toBean(Class<T> clazz, T object, XMLNode node) throws Exception ;
}
