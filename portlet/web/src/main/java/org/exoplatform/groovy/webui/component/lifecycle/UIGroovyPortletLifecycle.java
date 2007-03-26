/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.groovy.webui.component.lifecycle;

import groovy.text.Template;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.groovy.webui.component.UIGroovyPortlet;
import org.exoplatform.templates.groovy.BindingContext;
import org.exoplatform.templates.groovy.GroovyTemplateService;
import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.lifecycle.Lifecycle;
import org.exoplatform.webui.component.lifecycle.WebuiBindingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 2, 2006
 */
public class UIGroovyPortletLifecycle  extends  Lifecycle {
  
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    UIGroovyPortlet uiPortlet =  (UIGroovyPortlet)  uicomponent ;    
    String template = uiPortlet.getTemplate() ;
    WebuiApplication app = context.getApplication() ;
    ExoContainer pcontainer =  app.getApplicationServiceContainer() ;
    GroovyTemplateService service = 
      (GroovyTemplateService) pcontainer.getComponentInstanceOfType(GroovyTemplateService.class) ;
    ResourceResolver resolver =  app.getResourceResolver() ;
    Template groovyTemplate = service.getTemplate(template, resolver) ;
    BindingContext bcontext = 
      new WebuiBindingContext(resolver, context.getWriter(), uicomponent, context) ;
    bcontext.put("uicomponent", uicomponent) ;
    service.merge(groovyTemplate, bcontext) ;
  }
   
}
