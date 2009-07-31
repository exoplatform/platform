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

import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UISearch;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Nguyen Viet Chung
 *          chung.nguyen@exoplatform.com
 * Jun 22, 2006
 * @version: $Id$
 * 
 * Represents a search form
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UISearchForm.gtmpl",
    events = @EventConfig(listeners = UISearchForm.QuickSearchActionListener.class)
)
public class UISearchForm extends UIForm {
  /**
   * The name of the quick search set
   */
  final static  public String QUICK_SEARCH_SET = "QuickSearchSet" ;
  /**
   * The name of the advanced search set
   */
  final static  public String ADVANCED_SEARCH_SET = "AdvancedSearchSet" ;

  public UISearchForm() throws Exception{
    UIFormInputSet uiQuickSearchSet = new UIFormInputSet(QUICK_SEARCH_SET) ;
    uiQuickSearchSet.addUIFormInput(new UIFormStringInput("searchTerm", null, null)) ;
    uiQuickSearchSet.addUIFormInput(new UIFormSelectBox("searchOption", null, null)) ;
    addChild(uiQuickSearchSet) ;
    UIFormInputSet uiAdvancedSearchSet = new UIFormInputSet(ADVANCED_SEARCH_SET) ;
    addChild(uiAdvancedSearchSet) ;
    uiAdvancedSearchSet.setRendered(false) ;
  }

  public void setOptions(List<SelectItemOption<String>> options) {		
    UIFormSelectBox uiSelect = (UIFormSelectBox)getQuickSearchInputSet().getChild(1) ;
    uiSelect.setOptions(options) ;
  }

  public UIFormInputSet getQuickSearchInputSet() { return (UIFormInputSet) getChild(0) ;	}

  public UIFormInputSet getAdvancedSearchInputSet() { return (UIFormInputSet) getChild(1) ;	}

  public void addAdvancedSearchInput(UIFormInput input) {
    getAdvancedSearchInputSet().addUIFormInput(input) ;
  }

  static  public class QuickSearchActionListener extends EventListener<UISearchForm> {
    public void execute(Event<UISearchForm> event) throws Exception {
      UISearchForm uiForm = event.getSource() ;
      UISearch uiSearch = uiForm.getParent() ;
      uiSearch.quickSearch(uiForm.getQuickSearchInputSet()) ;
      for (UIComponent child : uiSearch.getChildren()) {
        if (child.isRendered())
          event.getRequestContext().addUIComponentToUpdateByAjax(child);
      }
    }
  }

  static  public class ShowAdvancedSearchActionListener extends EventListener<UISearchForm> {
    public void execute(Event<UISearchForm> event) throws Exception {
      UISearchForm uiForm = event.getSource() ;
      UISearch uiSearch = uiForm.getParent() ;    	
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSearch) ;
    }
  }

  static  public class CancelAdvancedSearchActionListener extends EventListener<UISearchForm> {
    public void execute(Event<UISearchForm> event) throws Exception {
      UISearchForm uiForm = event.getSource() ;
      UISearch uiSearch = uiForm.getParent() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSearch) ;
    }
  }

}