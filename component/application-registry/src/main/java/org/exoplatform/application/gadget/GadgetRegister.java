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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.exoplatform.services.log.Log;
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
      ServletContext servletContext = event.getServletContext() ;
      String containerName = servletContext.getInitParameter("portalContainerName") ;
      ExoContainer pcontainer ;
      if(containerName != null) pcontainer = ExoContainerContext.getContainerByName(containerName) ;
      else pcontainer = ExoContainerContext.getCurrentContainer() ;
      if(pcontainer == null) pcontainer = ExoContainerContext.getTopContainer() ;
      SourceStorage sourceStorage = (SourceStorage) pcontainer.getComponentInstanceOfType(SourceStorage.class);
      GadgetRegistryService gadgetService = (GadgetRegistryService) pcontainer.getComponentInstanceOfType(GadgetRegistryService.class);
      String confLocation = "/WEB-INF/gadget.xml" ;
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
      InputStream in = event.getServletContext().getResourceAsStream(confLocation) ;
      Document docXML = db.parse(in) ;
      NodeList nodeList = docXML.getElementsByTagName("gadget") ;
      String gadgetName = null, address = null ;
      for(int i=0; i<nodeList.getLength(); i++) {
        Element gadgetElement = (Element) nodeList.item(i);
        gadgetName = gadgetElement.getAttribute("name");
        if(gadgetService.getGadget(gadgetName) != null) continue;
        try {
          NodeList nodeChild = gadgetElement.getChildNodes() ;
          for(int j=0; j<nodeChild.getLength(); j++) {
            Node node = nodeChild.item(j) ;
            address = node.getTextContent() ;
            if (node.getNodeName().equals("path")) {
              InputStream sourceIs = event.getServletContext().getResourceAsStream(address) ;
              String realPath =  event.getServletContext().getRealPath(address);
              File sourceFile = new File(realPath);
              File homeDir = sourceFile.getParentFile();              
              String fileName = sourceFile.getName();
              //Saves source of gadget
              Source source = new Source(fileName, getMimeType(event.getServletContext(), fileName), "UTF-8");
              source.setStreamContent(sourceIs);
              source.setLastModified(Calendar.getInstance());
              String homeName = homeDir.getName();
              sourceStorage.saveSource(homeName, source);
              //Saves gadget
              ModulePrefs prefs = GadgetApplication.getModulePreferences(Uri.parse("http://www.exoplatform.org"), source.getTextContent());
              Gadget gadget = new Gadget();
              gadget.setName(gadgetName);
              gadget.setUrl(sourceStorage.getSourceURI(homeName + "/" + fileName));
              gadget.setTitle(getGadgetTitle(prefs, gadget.getName()));
              gadget.setDescription(prefs.getDescription());
              gadget.setThumbnail(prefs.getThumbnail().toString());
              gadget.setReferenceUrl(prefs.getTitleUrl().toString());
              gadget.setLocal(true);            
              gadgetService.saveGadget(gadget);
              //Saves source's included
//              int dotIdx = address.lastIndexOf('.'); 
//              if(dotIdx < 0) continue;
//              String dirPath = address.substring(0, dotIdx);
              if(homeDir.exists() && homeDir.isDirectory()) {
                File [] files = homeDir.listFiles();
                for(int k = 0; k < files.length; k++) {
                  saveTree(files[k], homeName, event.getServletContext(), sourceStorage);
                }
              }
            }
            else if (node.getNodeName().equals("url")) {
              URL urlObj = new URL(address) ;
              URLConnection conn = urlObj.openConnection() ;
              InputStream is = conn.getInputStream() ;
              String source = IOUtils.toString(is, "UTF-8") ;            
              ModulePrefs prefs = GadgetApplication.getModulePreferences(Uri.parse(address), source);
              Gadget gadget = new Gadget();
              gadget.setName(gadgetName);
              gadget.setUrl(address);
              gadget.setTitle(getGadgetTitle(prefs, gadget.getName()));
              gadget.setDescription(prefs.getDescription());
              gadget.setThumbnail(prefs.getThumbnail().toString());
              gadget.setReferenceUrl(prefs.getTitleUrl().toString());
              gadget.setLocal(false);            
              gadgetService.saveGadget(gadget);            
            }
          }
        } catch (Exception ex) {
          log.warn("Can not register the gadget: '" + gadgetName + "' ");
        }
      }
    } catch(Exception ex) {
      log.error("Error while deploying a gadget", ex);
    }
  }
  
  private void saveTree(File file, String savePath,
                        ServletContext context, SourceStorage storage) throws Exception {
    if(file.isFile()) {
      Source includedSource = new Source(file.getName(), getMimeType(context, file.getName()), "UTF-8");
      includedSource.setStreamContent(new FileInputStream(file));
      includedSource.setLastModified(Calendar.getInstance());
      storage.saveSource(savePath, includedSource);
    } else if (file.isDirectory()) {
      File [] files = file.listFiles();
      String childPath = savePath + "/" + file.getName();
      for(int i = 0; i < files.length; i++) saveTree(files[i], childPath, context, storage);
    }
  }
  
  private String getGadgetTitle(ModulePrefs prefs, String defaultValue) {
    String title = prefs.getDirectoryTitle() ;
    if(title == null || title.trim().length() < 1) title = prefs.getTitle() ;
    if(title == null || title.trim().length() < 1) return defaultValue ;
    return title;
  }
  
  private String getMimeType(ServletContext context, String fileName) {
    return (context.getMimeType(fileName) != null) ? context.getMimeType(fileName) : "text/plain" ;    
  }
  
  /**
   * Destroys the listener context
   */
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  } 
}