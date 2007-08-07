/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.form.UIForm;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 29, 2006
 */
abstract public class UIWizard  extends UIContainer { 
  
  private int currentStep  = 1;
  private int selectedStep = 1 ;
  
	public UIWizard() throws Exception {
	}  
  
  public String url(String name) throws Exception {
    UIComponent renderedChild = getChild(currentStep-1);
    if(!(renderedChild instanceof UIForm)) return super.event(name);
    
    org.exoplatform.webui.config.Event event = config.getUIComponentEventConfig(name) ;
    if(event == null) return "??config??" ;
    
    UIForm uiForm = (UIForm) renderedChild;
    return uiForm.event(name);
  }
  
  public void viewStep(int step) throws Exception {
    if(selectedStep < getChildren().size()+1 && step > currentStep) selectedStep++;
    currentStep = step < selectedStep ? step : selectedStep;   
    step = currentStep - 1;
    List<UIComponent> children = getChildren(); 
    for(int i=0; i<children.size(); i++){
      if(i == step) {
        children.get(i).setRendered(true);
      } else {
        children.get(i).setRendered(false);
      }
    }
//    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;   
//    context.addUIComponentToUpdateByAjax(this) ;
  }

  public int getCurrentStep() { return currentStep; }

  public int getSelectedStep() { return selectedStep; }

}
