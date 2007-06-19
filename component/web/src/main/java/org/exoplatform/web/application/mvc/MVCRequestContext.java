/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application.mvc;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.application.URLBuilder;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007
 */
public class MVCRequestContext extends RequestContext {
  public MVCRequestContext(Application app, RequestContext parent) {
    super(app) ;
    setParentAppRequestContext(parent) ;
  }
  
  public String getRequestParameter(String arg0) {
    return null ;
  }

  public String[] getRequestParameterValues(String arg0) {
    return null ;
  }

  public URLBuilder getURLBuilder() {
    return null ;
  }

  public boolean useAjax() {
    return false;
  }
}