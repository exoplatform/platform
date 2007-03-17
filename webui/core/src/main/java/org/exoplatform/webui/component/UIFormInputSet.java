/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.Writer;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.model.BeanDataMapping;
import org.exoplatform.webui.component.model.ReflectionDataMapping;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 8, 2006
 */
public class UIFormInputSet extends  UIContainer {
  
  private BeanDataMapping  beanMapping = null;
  
  public UIFormInputSet() {}
  
	public UIFormInputSet(String name) {
    setId(name) ;
	}
	
	public UIFormInputSet addUIFormInput(UIFormInput input) {
		addChild((UIComponent)input) ;
		return this ;
	}
  
  public UIFormInputSet addUIFormInput(UIFormInputSet input) {
    addChild(input) ;
    return this ;
  }
	
	public String getName() {	return getId() ;	}
  
  @SuppressWarnings("unchecked")
  public <T extends UIFormInput> T getUIInput(String name) {
    return (T) findComponentById(name);
  }

  public UIFormStringInput getUIStringInput(String name) {
    return (UIFormStringInput) findComponentById(name) ;
  }
  
  public UIFormCheckBoxInput getUIFormCheckBoxInput(String name) {
    return (UIFormCheckBoxInput) findComponentById(name);
  }
  
  public UIFormSelectBox getUIFormSelectBox(String name) {
    return (UIFormSelectBox) findComponentById(name) ;
  }
  
  public UIFormInputInfo getUIFormInputInfo(String name) {
    return (UIFormInputInfo) findComponentById(name) ;
  }
  
  public UIFormTextAreaInput getUIFormTextAreaInput(String name) {
    return (UIFormTextAreaInput) findComponentById(name) ;
  }
  public void reset(){
    for(UIComponent uiChild : getChildren()){
      if(uiChild instanceof UIFormInput){
        ((UIFormInput)uiChild).reset();
      }
    }
  }
  
  public  void invokeGetBindingField(Object bean) throws Exception {
    if(beanMapping == null) beanMapping = new ReflectionDataMapping();
    beanMapping.mapField(this, bean);
  }
  
  public  void invokeSetBindingField(Object bean) throws Exception {
    if(beanMapping == null) beanMapping = new ReflectionDataMapping();
    beanMapping.mapBean(bean, this);
  }
  
	public void processDecode(RequestContext context) throws Exception {
		for(UIComponent child : getChildren()) 	{
      child.processDecode(context) ;
		}
	}
  
  public void processRender(RequestContext context) throws Exception {
    if(getComponentConfig() != null) {
      super.processRender(context) ;
      return ;
    }
    Writer w = context.getWriter() ;
    w.write("<div class=\"UIFormInputSet\">") ;

    ResourceBundle res = context.getApplicationResourceBundle() ;
    UIForm uiForm = getAncestorOfType(UIForm.class);
    for(UIComponent inputEntry :  getChildren()) {     
      String label = null ;
      try {
        label = uiForm.getLabel(res, inputEntry.getId());
      } catch(MissingResourceException ex){ 
        System.err.println("\n "+uiForm.getId()+".label." + inputEntry.getId()+" not found value");
      }
      
      w.write("<div class=\"FieldContainer\">") ;
      if(label.trim().length() > 0){
        w.write("<label>") ; w.write(label); w.write("</label>") ;
      }
      renderUIComponent(inputEntry) ;
      w.write("</div>") ;

     }
     w.write("</div>") ;
  }
  
}