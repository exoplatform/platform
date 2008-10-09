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
package org.exoplatform.portal.webui.page;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIFormInputItemSelector;
/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Viet Chung
 *          nguyenchung136@yahoo.com
 * Aug 10, 2006  
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/page/UIPageTemplateOptions.gtmpl",
    initParams = @ParamConfig(
        name = "PageLayout",
        value = "system:/WEB-INF/conf/uiconf/portal/webui/page/PageConfigOptions.groovy"
    )
)
public class UIPageTemplateOptions extends UIFormInputItemSelector {
  
  private SelectItemOption<?> selectedItemOption_  = null;

  @SuppressWarnings("unchecked")
  public UIPageTemplateOptions(InitParams initParams) throws Exception {
    super("UIPageTemplateOptions", null) ;
    if(initParams == null) return ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("PageLayout");          
    categories_ = param.getFreshObject(context) ;   
    selectedItemOption_ = getDefaultItemOption() ;
    List<SelectItemOption<String>> itemOptions = new ArrayList<SelectItemOption<String>>();
    
    for(SelectItemCategory itemCategory: categories_){
      itemOptions.add(new SelectItemOption(itemCategory.getName()));
    }
    
    // modify: Dang.Tung
    UIDropDownControl uiItemSelector = addChild(UIDropDownControl.class, null, "UIDropDownPageTemp");
    uiItemSelector.setOptions(itemOptions);
    uiItemSelector.setAction("eXo.webui.UIItemSelector.selectPageLayout");
    // end modify
  }
  
  public SelectItemOption<?> getDefaultItemOption() {
    SelectItemCategory category = getSelectedCategory();
    if(category == null) return null ;
    SelectItemOption<?> itemOption = category.getSelectedItemOption();
    if(itemOption == null) return  null ;
    return itemOption;    
  }
  
  public void setSelectOptionItem(String value) {
    for(SelectItemCategory itemCategory : categories_){      
      for(SelectItemOption<?> itemOption : itemCategory.getSelectItemOptions()){
        if(itemOption.getLabel().equals(value)){
          selectedItemOption_ = itemOption;
          for(SelectItemOption<?> item : itemCategory.getSelectItemOptions()){
            item.setSelected(false);
          }
          itemOption.setSelected(true);
          return;
        }
      }
    }
    selectedItemOption_ = null;
  }
  
  public SelectItemOption getSelectedItemOption() { return selectedItemOption_; }
  
  public void decode(Object input, WebuiRequestContext context) throws Exception {  
    if(input == null || String.valueOf(input).length() < 1) return;    
    setSelectOptionItem((String)input) ;
  }
  
  public void setSelectedOption(SelectItemOption selectedItemOption){
    selectedItemOption_ = selectedItemOption;
  }
  
  public Page createPageFromSelectedOption(String ownerType, String ownerId) throws Exception {
    if(selectedItemOption_ == null) selectedItemOption_ = getDefaultItemOption() ;
    if(selectedItemOption_ == null) return null ;
    Object temp = selectedItemOption_.getValue() ; 
    if(temp == null) return null;
    UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class) ;
    return configService.createPageTemplate(temp.toString(), ownerType, ownerId) ; 
  }
}