/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.commons.utils.MapResourceBundle;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleService;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
abstract class BaseResourceBundleService implements ResourceBundleService  {

  Log log_;

  List classpathResources_ ;
  String[]  portalResourceBundleNames_ ;
  LocaleConfigService localeService_;

  ExoCache cache_;

  @SuppressWarnings("unchecked")
  void initParams(InitParams params) throws Exception {
    classpathResources_ = params.getValuesParam("classpath.resources").getValues();
    List  prnames = params.getValuesParam("portal.resource.names").getValues();
    portalResourceBundleNames_ = new String[prnames.size()] ;
    for(int i = 0; i < prnames.size(); i++) {
      portalResourceBundleNames_[i] = (String)prnames.get(i) ; 
    }
    PageList pl  = findResourceDescriptions(new  Query(null, null)) ;
    if(pl.getAvailable() > 0)  return ;
    List<String> initResources = params.getValuesParam("init.resources").getValues();
    for(String resource : initResources) {
      initResources(resource, Thread.currentThread().getContextClassLoader()) ;
    }
  }

  public ResourceBundle getResourceBundle(String[] name, Locale locale) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return getResourceBundle(name, locale, cl);
  }

  public ResourceBundle getResourceBundle(String name, Locale locale) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return getResourceBundle(name, locale, cl);
  }

  public String[] getSharedResourceBundleNames() { return portalResourceBundleNames_ ; }

  public ResourceBundleData createResourceBundleDataInstance() {
    return new ResourceBundleData();
  }

  boolean isClasspathResource(String name) {
    if (classpathResources_ == null)  return false;
    for (int i = 0; i < classpathResources_.size(); i++) {
      String pack = (String) classpathResources_.get(i);
      if (name.startsWith(pack)) return true;
    }
    return false;
  }


  void initResources(String baseName, ClassLoader cl) {    
    String name = baseName.replace('.', '/');
    String fileName = null;
    try {
      Collection localeConfigs = localeService_.getLocalConfigs();
      String defaultLang = localeService_.getDefaultLocaleConfig().getLanguage();
      for (Iterator iter = localeConfigs.iterator(); iter.hasNext();) {
        LocaleConfig localeConfig = (LocaleConfig) iter.next();
        String language = localeConfig.getLanguage();
        if (defaultLang.equals(language)) {
          fileName = name + ".properties";
        } else {
          fileName = name + "_" + language + ".properties";
        }
        URL url = cl.getResource(fileName);
        if (url != null) {
          InputStream is = url.openStream();
          byte buf[] = IOUtil.getStreamContentAsBytes(is);
          ResourceBundleData data = new ResourceBundleData();
          data.setId(baseName + "_" + language) ;
          data.setName(baseName);
          data.setLanguage(language);
          data.setData(new String(buf, "UTF-8"));
          saveResourceBundle(data);
          is.close();
        }
      }
    } catch (Exception ex) {
      log_.error("Error while reading the file: " + fileName, ex);
    }
  }

  public ResourceBundle getResourceBundle(String name, Locale locale, ClassLoader cl) {
    if (isClasspathResource(name))  return ResourceBundle.getBundle(name, locale, cl);
    String id = name + "_" + locale.getLanguage();
    try {
      Object obj = cache_.get(id);
      if (obj != null) return (ResourceBundle) obj;
    } catch (Exception ex) {}

    try {
      ResourceBundle res = null;
      String rootId = name + "_" + localeService_.getDefaultLocaleConfig().getLanguage();
      ResourceBundle parent = getResourceBundleFromDb(rootId, null, locale);
      if (parent != null) {
        res = getResourceBundleFromDb(id, parent, locale);
        if (res == null) res = parent;
        cache_.put(id, res);
        return res;
      }
    } catch (Exception ex) {
      log_.error("Error: " + id, ex);
    }
    return null;
  }

  public ResourceBundle getResourceBundle(String[] name, Locale locale, ClassLoader cl) {
    StringBuilder idBuf = new StringBuilder("merge:");
    for (String n : name) idBuf.append(n).append("_");
    idBuf.append(locale.getLanguage());
    String id = idBuf.toString();
    try {
      ResourceBundle  res = (ResourceBundle) cache_.get(id);
      if (res != null) return res;
      MapResourceBundle outputBundled = new MapResourceBundle(locale);
      for (int i = 0; i < name.length; i++) {
        ResourceBundle temp = getResourceBundle(name[i], locale, cl);
        if (temp != null) {
          outputBundled.merge(temp);
          continue;
        }
        log_.warn("Cannot load and merge the bundle: " + name[i]);
      }
      outputBundled.resolveDependencies();
      cache_.put(id, outputBundled);
      return outputBundled;
    } catch (Exception ex) {
      log_.error("Cannot load and merge the bundle: " + id, ex);
    }
    return null;
  }

  abstract ResourceBundle getResourceBundleFromDb(String id, ResourceBundle parent, Locale locale) throws Exception ;

}
