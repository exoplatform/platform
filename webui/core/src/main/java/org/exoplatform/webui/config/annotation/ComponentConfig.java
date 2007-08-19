/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Jul 4, 2006
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ComponentConfig {
  
  String id() default "";
  Class  type() default void.class;
  String template() default "";
  Class  lifecycle() default void.class;
  String decorator() default "" ;
  
  ParamConfig [] initParams() default {};
  
  ValidatorConfig [] validators() default {};
  EventConfig [] events() default {};
  EventInterceptorConfig []  eventInterceptors() default {};
}
