/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.session;

import javax.jcr.PathNotFoundException;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by The eXo Platform SAS
 * Author : Tan Pham Dinh
 *          tan.pham@exoplatform.com
 * Apr 9, 2009  
 */
public class SessionManagementService {
  private RegistryService regService_ ;
  private final String ATTRIBUTE_NAME = "userName" ;
  
  public static final String SERVICE_NAME = "SessionManagement" ;
  
  public SessionManagementService(InitParams initParams, RegistryService rService) {
    regService_ = rService ;
  }
  
  public void saveSessionUsername(String sessionId, String userName) throws Exception {
    if(sessionId == null || sessionId.length() == 0 || userName == null || userName.length() == 0) {
      return ;
    }
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String servicePath = getServiceRegistryPath() ;
    String entryPath = servicePath + "/" + sessionId ;
    RegistryEntry entry ;
    try {
      try {
        entry = regService_.getEntry(sessionProvider, entryPath) ;
      } catch (PathNotFoundException e) {
        entry = new RegistryEntry(sessionId) ;
        regService_.createEntry(sessionProvider, servicePath, entry) ;
      }
      Document doc = entry.getDocument() ;
      doc.getDocumentElement().setAttribute(ATTRIBUTE_NAME, userName);
      regService_.recreateEntry(sessionProvider, servicePath, entry);
    } finally {
      sessionProvider.close() ;
    }
  }
  
  public String getSessionUsername(String id) throws Exception {
    String entryPath = getServiceRegistryPath() + "/" + id ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry entry ;
      try {
        entry = regService_.getEntry(sessionProvider, entryPath) ;
      } catch (Exception e) {
        return null ;
      }
      return entry.getDocument().getDocumentElement().getAttribute(ATTRIBUTE_NAME) ;
    } finally {
      sessionProvider.close() ;
    }
  }
  
  public String deleteSessionUsername(String id) throws Exception {
    String userName = getSessionUsername(id) ;
    if(userName == null) return null ; 
    String entryPath = getServiceRegistryPath() + "/" + id ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, entryPath) ;
      return userName ;
    } finally {
      sessionProvider.close() ;
    }
  }
  
  public void clearAll() throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String entryPath = getServiceRegistryPath() ;
    RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
    Element docEle = entry.getDocument().getDocumentElement() ;
    NodeList childNodes = docEle.getChildNodes() ;
    while(childNodes.getLength() > 0) {
      Node node = childNodes.item(0) ;
      docEle.removeChild(node) ;
    }
    regService_.recreateEntry(sessionProvider, RegistryService.EXO_SERVICES, entry) ;
  }
  
  private String getServiceRegistryPath() {
    return RegistryService.EXO_SERVICES + "/" + SERVICE_NAME ;
  }

}
