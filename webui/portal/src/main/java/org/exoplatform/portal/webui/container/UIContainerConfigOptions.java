/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.container;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDropDownItemSelector;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
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
    template = "system:/groovy/portal/webui/container/UIContainerConfigOptions.gtmpl" ,
    initParams = @ParamConfig(
        name = "ContainerConfigOption",
        value = "system:/WEB-INF/conf/uiconf/portal/webui/container/ContainerConfigOption.groovy"
    ),
    events = @EventConfig(listeners = UIContainerConfigOptions.ChangeOptionActionListener.class)
)
public class UIContainerConfigOptions extends UIContainer {

  private List<SelectItemCategory> categories_ ;  
  private SelectItemCategory selectedCategory_ ;

  @SuppressWarnings("unchecked")
  public UIContainerConfigOptions(InitParams initParams) throws Exception{
    setComponentConfig(UIContainerConfigOptions.class, null);    
    selectedCategory_ = null;
    UIDropDownItemSelector uiDropCategories = addChild(UIDropDownItemSelector.class, null, null);
    uiDropCategories.setTitle("ContainerCategory");
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    uiDropCategories.setOptions(options);
    uiDropCategories.setOnServer(true);
    uiDropCategories.setOnChange("ChangeOption");
    if(initParams == null) return ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("ContainerConfigOption");          
    categories_ = param.getMapGroovyObject(context) ;
    if(categories_ == null) return ;
    if(selectedCategory_ == null) setCategorySelected(categories_.get(0)) ;
    for(SelectItemCategory itemCategory: categories_) {
      options.add(new SelectItemOption<String>(itemCategory.getName()));
    }
  } 

  public void setCategorySelected(SelectItemCategory selectedCategory) {
    selectedCategory_ = selectedCategory ;
  }  
  
  public void setCategorySelected(String name) {
    for(SelectItemCategory itemCategory: categories_) {
      if(itemCategory.getName().equals(name)){
        selectedCategory_ = itemCategory;
        return;
      }
    }
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
  
  public void processRender(WebuiRequestContext context) throws Exception {   
    super.processRender(context);    
    Util.showComponentLayoutMode(org.exoplatform.portal.webui.container.UIContainer.class);
//    context.addJavascript("eXo.webui.UIContainerConfigOptions.init();"); 
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIContainerConfigOptions> {
    public void execute(Event<UIContainerConfigOptions> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIContainerConfigOptions uiContainerOptions = event.getSource();
      UIDropDownItemSelector uiDropDownItemSelector = uiContainerOptions.getChild(UIDropDownItemSelector.class);
      SelectItemOption<String> option = uiDropDownItemSelector.getOption(selectedContainerId);
      if(option != null) uiDropDownItemSelector.setSelectedItem(option);
      uiContainerOptions.setCategorySelected(selectedContainerId);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiContainerOptions.getParent());
    }
  }
}
