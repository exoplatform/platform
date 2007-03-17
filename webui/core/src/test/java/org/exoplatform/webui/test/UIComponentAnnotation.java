/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.test;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.EventInterceptorConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.config.annotation.ValidatorConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
    type = UIComponentAnnotation.class,
    lifecycle = UILifecycle.class,
    template = ".....................",
    decorator = "....................",
    events = {
      @EventConfig (
          name = "name",
          phase = Phase.DECODE,
          listeners = UIComponentAnnotation.UIComponentEvent.class,
          initParams = {
            @ParamConfig(
               name = "name",
               value = "value"
            )
          }
      )
    },
    initParams = {
      @ParamConfig(
         name = "name",
         value = "value"
      )
    },   
    validators = {
      @ValidatorConfig (
          type = UIComponentAnnotation.UIComponentValidator.class,          
          initParams = {
            @ParamConfig(
               name = "name",
               value = "value"
            )
          }
      )
    },
    eventInterceptors = {
      @EventInterceptorConfig (
          type = UIComponentAnnotation.UIComponentEventInterceptor.class,
          interceptors = {"inter1"},
          initParams = {
            @ParamConfig(
              name = "name",
              value = "value"
            )
          }
      )
    }
    
)

public class UIComponentAnnotation {
  
  static public class UIComponentValidator  {
    
  }
  
  static public class UIComponentEventInterceptor  {
    
  }
  
  static public class UIComponentEvent  extends EventListener{
    @SuppressWarnings("unused")
    public void execute(Event event) throws Exception {      
      
    }    
  }
}
