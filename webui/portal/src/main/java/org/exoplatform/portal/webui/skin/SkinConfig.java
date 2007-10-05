/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.skin;

/**
 * Created by The eXo Platform SAS
 * Jan 19, 2007  
 */
public class SkinConfig {
  
  private String module_ ;
  private String skinName_ ;
  private String cssPath_ ;
  private String id_ ;
  private boolean isPrimary_ = false ;

  public SkinConfig(String module, String skinName, String cssPath) {
    this(module, skinName, cssPath, false) ;
  }

  public SkinConfig(String module, String skinName, String cssPath, boolean isPrimary) {
    module_ = module;
    skinName_ = skinName;
    cssPath_ = cssPath;
    id_  = module.replace('/', '_') ;
    isPrimary_ = isPrimary ;
  }
  
  public String getId() { return id_ ; }
  public String getModule(){ return module_; }
  public String getSkinName(){ return skinName_; }
  public String getCSSPath(){ return cssPath_; }
  public boolean isPrimary() { return isPrimary_; }

}