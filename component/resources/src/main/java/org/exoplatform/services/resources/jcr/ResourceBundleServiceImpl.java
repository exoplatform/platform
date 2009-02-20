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
package org.exoplatform.services.resources.jcr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.MapResourceBundle;
import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.ExoResourceBundle;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.impl.BaseResourceBundleService;
import org.picocontainer.Startable;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Dec 1, 2007  
 */
public class ResourceBundleServiceImpl extends BaseResourceBundleService implements Startable {
  
  RegistryService regService_ ;
  DataMapper mapper_ = new DataMapper() ;
  static final private String SERVICE_NAME = "ResourceBundles" ;
  private InitParams params_ ;

  public ResourceBundleServiceImpl(InitParams params, 
      CacheService cService, 
      RegistryService service,
      LocaleConfigService localeService) throws Exception {
    log_ = ExoLogger.getLogger("org.exoplatform.services.resources");
    localeService_ = localeService;
    cache_ = cService.getCacheInstance(ResourceBundleData.class.getName());
    regService_ = service ;
    params_ = params;
  }

	public void start() {
		try {
			initParams(params_);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	public void stop() {}
	
  /**
   * Overridden method.
   * 
   * @param id 
   * @return resourceData
   * @throws Exception exception
   * @see org.exoplatform.services.resources.ResourceBundleService#getResourceBundleData(java.lang.String)
   */
  public ResourceBundleData getResourceBundleData(String id) throws Exception {
    String resourceDataPath = getServiceRegistryPath() + "/" + id ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry entry ;
      try {
        entry = regService_.getEntry(sessionProvider, resourceDataPath) ;
      } catch (PathNotFoundException ie) {
        return null ;
      }
      ResourceBundleData resourceData = mapper_.toResourceBundleData(entry.getDocument()) ;
      return resourceData ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  /**
   * Overridden method.
   * 
   * @param id id
   * @return resource
   * @throws Exception exception
   * @see org.exoplatform.services.resources.ResourceBundleService#removeResourceBundleData(java.lang.String)
   */
  public ResourceBundleData removeResourceBundleData(String id) throws Exception {
    ResourceBundleData resource = getResourceBundleData(id) ;
    if(resource == null) return null ;
    String resourceDataPath = getServiceRegistryPath() + "/" + id ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, resourceDataPath) ;
      cache_.remove(id) ;
      return resource ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  /**
   * Overridden method.
   * 
   * @param data resourceData
   * @throws Exception exception
   * @see org.exoplatform.services.resources.ResourceBundleService#saveResourceBundle(org.exoplatform.services.resources.ResourceBundleData)
   */
  public void saveResourceBundle(ResourceBundleData resourceData) throws Exception {
    String id = resourceData.getId() ;
    String servicePath = getServiceRegistryPath() ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry entry ;
      try {
        entry = regService_.getEntry(sessionProvider, servicePath + "/" + id) ;
      } catch (PathNotFoundException ie) {
        entry = new RegistryEntry(id) ;
        regService_.createEntry(sessionProvider, servicePath, entry) ;
      }
      mapper_.map(entry.getDocument(), resourceData) ;
      regService_.recreateEntry(sessionProvider, servicePath, entry) ;
      cache_.select(new ExpireKeyStartWithSelector(id)) ;
    }
    finally {
      sessionProvider.close();
    }
  }
  
  /**
   * Overridden method.
   * 
   * @param q query
   * @return page list
   * @throws Exception exception
   * @see org.exoplatform.services.resources.ResourceBundleService#findResourceDescriptions(org.exoplatform.services.resources.Query)
   */
  public PageList findResourceDescriptions(Query q) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      Node regNode = regService_.getRegistry(sessionProvider).getNode() ;
      StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT) ;
      generateScript(builder, "jcr:path", regNode.getPath() + "/" + getServiceRegistryPath() + "/%") ;
      generateScript(builder, DataMapper.TYPE, DataMapper.LOCALE) ;
      generateScript(builder, DataMapper.NAME, q.getName()) ;
      generateScript(builder, DataMapper.LANGUAGE, q.getLanguage()) ;
      builder.append("order by ").append(DataMapper.NAME).append(",").append(DataMapper.LANGUAGE);
      Session session = regNode.getSession() ;
      QueryManager queryManager = session.getWorkspace().getQueryManager() ;
      javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql") ;
      QueryResult result = query.execute() ;
      NodeIterator itr = result.getNodes() ;
      List<ResourceBundleData> resources = new ArrayList<ResourceBundleData>() ;
      while(itr.hasNext()) {
        String entryPath = itr.nextNode().getPath().substring(regNode.getPath().length() + 1) ;
        RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
        ResourceBundleData data = mapper_.toResourceBundleData(entry.getDocument()) ;
        resources.add(data) ;
      }
      return new ObjectPageList(resources, 20);
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  /**
   * Overridden method.
   * 
   * @param id String
   * @param parent ResourceBundle
   * @param locale Locale
   * @return resource bundle
   * @throws Exception exception
   * @see org.exoplatform.services.resources.impl.BaseResourceBundleService#getResourceBundleFromDb(java.lang.String,
   *      java.util.ResourceBundle, java.util.Locale)
   */
  @Override
  protected ResourceBundle getResourceBundleFromDb(String id, ResourceBundle parent, Locale locale) throws Exception {
    ResourceBundleData data = getResourceBundleData(id) ;
    if(data == null) return null ;
    ResourceBundle res = new ExoResourceBundle(data.getData(), parent);
    MapResourceBundle mres = new MapResourceBundle(res, locale) ;
    return mres;
  }
  /**
   * This method will generate condition for sql statement
   * @param sql
   * @param name
   * @param value
   */
  //-----------------------------------------------------------------------------------//
  private void generateScript(StringBuilder sql, String name, String value){
    if(value == null || value.length() < 1) return ;
    if(sql.indexOf(" where") < 0) sql.append(" where "); else sql.append(" and "); 
    value = value.replace('*', '%') ;
    //TODO: dang.tung
    value = value.replaceAll("'", "&#39;");
    sql.append(name).append(" like '").append(value).append("'");
  }
  
  /**
   * 
   * @return
   */
  private String getServiceRegistryPath() {
    return RegistryService.EXO_SERVICES + "/" + SERVICE_NAME ;
  }
}
