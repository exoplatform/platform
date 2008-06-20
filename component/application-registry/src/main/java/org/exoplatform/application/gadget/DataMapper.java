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
package org.exoplatform.application.gadget;

import org.exoplatform.web.application.gadget.GadgetApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 18, 2008  
 */
public class DataMapper {
  
  final static String EXO_REGISTRYENTRY_NT = "exo:registryEntry" ;
  final static String PRIMARY_TYPE = "jcr:primaryType" ;
  
  final static String EXO_DATA_TYPE = "exo:dataType" ;
  final static String EXO_GADGET_ID = "exo:gadgetId" ;
  final static String EXO_GADGET_URL = "exo:gadgetUrl" ;  

  public void map(Document doc, GadgetApplication app) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(PRIMARY_TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(EXO_DATA_TYPE, GadgetApplication.class.getSimpleName()) ;
    root.setAttribute(EXO_GADGET_ID, app.getApplicationId()) ;
    root.setAttribute(EXO_GADGET_URL, app.getUrl()) ;
  }
  
  public GadgetApplication toApplciation(Document doc) throws Exception {
    Element root = doc.getDocumentElement() ;
    GadgetApplication app = new GadgetApplication(root.getAttribute(EXO_GADGET_ID), root.getAttribute(EXO_GADGET_URL)) ;
    return app ;
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
  
}
