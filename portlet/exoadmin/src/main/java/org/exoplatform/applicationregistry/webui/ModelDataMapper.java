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
package org.exoplatform.applicationregistry.webui;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.web.application.gadget.GadgetApplication;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Aug 26, 2008  
 */
public class ModelDataMapper {

  static final public Gadget toGadgetModel(GadgetApplication gadgetApp) throws Exception {
    Gadget gadget = new Gadget();
    gadget.setName(gadgetApp.getApplicationName());
    gadget.setUrl(gadgetApp.getUrl()) ;
    Map<String, String> metaData = gadgetApp.getMapMetadata() ;
    String title = metaData.get("directoryTitle") ;
    if(title == null || title.trim().length() < 1) title = metaData.get("title") ;
    gadget.setTitle(title) ;
    gadget.setDescription(metaData.get("description")) ;
    gadget.setReferenceUrl(metaData.get("titleUrl")) ;
    gadget.setThumbnail(metaData.get("thumbnail")) ;
    return gadget ;
  }
  
  static final private String saveThumbnail(String name, String u) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RepositoryService repoService = (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
    Session session = sessionProvider.getSession("gadgets", repoService.getRepository("repository")) ;
    Node homeNode = session.getRootNode() ;
    URL url = new URL(u) ;
    URLConnection conn = url.openConnection() ;
    InputStream is = conn.getInputStream() ;
    Node contentNode ;
    if(!homeNode.hasNode(name)) {
      Node fileNode = homeNode.addNode(name, "nt:file") ;
      contentNode = fileNode.addNode("jcr:content", "nt:resource") ;
    } else contentNode = homeNode.getNode(name + "/jcr:content") ;
    contentNode.setProperty("jcr:data", is) ;
    contentNode.setProperty("jcr:mimeType", conn.getContentType()) ;
    contentNode.setProperty("jcr:lastModified", Calendar.getInstance()) ;
    session.save() ;
    sessionProvider.close() ;
    return "/portal/rest/jcr/repository/gadgets/" + name ;
  }
}
