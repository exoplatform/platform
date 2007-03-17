  package org.exoplatform.portal.component.customization;

import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : lxchiati   
 *          lebienthuy@gmail.com
 * Jul 11, 2006  
 */
@ComponentConfig(
  template = "system:/groovy/portal/webui/component/customization/UIPageCreateDescription.gtmpl"   
)
public class UIPageCreateDescription extends UIContainer{
   
  private String title_ = "Page Creation Wizard";
  
  public UIPageCreateDescription() throws Exception{   
           
  } 
  
  public void setTitle(String title){
    title_ = title;
  }
  
  public String getTitle(){
    return title_;
  }
}