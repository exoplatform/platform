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
package org.exoplatform.application.gadget.jcr;

import org.exoplatform.application.gadget.Gadget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 18, 2008  
 */
public class DataMapper {
  
  final static String EXO_REGISTRYENTRY_NT = "exo:registryEntry" ;
  final static String PRIMARY_TYPE = "jcr:primaryType" ;
  final static String GADGET_SOURCE = "GadgetSource" ;
  final static private String DATA_ELEMENT = "data" ;
  final static String EXO_DATA_TYPE = "exo:dataType" ;
  final static String EXO_GADGET_NAME = "exo:gadgetName" ;
  final static String EXO_GADGET_URL = "exo:gadgetUrl" ;  
  final static String EXO_GADGET_IS_REMOTE = "exo:gadgetIsRemote" ;

  public void map(Document doc, Gadget app) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(PRIMARY_TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_DATA_TYPE, app.getClass().getSimpleName()) ;
    root.setAttribute(EXO_GADGET_NAME, app.getName()) ;
    root.setAttribute(EXO_GADGET_URL, app.getUrl()) ;
    root.setAttribute(EXO_GADGET_IS_REMOTE, String.valueOf(app.isRemote())) ;
  }
  
  public Gadget toApplciation(Document doc) throws Exception {
    Element root = doc.getDocumentElement() ;
    Gadget app = new Gadget() ;
    app.setName(root.getAttribute(EXO_GADGET_NAME)) ;
    app.setUrl(root.getAttribute(EXO_GADGET_URL)) ;
    app.setRemote(Boolean.valueOf(root.getAttribute(EXO_GADGET_IS_REMOTE))) ;
    return app ;
  }
  
  public void map(Document doc, String source) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(PRIMARY_TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_DATA_TYPE, GADGET_SOURCE) ;
    setDataValue(doc, DATA_ELEMENT, source) ;
  }
  
  public String toSource(Document doc) throws Exception {
    return getDataValue(doc, DATA_ELEMENT) ;
  }
  
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

  
}
