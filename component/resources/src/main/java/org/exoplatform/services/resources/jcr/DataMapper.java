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
package org.exoplatform.services.resources.jcr;

import org.exoplatform.services.resources.ResourceBundleData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Dec 1, 2007  
 */
public class DataMapper {

  final static String LOCALE =  "locale";
  final static String TYPE = "exo:type";
  
  final static String ID = "exo:resourceId";
  final static String NAME = "exo:resourceName";
  final static String LANGUAGE = "exo:resourceLanguage";
  final static String COUNTRY = "exo:resourceCountry";
  final static String VARIANT = "exo:resourceVariant";
  final static String RESOUCE_TYPE = "exo:resourceType";
  final static String DATA = "data";

  public void map(Document doc, ResourceBundleData resource) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(TYPE, LOCALE) ;
    
    root.setAttribute(ID, resource.getId()) ;
    root.setAttribute(NAME, resource.getName()) ;
    root.setAttribute(LANGUAGE, resource.getLanguage()) ;
    root.setAttribute(COUNTRY, resource.getCountry()) ;
    root.setAttribute(VARIANT, resource.getVariant()) ;
    root.setAttribute(RESOUCE_TYPE, resource.getResourceType()) ;
    setDataValue(doc, DATA, resource.getData()) ;
  }
  
  public ResourceBundleData toResourceBundleData(Document doc) throws Exception {
    ResourceBundleData resource = new ResourceBundleData() ;
    Element root = doc.getDocumentElement() ;
    resource.setId(root.getAttribute(ID)) ;
    resource.setName(root.getAttribute(NAME)) ;
    resource.setLanguage(root.getAttribute(LANGUAGE)) ;
    resource.setCountry(root.getAttribute(COUNTRY)) ;
    resource.setVariant(root.getAttribute(VARIANT)) ;
    resource.setResourceType(root.getAttribute(RESOUCE_TYPE)) ;
    resource.setData(getDataValue(doc, DATA)) ;
    return resource ;
  }
  
  //---------------------------------------------------------------------------------//
  private void prepareXmlNamespace(Element element) {
    String xmlns = element.getAttribute("xmlns:exo") ; 
    if(xmlns == null || xmlns.trim().length() < 1) {
      element.setAttribute("xmlns:exo", "http://www.exoplatform.com/jcr/exo/1.0") ;
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
  
  String getDataValue(Document doc, String name) {
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
