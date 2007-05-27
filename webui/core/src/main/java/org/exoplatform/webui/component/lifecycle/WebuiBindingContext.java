package org.exoplatform.webui.component.lifecycle;

import java.io.Writer;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.groovyscript.text.BindingContext;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIContainer;

@SuppressWarnings("serial")
public class WebuiBindingContext extends BindingContext {
  
  private UIComponent uicomponent_ ;
  private WebuiRequestContext rcontext_ ;
  
  public WebuiBindingContext(ResourceResolver resolver, Writer w, 
                             UIComponent uicomponent, WebuiRequestContext context) {
    super(resolver, w) ;
    uicomponent_ =  uicomponent ;
    rcontext_ = context ;
  }
  
  public UIComponent getUIComponent() {  return uicomponent_ ; }
  
  public WebuiRequestContext getRequestContext() { return rcontext_ ; }
  
  public BindingContext clone() {
    BindingContext newContext = new WebuiBindingContext(resolver_, writer_, uicomponent_,rcontext_);
    newContext.putAll(this) ;
    newContext.setGroovyTemplateService(service_) ;
    return newContext ;  
  }

  
  public String appRes(String mesgKey) throws Exception {
    String value ;
    try {
      ResourceBundle res = rcontext_.getApplicationResourceBundle() ;
      value = res.getString(mesgKey) ;
    } catch(MissingResourceException ex) {      
      //TODO review logic
      System.err.println("\nkey : "+mesgKey+"\n");
      value = mesgKey.substring(mesgKey.lastIndexOf('.') + 1) ;
    }
    return value ;
  }
  
  public void renderChildren() throws Exception {
    if(uicomponent_ instanceof UIComponentDecorator){
      UIComponentDecorator uiComponentDecorator = (UIComponentDecorator) uicomponent_;
      if(uiComponentDecorator.getUIComponent() == null) return;
      uiComponentDecorator.getUIComponent().processRender(rcontext_);
      return;
    }
    UIContainer uicontainer = (UIContainer)  uicomponent_ ;
    List<UIComponent> children = uicontainer.getChildren() ;
    for(UIComponent child :  children) {
      if(child.isRendered()) {
        child.processRender(rcontext_) ;
      }
    }
  }
  
  public void renderChild(String id) throws Exception {
    if(!(uicomponent_ instanceof UIContainer)) return;     
    UIContainer uicontainer = (UIContainer)  uicomponent_ ;
    UIComponent uiChild = uicontainer.getChildById(id) ;
    uiChild.processRender(rcontext_) ;
  }
  
  public void renderUIComponent(UIComponent uicomponent) throws Exception {
    uicomponent.processRender(rcontext_) ;
  }
  
  public void renderChild(int index) throws Exception {
    if(!(uicomponent_ instanceof UIContainer)) return;     
    UIContainer uicontainer = (UIContainer)  uicomponent_ ;
    UIComponent uiChild = uicontainer.getChild(index) ;
    uiChild.processRender(rcontext_) ;
  }
  
  @SuppressWarnings("unused")
  public void userRes(String mesgKey) {
    
  }
}