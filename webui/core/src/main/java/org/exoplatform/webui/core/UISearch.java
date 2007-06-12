/**
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.
 * Please look at license.txt in info directory for more license detail.
 **/
package org.exoplatform.webui.core;

import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UISearchForm;

/**
 * Author : Nguyen Viet Chung
 *          chung.nguyen@exoplatform.com
 * Jun 22, 2006
 * @version: $Id$
 */
@ComponentConfig()
abstract public class UISearch extends UIContainer {
	
	public UISearch(List<SelectItemOption<String>> searchOption) throws Exception{
		UISearchForm uiForm = addChild(UISearchForm.class, null, null) ;
		uiForm.setOptions(searchOption);
	}
  
	public UISearchForm getUISearchForm() { return (UISearchForm) getChild(0) ; }

	abstract public void quickSearch(UIFormInputSet quickSearchInput) throws Exception ;
	
	abstract public void advancedSearch(UIFormInputSet advancedSearchInput) throws Exception ;
}
