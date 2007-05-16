/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.jcr.Node;

import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 20, 2007  
 */
public class DataMapper {
  
//-------------------------------- Portal Config ---------------------------------------------
  void map(Node node, PortalConfig config) throws Exception {    
    node.setProperty("id", config.getName()) ;
    node.setProperty("ownerType", "portal");
    node.setProperty("ownerId", "portalConfig");
    node.setProperty("name", config.getName()) ;    
    node.setProperty("editPermission", config.getEditPermission());
    node.setProperty("accessPermissions", config.getAccessPermission()) ;
    node.setProperty("dataType", config.getClass().getSimpleName()) ;    
    node.setProperty("data", toXML(config)) ;
  }
  
  PortalConfig toPortalConfig(Node node) throws Exception  {
    String xml = node.getProperty("data").getValue().getString() ;
    return fromXML(xml, PortalConfig.class) ;
  }
  
//------------------------------- Page ---------------------------------------------------------
  void map(Node node, Page page) throws Exception {
    node.setProperty("id", page.getPageId()) ;
    node.setProperty("ownerType", page.getOwnerType());
    node.setProperty("ownerId", page.getOwnerId());
    node.setProperty("name", page.getName()) ;
    node.setProperty("editPermission", page.getEditPermission());
    node.setProperty("accessPermissions", page.getAccessPermission()) ;
    node.setProperty("dataType", page.getClass().getSimpleName()) ;
    node.setProperty("data", toXML(page)) ;
  }
  
  Page toPage(Node node) throws Exception {
    String xml = node.getProperty("data").getValue().getString() ;
    return fromXML(xml, Page.class) ;
  }
  
//------------------------------ Page Navigation ----------------------------------------------  
  void map(Node node, PageNavigation navigation) throws Exception {
    node.setProperty("id", navigation.getId()) ;
    node.setProperty("ownerType", navigation.getOwnerType());
    node.setProperty("ownerId", navigation.getOwnerId());
    node.setProperty("name", navigation.getId()) ;
    node.setProperty("editPermission", navigation.getEditPermission());
    node.setProperty("accessPermissions", navigation.getAccessPermission()) ;
    node.setProperty("dataType", navigation.getClass().getSimpleName()) ;    
    node.setProperty("data", toXML(navigation)) ;
  }

  PageNavigation toPageNavigation(Node node) throws Exception {
    String  xml = node.getProperty("data").getValue().getString() ;
    return fromXML(xml, PageNavigation.class) ;
  }
  
//------------------------------ Portlet Preferences -----------------------------------------------
  
  void map(Node node, PortletPreferences portlet) throws Exception {
    node.setProperty("id", portlet.getWindowId()) ;
    node.setProperty("ownerType", "portal");
    node.setProperty("ownerId", portlet.getOwner());
    node.setProperty("name", portlet.getWindowId()) ;
    node.setProperty("dataType", "portletPreferences") ;    
    node.setProperty("data", toXML(portlet)) ;
  }
  
  public PortletPreferences toPortletPreferences(Node node) throws Exception {
    String  xml = node.getProperty("data").getValue().getString() ;
    return fromXML(xml, PortletPreferences.class) ;
  }
  
//------------------------------ Util method -----------------------------------------------  
  private String toXML(Object object) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream() ;
    IBindingFactory bfact = BindingDirectory.getFactory(object.getClass()) ;
    IMarshallingContext mctx = bfact.createMarshallingContext() ;
    mctx.setIndent(2);
    mctx.marshalDocument(object, "UTF-8", null, os) ;
    return new String(os.toByteArray(), "UTF-8");
  }
  
  <T> T fromXML(String xml, Class<T> clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes()) ;
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    return clazz.cast(uctx.unmarshalDocument(is, null));
  }
  
}
