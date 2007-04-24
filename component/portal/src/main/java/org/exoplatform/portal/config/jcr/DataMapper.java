/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;

import javax.jcr.Node;

import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
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
  
  final public static String PORTAL_TYPE = "portal" ;
  final public static String PAGE_TYPE = "page" ;
  final public static String NAVIGATION_TYPE = "navigation" ;
  
//-------------------------------- Portal Config ---------------------------------------------
  void map(Node node, PortalConfig config) throws Exception {    
    node.setProperty("id", config.getOwner()) ;
    node.setProperty("owner", config.getOwner()) ;
    node.setProperty("dataType", PORTAL_TYPE) ;    
    node.setProperty("data", toXML(config)) ;
  }
  
  PortalConfig toPortalConfig(Node node) throws Exception  {
    String xml = node.getProperty("data").getValue().getString() ;
    return (PortalConfig)fromXML(xml, PortalConfig.class) ;
  }
  
//------------------------------- Page ---------------------------------------------------------
  void map(Node node, Page page) throws Exception {
    node.setProperty("id", page.getPageId()) ;
    node.setProperty("owner", page.getOwner()) ;
    node.setProperty("dataType", PAGE_TYPE) ;
    node.setProperty("data", toXML(page)) ;
  }
  
  Page toPage(Node node) throws Exception {
    String xml = node.getProperty("data").getValue().getString() ;
    return (Page)fromXML(xml, Page.class) ;
  }
  
//------------------------------ Page Navigation ----------------------------------------------  
  void map(Node node, PageNavigation navigation) throws Exception {
    node.setProperty("id", navigation.getOwner()) ;
    node.setProperty("owner", navigation.getOwner()) ;
    node.setProperty("createdDate", new GregorianCalendar()) ;
    node.setProperty("dataType", NAVIGATION_TYPE) ;    
    node.setProperty("data", toXML(navigation)) ;
  }

  PageNavigation toPageNavigation(Node node) throws Exception {
    String  xml = node.getProperty("data").getValue().getString() ;
    return (PageNavigation)fromXML(xml, PageNavigation.class) ;
  }
  
//------------------------------ Util method -----------------------------------------------  
  private String toXML(Object object) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream() ;
    
    IBindingFactory bfact = BindingDirectory.getFactory(object.getClass()) ;
    IMarshallingContext mctx = bfact.createMarshallingContext() ;
    mctx.setIndent(2);
    mctx.marshalDocument(object, "UTF-8", null, os) ;
 
    return new String(os.toByteArray());
  }
  
  private Object fromXML(String xml, Class clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes()) ;
    
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    
    return uctx.unmarshalDocument(is, null) ;
  }
  
}
