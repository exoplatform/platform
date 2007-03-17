/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Feb 12, 2007  
 */
public class JCRPortalDAO extends JCRDataService implements PortalDAO {

  public JCRPortalDAO(CacheService cservice) throws Exception {
    super(cservice);
  }

  final private static String PORTAL_CONFIG = "portalConfig.xml";
  final private static String PAGE = "page";
  final private static String PAGE_NAVIGATION = "pageNavigation.xml";
  
  final private static String queryDataType = "select * from nt:base";
  
//-------------------------- Portal Config -------------------------------
  
  public void savePortalConfig(PortalConfig config) throws Exception {
    Node portalNode = getPortalServiceNode(config.getOwner(), true);
    Data data = portalConfigToData(config);
    saveData(portalNode, data, PORTAL_CONFIG);
    portalConfigCache_.select(new ExpireKeyStartWithSelector(config.getOwner())) ;
  }
  
  public PortalConfig getPortalConfig(String owner) throws Exception {
    PortalConfig config = (PortalConfig) portalConfigCache_.get(owner) ;
    if(config != null) return config ;
    Data data = getData(owner, owner+":/"+PortalConfig.class.getName());
    if(data == null) return null;
    config = (PortalConfig)fromXML(data.getData(), PortalConfig.class);
    if(config != null) portalConfigCache_.put(owner, config) ;
    return config ;
  }
  
  public void removePortalConfig(String owner) throws Exception {
    removeData(owner, PortalConfig.class.getName());   
    portalConfigCache_.remove(owner);
  }
  
  public PageList getPortalConfigs() throws Exception {
    org.exoplatform.portal.config.Query query =
      new org.exoplatform.portal.config.Query(null, null, null, PortalConfig.class);
    return findDataDescriptions(query);
  }
  
//-------------------------- Page -------------------------------

  public void savePage(Page page) throws Exception {
    page.setId(page.getPageId());
    Node parentNode = getDataServiceNode(page.getOwner(), PAGE, true);
    Data data = pageToData(page);
    saveData(parentNode, data, page.getName()+".xml");
    pageConfigCache_.select(new ExpireKeyStartWithSelector(page.getId())) ;
  }
  
  public Page getPage(String pageId) throws Exception {
    Page page = (Page) pageConfigCache_.get(pageId) ;
    if(page != null) return page ;
    
    if(pageId.indexOf(':') < 0) return  null;
    String owner = pageId.substring(0, pageId.indexOf(':'));
    
    Node parentNode = getDataServiceNode(owner, PAGE, true);
    Node node = getNode(parentNode, pageId);
    if(node == null) return null;
    Data data = nodeToData(node);
    page = (Page)fromXML(data.getData(), Page.class);
    
    if(page != null) pageConfigCache_.put(pageId, page) ;
    return page ;
  }
  
  public PageSet getPageOfOwner(String owner) throws Exception {
    PageSet pageSet = new PageSet();
    Node parentNode = getDataServiceNode(owner, PAGE, true);
    NodeIterator iterator = parentNode.getNodes();
    while(iterator.hasNext()){
      Data data = nodeToData(iterator.nextNode());
      pageSet.getPages().add((Page)fromXML(data.getData(), Page.class));
    }
    return pageSet;
  }
  
  public void removePage(String pageId) throws Exception {
    removeData(pageId);  
    pageConfigCache_.remove(pageId);
  }
  
  public void removePageOfOwner(String owner) throws Exception {
    Node node = getDataServiceNode(owner, PAGE, false);
    if(node == null) return ;
    Node parentNode  = node.getParent();
    node.remove();
    parentNode.save();
    getSession().save();
  }
  
//-------------------------- Pave Navigation -------------------------------
  
  public void savePageNavigation(PageNavigation pageNavigation) throws Exception {
    Node portalNode = getPortalServiceNode(pageNavigation.getOwner(), true);
    Data data = pageNavigationToData(pageNavigation);
    saveData(portalNode, data, PAGE_NAVIGATION);
    pageNavigationCache_.select(new ExpireKeyStartWithSelector(pageNavigation.getOwner())) ;
  }
  
