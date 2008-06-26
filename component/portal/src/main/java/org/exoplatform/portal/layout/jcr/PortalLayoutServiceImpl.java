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
package org.exoplatform.portal.layout.jcr;

import javax.jcr.PathNotFoundException;

import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.layout.PortalLayoutService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 25, 2008  
 */
public class PortalLayoutServiceImpl implements PortalLayoutService {
  
  final static public String APP_PATH = RegistryService.EXO_APPLICATIONS + "/DashBoard" ;
  
  private RegistryService regService_ ;
  private DataMapper mapper_ = new DataMapper () ;
  
  public PortalLayoutServiceImpl(RegistryService service) throws Exception {
    regService_ = service ;
  }

  public void create(Container container) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry = new RegistryEntry(container.getId()) ;
    mapper_.map(entry.getDocument(), container) ;
    regService_.createEntry(sessionProvider, APP_PATH, entry) ;
    sessionProvider.close() ;
  }

  public Container getContainer(String id) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String path = APP_PATH + "/" + id ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, path) ;
    } catch (PathNotFoundException pnfe) {
      sessionProvider.close() ;
      return null ;
    }
    Container container = mapper_.toContainer(entry.getDocument()) ;
    sessionProvider.close() ;
    return container ;
  }

  public void remove(Container container) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String path = APP_PATH + "/" + container.getId() ;
    regService_.removeEntry(sessionProvider, path) ;
    sessionProvider.close() ;
  }

  public void save(Container container) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry = regService_.getEntry(sessionProvider, APP_PATH + "/" + container.getId());
    mapper_.map(entry.getDocument(), container) ;
    regService_.recreateEntry(sessionProvider, APP_PATH, entry) ;
    sessionProvider.close() ;
  }

}
