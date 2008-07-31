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
package org.exoplatform.webui.form;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;

/**
 * Created by The eXo Platform SARL
 * Author : Tung.Pham
 *          tung.pham@exoplatform.com
 * Aug 20, 2007  
 */
@ComponentConfig(template = "system:/groovy/webui/form/UIFormTableIteratorInputSet.gtmpl")

public class UIFormTableIteratorInputSet extends UIFormTableInputSet {
  
  UIFormPageIterator uiIterator_ ;

  public UIFormTableIteratorInputSet() throws Exception {
    uiIterator_ = createUIComponent(UIFormPageIterator.class, null, null) ;
    addChild(uiIterator_) ;
  }
  
  public UIFormPageIterator getUIFormPageIterator() { return uiIterator_ ; }
    
  @SuppressWarnings("unchecked")
  public UIComponent  findComponentById(String lookupId) {
    if(uiIterator_.getId().equals(lookupId))  return uiIterator_  ;
    return super.findComponentById(lookupId);
  }

  @SuppressWarnings("unchecked")
  public void processDecode(WebuiRequestContext context) throws Exception {
    List<UIComponent> children = uiIterator_.getCurrentPageData() ; 
    for(UIComponent child : children) {
      List<UIFormInputBase> inputs = new ArrayList<UIFormInputBase>() ; 
      child.findComponentOfType(inputs, UIFormInputBase.class) ;
      for(UIFormInputBase input :  inputs) {
        if(!input.isValid()) continue;
        String inputValue = context.getRequestParameter(input.getId()) ;
        if(inputValue == null || inputValue.trim().length() == 0){
          inputValue = context.getRequestParameter(input.getName()) ;
        }
        input.decode(inputValue, context);
      }
      child.processDecode(context) ;
    }
  }

}
