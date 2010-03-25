/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ks.test.mock;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public class MockServletContext implements ServletContext {

  String servletContextName;
  
  public MockServletContext(String servletContextName) {
    this.servletContextName = servletContextName;
  }
  
  
  public Object getAttribute(String name) {

    return null;
  }

  public Enumeration getAttributeNames() {

    return null;
  }

  public ServletContext getContext(String uripath) {

    return null;
  }

  public String getInitParameter(String name) {

    return null;
  }

  public Enumeration getInitParameterNames() {

    return null;
  }

  public int getMajorVersion() {

    return 0;
  }

  public String getMimeType(String file) {

    return null;
  }

  public int getMinorVersion() {

    return 0;
  }

  public RequestDispatcher getNamedDispatcher(String name) {

    return null;
  }

  public String getRealPath(String path) {

    return null;
  }

  public RequestDispatcher getRequestDispatcher(String path) {

    return null;
  }

  public URL getResource(String path) throws MalformedURLException {

    return null;
  }

  public InputStream getResourceAsStream(String path) {

    return null;
  }

  public Set getResourcePaths(String path) {

    return null;
  }

  public String getServerInfo() {

    return null;
  }

  public Servlet getServlet(String name) throws ServletException {

    return null;
  }

  public String getServletContextName() {

    return servletContextName;
  }

  public Enumeration getServletNames() {

    return null;
  }

  public Enumeration getServlets() {

    return null;
  }

  public void log(String msg) {
  }

  public void log(Exception exception, String msg) {
  }

  public void log(String message, Throwable throwable) {
  }

  public void removeAttribute(String name) {
  }

  public void setAttribute(String name, Object object) {
  }


  public String getContextPath() {
    
    return null;
  }

}
