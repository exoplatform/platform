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
package org.exoplatform.webui.core.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.UIFormInputContainer;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.validator.Validator;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 1, 2006
 */
public class UIFormLifecycle  extends Lifecycle {

  public void processDecode(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    UIForm uiForm = (UIForm) uicomponent ;
//    HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest() ;
    uiForm.setSubmitAction(null) ;
//    if(ServletFileUpload.isMultipartContent(new ServletRequestContext(httpRequest))) {
//      processMultipartRequest(uiForm, context) ;
//    } else {
    processNormalRequest(uiForm, context) ;
//    }
    List<UIComponent>  children =  uiForm.getChildren() ;
    for(UIComponent uiChild :  children) {
      uiChild.processDecode(context) ;     
    }
    String action =  uiForm.getSubmitAction();
    String subComponentId = context.getRequestParameter(UIForm.SUBCOMPONENT_ID);
    if(subComponentId == null || subComponentId.trim().length() < 1) {
      Event<UIComponent> event = uiForm.createEvent(action, Event.Phase.DECODE, context) ;
      if(event != null) event.broadcast() ;
      return;
    }
    UIComponent uiSubComponent = uiForm.findComponentById(subComponentId);
    Event<UIComponent> event = uiSubComponent.createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null)  event.broadcast() ;      
  }

  public void processAction(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    UIForm uiForm = (UIForm) uicomponent ;
    String action =  context.getRequestParameter(UIForm.ACTION);
    if(action == null) action = uiForm.getSubmitAction();
    if(action == null) return ;    
    Event<UIComponent> event = uicomponent.createEvent(action, Event.Phase.PROCESS, context) ;
    if(event == null) {
      event = uicomponent.<UIComponent>getParent().createEvent(action, Event.Phase.PROCESS, context) ;
    }
    if(event == null) return;
    UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
    List<UIComponent>  children = uiForm.getChildren() ;
    validateChildren(children, uiApp, context);
    
    /*List<Validator> validators = uiForm.getValidators() ;
    if(validators != null) {
      try {
        for(Validator validator : validators) validator.validate(uiForm) ;
      } catch (MessageException ex) {
        uiApp.addMessage(ex.getDetailMessage()) ;
        context.setProcessRender(true) ;
      } catch(Exception ex) {
        //TODO:  This is a  critical exception and should be handle  in the UIApplication
        uiApp.addMessage(new ApplicationMessage(ex.toString(), null)) ;        
        context.setProcessRender(true) ;
      }
    }*/
    
    if(context.getProcessRender()) {
      context.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
      return ;     
    }
    event.broadcast()  ;
  }
  
  private void processNormalRequest(UIForm uiForm, WebuiRequestContext context) throws Exception {
    List<UIFormInputBase> inputs = new ArrayList<UIFormInputBase>() ;
    uiForm.findComponentOfType(inputs, UIFormInputBase.class) ;
    uiForm.setSubmitAction(context.getRequestParameter(UIForm.ACTION)) ;
    for(UIFormInputBase input :  inputs) {
      if(!input.isValid()) continue;
      String inputValue = context.getRequestParameter(input.getId()) ;
      if(inputValue == null || inputValue.trim().length() == 0){
        inputValue = context.getRequestParameter(input.getName()) ;
      }
      input.decode(inputValue, context);
    }
  }
  
  /*private void processMultipartRequest(UIForm uiForm, RequestContext context) throws Exception {
    HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest() ;
    ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
    List items = upload.parseRequest(httpRequest);
    Iterator iter = items.iterator();
    while (iter.hasNext()) {
      FileItem item = (FileItem) iter.next();
      String fieldName = item.getFieldName();      
      if (item.isFormField()) {  //Normal  inputs
        String inputValue = item.getString() ;
        if (UIForm.ACTION.equals(fieldName)) {
          uiForm.setSubmitAction(inputValue) ;
          continue;
        } else if(UIFormTabPane.RENDER_TAB.equals(fieldName)){
          ((UIFormTabPane)uiForm).setRenderTabId(inputValue);
          continue;
        }
        UIFormInputBase input =  uiForm.findComponentById(fieldName) ;
        if(input != null) input.decode(inputValue, context) ;
        continue;
      }
      UIFormInputBase input =  uiForm.findComponentById(fieldName) ;  // File input
      if(input != null) input.decode(item, context) ;
    }
    
  }*/
  
  @SuppressWarnings("unchecked")
  private void validateChildren(List<UIComponent>  children, UIApplication uiApp, WebuiRequestContext context) {
    for(UIComponent uiChild : children) {
      if(uiChild instanceof UIFormInputBase) {
        UIFormInputBase uiInput =  (UIFormInputBase) uiChild ;
        if(!uiInput.isValid()) continue;
        List<Validator> validators = uiInput.getValidators() ;
        if(validators == null) continue;
        try {
          for(Validator validator : validators) validator.validate(uiInput) ;
        } catch (MessageException ex) {
          uiApp.addMessage(ex.getDetailMessage()) ;
          context.setProcessRender(true) ;
        } catch(Exception ex) {
          //TODO:  This is a  critical exception and should be handle  in the UIApplication
          uiApp.addMessage(new ApplicationMessage(ex.getMessage(), null)) ;
          context.setProcessRender(true) ;
        }
      } else if(uiChild instanceof UIFormInputSet){
        UIFormInputSet uiInputSet = (UIFormInputSet)uiChild;
        validateChildren(uiInputSet.getChildren(), uiApp, context);
      } else if(uiChild instanceof UIFormInputContainer) {
        UIFormInputContainer uiInput =  (UIFormInputContainer) uiChild ;
        List<Validator> validators = uiInput.getValidators() ;
        if(validators == null) continue;
        try {
          for(Validator validator : validators) validator.validate(uiInput) ;
        } catch (MessageException ex) {
          uiApp.addMessage(ex.getDetailMessage()) ;
          context.setProcessRender(true) ;
        } catch(Exception ex) {
          //TODO:  This is a  critical exception and should be handle  in the UIApplication
          uiApp.addMessage(new ApplicationMessage(ex.getMessage(), null)) ;
          context.setProcessRender(true) ;
        }
      }
    }
  }
}