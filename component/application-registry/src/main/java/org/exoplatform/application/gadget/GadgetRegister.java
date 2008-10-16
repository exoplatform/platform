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
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.spec.ModulePrefs;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
      String name = null, address = null ;
      for(int i=0; i<nodeList.getLength(); i++) {
        Element gadgetElement = (Element) nodeList.item(i);
        name = gadgetElement.getAttribute("name");
        if(gadgetService.getGadget(name) != null) continue;
        NodeList nodeChild = gadgetElement.getChildNodes() ;
        for(int j=0; j<nodeChild.getLength(); j++) {
          Node node = nodeChild.item(j) ;
          if (node.getNodeName().equals("path")) {
            address = node.getTextContent() ;
            InputStream sourceIn = event.getServletContext().getResourceAsStream(address) ;
            String source = IOUtils.toString(sourceIn, "UTF-8");
            sourceStorage.saveSource(name, source);
            ModulePrefs prefs = GadgetApplication.getModulePreferences(Uri.parse("http://www.exoplatform.org"), source);
            Gadget gadget = new Gadget();
            gadget.setName(name);
            gadget.setUrl(sourceStorage.getSourcePath(name));
            gadget.setTitle(getGadgetTitle(prefs, gadget.getName()));
            gadget.setDescription(prefs.getDescription());
            gadget.setThumbnail(prefs.getThumbnail().toString());
            gadget.setReferenceUrl(prefs.getTitleUrl().toString());
            gadget.setLocal(true);            
            gadgetService.saveGadget(gadget);            
          }
          else if (node.getNodeName().equals("url")) {
            address = node.getTextContent() ;
            URL urlObj = new URL(address) ;
            URLConnection conn = urlObj.openConnection() ;
            InputStream is = conn.getInputStream() ;
            String source = IOUtils.toString(is, "UTF-8") ;            
            ModulePrefs prefs = GadgetApplication.getModulePreferences(Uri.parse(address), source);
            Gadget gadget = new Gadget();
            gadget.setName(name);
            gadget.setUrl(address);
            gadget.setTitle(getGadgetTitle(prefs, gadget.getName()));
            gadget.setDescription(prefs.getDescription());
            gadget.setThumbnail(prefs.getThumbnail().toString());
            gadget.setReferenceUrl(prefs.getTitleUrl().toString());
            gadget.setLocal(false);            
            gadgetService.saveGadget(gadget);            
          }
        }
      }
    } catch(Exception ex) {
      log.error("Error while deploying a gadget", ex);
    }
  }
  
  private String getGadgetTitle(ModulePrefs prefs, String defaultValue) {
    String title = prefs.getDirectoryTitle() ;
    if(title == null || title.trim().length() < 1) title = prefs.getTitle() ;
    if(title == null || title.trim().length() < 1) return defaultValue ;
    return title;
  }
  
  /**
   * Destroys the listener context
   */
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  } 
}