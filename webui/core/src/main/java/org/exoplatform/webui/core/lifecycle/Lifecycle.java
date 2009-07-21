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

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.groovyscript.text.TemplateService;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;

/**
 * Created by The eXo Platform SAS
 * May 7, 2006
 */
public class Lifecycle {
  
  protected static Log log = ExoLogger.getLogger("portal:Lifecycle");
  
  private Decorator decorator_ = new Decorator()   ;
  
//  public void init(UIComponent uicomponent, WebuiRequestContext context) throws Exception {}
  
  public void processDecode(UIComponent uicomponent , WebuiRequestContext context) throws Exception {}
  
  public void processAction(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    String action =  context.getRequestParameter(context.getActionParameterName()) ;
    if(action == null) return ;
    Event<UIComponent> event = uicomponent.createEvent(action, Event.Phase.PROCESS, context) ;
    if(event != null) event.broadcast()  ;
  }
  
  /**
   * That method is the most generic one for every UICOmponent that is bound to this Lifecycle object
   * and the class that extends it withouyt overiding the method.
   * 
   * According to the template type associated with the UI component (groovy or javascript one); the 
   * template is rendered using either the method renderJSTemplate() or renderTemplate(). In the case
   * of the use of a groovy template, a context object of type WebuiBindingContext is used to then 
   * provide to the template all the necessary objects to render (WebuiBindingContext extends the Map
   * class) 
   * 
   */
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    String template = uicomponent.getTemplate() ;
    ResourceResolver resolver =  uicomponent.getTemplateResourceResolver(context, template);
    WebuiBindingContext bcontext = 
      new WebuiBindingContext(resolver, context.getWriter(), uicomponent, context) ;
    bcontext.put("uicomponent", uicomponent) ;
    bcontext.put(uicomponent.getUIComponentName(), uicomponent) ;   
    renderTemplate(template, bcontext) ;
  }
  
//  public void destroy(UIComponent uicomponent) throws Exception {}
   
  /**
   * The method allows to use Groovy templates to render the portal components.
   * 
   * 1) Add a decorator object into the context
   * 2) Get a reference of the TemplateService
   * 3) If the system property "exo.product.developing" is set to true, the templates are not cached
   * 4) If the writer used to render the output is of type HtmlValidator, which is the case in the
   *    Portal environement, then it is also possible to validate the generated HTML (for debug purposes)
   * 6) The template and the context are then merged using the method service.merge(groovyTemplate, bcontext)
   *    to generate the HTML fragment  
   * 
   */
  protected void renderTemplate(String template, WebuiBindingContext bcontext) throws Exception {
    bcontext.put("decorator", decorator_) ;
    WebuiRequestContext context = bcontext.getRequestContext() ;
    ExoContainer pcontainer =  context.getApplication().getApplicationServiceContainer() ;
    TemplateService service = 
      (TemplateService) pcontainer.getComponentInstanceOfType(TemplateService.class) ;
    ResourceResolver resolver = bcontext.getResourceResolver() ;
    
    if(PropertyManager.isDevelopping()) {
      WebuiRequestContext rootContext = (WebuiRequestContext)context.getParentAppRequestContext() ;
      if(rootContext == null)  rootContext = context ;
      long lastAccess =  rootContext.getUIApplication().getLastAccessApplication() ;
      if(resolver.isModified(template, lastAccess)) {
    	if(log.isDebugEnabled())
    	  log.debug("Invalidate the template: " + template);
        service.invalidateTemplate(template, resolver) ;
      }
    }    
    
    try {
      if(bcontext.getWriter() instanceof HtmlValidator) {
        HtmlValidator validator = (HtmlValidator) bcontext.getWriter();
        validator.startComponent();
      }
      service.merge(template, bcontext) ;     
      if(bcontext.getWriter() instanceof HtmlValidator) {
        HtmlValidator validator = (HtmlValidator) bcontext.getWriter();
        validator.endComponent();
      }
    } catch (Exception e) {
      log.error("template : " + template, e);
    }
  }
}