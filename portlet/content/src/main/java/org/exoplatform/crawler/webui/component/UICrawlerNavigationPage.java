/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.crawler.webui.component ;
import org.exoplatform.webui.config.annotation.ComponentConfig ;
import org.exoplatform.webui.core.UIContainer;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * Jul 26, 2006  
 */
@ComponentConfig(
  template = "app:/groovy/crawler/webui/component/UICrawlerNavigationPage.gtmpl"
)
public class UICrawlerNavigationPage extends UIContainer {
  
  public UICrawlerNavigationPage() throws Exception {
    addChild(UICrawlerLayoutControl.class, null, null) ;
  }
}
