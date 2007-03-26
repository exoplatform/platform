/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.InputStream;

import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.lifecycle.Lifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Date: Aug 11, 2003
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: UIBasicComponent.java,v 1.10 2004/09/26 02:25:46 tuan08 Exp $
 */
@ComponentConfig(  
  lifecycle = Lifecycle.class,
  template = "system:/groovy/webui/component/UIQuickHelp.gtmpl" 
)
public class UIQuickHelp extends UIContainer {
  
//  private static SimpleExoCache cache_ = new SimpleExoCache(200);
  
  private String helpUri_ ;
  
  public UIQuickHelp() throws Exception {
    
  }
  
  public String getHelpUri() { return helpUri_ ; }
  public void   setHelpUri(String id) { helpUri_ = id ;} ;
  
  public String getHelpContent() throws Exception {
    if(helpUri_ == null)  return "No Help Configuration" ;
    String helpContent =  helpUri_;
    //if(helpContent == null) {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
      ResourceResolver resolver = context.getResourceResolver(helpUri_) ;
      InputStream is = resolver.getInputStream(helpUri_) ;
      helpContent =  new String( IOUtil.getStreamContentAsBytes(is)) ;
      is.close() ;
    //  cache_.put(helpUri_, helpContent) ;
   // }
    return helpContent ;
  }
}