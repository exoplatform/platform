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
package org.exoplatform.application.gadget;

import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 15, 2008   
 */
/**
 * This class represents an registry for gadget application, it hear from context and then deployed
 * gadget
 */
public class GadgetRegister implements ServletContextListener {
  protected static Log log = ExoLogger.getLogger("gadget:GadgetRegister");
  /**
   * Initializes the listener and each time a new gadget application war is deployed the gadgets
   * are added into the JCR node by GadgetRegistryService
   * @throws Exception when can't parse xml file
   */
  public void contextInitialized(ServletContextEvent event) {
    try {
      ExoContainer pcontainer =  ExoContainerContext.getContainerByName("portal") ;
      SourceStorage sourceStorage = (SourceStorage) pcontainer.getComponentInstanceOfType(SourceStorage.class);
      GadgetRegistryService gadgetService = (GadgetRegistryService) pcontainer.getComponentInstanceOfType(GadgetRegistryService.class);
      String confLocation = "/WEB-INF/gadget.xml" ;
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
      InputStream in = event.getServletContext().getResourceAsStream(confLocation) ;
      Document docXML = db.parse(in) ;
      NodeList nodeList = docXML.getElementsByTagName("gadget") ;
      String name = null, title = null, desc = null, thumb = null, ref= null ;
      for(int i=0; i<nodeList.getLength(); i++) {
        NodeList nodeChild = nodeList.item(i).getChildNodes() ;
        for(int j=0; j<nodeChild.getLength(); j++) {
          Node node = nodeChild.item(j) ;
          if (node.getNodeName().equals("name"))  name = node.getTextContent() ;
          else if (node.getNodeName().equals("title"))  title = node.getTextContent() ;
          else if (node.getNodeName().equals("description"))  desc = node.getTextContent() ;
          else if (node.getNodeName().equals("thumbnail")) thumb = node.getTextContent() ;
          else if (node.getNodeName().equals("reference"))  ref = node.getTextContent() ;
        }
        String filePath = "/gadgets/" + name + ".xml";
        InputStream sourceIn = event.getServletContext().getResourceAsStream(filePath) ;
        String source = IOUtils.toString(sourceIn, "UTF-8");
        sourceStorage.saveSource(name, source);
        Gadget gadget = new Gadget();
        gadget.setName(name);
        gadget.setUrl(sourceStorage.getSourceLink(name));
        gadget.setTitle(title);
        gadget.setDescription(desc);
        gadget.setThumbnail(thumb);
        gadget.setReferenceUrl(ref);
        gadget.setLocal(true);
        
        gadgetService.addGadget(gadget);
      }
    } catch(Exception ex) {
      log.error("Error while deploying a gadget", ex);
    }
  }
  
  /**
   * Destroys the listener context
   */
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  } 
}