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
import javax.jcr.RepositoryException;

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
  
  final static public String APP_PATH = RegistryService.EXO_APPLICATIONS + "/Dashboard";
  final static public String SHARED_PATH = "shared";
  final static public String USERS_PATH = "users";
  
  private RegistryService regService_ ;
  private DataMapper mapper_ = new DataMapper () ;
  
  public PortalLayoutServiceImpl(RegistryService service) throws Exception {
    regService_ = service ;
  }

  public void create(Container container) throws Exception {
    create(container, null);
  }

  public void create(Container container, String userId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    RegistryEntry entry = new RegistryEntry(container.getId());
    mapper_.map(entry.getDocument(), container);
    String path;
    if(userId == null)
      path = getSharedPath();
    else
      path = getUserPath(userId);
    regService_.createEntry(sessionProvider, path, entry);
    sessionProvider.close();
  }

  public Container getContainer(String id) throws Exception {
    return getContainer(id, null);
  }

  public Container getContainer(String id, String userId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String path = getPath(id, userId);
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
    remove(container, null);
  }

  public void remove(Container container, String userId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;

    String path = getPath(container.getId(), userId);
    regService_.removeEntry(sessionProvider, path) ;
    sessionProvider.close() ;
  }

  public void save(Container container) throws Exception {
    save(container, null);
  }

  public void save(Container container, String userId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;

    String path = getPath(container.getId(), userId);

    String parentPath;
    if(userId == null)
      parentPath = getSharedPath();
    else
      parentPath = getUserPath(userId);

    RegistryEntry entry = regService_.getEntry(sessionProvider, path);
    mapper_.map(entry.getDocument(), container) ;
    regService_.recreateEntry(sessionProvider, parentPath, entry) ;
    sessionProvider.close() ;
  }

  private String getPath(String containerId, String userId) {
    String path;
    if(userId == null)
      path = getSharedPath() + "/" + containerId;
    else {
      path = getUserPath(userId) + "/" + containerId;
    }
    return path;
  }

  private String getSharedPath() {
    return APP_PATH + "/"  + SHARED_PATH;
  }

  private String getUserPath(String userId) {
    return APP_PATH + "/"  + USERS_PATH + "/" + userId;
  }
}
