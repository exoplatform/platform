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
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Oct 10, 2007
 * 
 * Validates whether this value matches one regular expression.
 */
public class ExpressionValidator implements Validator {
  private String expression_;
  private String key_;
  
  public ExpressionValidator(final String expression) {
    expression_ = expression;
    key_ = "ExpressionValidator.msg.value-invalid";
  }
  
  public ExpressionValidator(final String exp, final String key) {
    expression_ = exp; 
    key_ = key;
  }
  
  public void validate(final UIFormInput uiInput) throws Exception {
	  if (uiInput.getValue() == null || ((String) uiInput.getValue()).trim().length() == 0) { return; }
    if (uiInput.getValue() != null) {
      String value = ((String) uiInput.getValue()).trim();
      if (value.matches(expression_)) { return; }
    }    
    
    //  modified by Pham Dinh Tan
    UIComponent uiComponent = (UIComponent) uiInput;
    UIForm uiForm = uiComponent.getAncestorOfType(UIForm.class);    
    String label;
    try {
      label = uiForm.getLabel(uiInput.getName());
    } catch (Exception e) {
      label = uiInput.getName();
    }
    label = label.trim();
    if (label.charAt(label.length() - 1) == ':') { label = label.substring(0, label.length() - 1); }
    Object[] args = {label, };
    throw new MessageException(new ApplicationMessage(key_, args, ApplicationMessage.WARNING));
  }
}
