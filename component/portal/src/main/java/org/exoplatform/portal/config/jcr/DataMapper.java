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
package org.exoplatform.portal.config.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Nov 14, 2007  
 */
public class DataMapper {
  
  final static private String DATA_ELEMENT = "data" ;
  
  final static public String EXO_REGISTRYENTRY_NT = "exo:registryEntry" ;
  final static public String TYPE = "jcr:primaryType" ;
  final static public String EXO_ID = "exo:id" ;
  final static public String EXO_NAME = "exo:name" ;
  final static public String EXO_OWNER_TYPE = "exo:ownerType" ;
  final static public String EXO_OWNER_ID = "exo:ownerId" ;
  final static public String EXO_DATA_TYPE = "exo:dataType" ;
  final static public String EXO_TITLE = "exo:title" ;
  
  public void map(Document doc, PortalConfig portal) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_ID, portal.getName()) ;
    root.setAttribute(EXO_NAME, portal.getName()) ;    
    root.setAttribute(EXO_TITLE, portal.getTitle()) ;
    root.setAttribute(EXO_OWNER_TYPE, PortalConfig.PORTAL_TYPE);
    root.setAttribute(EXO_OWNER_ID, "portalConfig");
    root.setAttribute(EXO_DATA_TYPE, portal.getClass().getSimpleName()) ;    
    setDataValue(doc, DATA_ELEMENT, toXML(portal)) ;
  }
  
  public PortalConfig toPortalConfig(Document doc) throws Exception {
    String data = getDataValue(doc, DATA_ELEMENT) ;
    return fromXML(data, PortalConfig.class) ;
  }
  
  public void map(Document doc, Page page) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_ID, page.getPageId()) ;
    root.setAttribute(EXO_NAME, page.getName()) ;    
    root.setAttribute(EXO_TITLE, page.getTitle()) ;
    root.setAttribute(EXO_OWNER_TYPE, page.getOwnerType());
    root.setAttribute(EXO_OWNER_ID, page.getOwnerId());
    root.setAttribute(EXO_DATA_TYPE, page.getClass().getSimpleName()) ;    
    setDataValue(doc, DATA_ELEMENT, toXML(page)) ;
  }
  
  public Page toPageConfig(Document doc) throws Exception {
    String data = getDataValue(doc, DATA_ELEMENT) ;
    return fromXML(data, Page.class) ;
  }
  
  public void map(Document doc, PageNavigation navigation) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_ID, navigation.getOwner()) ;
    root.setAttribute(EXO_NAME, navigation.getOwner()) ;    
    root.setAttribute(EXO_OWNER_TYPE, navigation.getOwnerType());
    root.setAttribute(EXO_OWNER_ID, navigation.getOwnerId());
    root.setAttribute(EXO_DATA_TYPE, navigation.getClass().getSimpleName()) ;    
    setDataValue(doc, DATA_ELEMENT, toXML(navigation)) ;
  }
  
  public PageNavigation toPageNavigation(Document doc) throws Exception {
    String data = getDataValue(doc, DATA_ELEMENT) ;    
    return fromXML(data, PageNavigation.class) ;
  }

  public void map(Document doc, PortletPreferences portletPreferences) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    String id = portletPreferences.getWindowId().replace('/', '_').replace(':', '_').replace('#', '_') ;
    root.setAttribute(TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_ID, id) ;
    root.setAttribute(EXO_NAME, id) ;    
    root.setAttribute(EXO_OWNER_TYPE, portletPreferences.getOwnerType());
    root.setAttribute(EXO_OWNER_ID, portletPreferences.getOwnerId());
    root.setAttribute(EXO_DATA_TYPE, portletPreferences.getClass().getSimpleName()) ;    
    setDataValue(doc, DATA_ELEMENT, toXML(portletPreferences)) ;
  }
  
  public PortletPreferences toPortletPreferences(Document doc) throws Exception {
    String data = getDataValue(doc, DATA_ELEMENT) ;
    return fromXML(data, PortletPreferences.class) ;
  }
  
  public <T> T fromDocument(Document doc, Class<T> clazz) throws Exception {
    String data = getDataValue(doc, DATA_ELEMENT) ;
    return fromXML(data, clazz) ;
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