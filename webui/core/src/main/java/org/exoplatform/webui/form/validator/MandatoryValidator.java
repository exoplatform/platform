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
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 * 
 * Validates whether a field is empty
 * This class acts like a flag "mandatory". When you want to specify that a UIFormInput field
 * is mandatory in your form, add it this validator. A '*' character will be automatically added
 * during the rendering phase to specify the user
 */
public class MandatoryValidator implements Validator {
  
  public void validate(UIFormInput uiInput) throws Exception {
    if((uiInput.getValue() != null) && ((String)uiInput.getValue()).trim().length() > 0) {
      return ;
    }
    
    //modified by Pham Dinh Tan
    UIComponent uiComponent = (UIComponent) uiInput ;
    UIForm uiForm = uiComponent.getAncestorOfType(UIForm.class) ;    
    String label = uiForm.getLabel(uiInput.getName());
    
    if(label == null) label = uiInput.getName();
    label = label.trim();
    if(label.charAt(label.length() - 1) == ':') label = label.substring(0, label.length() - 1);
    Object[]  args = {label, uiInput.getBindingField() } ;
    throw new MessageException(new ApplicationMessage("EmptyFieldValidator.msg.empty-input", args, 
                                                      ApplicationMessage.WARNING)) ;
  }
}
