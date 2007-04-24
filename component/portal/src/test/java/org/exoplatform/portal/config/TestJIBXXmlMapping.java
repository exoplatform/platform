/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.test.BasicTestCase;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Thu, May 15, 2003 @   
 * @author: Tuan Nguyen
 * @version: $Id: TestConverter.java,v 1.6 2004/07/20 12:41:09 tuan08 Exp $
 * @since: 0.0
 * @email: tuan08@yahoo.com
 */
public class TestJIBXXmlMapping  extends BasicTestCase {

  private boolean ignoreTest = true ; 
  public TestJIBXXmlMapping(String name) {
    super(name);
  }

  public void setUp() throws Exception {

  }

  public void testPageSetMapping() throws Exception {
    if(ignoreTest) return ;
    IBindingFactory bfact = BindingDirectory.getFactory(PageSet.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/main/resources/pages.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/pages.xml")) ;

    obj = uctx.unmarshalDocument(new FileInputStream("target/pages.xml"), null);
    System.out.print(obj) ;
  }

  public void testPortalConfigMapping() throws Exception {
    if(ignoreTest) return ;
    IBindingFactory bfact = BindingDirectory.getFactory(PortalConfig.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/main/resources/config.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/config.xml")) ;
  }

  public void testNavigationMapping() throws Exception {
    if(ignoreTest) return ;
    IBindingFactory bfact = BindingDirectory.getFactory(PageNavigation.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/main/resources/navigation.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/navigation.xml")) ;

    obj = uctx.unmarshalDocument(new FileInputStream("target/navigation.xml"), null);
    System.out.print(obj) ;
  }

  public void testPortletPreferencesMapping() throws Exception {
    if(ignoreTest) return ;
    IBindingFactory bfact = BindingDirectory.getFactory(PortalConfig.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/main/resources/portlet-preferences.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/portlet-preferences.xml")) ;
  }  
}
