/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.skin;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Jan 19, 2007  
 */
public class SkinConfig {
  
  private String module_ ;
  private String skinName_ ;
  private String cssPath_ ;
  private String id_ ;

  public SkinConfig(String module, String skinName, String cssPath) {
    module_ = module;
    skinName_ = skinName;
    cssPath_ = cssPath;
    id_  = module.replace('/', '_') ;
  }
  
  public String getId() { return id_ ; }
  
  public String getModule(){ return module_; }

  public String getSkinName(){ return skinName_; }

  public String getCSSPath(){ return cssPath_; }

}