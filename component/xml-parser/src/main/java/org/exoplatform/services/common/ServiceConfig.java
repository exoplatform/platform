/***************************************************************************
 * Copyright 2003-2007 by eXoPlatform - All rights reserved.                *    
 **************************************************************************/
package org.exoplatform.services.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 3, 2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ServiceConfig {
  
  ServiceType type () default ServiceType.INSTANCE;
  
  public static enum ServiceType {
    SINGLE_FINAL, SOFT_REFERENCE, INSTANCE //, LAZY_FINAL
  }
 }
