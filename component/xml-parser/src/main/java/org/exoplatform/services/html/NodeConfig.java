/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 30, 2006
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NodeConfig {  
  Name name() ;
  Class<?> type();
  
  boolean only () default false;  
  boolean hidden () default false;
  boolean block() default false;
  
  MoveType move() default MoveType.ADD;
  
  Tag start () default Tag.REQUIRED;
  Tag end () default Tag.REQUIRED;  
  Class<?> [] end_types() default {};
  Name [] end_names() default {};
  
  
  Name [] parent() default {};
  Name [] children() default {};
  Class<?> [] children_types () default {}; 
}
