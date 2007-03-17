/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.util.Calendar;
import java.util.Date;

import org.exoplatform.portal.content.model.ContentData;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.database.HibernateService;
import org.hibernate.Session;
import org.hibernate.Transaction;
/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: Jun 14, 2003
 * Time: 1:12:22 PM
 */
public class HibernateContentDAO extends BaseContentService implements ContentDAO {
  
  private HibernateService hservice_ ;
  
  public HibernateContentDAO(HibernateService hservice, CacheService cservice) throws Exception {
    super(cservice);
    hservice_ = hservice;
  }
  
  public void saveContentNavigation(ContentNavigation navigation) throws Exception {     
    ContentData data = new ContentData();
    data.setDataType(ContentNavigation.class.getName());    
    data.setId(navigation.getOwner()+":/"+ContentNavigation.class.getName());
    data.setOwner(navigation.getOwner());
    data.setData(toXML(navigation));
    saveData(data);    
  }
  
  public void removeContentNavigation(String owner) throws Exception {
    removeData(owner, ContentNavigation.class.getName());    
  }
  
  public ContentNavigation getContentNavigation(String owner) throws Exception {
    ContentData data = getData(owner+":/"+ContentNavigation.class.getName());
    if(data == null) return null;
    return (ContentNavigation)fromXML(data.getData(), ContentNavigation.class);
  }  
  
  public ContentData getData(String id) throws Exception {
    Session session = hservice_.openSession() ;
    return (ContentData)session.get(ContentData.class, id);
  }
  
  public void removeData(String id) throws Exception {
    Session session =  hservice_.openSession() ;
    ContentData data  = getData(id);
    Transaction transaction = session.beginTransaction() ;    
    if(data != null) session.delete(data);  
    transaction.commit(); 
  }
  
  public void removeData(String owner, String type) throws Exception {
    removeData(owner+":/"+type); 
  } 
  
  private void saveData(ContentData data) throws Exception {
    Session session =  hservice_.openSession() ;
    ContentData oldData = (ContentData)session.get(ContentData.class, data.getId());    
    Transaction transaction = session.beginTransaction() ;
    Date time = Calendar.getInstance().getTime();
    if(oldData == null) {
      data.setCreatedDate(time);
      data.setModifiedDate(time);
      session.save(data) ;      
    } else{      
      oldData.setData(data.getData());
      oldData.setOwner(data.getOwner());
      oldData.setDataType(data.getDataType());
      oldData.setModifiedDate(time);
      session.update(oldData) ;
    }
    transaction.commit();    
  }
  
}