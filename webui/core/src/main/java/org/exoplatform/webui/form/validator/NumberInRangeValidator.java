/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
 * Created by The eXo Platform SAS
 * Author : Pham Dinh Tan
 *          pdtanit@gmail.com
 * Apr 16, 2008  
 */
public class NumberInRangeValidator implements Validator {

  /**
   * min number of range
   */
  private Integer min_ = 0;
  
  /**
   * max number of range
   */
  private Integer max_ = 0;
  
  public NumberInRangeValidator(Integer min, Integer max) {
    this.max_ = max ;
    this.min_ = min;
  }
  
  public void validate(UIFormInput uiInput) throws Exception {
    if (uiInput.getValue()==null || ((String)uiInput.getValue()).trim().length()==0) return;
    //  modified by Pham Dinh Tan
    UIComponent uiComponent = (UIComponent) uiInput ;
    UIForm uiForm = uiComponent.getAncestorOfType(UIForm.class) ;    
    String label = uiForm.getLabel(uiInput.getName());
    if(label == null) label = uiInput.getName();
    label = label.trim();
    if(label.charAt(label.length() - 1) == ':') label = label.substring(0, label.length() - 1);
    String s = (String)uiInput.getValue();
    boolean error = false;
    for(int i = 0; i < s.length(); i ++){
      char c = s.charAt(i);
      if (Character.isDigit(c) || (s.charAt(0) == '-' && i == 0)){
        error = true;
        continue;  
      }
      error = false;
      Object[] args = { label, uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("NumberFormatValidator.msg.Invalid-number", args,ApplicationMessage.WARNING)) ;
    }
    if(error){
      int num = Integer.parseInt(s);      
      if(min_>num || max_<num){
        Object[] args = {label, min_.toString(), max_.toString()};
        throw new MessageException(new ApplicationMessage("NumberInRangeValidator.msg.Invalid-number", args, ApplicationMessage.WARNING));
      }
    }
  }
}
