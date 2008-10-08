/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
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

  public TestJIBXXmlMapping(String name) {
    super(name);
  }

  public void setUp() throws Exception {

  }

  public void testPageSetMapping() throws Exception {
    IBindingFactory bfact = BindingDirectory.getFactory(PageSet.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/test/resources/PortalApp/portalone/pages.xml"), null);
    System.out.print(" === step 1 ===== > "+obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/pages.xml")) ;

    obj = uctx.unmarshalDocument(new FileInputStream("target/pages.xml"), null);
    System.out.print(" === step 2 ===== > "+obj) ;
  }

  public void testPortalConfigMapping() throws Exception {
    IBindingFactory bfact = BindingDirectory.getFactory(PortalConfig.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/test/resources/PortalApp/portalone/portal.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/portal.xml")) ;
  }

  public void testNavigationMapping() throws Exception {
    IBindingFactory bfact = BindingDirectory.getFactory(PageNavigation.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/test/resources/PortalApp/portalone/navigation.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/navigation.xml")) ;

    obj = uctx.unmarshalDocument(new FileInputStream("target/navigation.xml"), null);
    System.out.print(obj) ;
  }

  public void testPortletPreferencesMapping() throws Exception {
    IBindingFactory bfact = BindingDirectory.getFactory(PortalConfig.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(new FileInputStream("src/test/resources/PortalApp/portalone/portlet-preferences.xml"), null);
    System.out.print(obj) ;

    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null,  new FileOutputStream("target/portlet-preferences.xml")) ;
  }
}
