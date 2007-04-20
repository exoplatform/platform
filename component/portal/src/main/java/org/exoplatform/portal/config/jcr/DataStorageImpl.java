/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.jcr;

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.jcr.RepositoryService;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 20, 2007  
 */
public class DataStorageImpl  implements DataStorage {
  private RepositoryService service_ ;
  private DataMapper mapper = new DataMapper() ;

  public DataStorageImpl(RepositoryService service) {
    service_ = service ;
  }
  
  public Page getPage(String pageId) throws Exception {
    return null;
  }

  public PageNavigation getPageNavigation(String id) throws Exception {
    return null;
  }

  public PortalConfig getPortalConfig(String portalName) throws Exception {
    return null;
  }

  public void remove(PortalConfig config) throws Exception {
  }

  public void remove(Page page) throws Exception {
  }

  public void remove(PageNavigation navigation) throws Exception {
  }

  public void save(PortalConfig config) throws Exception {
  }

  public void save(Page page) throws Exception {
  }

  public void save(PageNavigation navigation) throws Exception {
  }
}
