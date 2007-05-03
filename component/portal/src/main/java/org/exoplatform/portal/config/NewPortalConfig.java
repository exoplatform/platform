/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.HashSet;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 23, 2006
 */
public class NewPortalConfig {
  
  private HashSet predefinedOwner = new HashSet(5);
  private String  ownerType;
  private String  templateOwner ;
  private String  templateLocation ;
  
  public HashSet getPredefinedOwner() {   return predefinedOwner; }
  public void setPredefinedOwner(HashSet s) {  this.predefinedOwner = s; }
  
  public String getTemplateLocation() {  return templateLocation; }
  public void setTemplateLocation(String s) { this.templateLocation = s; }
  
  public String getTemplateOwner() {  return templateOwner;}
  public void setTemplateOwner(String s) {  this.templateOwner = s; }
  
  public boolean isPredefinedOwner(String user) { return predefinedOwner.contains(user) ; }
  
  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
}
