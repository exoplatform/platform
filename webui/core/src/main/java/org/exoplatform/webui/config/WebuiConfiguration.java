/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.config;

import java.util.ArrayList;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 4, 2006
 */
public class WebuiConfiguration {
  
  private ArrayList<String>  annotationClasses ;
  private ArrayList<Component>  components ;
  private Application  application ;
  
  public  ArrayList<String>  getAnnotationClasses() { return annotationClasses ; }
  public  ArrayList<Component>  getComponents() { return components ; }
  public  Application  getApplication() {  return application ; }
  
}
