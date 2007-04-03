/*******************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL All rights reserved. * Please look
 * at license.txt in info directory for more license detail. *
 ******************************************************************************/
package org.exoplatform.services.resources.impl;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.MapResourceBundle;
import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;
import org.exoplatform.services.log.LogService;
import org.exoplatform.services.resources.ExoResourceBundle;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;

public class JCRResourceBundleServiceImpl extends BaseJCRService {
  
  final private static String queryDataType = "select * from nt:base where type like 'locale'";
 
  public JCRResourceBundleServiceImpl(InitParams params, LocaleConfigService localeService, 
                                      LogService lservice, CacheService cService) throws Exception {
    log_ = lservice.getLog("org.exoplatform.services.resources");
    localeService_ = localeService;
    cache_ = cService.getCacheInstance(ResourceBundleData.class.getName());
    initParams(params);
  }

  private ResourceBundleData getResourceBundleDataFromDB(String id) throws Exception {
    Node rootNode = getResourceBundleNode(false);
    if(!rootNode.hasNode(id)) return null;
    Node node = rootNode.getNode(id);
    return nodeToResourceBundleData(node);
  }

  public ResourceBundleData getResourceBundleData(String id) throws Exception {
    ResourceBundleData resource = (ResourceBundleData) cache_.get(id) ;
    if(resource != null) return resource ;
    resource =   getResourceBundleDataFromDB(id);
    if(resource != null) cache_.put(id, resource) ;
    return resource ;
  }
  
  public ResourceBundleData removeResourceBundleData(String id) throws Exception {
    Node rootNode = getResourceBundleNode(false);
    if(!rootNode.hasNode(id)) return null;
    Node node = rootNode.getNode(id);
    ResourceBundleData data = nodeToResourceBundleData(node);
    node.remove();
    rootNode.save();
    getSession().save();
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
    
    QueryManager queryManager = getSession().getWorkspace().getQueryManager() ;
    javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql") ;
    QueryResult queryResult = query.execute() ;
    ArrayList<Object> list = new ArrayList<Object>();
    NodeIterator iterator = queryResult.getNodes();
    while(iterator.hasNext()){
      ResourceBundleData data = nodeToResourceBundleData(iterator.nextNode());
      list.add(data);
    }
    
    return new ObjectPageList(list, 20);
  }
  
  private void generateScript(StringBuilder sql, String name, String value){
    if(value == null || value.length() < 1) return ;
    value = value.replace('*', '%') ;
    sql.append(name).append(" like '").append(value).append("'");
  }
  
  public void saveResourceBundle(ResourceBundleData data) throws Exception {
    Node rootNode = getResourceBundleNode(true);
    Node resourceNode = null;
    if(rootNode.hasNode(data.getId())){
      resourceNode = rootNode.getNode(data.getId());
      resourceBundleToNode(resourceNode, data);
      resourceNode.save();
    } else {
      resourceNode = rootNode.addNode(data.getId(), RESOURCE_BUNDLE_TYPE);
      resourceBundleToNode(resourceNode, data);
      rootNode.save();
    }
    cache_.select(new ExpireKeyStartWithSelector(data.getId())) ;
    getSession().save();
  }
  
  protected ResourceBundle getResourceBundleFromDb(String id, ResourceBundle parent, Locale locale) throws Exception {
    Node rootNode = getResourceBundleNode(false);
    if(!rootNode.hasNode(id)) return null;
    Node node = rootNode.getNode(id);
    ResourceBundleData data = nodeToResourceBundleData(node);
    ResourceBundle res = new ExoResourceBundle(data.getData(), parent);
    MapResourceBundle mres = new MapResourceBundle(res, locale) ;
    return mres;
  }
  
}