  public PageNavigation getPageNavigation(String owner) throws Exception {
    PageNavigation page = (PageNavigation) pageNavigationCache_.get(owner) ;
    if(page != null) return page ;
    
    Data data = getData(owner, owner+":/"+PageNavigation.class.getName());
    if(data == null) return null;
    page = (PageNavigation)fromXML(data.getData(), PageNavigation.class);
    
    if(page != null) pageNavigationCache_.put(owner, page) ;
    return page ;
  }
  
  public void removePageNavigation(String owner) throws Exception {
    removeData(owner, PageNavigation.class.getName()); 
    pageNavigationCache_.remove(owner);
  }
  
//-------------------------- Portlet Preferences -------------------------------
  
  public void savePortletPreferencesConfig(PortletPreferences portletPreferences) throws Exception {
    Node parentNode = getDataServiceNode(portletPreferences.getCreator(), PORTLE_TPREFERENCES, true);
    Data data = portletPreferencesConfigToData(portletPreferences);
    saveData(parentNode, data, String.valueOf(portletPreferences.getWindowId().hashCode()));
  }
  
//-------------------------- Data -------------------------------
  
  public Data getData(String id) throws Exception {
    String owner = null;
    if(id.indexOf(':') > -1) owner = id.substring(0, id.indexOf(':'));
    return getData(owner, id);
  }
  
  public PageList findDataDescriptions(org.exoplatform.portal.config.Query configQuery) throws Exception {
    StringBuilder  builder = new StringBuilder(queryDataType);
    generateScript(builder, OWNER, configQuery.getOwner(), false);
    generateScript(builder, DATA_TYPE, configQuery.getType(), false);
    generateScript(builder, VIEW_PERMISSION, configQuery.getViewPermission(), true);
    generateScript(builder, EDIT_PERMISSION, configQuery.getEditPermission(), true);
    
    QueryManager queryManager = getSession().getWorkspace().getQueryManager() ;
    Query query = queryManager.createQuery(builder.toString(), "sql") ;
    QueryResult queryResult = query.execute() ;
    ArrayList<Object> list = new ArrayList<Object>();
    NodeIterator iterator = queryResult.getNodes();
    while(iterator.hasNext()){
      Data data = nodeToData(iterator.nextNode());
      list.add(fromXML(data.getData(), configQuery.getClassType()));
    }
    
    return new ObjectPageList(list, 20);
  }
  
  private void generateScript(StringBuilder sql, String name, String value, boolean isPermission){
    if(value == null || value.length() < 1) return ;
    if(sql.indexOf(" where") < 0) sql.append(" where "); else sql.append(" and "); 
    value = value.replace('*', '%') ;
    if(isPermission && value.indexOf(":/") == 0) value = "%"+ value;
    sql.append(name).append(" like '").append(value).append("'");
  }
  
  public void removeData(String id) throws Exception {
    String owner = null;
    if(id.indexOf(':') > -1) owner = id.substring(0, id.indexOf(':'));
    removeDataById(owner, id);
  }
  
  public void removeData(String owner, String type) throws Exception {
    removeDataById(owner, owner+":/"+type);
  }
  
  private void removeDataById(String owner, String id) throws Exception {
    if(owner == null) return;
    Node parentNode = getPortalServiceNode(owner, false);
    Node node = getNode(parentNode, id);
    if(node == null)  return;
    parentNode = node.getParent(); 
    node.remove();
    parentNode.save();
    getSession().save();
  }
  
  private void saveData(Node parentNode, Data data, String name) throws Exception {
    Node node;
    Date time = Calendar.getInstance().getTime();
    data.setModifiedDate(time);
    if(data.getCreatedDate() == null) data.setCreatedDate(time);
    if(parentNode.hasNode(name)) {
      node = parentNode.getNode(name);
      dataToNode(data, node);
      node.save();
    } else {
      node = parentNode.addNode(name, DATA_NODE_TYPE);
      dataToNode(data, node);
      parentNode.save();
    }
    getSession().save();
  }
  
}
