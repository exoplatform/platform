/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.portlet.PortletPreferences;
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
 * Feb 12, 2007  
 */
public abstract class BaseDataService {
  
  final public static String OWNER = "owner" ;
  final public static String DATA_TYPE = "dataType" ;
  
  final protected static String VIEW_PERMISSION = "viewPermission" ;
  final protected static String EDIT_PERMISSION = "editPermission";
  
  protected ExoCache portalConfigCache_ ;
  protected ExoCache pageConfigCache_ ;
  protected ExoCache pageNavigationCache_ ;
  
  public BaseDataService(){}
  
  public BaseDataService(CacheService cservice) throws Exception { 
    portalConfigCache_ = cservice.getCacheInstance(PortalConfig.class.getName()) ;
    pageConfigCache_ = cservice.getCacheInstance(Page.class.getName()) ;
    pageNavigationCache_ = cservice.getCacheInstance(PageNavigation.class.getName()) ;
  }
  
  public Data portalConfigToData(PortalConfig config) throws Exception  {    
    Data data = new Data();
    data.setDataType(PortalConfig.class.getName());
    data.setEditPermission(config.getEditPermission());
    data.setViewPermission(config.getViewPermission());
    data.setId(config.getOwner()+":/"+PortalConfig.class.getName());
    data.setOwner(config.getOwner());
    data.setData(toXML(config));
    return data;
  }
  
  public Data pageToData(Page page) throws Exception  {
    //id  ==  owner:/ page.getName()
    Data data = new Data();
    data.setDataType(Page.class.getName());
    data.setEditPermission(page.getEditPermission());
    data.setViewPermission(page.getViewPermission());
    data.setId(page.getPageId());
    data.setOwner(page.getOwner());
    data.setData(toXML(page));
    return data;
  }
  
  public Data pageNavigationToData(PageNavigation navigation) throws Exception {     
    Data data = new Data();
    data.setDataType(PageNavigation.class.getName());
    if(navigation.getNodes().size() > 0){
      data.setEditPermission(navigation.getEditPermission());
      data.setViewPermission(navigation.getAccessPermission());
    }
    data.setId(navigation.getOwner()+":/"+PageNavigation.class.getName());
    data.setOwner(navigation.getOwner());
    data.setData(toXML(navigation));
    return data;
  }
  
  public Data portletPreferencesConfigToData(PortletPreferences portletPreferences) throws Exception {
    Data data = new Data();
    data.setDataType(PortletPreferences.class.getName());    
    data.setId(portletPreferences.getWindowId());
    data.setOwner(portletPreferences.getCreator());
    data.setData(toXML(portletPreferences));
    return data;
  }
  
  public String toXML(Object object) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream() ;    
    marshall(os , object) ;
    return new String(os.toByteArray()) ;
  }
  
  public Object fromXML(String xml, Class type) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes()) ;    
    return unmarshall(is, type) ;
  }
  
  private Object unmarshall(InputStream is, Class type) throws Exception {
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
