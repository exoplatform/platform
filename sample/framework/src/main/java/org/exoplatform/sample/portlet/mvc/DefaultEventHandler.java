/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.sample.portlet.mvc;

import org.exoplatform.web.framework.portlet.mvc.EventHandler;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 26, 2007  
 */
public class DefaultEventHandler extends EventHandler {
  public void onRender() throws Exception {
    setUseTemplate("app:/templates/test/test.jstmpl") ;
    super.onRender() ;
  }
}
