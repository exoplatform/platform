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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;

import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.gadget.GadgetRegistryService;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 18, 2008  
 */
public class GadgetRegistryServiceImpl implements GadgetRegistryService {
  
  private static final String PATH = RegistryService.EXO_SERVICES + "/Gadgets" ;
  
  private RegistryService regService_ ;
  private DataMapper mapper_ = new DataMapper() ;
  
  public GadgetRegistryServiceImpl(RegistryService service) throws Exception {
    regService_ = service ;
  }

  public GadgetApplication getGadget(String id) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, PATH + "/" + id) ;
    } catch (PathNotFoundException pnfe) {
      sessionProvider.close() ;
      return null ;
    }
    GadgetApplication gadget = mapper_.toApplciation(entry.getDocument()) ;
    sessionProvider.close() ;    
    return gadget ;
  }
  
  public List<GadgetApplication> getAllGadgets() throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node regNode = regService_.getRegistry(sessionProvider).getNode() ;
    if(!regNode.hasNode(PATH)) {
      sessionProvider.close() ;
      return new ArrayList<GadgetApplication>() ;
    }
    NodeIterator itr = regNode.getNode(PATH).getNodes() ;
    List<GadgetApplication> apps = new ArrayList<GadgetApplication>() ;
    while(itr.hasNext()) {
      String entryPath = itr.nextNode().getPath().substring(regNode.getPath().length() + 1) ;
      RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
      GadgetApplication app = mapper_.toApplciation(entry.getDocument()) ;
      apps.add(app) ;
    }
    sessionProvider.close() ;
    return apps ;
  }
  
  public void addGadget(GadgetApplication app) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, PATH + "/" + app.getApplicationId()) ;
      mapper_.map(entry.getDocument(), app) ;
      regService_.recreateEntry(sessionProvider, PATH, entry) ;
    } catch (PathNotFoundException pnfe) {
      entry = new RegistryEntry(app.getApplicationId()) ;
      mapper_.map(entry.getDocument(), app) ;
      regService_.createEntry(sessionProvider, PATH, entry) ;
    } finally {
      sessionProvider.close() ;      
    }
  }

  public void removeGadget(String id) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    regService_.removeEntry(sessionProvider, PATH + "/" + id) ;
    sessionProvider.close() ;
  }
  
}
