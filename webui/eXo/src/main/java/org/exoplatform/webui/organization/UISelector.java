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
package org.exoplatform.webui.organization;

import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputContainer;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 19, 2006  
 */
abstract class UISelector<T> extends UIFormInputContainer<T> {

	protected UISelector() {
	}

	public UISelector(String name, String bindingField) {
		super(name, bindingField);
	}

	abstract void setMembership(String groupId, String membershipType)
			throws Exception;

	static public class SelectMembershipActionListener extends
			EventListener<UIGroupMembershipSelector> {
		public void execute(Event<UIGroupMembershipSelector> event)
				throws Exception {
			UIGroupMembershipSelector uiMemebershipSelector = event.getSource();
			UISelector uiSelector = uiMemebershipSelector.<UIComponent> getParent()
					.getParent();
			String membershipType = event.getRequestContext().getRequestParameter(
					OBJECTID);
			uiSelector.setMembership(uiMemebershipSelector.getCurrentGroup().getId(),
					membershipType);
			UIForm uiForm = uiSelector.getAncestorOfType(UIForm.class);
			if (uiForm != null) {
				//event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
				uiForm.broadcast(event, event.getExecutionPhase());
			}
		}
	}

}
