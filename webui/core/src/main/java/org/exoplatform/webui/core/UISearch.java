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
 * 
 * A container that holds a UISearchForm
 * @see UISearchForm
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
