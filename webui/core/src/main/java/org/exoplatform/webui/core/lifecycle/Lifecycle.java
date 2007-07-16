/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core.lifecycle;

import groovy.text.Template;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.groovyscript.text.TemplateService;
import org.exoplatform.javascript.JavaScriptEngineService;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
public class Lifecycle {
  
  private static boolean DEVELOPING = false;
  
  static {
    DEVELOPING =  "true".equals(System.getProperty("exo.product.developing")) ;
    //System.out.println("===> CHECK_MODIFIED_TEMPLATE = " + DEVELOPING) ;
  }
  
  private Decorator decorator_ = new Decorator()   ;
  
  @SuppressWarnings("unused")
  public void init(UIComponent uicomponent, WebuiRequestContext context) throws Exception {  
    
  }
  
  @SuppressWarnings("unused")
  public void processDecode(UIComponent uicomponent , WebuiRequestContext context) throws Exception {  
    
  }
  
  public void processAction(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    String action =  context.getRequestParameter(context.getActionParameterName()) ;
    if(action == null) return ;
    Event<UIComponent> event = uicomponent.createEvent(action, Event.Phase.PROCESS, context) ;
    if(event != null) event.broadcast()  ;
  }
  
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    String template = uicomponent.getTemplate() ;
    if(template.endsWith(".gtmpl")) {
      ResourceResolver resolver =  uicomponent.getTemplateResourceResolver(context, template); 
      WebuiBindingContext bcontext = 
        new WebuiBindingContext(resolver, context.getWriter(), uicomponent, context) ;
      bcontext.put("uicomponent", uicomponent) ;
      bcontext.put(uicomponent.getUIComponentName(), uicomponent) ;   
      renderTemplate(template, bcontext) ;
    } else if(template.endsWith(".jstmpl")) {
      ResourceResolver resolver =  uicomponent.getTemplateResourceResolver(context, template);
      Map<String, Object>  variables = new HashMap<String, Object>() ;
      renderJSTemplate(template, variables, resolver, uicomponent, context) ;
    }
  }
  
  @SuppressWarnings("unused")
  public void destroy(UIComponent uicomponent) throws Exception {  
    
  }
   
  protected void renderTemplate(String template, WebuiBindingContext bcontext) throws Exception {
    bcontext.put("decorator", decorator_) ;
    WebuiRequestContext context = bcontext.getRequestContext() ;
    ExoContainer pcontainer =  context.getApplication().getApplicationServiceContainer() ;
    TemplateService service = 
      (TemplateService) pcontainer.getComponentInstanceOfType(TemplateService.class) ;
    ResourceResolver resolver = bcontext.getResourceResolver() ;
    
    if(DEVELOPING) {
      WebuiRequestContext rootContext = (WebuiRequestContext)context.getParentAppRequestContext() ;
      if(rootContext == null)  rootContext = context ;
      long lastAccess =  rootContext.getUIApplication().getLastAccessApplication() ;
      if(resolver.isModified(template, lastAccess)) {
        //System.out.println("\nInvalidate the template: " + template);
        service.invalidateTemplate(template, resolver) ;
      }
    }    
    
    try {
      Template groovyTemplate = service.getTemplate(template, resolver) ;
      if(bcontext.getWriter() instanceof HtmlValidator) {
        HtmlValidator validator = (HtmlValidator) bcontext.getWriter();
        validator.startComponent();
      }
      service.merge(groovyTemplate, bcontext) ;     
      if(bcontext.getWriter() instanceof HtmlValidator) {
        HtmlValidator validator = (HtmlValidator) bcontext.getWriter();
        validator.endComponent();
      }
    } catch (Exception e) {
      System.out.println("\n\n template : " + template);
      System.out.println(e.toString()+"\n\n") ;
    }
  }
  
  protected void renderJSTemplate(String template, Map<String, Object> variables, ResourceResolver resolver, 
                                  UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    ExoContainer pcontainer =  context.getApplication().getApplicationServiceContainer() ;
    JavaScriptEngineService service = 
      (JavaScriptEngineService) pcontainer.getComponentInstanceOfType(JavaScriptEngineService.class) ;
    WebuiTemplateContext webuiTmplContext = 
      new WebuiTemplateContext(service, resolver, uicomponent, context) ;
    webuiTmplContext.setVariables(variables) ;
    webuiTmplContext.setVariable("decorator", decorator_) ;
    webuiTmplContext.setVariable("uicomponent", uicomponent) ;
    webuiTmplContext.setVariable(uicomponent.getUIComponentName(), uicomponent) ;
    webuiTmplContext.render(template) ;
  }
}