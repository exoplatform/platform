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
package org.exoplatform.web.application;

import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.gadget.GadgetRegistryService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 15, 2008   
 */
public class GadgetRegister implements ServletContextListener {
  
  protected static Log log = ExoLogger.getLogger("gadget:GadgetRegister");
  
  /**
   * Each time a new gadget application war is deployed then gadgets are registered into the
   * WebAppController
   */
  public void contextInitialized(ServletContextEvent event) {
    try {
      RootContainer root = RootContainer.getInstance() ;
      PortalContainer pcontainer =  root.getPortalContainer("portal") ;
      GadgetRegistryService gadgetService = (GadgetRegistryService) pcontainer.getComponentInstanceOfType(GadgetRegistryService.class) ;
      String strLocation = "/WEB-INF/gadget.xml" ;
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
      InputStream in = event.getServletContext().getResourceAsStream(strLocation) ;
      Document docXML = db.parse(in) ;
      NodeList nodeList = docXML.getElementsByTagName("gadget") ;
      String name=null, url=null ;
      for(int i=0; i<nodeList.getLength(); i++) {
        NodeList nodeChild = nodeList.item(i).getChildNodes() ;
        for(int j=0; j<nodeChild.getLength(); j++) {
          Node node = nodeChild.item(j) ;
          if(node.getNodeName().equals("name"))  name = node.getTextContent() ;
          if(node.getNodeName().equals("url"))  url = node.getTextContent() ;
        }
        gadgetService.addGadget(new GadgetApplication(name, url)) ;
      }
    } catch(Exception ex) {
      log.error("Error while deploying a gadget", ex);
    }
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  } 
}