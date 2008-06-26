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
package org.exoplatform.portal.layout.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.exoplatform.portal.config.model.Container;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 25, 2008  
 */
public class DataMapper {
  
  final static String EXO_REGISTRYENTRY_NT = "exo:registryEntry" ;
  final static String PRIMARY_TYPE = "jcr:primaryType" ;
  
  final static String EXO_DATA_TYPE = "exo:dataType" ;
  final static String EXO_CONTAINER_ID = "exo:containerId" ;
  final static String EXO_CONTAINER_NAME = "exo:containerName" ;
  final static private String DATA_ELEMENT = "data" ;
  
  public void map(Document doc, Container container) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(PRIMARY_TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_DATA_TYPE, container.getClass().getSimpleName()) ;
    root.setAttribute(EXO_CONTAINER_ID, container.getId()) ;
    root.setAttribute(EXO_CONTAINER_NAME, container.getName()) ;
    setDataValue(doc, DATA_ELEMENT, toXML(container)) ; 
  }
  
  public Container toContainer(Document doc) throws Exception {
    String data = getDataValue(doc, DATA_ELEMENT) ;
    return fromXML(data, Container.class) ;
  }

  //------------------------------Util function-----------------------------------//
  
  private void prepareXmlNamespace(Element element) {
    setXmlNameSpace(element, "xmlns:exo", "http://www.exoplatform.com/jcr/exo/1.0") ;
    setXmlNameSpace(element, "xmlns:jcr", "http://www.jcp.org/jcr/1.0") ;
  }
  
  private void setXmlNameSpace(Element element, String key, String value) {
    String xmlns = element.getAttribute(key) ; 
    if(xmlns == null || xmlns.trim().length() < 1) {
      element.setAttribute(key, value) ;
    }    
  }

  private void setDataValue(Document doc, String name, String value) {
    Node dataElement = createDataElement(doc, name) ;
    Node child ;
    while((child = dataElement.getFirstChild()) != null) {
      dataElement.removeChild(child) ;
    }
    Node data = doc.createCDATASection(value);
    dataElement.appendChild(data) ;    
  }
  
  private String getDataValue(Document doc, String name) {
    Node dataElement = createDataElement(doc, name) ;
    return dataElement.getFirstChild().getNodeValue() ;
  }
  
  private Element createDataElement(Document doc, String name) {
    Element ele = (Element) doc.getElementsByTagName(name).item(0) ;
    if(ele == null) {
      ele = doc.createElement(name) ;
      doc.getDocumentElement().appendChild(ele) ;
    }
    return ele ;
  }

  private String toXML(Object object) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream() ;
    IBindingFactory bfact = BindingDirectory.getFactory(object.getClass()) ;
    IMarshallingContext mctx = bfact.createMarshallingContext() ;
    mctx.setIndent(2);
    mctx.marshalDocument(object, "UTF-8", null, os) ;
    return new String(os.toByteArray(), "UTF-8");
  }
  
  private <T> T fromXML(String xml, Class<T> clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8")) ;
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    return clazz.cast(uctx.unmarshalDocument(is, null));
  }

}
