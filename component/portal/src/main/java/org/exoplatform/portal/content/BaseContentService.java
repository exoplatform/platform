/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.portal.content.model.ContentItem;
import org.exoplatform.portal.content.model.ContentNode;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 6, 2007  
 */
public abstract class BaseContentService {
  
  private List<ContentPlugin> plugins_;
  
  protected ExoCache contentCache_ ; 
  
  public BaseContentService(CacheService cservice) throws Exception {
    contentCache_ = cservice.getCacheInstance(ContentDAO.class.getName());
    contentCache_.setLiveTime(300000);
    contentCache_.setMaxSize(10);
    
    plugins_ = new ArrayList<ContentPlugin>();
  }
  
  @SuppressWarnings("unused")
  public void initListener(ComponentPlugin listener) { }
  
  public void addPlugin(ComponentPlugin plugin){
    if(plugin instanceof ContentPlugin) {
      plugins_.add((ContentPlugin)plugin);      
    }
  }
  
  public List<String> getTypes(){
    List<String> types  = new ArrayList<String>();
    for(ContentPlugin plugin : plugins_){
      if(types.contains(plugin.getType())) continue;
      types.add(plugin.getType());
    }
    return types;
  }
  
  public <T extends ContentItem> PageList getContentData(ContentNode node) throws Exception {
    PageList pageList = (PageList)contentCache_.get(node.getId());
    if(pageList != null) return pageList;
    for(ContentPlugin  plugin : plugins_){      
      if(plugin.getType().equals(node.getType())) {        
        pageList = plugin.loadContentMeta(node);
        contentCache_.put(node.getId(), pageList);
        return pageList;
      }
    }
    return new PageList(0){
      public List<?> getAll(){ return null; }
      @SuppressWarnings("unused")
      public void populateCurrentPage(int page) throws Exception   {}
    };
  }
  
  public void removeCache(String id) throws Exception {
    contentCache_.remove(id);
  }
  
  
  public String toXML(Object object) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream() ;
    marshall(os , object) ;
    return new String(os.toByteArray()) ;
  }
  
  public Object fromXML(String xml, Class<?> type) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream( xml.getBytes()) ;    
    return unmarshall(is, type) ;
  }
  
  private Object unmarshall(InputStream is, Class<?> type) throws Exception {
    IBindingFactory bfact = BindingDirectory.getFactory(type);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return uctx.unmarshalDocument(is, null);
  }
  
  private void marshall(OutputStream os, Object obj) throws Exception {  
    IBindingFactory bfact = BindingDirectory.getFactory( obj.getClass());
    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);   
    mctx.marshalDocument(obj, "UTF-8", null,  os) ;
  }
  
}
