/**
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.
 * Please look at license.txt in info directory for more license detail.
 **/
package org.exoplatform.webui.form;

import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UISearch;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UISearchForm.*;

/**
 * Author : Nguyen Viet Chung
 *          chung.nguyen@exoplatform.com
 * Jun 22, 2006
 * @version: $Id$
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UISearchForm.gtmpl",
    events = @EventConfig(listeners = QuickSearchActionListener.class)
)
public class UISearchForm extends UIForm {

  final static  public String QUICK_SEARCH_SET = "QuickSearchSet" ;
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
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSearch) ;
    }
  }

//  static  public class AdvancedSearchActionListener extends EventListener<UISearchForm> {
//    public void execute(Event<UISearchForm> event) throws Exception {
//      UISearchForm uiForm = event.getSource() ;
//      UISearch uiSearch = uiForm.getParent() ;
//      uiSearch.advancedSearch(uiForm.getAdvancedSearchInputSet()) ;
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiSearch) ;
//    }
//  }

  @SuppressWarnings("unused")
  static  public class ShowAdvancedSearchActionListener extends EventListener<UISearchForm> {
    public void execute(Event<UISearchForm> event) throws Exception {
      UISearchForm uiForm = event.getSource() ;
      UISearch uiSearch = uiForm.getParent() ;    	
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSearch) ;
    }
  }

  @SuppressWarnings("unused")
  static  public class CancelAdvancedSearchActionListener extends EventListener<UISearchForm> {
    public void execute(Event<UISearchForm> event) throws Exception {
      UISearchForm uiForm = event.getSource() ;
      UISearch uiSearch = uiForm.getParent() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSearch) ;
    }
  }

}