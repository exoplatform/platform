/*******************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL All rights reserved. * Please look
 * at license.txt in info directory for more license detail. *
 ******************************************************************************/
package org.exoplatform.services.resources.jcr;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.MapResourceBundle;
import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.registry.ServiceRegistry;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;
import org.exoplatform.services.log.LogService;
import org.exoplatform.services.resources.ExoResourceBundle;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.impl.BaseResourceBundleService;

public class ResourceBundleServiceImpl extends BaseResourceBundleService {
  
  final private static String queryDataType = "select * from nt:base where type like 'locale'";
  final static String RESOURCE_BUNDLE_TYPE = "exo:resourceBundleData";
  private JCRRegistryService jcrRegService_;
  
  final private static String APPLLICATION_NAME = "ResourceBundles";
  private DataMapper mapper_ = new DataMapper();
  
  public ResourceBundleServiceImpl(InitParams params, 
                                    LogService lservice, 
                                    CacheService cService, 
                                    JCRRegistryService jcrRegService,
                                    LocaleConfigService localeService) throws Exception {
    log_ = lservice.getLog("org.exoplatform.services.resources");
    localeService_ = localeService;
    cache_ = cService.getCacheInstance(ResourceBundleData.class.getName());
    
    jcrRegService_ = jcrRegService; 
    jcrRegService_.createServiceRegistry(new ServiceRegistry(APPLLICATION_NAME), false);
    
    initParams(params);
  }

  private ResourceBundleData getResourceBundleDataFromDB(String id) throws Exception {
    Session session  = jcrRegService_.getSession();
    Node rootNode = jcrRegService_.getServiceRegistryNode(session, APPLLICATION_NAME);
    if(!rootNode.hasNode(id)) return null;
    Node node = rootNode.getNode(id);
    ResourceBundleData resource = mapper_.nodeToResourceBundleData(node);
    session.logout();
    return resource;
  }

  public ResourceBundleData getResourceBundleData(String id) throws Exception {
    ResourceBundleData resource = (ResourceBundleData) cache_.get(id) ;
    if(resource != null) return resource ;
    resource =   getResourceBundleDataFromDB(id);
    if(resource != null) cache_.put(id, resource) ;
    return resource ;
  }
  
  public ResourceBundleData removeResourceBundleData(String id) throws Exception {
    Session session  = jcrRegService_.getSession();
    Node rootNode = jcrRegService_.getServiceRegistryNode(session, APPLLICATION_NAME);
    if(!rootNode.hasNode(id)) return null;
    Node node = rootNode.getNode(id);
    ResourceBundleData data = mapper_.nodeToResourceBundleData(node);
    node.remove();
    rootNode.save();
    session.save();
    session.logout();
    removeResourceBundleDataCache(id);
    return data;
  }
  
  public void removeResourceBundleDataCache(String id) throws Exception {
    cache_.remove(id);
  }

  public PageList findResourceDescriptions(Query q) throws Exception {
    String name = q.getName();
    if (name == null || name.length() == 0)	name = "%";
    
    StringBuilder  builder = new StringBuilder(queryDataType);
    if(name != null || q.getLanguage() != null){
      builder.append(" and ");
      generateScript(builder, "name", name);
      generateScript(builder, "language", q.getLanguage());
    }
    Session session = jcrRegService_.getSession();
    QueryManager queryManager = session.getWorkspace().getQueryManager() ;
    javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql") ;
    QueryResult queryResult = query.execute() ;
    ArrayList<Object> list = new ArrayList<Object>();
    NodeIterator iterator = queryResult.getNodes();
    while(iterator.hasNext()){
      ResourceBundleData data = mapper_.nodeToResourceBundleData(iterator.nextNode());
      list.add(data);
    }
    session.logout();
    return new ObjectPageList(list, 20);
  }
  
  private void generateScript(StringBuilder sql, String name, String value){
    if(value == null || value.length() < 1) return ;
    value = value.replace('*', '%') ;
    sql.append(name).append(" like '").append(value).append("'");
  }
  
  public void saveResourceBundle(ResourceBundleData data) throws Exception {
    Session session  = jcrRegService_.getSession();
    Node rootNode = jcrRegService_.getServiceRegistryNode(session, APPLLICATION_NAME);
    Node resourceNode = null;
    if(rootNode.hasNode(data.getId())){
      resourceNode = rootNode.getNode(data.getId());
      mapper_.map(resourceNode, data);
      resourceNode.save();
    } else {
      resourceNode = rootNode.addNode(data.getId(), RESOURCE_BUNDLE_TYPE);
      mapper_.map(resourceNode, data);
      rootNode.save();
    }
    cache_.select(new ExpireKeyStartWithSelector(data.getId())) ;
    session.save();
    session.logout();
  }
  
  protected ResourceBundle getResourceBundleFromDb(String id, ResourceBundle parent, Locale locale) throws Exception {
    Session session  = jcrRegService_.getSession();
    Node rootNode = jcrRegService_.getServiceRegistryNode(session, APPLLICATION_NAME);
    if(!rootNode.hasNode(id)) {
      session.logout();
      return null;
    }
    Node node = rootNode.getNode(id);
    ResourceBundleData data = mapper_.nodeToResourceBundleData(node);
    ResourceBundle res = new ExoResourceBundle(data.getData(), parent);
    MapResourceBundle mres = new MapResourceBundle(res, locale) ;
    session.logout();
    return mres;
  }
  
}
