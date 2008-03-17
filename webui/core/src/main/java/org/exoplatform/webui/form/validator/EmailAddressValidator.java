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
package org.exoplatform.webui.form.validator;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormInput;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 * 
 * Validates whether an email is in the correct format
 */
public class EmailAddressValidator implements Validator {
  
  static private final String EMAIL_REGEX = 
    "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-.]+";
  
  public void validate(UIFormInput uiInput) throws Exception {
	  if (uiInput.getValue()==null || ((String)uiInput.getValue()).trim().length()==0) return;
    String s = (String)uiInput.getValue();
    if(s.matches(EMAIL_REGEX)) return;
//    Object[]  args = { uiInput.getName(), uiInput.getBindingField() } ;
    throw new MessageException(new ApplicationMessage("EmailAddressValidator.msg.Invalid-input", null)) ;
  }
}