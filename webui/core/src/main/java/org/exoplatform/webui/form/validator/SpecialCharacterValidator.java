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
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Dec 12, 2007  
 */
public class SpecialCharacterValidator implements Validator {
  
  public void validate(UIFormInput uiInput) throws Exception {
    String s = (String)uiInput.getValue();
    if(Character.isDigit(s.charAt(0))) {
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("SpecialCharacterValidator.msg.invalid-digit", args, ApplicationMessage.WARNING)) ;
    }
    for(int i=0;i<s.length();i++) {
      if(isValidChar(s.toCharArray(), i)) {
        Object[] args = { uiInput.getName(), uiInput.getBindingField() };
        throw new MessageException(new ApplicationMessage("SpecialCharacterValidator.msg.invalid-input", args, ApplicationMessage.WARNING)) ;
      }
    }
  }
  private boolean isValidChar(char[] a, int index) {
    int codeValue = Character.codePointAt(a, index) ;
    if(((32<=codeValue)&&(codeValue<=45))||(codeValue==47)) return false;
    if((58<=codeValue)&&(codeValue<=64)) return false;
    if((91<=codeValue)&&(codeValue<=96)) return false;
    if((123<=codeValue)&&(codeValue<=126)) return false;  
    return true;
  }
}
