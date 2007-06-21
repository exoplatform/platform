/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.xml.serialize;

import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 9, 2007
 */
public interface XMLMapper {

  public void toXML(Object bean, XMLNode node) throws Exception ;
  
  public XMLDocument toXMLDocument(Object bean) throws Exception ;
  
}
