/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources;

import java.io.Serializable;
/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: May 14, 2004
 * Time: 1:12:22 PM
 */
@SuppressWarnings("serial")
public class ResourceBundleData extends ResourceBundleDescription implements Serializable {
  
  private String data_ ;

  public ResourceBundleData() {
    setResourceType("-") ;
    setLanguage(DEFAULT_LANGUAGE) ;
  }

  /**
   * @hibernate.property length="65535" type="org.exoplatform.services.database.impl.TextClobType"
   **/
  public String   getData() { return data_ ; }
  public void     setData(String data) { data_ = data; }
}