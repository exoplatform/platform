/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Component.java,v 1.6 2004/11/03 01:23:55 tuan08 Exp $
 **/
abstract public class Component implements Cloneable  {
  
  protected String id = "" ;
  protected String factoryId ;
	protected String template ;
  protected String decorator ;
  protected String width ;
  protected String height ;
  private transient boolean modifiable ;
  
  public Component() {  
  } 
  
  public Component(String renderer, String style, String width, String height) {
  	this.template = renderer ;
  	this.decorator = style ;
  	this.width = width ;
  	this.height = height ;
  }
  
  public String getId() { return id ;}
  public void   setId(String s) { id = s ; }
  
  public String getTemplate() { return template ; }
  public void   setTemplate(String s) { template = s ; }
  
  public String getDecorator() { return decorator ; }
  public void   setDecorator(String s) { decorator = s ; }
  
  public String getWidth() { return width ; }
  public void   setWidth(String s) { width = s ;}
  
  public String getHeight() { return height ; }
  public void   setHeight(String s) { height = s ;}
  
  public boolean isModifiable() { return modifiable ; }
  public void    setModifiable(boolean b) { modifiable = b ; }

  public String getFactoryId() { return factoryId; }
  public void setFactoryId(String factoryId) { this.factoryId = factoryId; }  
}