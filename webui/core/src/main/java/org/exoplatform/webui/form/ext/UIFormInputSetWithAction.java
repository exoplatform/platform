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
package org.exoplatform.webui.form.ext;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputSet;

/**
 * Created by The eXo Platform SARL Author : Dang Van Minh
 * minh.dang@exoplatform.com Sep 20, 2006
 */
@ComponentConfig(
   template = "system:/groovy/webui/form/ext/UIFormInputSetWithAction.gtmpl"
)
public class UIFormInputSetWithAction extends UIFormInputSet implements UIFormInput {

  private String[] actions ;
  private String[] values ;
  private boolean isView ;
  private boolean isShowOnly = false ;
  private boolean isDeleteOnly = false ;
  private HashMap<String, String> info = new HashMap<String, String>() ;
  private HashMap<String, List<String>> listInfo = new HashMap<String, List<String>>() ;
  private HashMap<String, String[]> actionInfo = new HashMap<String, String[]>() ;
  private HashMap<String, String[]> fieldActions = new HashMap<String, String[]>() ;
  private boolean isShowActionInfo = false ;
  private HashMap<String, String> msgKeys = new HashMap<String, String>();
  
  /**
   * Instantiates a new uI form input set with action.
   * 
   * @param name the name
   */
  public UIFormInputSetWithAction(String name) {  
    setId(name) ;
    setComponentConfig(getClass(), null) ;  
  }
  
  /**
   * Checks if is show action info.
   * 
   * @return true, if is show action info
   */
  public boolean isShowActionInfo() {return isShowActionInfo ;}
  
  /**
   * Show action info.
   * 
   * @param isShow the is show
   */
  public void showActionInfo(boolean isShow) {isShowActionInfo = isShow ;}
  
  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInputSet#processRender(org.exoplatform.webui.application.WebuiRequestContext)
   */
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context) ;
  }

  /**
   * Sets the actions.
   * 
   * @param actionList the action list
   * @param values the values
   */
  public void setActions(String[] actionList, String[] values){ 
    actions = actionList ; 
    values = values ;    
  }
  
  /**
   * Gets the input set actions.
   * 
   * @return the input set actions
   */
  public String[] getInputSetActions() { return actions ; }
  
  /**
   * Gets the action values.
   * 
   * @return the action values
   */
  public String[] getActionValues() { return values ; }
  
  /**
   * Gets the form name.
   * 
   * @return the form name
   */
  public String getFormName() { 
    UIForm uiForm = getAncestorOfType(UIForm.class);
    return uiForm.getId() ; 
  }
  
  /**
   * Checks if is show only.
   * 
   * @return true, if is show only
   */
  public boolean isShowOnly() { return isShowOnly ; }
  
  /**
   * Sets the checks if is show only.
   * 
   * @param isShowOnly the new checks if is show only
   */
  public void setIsShowOnly(boolean isShowOnly) { isShowOnly = isShowOnly ; }

  /**
   * Checks if is delete only.
   * 
   * @return true, if is delete only
   */
  public boolean isDeleteOnly() { return isDeleteOnly ; }
  
  /**
   * Sets the checks if is delete only.
   * 
   * @param isDeleteOnly the new checks if is delete only
   */
  public void setIsDeleteOnly(boolean isDeleteOnly) { isDeleteOnly = isDeleteOnly ; }
  
  /**
   * Sets the list info field.
   * 
   * @param fieldName the field name
   * @param listInfor the list infor
   */
  public void setListInfoField(String fieldName, List<String> listInfor) {
    listInfo.put(fieldName, listInfor) ;
  }
  
  /**
   * Gets the list info field.
   * 
   * @param fieldName the field name
   * @return the list info field
   */
  public List<String> getListInfoField(String fieldName) {
    if(listInfo.containsKey(fieldName)) return listInfo.get(fieldName) ;
    return null ;
  }
  
  /**
   * Sets the info field.
   * 
   * @param fieldName the field name
   * @param fieldInfo the field info
   */
  public void setInfoField(String fieldName, String fieldInfo) {
    info.put(fieldName, fieldInfo) ;
  }
  
  /**
   * Gets the info field.
   * 
   * @param fieldName the field name
   * @return the info field
   */
  public String getInfoField(String fieldName) {
    if(info.containsKey(fieldName)) return info.get(fieldName) ;
    return null ;
  }
  
  /**
   * Sets the action info.
   * 
   * @param fieldName the field name
   * @param actionNames the action names
   */
  public void setActionInfo(String fieldName, String[] actionNames) {
    actionInfo.put(fieldName, actionNames) ;
  }
  
  /**
   * Gets the action info.
   * 
   * @param fieldName the field name
   * @return the action info
   */
  public String[] getActionInfo(String fieldName) {
    if(actionInfo.containsKey(fieldName)) return actionInfo.get(fieldName) ;
    return null ;
  }
  
  /**
   * Sets the field actions.
   * 
   * @param fieldName the field name
   * @param actionNames the action names
   */
  public void setFieldActions(String fieldName, String[] actionNames) {
    fieldActions.put(fieldName, actionNames) ;
  }
  
  /**
   * Gets the field actions.
   * 
   * @param fieldName the field name
   * @return the field actions
   */
  public String[] getFieldActions(String fieldName) {
   return fieldActions.get(fieldName) ;
  }
  
  /**
   * Sets the checks if is view.
   * 
   * @param isView the new checks if is view
   */
  public void setIsView(boolean isView) { isView = isView; }
  
  /**
   * Checks if is view.
   * 
   * @return true, if is view
   */
  public boolean isView() { return isView ; }

  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#getBindingField()
   */
  public String getBindingField() { return null; }

  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#getValidators()
   */
  public List getValidators() { return null; }
  
  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#addValidator(java.lang.Class, java.lang.Object[])
   */
  @SuppressWarnings("unused")
  public UIFormInput addValidator(Class clazz, Object...params) throws Exception { return this; }
  
  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#getValue()
   */
  public Object getValue() throws Exception { return null; }

  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#setValue(java.lang.Object)
   */
  @SuppressWarnings("unused")
  public UIFormInput setValue(Object value) throws Exception { return null; }

  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#getTypeValue()
   */
  public Class getTypeValue() { return null ; }
  
  /**
   * Sets the introduction.
   * 
   * @param fieldName the field name
   * @param msgKey the msg key
   */
  public void setIntroduction(String fieldName, String msgKey) { msgKeys.put(fieldName, msgKey) ; }
  
  /**
   * Gets the msg key.
   * 
   * @param fieldName the field name
   * @return the msg key
   */
  public String getMsgKey(String fieldName) { return msgKeys.get(fieldName) ; }
  
  /* (non-Javadoc)
   * @see org.exoplatform.webui.form.UIFormInput#getLabel()
   */
  public String getLabel() {
    return getId();
  }
  
  /**
   * Adds the validator.
   * 
   * @param validator the validator
   * @return the uI form input
   * @throws Exception the exception
   */
  @SuppressWarnings("unused")
  public UIFormInput addValidator(Class validator) throws Exception {
    return null;
  }
}
