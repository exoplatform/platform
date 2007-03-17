/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * May 3, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: LocaleConfig.java 5799 2006-05-28 17:55:42Z geaz $
 **/
public interface LocaleConfig {
   
	public String getDescription();
	public void   setDescription(String desc);
	
	public String getOutputEncoding();
	public void   setOutputEncoding(String enc);
	
	public String getInputEncoding();
	public void setInputEncoding(String enc);
	
	public Locale getLocale();
	public void setLocale(Locale locale);
	public void setLocale(String localeName);
	
  public String getLanguage();
 
  public String getLocaleName();
  
  public ResourceBundle getResourceBundle(String name);
  
  public ResourceBundle getMergeResourceBundle(String[] names);
  
  public ResourceBundle getOwnerResourceBundle(String owner);
  
  public void setInput(HttpServletRequest req)  throws java.io.UnsupportedEncodingException;
  
  public void setOutput(HttpServletResponse res);
}