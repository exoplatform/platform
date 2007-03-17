/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.ResourceBundleService;
/**
 * @author Benjamin Mestrallet
 * benjamin.mestrallet@exoplatform.com
 */
public class LocaleConfigImpl implements LocaleConfig {
  
  static private Map<String, Locale> predefinedLocaleMap_ = null ;
  
  static {
    predefinedLocaleMap_ = new HashMap<String, Locale>(10) ;
    predefinedLocaleMap_.put("us" , Locale.US) ;
    predefinedLocaleMap_.put("en" , Locale.ENGLISH) ;
    predefinedLocaleMap_.put("fr" , Locale.FRANCE) ;
    predefinedLocaleMap_.put("zh" , Locale.SIMPLIFIED_CHINESE) ;
  }
    
  private Locale locale_;
  private String outputEncoding_;
  private String inputEncoding_;
  private String description_ ;
  private String localeName_ ;
  
  public LocaleConfigImpl() {
  }
   
  public final String getDescription() { return description_; }
  public final void   setDescription(String desc) { description_ = desc; }
  
  public final String getOutputEncoding() { return outputEncoding_; }
  public final void   setOutputEncoding(String enc) { outputEncoding_ = enc; }
  
  public final String getInputEncoding() { return inputEncoding_ ; }
  public final void setInputEncoding(String enc) {  inputEncoding_ = enc;}
  
  public final Locale getLocale() { return locale_ ; }
  public final void setLocale(Locale locale) { locale_ = locale; }
  public final void setLocale(String localeName) {
    localeName_ = localeName ;
    locale_ =  predefinedLocaleMap_.get(localeName) ;
    if(locale_ == null) locale_ = new Locale(localeName) ;
  }
  
  public final String getLanguage() { return locale_.getLanguage() ; }
 
  public final String getLocaleName() { return localeName_ ; }
  
  public ResourceBundle getResourceBundle(String name) {
    ResourceBundleService service = 
      (ResourceBundleService)PortalContainer.getComponent(ResourceBundleService.class) ;
    ResourceBundle res = service.getResourceBundle(name, locale_) ;
    return res ;
  }
  
  public ResourceBundle getMergeResourceBundle(String[] names) {
    ResourceBundleService service = 
      (ResourceBundleService)PortalContainer.getComponent(ResourceBundleService.class) ;
    ResourceBundle res = service.getResourceBundle(names, locale_) ;
    return res ;
  }
  
  public ResourceBundle getOwnerResourceBundle(String owner) {
    PortalContainer manager = PortalContainer.getInstance() ; 
    ResourceBundleService service = 
      (ResourceBundleService)manager.getComponentInstanceOfType(ResourceBundleService.class) ;
    try {
      ResourceBundle res = service.getResourceBundle("locale.users." + owner, locale_) ;
      if(res == null ) {
        res = service.getResourceBundle("locale.users.default", locale_) ;
      }
      return res ;
    } catch (Exception ex) {
      return service.getResourceBundle("locale.users.default", locale_) ;
    }
  }
  
  public void setInput(HttpServletRequest req) throws java.io.UnsupportedEncodingException {
    req.setCharacterEncoding(inputEncoding_) ;
  }
  
  public void setOutput(HttpServletResponse res) {
    res.setContentType("text/html; charset=" +  outputEncoding_) ;
    res.setLocale(locale_) ;
  }  
}