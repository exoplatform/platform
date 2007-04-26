/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.eXoFramework;

import java.io.Writer;
import java.util.Locale;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.application.URLBuilder;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007
 */
public class ExoFrameworkRequestContext extends RequestContext {

  public ExoFrameworkRequestContext(Application app, RequestContext parent) {
    super(app) ;
    setParentAppRequestContext(parent) ;
  }
  
  public Locale getLocale() {
    return null;
  }

  public String getRemoteUser() {
    return null;
  }

  public String getRequestParameter(String arg0) {
    return null;
  }

  public String[] getRequestParameterValues(String arg0) {
    return null;
  }

  public URLBuilder getURLBuilder() {
    return null;
  }

  public Writer getWriter() throws Exception {
    return null;
  }

  public boolean isUserInRole(String arg0) {
    return false;
  }

  public boolean useAjax() {
    return false;
  }
}