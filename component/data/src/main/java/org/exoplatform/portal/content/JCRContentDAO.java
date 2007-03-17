/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;

import org.exoplatform.portal.content.model.ContentData;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.exoplatform.services.cache.CacheService;

import static org.exoplatform.portal.config.JCRDataService.*;
/**
 * Created by The eXo Platform SARL        .
 * Author : Le Bien Thuy  
 *          lebienthuy@gmail.com
 * Date: 3/5/2007
 * Time: 1:12:22 PM
 */
public class JCRContentDAO extends BaseContentService implements ContentDAO {

  final private static String NODE_NAME = "contentNavigation.xml";
  
  public JCRContentDAO(CacheService cservice) throws Exception {
    super(cservice);
  }
  
  public void saveContentNavigation(ContentNavigation navigation) throws Exception {
    Node portalNode = getPortalServiceNode(navigation.getOwner(), true); 
    ContentData data = new ContentData();
    data.setDataType(ContentNavigation.class.getName());    
    data.setId(navigation.getOwner()+":/"+ContentNavigation.class.getName());
    data.setOwner(navigation.getOwner());
    data.setData(toXML(navigation));
    saveData(portalNode, data);  
  }
  
  public ContentNavigation getContentNavigation(String owner) throws Exception {
    ContentData data = getDataByOwner(owner);
    if(data == null) return null;
    return (ContentNavigation)fromXML(data.getData(), ContentNavigation.class);
  }
  
  public void removeContentNavigation(String owner) throws Exception {
    removeData(owner, NODE_NAME);
  }
  
  public ContentData getData(String id) throws Exception {
    String owner = id.substring(0, id.indexOf(':'));
    return getDataByOwner(owner);
  }
  
  private ContentData getDataByOwner(String owner) throws Exception {
    Node parentNode = getPortalServiceNode(owner, false);
    if(parentNode.hasNode(NODE_NAME) == false) return null;    
    Node node = parentNode.getNode(NODE_NAME);
    return nodeToContentData(node);
  }
  
  public void removeData(String id) throws Exception {
    removeDataByOwner(id.substring(0, id.indexOf(':')));
  }
  
  @SuppressWarnings("unused")
  public void removeData(String owner, String type) throws Exception {
    removeDataByOwner(owner); 
  } 
  
  private void removeDataByOwner(String owner) throws Exception {
    Node parentNode = getPortalServiceNode(owner, false);
    if(parentNode.hasNode(NODE_NAME) == false) return ;
    Node node = parentNode.getNode(NODE_NAME);
    node.remove();
    parentNode.save();
    getSession().save();
  }
  
  private void saveData(Node parentNode, ContentData data) throws Exception {
    Node node;
    Date time = Calendar.getInstance().getTime();
    data.setModifiedDate(time);
    if(data.getCreatedDate() == null) data.setCreatedDate(time);
    if(parentNode.hasNode(NODE_NAME)) {
      node = parentNode.getNode(NODE_NAME);
      contentDataToNode(data, node);
      node.save();
    } else {
      node = parentNode.addNode(NODE_NAME, DATA_NODE_TYPE);
      contentDataToNode(data, node);
      parentNode.save();
    }
    getSession().save();
  }
    
  private ContentData nodeToContentData(Node node) throws Exception {
    ContentData data = new ContentData();
    if(!node.hasProperty(ID)) return null;
    data.setId(node.getProperty(ID).getString()); 
    if(!node.hasProperty(OWNER)) return null; 
    data.setOwner(node.getProperty(OWNER).getString()); 
    data.setDataType(node.getProperty(DATA_TYPE).getString());
    data.setData(node.getProperty(DATA).getString());
    data.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    data.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return data;
  }
  
  private void contentDataToNode(ContentData data, Node node) throws  Exception {
    node.setProperty(ID, data.getId());
    node.setProperty(OWNER, data.getOwner());
    node.setProperty(DATA_TYPE, data.getDataType());
    node.setProperty(DATA, data.getData());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);
    calendar = Calendar.getInstance();
    calendar.setTime(data.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);    
  }
 
}