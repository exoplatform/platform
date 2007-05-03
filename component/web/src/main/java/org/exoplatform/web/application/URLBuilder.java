/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;


/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 29, 2007  
 */
abstract public class URLBuilder<T> {

  protected String baseURL_;

  public URLBuilder(String baseURL) {
    baseURL_ = baseURL;
  }

  public String getBaseURL() { return baseURL_; }
  
  public void setBaseURL(String url) { baseURL_ = url; }

  public String createURL(String action) { return createURL(action, (Parameter[])null) ; }

  abstract public String createURL(String action, Parameter[] params) ;

  public String createURL(String action, String objectId) {
    return createURL(action, objectId, (Parameter[]) null) ;
  }

  abstract public String createURL(String action, String objectId, Parameter[] params) ;

  public String createURL(T targetComponent, String action, String targetBeanId) {
    return createURL(targetComponent, action, targetBeanId, (Parameter[])null) ;
  }
  
  public String createAjaxURL(T targetComponent, String action, String targetBeanId) {
    return createAjaxURL(targetComponent, action, targetBeanId, (Parameter[])null) ;
  }

  public String createAjaxURL(T targetComponent, String action, String targetBeanId, Parameter[] params) {
    StringBuilder builder = new StringBuilder("javascript:ajaxGet('");
    createURL(builder, targetComponent, action, targetBeanId, params);
    builder.append("&amp;ajaxRequest=true'") ;
    // Modified by Philippe
    // Maybe not the best solution, but the only way to resize rows (td) when one is deleted
    if (action.equalsIgnoreCase("DeleteComponent")) builder.append(", eXo.portal.PortalDragDrop.resizeRows");
    System.out.println("delete component");
    builder.append(")");
    return builder.toString();    
  }

  public String createURL(T targetComponent, String action, String targetBeanId, Parameter[] params) {
    StringBuilder builder = new StringBuilder();
    createURL(builder, targetComponent, action, targetBeanId, params);
    return builder.toString();
  }

  abstract protected void createURL(StringBuilder builder, T targetComponent, String action, String targetBeanId, Parameter[] params) ;
}
