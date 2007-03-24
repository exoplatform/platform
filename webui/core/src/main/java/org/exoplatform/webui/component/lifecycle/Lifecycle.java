/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.lifecycle;

import groovy.text.Template;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.templates.groovy.GroovyTemplateService;
import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIComponent;
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
    System.out.println("===> CHECK_MODIFIED_TEMPLATE = " + DEVELOPING) ;
  }
  
  private Decorator decorator_ = new Decorator()   ;
  
  @SuppressWarnings("unused")
  public void init(UIComponent uicomponent, RequestContext context) throws Exception {  
    
  }
  
  @SuppressWarnings("unused")
  public void processDecode(UIComponent uicomponent , RequestContext context) throws Exception {  
    
  }
  
  public void processAction(UIComponent uicomponent , RequestContext context) throws Exception {
    String action =  context.getRequestParameter(context.getActionParameterName()) ;
    if(action == null) return ;
    Event<UIComponent> event = uicomponent.createEvent(action, Event.Phase.PROCESS, context) ;
    if(event != null) event.broadcast()  ;
  }
  
  public void processRender(UIComponent uicomponent , RequestContext context) throws Exception {
    String template = uicomponent.getTemplate() ;
    ResourceResolver resolver =  uicomponent.getTemplateResourceResolver(context, template); 
    WebuiBindingContext bcontext = 
      new WebuiBindingContext(resolver, context.getWriter(), uicomponent, context) ;
    bcontext.put("uicomponent", uicomponent) ;
    bcontext.put(uicomponent.getUIComponentName(), uicomponent) ;   
    renderTemplate(template, bcontext) ;
  }
  
  @SuppressWarnings("unused")
  public void destroy(UIComponent uicomponent) throws Exception {  
    
  }
   
  protected void renderTemplate(String template, WebuiBindingContext bcontext) throws Exception {
    bcontext.put("decorator", decorator_) ;
    RequestContext context = bcontext.getRequestContext() ;
    ExoContainer pcontainer =  context.getApplication().getApplicationServiceContainer() ;
    GroovyTemplateService service = 
      (GroovyTemplateService) pcontainer.getComponentInstanceOfType(GroovyTemplateService.class) ;
    ResourceResolver resolver = bcontext.getResourceResolver() ;
    
    if(DEVELOPING) {
      RequestContext rootContext = context.getParentAppRequestContext() ;
      if(rootContext == null)  rootContext = context ;
      //System.out.println(template + " modified: " + file.lastModified() + ", access " + rootContext.getUIApplication().getLastAccessApplication());
      long lastAccess =  rootContext.getUIApplication().getLastAccessApplication() ;
      if(resolver.isModified(template, lastAccess)) {
        //System.out.println("\nInvalidate the template: " + file.getAbsolutePath());
        service.invalidateTemplate(template, resolver) ;
      }
    }    
    
    try{
      Template groovyTemplate = service.getTemplate(template, resolver) ; 
      service.merge(groovyTemplate, bcontext) ;
    }catch (Exception e) {
      //for log file
      System.out.println("\n\n template : " + template);
      System.out.println(e.toString()+"\n\n");
//      e.printStackTrace();
    }
  }
  
}