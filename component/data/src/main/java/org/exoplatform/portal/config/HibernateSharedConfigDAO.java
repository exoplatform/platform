/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.List;

import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.database.HibernateService;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 1, 2007  
 */
public class HibernateSharedConfigDAO implements SharedConfigDAO {
  
  private ExoCache sharedPortalCache_ ;  
  private ExoCache sharedNavCache_ ;
  
  private HibernateService hservice_ ;
  
  public HibernateSharedConfigDAO(HibernateService hservice, 
                                  CacheService cservice) throws Exception {
    hservice_ = hservice;
    sharedPortalCache_ = cservice.getCacheInstance(SharedPortal.class.getName()) ;
    sharedNavCache_ = cservice.getCacheInstance(SharedNavigation.class.getName()) ;
  }
  
//-------------------------- Shared Portal ------------------------------- 
  
  public SharedPortal getSharedPortal(String groupId) throws Exception {
    SharedPortal cportal = (SharedPortal) sharedPortalCache_.get(groupId) ;
    if(cportal == null) {
      cportal = (SharedPortal)hservice_.findOne(SharedPortal.class, groupId);
        //(SharedPortal)hservice_.findOne(SharedPortal.class, groupId) ;
      if(cportal == null) sharedPortalCache_.put(groupId, SharedConfigDAO.NO_COMMUNITY_PORTAL) ;
      else sharedPortalCache_.put(groupId, cportal) ;
    }
    if(cportal == SharedConfigDAO.NO_COMMUNITY_PORTAL)  return null ;
    return cportal ;
  }  
  
  public void saveSharedPortal(List<SharedPortal> list) throws Exception {
    if(list == null) return ;
    Session session =  hservice_.openSession() ;
    Transaction transaction = session.beginTransaction() ;
    for(SharedPortal sharedPortal : list) {
      session.save(sharedPortal) ; 
    }
    transaction.commit(); 
  }
  
  public void addSharedPortal(SharedPortal sharedPortal) throws Exception {
    Session session =  hservice_.openSession() ;
    Transaction transaction = session.beginTransaction() ;    
    session.save(sharedPortal) ; 
    transaction.commit(); 
    sharedPortalCache_.remove(sharedPortal.getGroupId()) ;
  }
  
//  @SuppressWarnings("unused")
//  public SharedPortal getSharedPortal(String groupId) throws Exception {
//    return (SharedPortal)hservice_.findOne(SharedPortal.class, groupId) ;
//  }
  
  @SuppressWarnings("unchecked")
  public List<SharedPortal> getSharedPortals() throws Exception {
    return hservice_.openSession().createCriteria(SharedPortal.class).list() ;    
  }
  
  public void removeSharedPortal(SharedPortal sharedPortal) throws Exception {
    hservice_.remove(SharedPortal.class, sharedPortal.getGroupId()) ;
    sharedPortalCache_.remove(sharedPortal.getGroupId()) ;
  }
  
//-------------------------- Shared Navigation -------------------------------  
  
  public void saveSharedNavigation(List<SharedNavigation> list) throws Exception {
    if(list == null) return ;
    Session session =  hservice_.openSession() ;
    for(SharedNavigation sharedNav : list) {
      session.save(sharedNav) ; 
    }
  }
  
  public void addSharedNavigation(SharedNavigation sharedNav) throws Exception {
    Session session =  hservice_.openSession() ;
    Transaction transaction = session.beginTransaction() ;    
    session.save(sharedNav) ; 
    transaction.commit(); 
    sharedNavCache_.remove(sharedNav.getGroupId()) ;
  }
  
  public SharedNavigation getSharedNavigation(String groupId) throws Exception {
    SharedNavigation cnav = (SharedNavigation) sharedNavCache_.get(groupId) ;
    if(cnav == null) {
      cnav = (SharedNavigation)hservice_.findOne(SharedNavigation.class, groupId) ;
      if(cnav == null) cnav =  SharedConfigDAO.NO_COMMUNITY_NAVIGATION  ;
      sharedNavCache_.put(groupId, cnav) ;
    }
    if(cnav == SharedConfigDAO.NO_COMMUNITY_NAVIGATION)  return null ;
    return cnav ;
  }
  
  @SuppressWarnings("unchecked")
  public List<SharedNavigation> getSharedNavigations() throws Exception {
    return hservice_.openSession().createCriteria(SharedNavigation.class).list() ; 
  }
  
  public void removeSharedNavigation(SharedNavigation sharedNav) throws Exception {
    hservice_.remove(SharedNavigation.class, sharedNav.getGroupId());
    sharedNavCache_.remove(sharedNav.getGroupId()) ;
  }
}
