/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;
import org.exoplatform.services.database.DBObjectPageList;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.database.ObjectQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: Jun 14, 2003
 * Time: 1:12:22 PM
 */
public class HibernatePortalDAO extends BaseDataService implements PortalDAO {
  
  public HibernatePortalDAO(CacheService cservice) throws Exception{
    super(cservice);
  }
  
//-------------------------- Portal Config -------------------------------
  
  public void savePortalConfig(PortalConfig config) throws Exception  {    
    Data data = portalConfigToData(config);
    saveData(data);
    portalConfigCache_.select(new ExpireKeyStartWithSelector(config.getOwner())) ;
  }
  
  public PortalConfig getPortalConfig(String owner) throws Exception {
    PortalConfig config = (PortalConfig) portalConfigCache_.get(owner) ;
    if(config != null) return config ;
    
//  id  ==  owner:/PortalConfig.class.getName() ;
    Data data = getData(owner+":/"+PortalConfig.class.getName());
    if(data == null) return null;
    config = (PortalConfig)fromXML(data.getData(), PortalConfig.class);
    
    if(config != null) portalConfigCache_.put(owner, config) ;
    return config ;
  }
  
  public PageList getPortalConfigs() throws Exception {
    Query query = new Query(null, null, null, PortalConfig.class.getName());
    PageList pageList  = findDataDescriptions(query);
    List list  = pageList.getAll();
    ArrayList<PortalConfig> arrayList = new ArrayList<PortalConfig>();
    for(Object ele : list){
      Data data  = (Data)ele;      
      arrayList.add((PortalConfig)fromXML(data.getData(), PortalConfig.class));
    }
    return new ObjectPageList(arrayList, 20);
  }
  
  public void removePortalConfig(String owner) throws Exception {
    removeData(owner, PortalConfig.class.getName());  
    portalConfigCache_.remove(owner);
  }
  
//-------------------------- Page ------------------------------- 
  
  public void  savePage(Page page) throws Exception  {
    //id  ==  owner:/ page.getName()
    Data data = pageToData(page);
    saveData(data);
    pageConfigCache_.select(new ExpireKeyStartWithSelector(page.getId())) ;
  }
  
  public Page getPage(String id) throws Exception {
    Page page = (Page) pageConfigCache_.get(id) ;
    if(page != null) return page ;
    
    Data data = getData(id) ;
    if(data == null) return null;
    page = (Page)fromXML(data.getData(), Page.class);
    
    if(page != null) pageConfigCache_.put(id, page) ;
    return page ;
  }
  
  public PageSet getPageOfOwner(String owner) throws Exception {    
    Query query = new Query(owner, null, null, Page.class.getName());
    PageList pageList  = findDataDescriptions(query);
    List list  = pageList.getAll();
    ArrayList<Page> arrayList = new ArrayList<Page>();
    for(Object ele : list){
      Data data  = (Data)ele;      
      arrayList.add((Page)fromXML(data.getData(), Page.class));
    }
    PageSet pageSet = new PageSet();
    pageSet.setPages(arrayList);
    return pageSet;
  }
  
  public void removePage(String pageId) throws Exception {
    removeData(pageId);    
    pageConfigCache_.remove(pageId);
  }
  
  
  public void removePageOfOwner(String owner) throws Exception {
    ArrayList<Page> arrayList = getPageOfOwner(owner).getPages();
    for(Page page : arrayList) removeData(page.getPageId());   
  }  
  
//-------------------------- Pave Navigation -------------------------------
  
  public void savePageNavigation(PageNavigation pageNavigation) throws Exception {     
    Data data = pageNavigationToData(pageNavigation);
    saveData(data);  
    pageNavigationCache_.select(new ExpireKeyStartWithSelector(pageNavigation.getOwner())) ;
  }
  
  public void removePageNavigation(String owner) throws Exception {
    removeData(owner, PageNavigation.class.getName());    
    pageNavigationCache_.remove(owner);
  }
  
  public PageNavigation getPageNavigation(String owner) throws Exception {
    PageNavigation pageNav = (PageNavigation) pageNavigationCache_.get(owner) ;
    if(pageNav != null) return pageNav ;
    
    //id  ==  owner:/PageNavigation.class.getName() ;   
    Data data = getData(owner+":/"+PageNavigation.class.getName());
    if(data == null) return null;
    pageNav  = (PageNavigation)fromXML(data.getData(), PageNavigation.class);
    
    if(pageNav != null) pageNavigationCache_.put(owner, pageNav) ;
    return pageNav ;
  }
  
//-------------------------- Portlet Preferences ------------------------------- 
  
  public void savePortletPreferencesConfig(PortletPreferences portletPreferences) throws Exception  {
    Data data = portletPreferencesConfigToData(portletPreferences);
    saveData(data);
  }
  
//-------------------------- Data -------------------------------
  
  public PageList  findDataDescriptions(Query configQuery) throws Exception {    
    String owner = configQuery.getOwner() ;
    if (owner == null || owner.length() == 0) owner = "%" ;
    ObjectQuery oq = new ObjectQuery(Data.class);
    
    generateScript(oq, OWNER, owner, false);
    generateScript(oq, DATA_TYPE, configQuery.getType(), false);
    generateScript(oq, VIEW_PERMISSION, configQuery.getViewPermission(), true);
    generateScript(oq, EDIT_PERMISSION, configQuery.getEditPermission(), true);

    PortalContainer manager  = PortalContainer.getInstance();   
    HibernateService hservice = (HibernateService)manager.getComponentInstanceOfType(HibernateService.class) ;
    return new DBObjectPageList(hservice, oq) ;
  }
  
  private void generateScript(ObjectQuery oq, String name, String value, boolean isPermission){
    if(value == null || value.length() < 1) return ;
    if(value.indexOf('*') > -1) value = value.replace('*', '%') ;
    if(isPermission && value.indexOf(":/") == 0) value = "%"+ value;
    oq.addLIKE(name, value) ;
  }
  
  public Data getData(String id) throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();   
    HibernateService hservice = (HibernateService)manager.getComponentInstanceOfType(HibernateService.class) ;
    
    Session session = hservice.openSession() ;
    return (Data)session.get(Data.class, id);
  }
  
  public void removeData(String id) throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();   
    HibernateService hservice = (HibernateService)manager.getComponentInstanceOfType(HibernateService.class) ;
    
    Session session =  hservice.openSession() ;
    Data data  = getData(id);
    Transaction transaction = session.beginTransaction() ;    
    if(data != null) session.delete(data);  
    transaction.commit(); 
  }
  
  public void removeData(String owner, String type) throws Exception {
    removeData(owner+":/"+type); 
  } 
  
  private void saveData(Data data) throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();   
    HibernateService hservice = (HibernateService)manager.getComponentInstanceOfType(HibernateService.class) ;
    
    Session session =  hservice.openSession() ;
    Data oldData = (Data)session.get(Data.class, data.getId());    
    Transaction transaction = session.beginTransaction() ;
    Date time = Calendar.getInstance().getTime();
    if(oldData == null) {
      data.setCreatedDate(time);
      data.setModifiedDate(time);
      session.save(data) ;
    } else{      
      oldData.setData(data.getData());
      oldData.setViewPermission(data.getViewPermission());
      oldData.setEditPermission(data.getEditPermission());
      oldData.setOwner(data.getOwner());
      oldData.setDataType(data.getDataType());
      oldData.setModifiedDate(time);
      session.update(oldData) ;
    }
    transaction.commit();    
  }
  
}