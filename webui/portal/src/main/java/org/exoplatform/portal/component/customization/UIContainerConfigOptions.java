/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 31, 2006
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/customization/UIContainerConfigOptions.gtmpl" ,
    initParams = @ParamConfig(
        name = "ContainerConfigOption",
        value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/ContainerConfigOption.groovy"
    )
)
public class UIContainerConfigOptions extends UIContainer {

  private List<SelectItemCategory> categories_ ;  
  private SelectItemCategory selectedCategory_ ;

  @SuppressWarnings("unchecked")
  public UIContainerConfigOptions(InitParams initParams) throws Exception{
    setComponentConfig(UIContainerConfigOptions.class, null);    
    selectedCategory_ = null;
    if(initParams == null) return ;
    RequestContext context = RequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("ContainerConfigOption");          
    categories_ = param.getMapGroovyObject(context) ;
    if(categories_ == null) return ;
    if(selectedCategory_ == null) setCategorySelected(categories_.get(0)) ;        
  } 

  public void setCategorySelected(SelectItemCategory selectedCategory) {
    selectedCategory_ = selectedCategory ;
  }  
  public SelectItemCategory getCategorySelected() { return selectedCategory_ ; }

  public List<SelectItemCategory> getCategories() { return  categories_ ; }
  public void setCategories(List<SelectItemCategory> categories) { categories_ = categories ; }
 
  public Container getContainer(String id) throws Exception {
    for(SelectItemCategory category : categories_){
      List<SelectItemOption> items = category.getSelectItemOptions();
      for(SelectItemOption item : items){
        if(item.getLabel().equals(id)) return toContainer(item.getValue().toString());
      }      
    }
    return null;
  }
  
  private Container toContainer(String xml) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream( xml.getBytes()) ; 
    IBindingFactory bfact = BindingDirectory.getFactory(Container.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return (Container) uctx.unmarshalDocument(is, null);
  }
  
  public void processRender(RequestContext context) throws Exception {   
    super.processRender(context);    
    Util.showComponentLayoutMode(UIContainer.class);
//    context.addJavascript("eXo.webui.UIContainerConfigOptions.init();"); 
  }
}
