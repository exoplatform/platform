/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
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
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDropDownControl;
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
@ComponentConfigs ({
  @ComponentConfig(
      template = "system:/groovy/portal/webui/container/UIContainerConfigOptions.gtmpl" ,
      initParams = @ParamConfig(
          name = "ContainerConfigOption",
          value = "system:/WEB-INF/conf/uiconf/portal/webui/container/ContainerConfigOption.groovy"
      )
  ),
  
  @ComponentConfig (
      type = UIDropDownControl.class ,
      id = "UIDropDownConfigs",
      template = "system:/groovy/webui/core/UIDropDownControl.gtmpl",
      events = {
          @EventConfig(listeners = UIContainerConfigOptions.ChangeOptionActionListener.class)
        }
    )
})


public class UIContainerConfigOptions extends UIContainer {

  private List<SelectItemCategory> categories_ ;  
  private SelectItemCategory selectedCategory_ ;

  @SuppressWarnings("unchecked")
  public UIContainerConfigOptions(InitParams initParams) throws Exception{
    setComponentConfig(UIContainerConfigOptions.class, null);    
    selectedCategory_ = null;
    UIDropDownControl uiDropCategories = addChild(UIDropDownControl.class, "UIDropDownConfigs", "UIDropDownConfigs");
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    uiDropCategories.setOptions(options);
    uiDropCategories.setParent(this);
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
  
  static  public class ChangeOptionActionListener extends EventListener<UIDropDownControl> {
    public void execute(Event<UIDropDownControl> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIDropDownControl uiDropDown = event.getSource();
      UIContainerConfigOptions uiContainerOptions = uiDropDown.getParent();
      uiDropDown.setValue(selectedContainerId);
      uiContainerOptions.setCategorySelected(selectedContainerId);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiContainerOptions.getParent());
    }
  }
}
