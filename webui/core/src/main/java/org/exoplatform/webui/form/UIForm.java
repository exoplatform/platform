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
package org.exoplatform.webui.form;

import java.io.Writer;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.bean.BeanDataMapping;
import org.exoplatform.webui.bean.ReflectionDataMapping;
import org.exoplatform.webui.config.Event;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 6, 2006
 */
public class UIForm extends UIContainer  {
  
  public final static String ACTION = "formOp" ;
  
  public final static String SUBCOMPONENT_ID= "subComponentId";
  
//  private List<Validator>  validators ;
  
  private String[] actions_ = null ;
  private String submitAction_ ;
  private boolean multipart_ =  false ;
  
  private BeanDataMapping  beanMapping = null;
  
  public UIForm addUIFormInput(UIFormInput  input) {
    addChild((UIComponent)input) ;
    return this ;
  }
  
  public UIForm addUIFormInput(UIFormInputSet input) {
    addChild(input) ;
    return this ;
  }
  
  public UIForm addUIComponentInput(UIComponent  input) {
    addChild(input) ;
    return this ;
  }
  
//  public UIForm addValidator(Class clazz) throws Exception {
//    if(validators == null)  validators = new ArrayList<Validator>(3) ;
//    validators.add((Validator)clazz.newInstance()) ;
//    return this ;
//  }
  
  public boolean isMultipart() { return multipart_ ; }
  public void    setMultiPart(boolean b) { multipart_ = b ; }
  
  public String getSubmitAction() { return submitAction_ ; }
  public void   setSubmitAction(String s) { submitAction_  = s; }  
  
//  public List<Validator>  getValidators() { return validators ; }
   
  @SuppressWarnings("unchecked")
  public <T extends UIFormInput> T getUIInput(String name) {
    return (T) findComponentById(name);
  }
  
  public UIFormStringInput getUIStringInput(String name) {
    return findComponentById(name) ;
  }
  
  public UIFormCheckBoxInput getUIFormCheckBoxInput(String name) {
  	return  findComponentById(name);
  }
  
  public UIFormSelectBox getUIFormSelectBox(String name) {
  	return  findComponentById(name) ;
  }
  
  public UIFormInputInfo getUIFormInputInfo(String name) {
    return  findComponentById(name) ;
  }
  
  public UIFormTextAreaInput getUIFormTextAreaInput(String name) {
    return  findComponentById(name) ;
  }
  
  public UIFormDateTimeInput getUIFormDateTimeInput(String name) {
    return  findComponentById(name) ;
  }
  
  public void reset(){
    for(UIComponent uiChild : getChildren()){
      if(uiChild instanceof UIFormInput){
        ((UIFormInput)uiChild).reset();
      }
    }
  }
 
  public  void invokeGetBindingBean(Object bean) throws Exception {
    if(beanMapping == null) beanMapping = new ReflectionDataMapping();
    beanMapping.mapField(this, bean);
  }
  
  public  void invokeSetBindingBean(Object bean) throws Exception {
    if(beanMapping == null) beanMapping = new ReflectionDataMapping();
    beanMapping.mapBean(bean, this);    
  }
  
  @SuppressWarnings("unchecked")
  public void begin() throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    String b = context.getURLBuilder().createURL(this, null, null) ;   
    
    Writer writer = context.getWriter() ;
    writer.
      append("<form class=\"UIForm\" name=\"").append(getId()).
      append("\" id=\"").append(getId()).append("\" action=\"").
      append(b).append('\"') ;
      if(submitAction_ != null) writer.append(" onsubmit=\"").append(submitAction_).append("\"");
    if(multipart_) {
      writer.append(" enctype=\"multipart/form-data\"") ;
    }
    writer.append(" method=\"post\">");
    writer.append("<input type=\"hidden\" name=\"").append(ACTION).append("\" value=\"\"/>") ;
  }

  @Override
  public void processRender(WebuiRequestContext context) throws Exception {
    if(getTemplate() != null) {
      super.processRender(context) ;
      return ;
    }
    Writer writer = context.getWriter();
    writer.append("<div class=='UIForm ").append(getId()).append("'>") ;
    begin() ;
    renderChildren(context) ;
    end() ;
    writer.append("</div>") ;
  }
  
  public void end() throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    context.getWriter().write("</form>") ;
  }
  
  public void setActions(String [] actions){ actions_ = actions; }
  public String[] getActions() {
    if(actions_ != null) return actions_;
    ArrayList<Event> events = config.getEvents();
    actions_ = new String[events.size()];    
    for(int i = 0; i < actions_.length; i++){
      actions_[i] = events.get(i).getName();
    }
    return actions_;  
  }
  
  public void renderField(String name) throws Exception {
    UIComponent uiInput = findComponentById(name);
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    uiInput.processRender(context) ;
  }
  
  public void renderField(UIComponent uiInput) throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    uiInput.processRender(context) ;
  }
  
  private String getFormId() {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    if(context instanceof PortletRequestContext) {
      return ((PortletRequestContext)context).getWindowId() + "#" + getId() ; 
    }
    return getId() ;
  }
  
  public String url(String name) throws Exception {
    StringBuilder b = new StringBuilder() ;
    b.append("javascript:eXo.webui.UIForm.submitForm('").append(getFormId()).append("','");
    b.append(name).append("',false)");
    return b.toString() ;
  }
  
  public String event(String name) throws Exception {

    StringBuilder b = new StringBuilder() ;
    b.append("javascript:eXo.webui.UIForm.submitForm('").append(getFormId()).append("','");
    b.append(name).append("',true)");
    return b.toString() ;
  }
  
  public String event(String name, String beanId) throws Exception {
    StringBuilder b = new StringBuilder() ;
    b.append("javascript:eXo.webui.UIForm.submitEvent('").append(getFormId()).append("','");
    b.append(name).append("','");
    b.append("&amp;").append(OBJECTID).append("=").append(beanId).append("')");    
    return b.toString() ;
  } 
  
  public String event(String name, String componentId, String beanId) throws Exception {   
    StringBuilder b = new StringBuilder() ;
    //TODO: Tung.Pham modified
    //------------------------
    b.append("javascript:") ;
    WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance() ;
    UIComponent subComponent = findComponentById(componentId) ;
    org.exoplatform.webui.config.Event event = subComponent.getComponentConfig().getUIComponentEventConfig(name) ;
    if(event == null) return "??config??" ;
    String confirm = event.getConfirm() ;
    if(confirm != null && confirm.trim().length() > 0) {
      confirm = rcontext.getApplicationResourceBundle().getString(confirm) ;
      b.append("if(confirm('").append(confirm.replaceAll("'", "\\\\'")).append("'))") ;
    }
    b.append("eXo.webui.UIForm.submitEvent('").append(getFormId()).append("','");
    //b.append("javascript:eXo.webui.UIForm.submitEvent('").append(getId()).append("','");
    //-------------------------
    
    b.append(name).append("','");
    b.append("&amp;").append(SUBCOMPONENT_ID).append("=").append(componentId);
    if(beanId != null) b.append("&amp;").append(OBJECTID).append("=").append(beanId);    
    b.append("')");
    return b.toString() ;
  }
  
  public String getLabel(String id) throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    ResourceBundle res = context.getApplicationResourceBundle() ;     
    return getLabel(res, id);
  }

  public String getLabel(ResourceBundle res, String id) throws Exception {
    String label = getId() + ".label." + id;
    try {
      return res.getString(label);      
    } catch (MissingResourceException e) {
      return id ;
    }
  }
  
  public String getUIComponentName() { return "uiform"; }
}