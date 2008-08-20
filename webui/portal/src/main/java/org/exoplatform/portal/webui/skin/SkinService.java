/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.skin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

public class SkinService {

  protected static Log log = ExoLogger.getLogger("portal.SkinService");

  private final static String REGEXP = "@import url(.*).*;";

  private final static String BACKGROUND_REGEXP = "background.*:.*url(.*).*;";

//private final static String CSS_SERVLET_URL = "/portal/css";  

  private Map<String, SkinConfig> portalSkins_ ;

  private Map<String, SkinConfig> skinConfigs_;

  private HashSet<String> availableSkins_;

  private Map<String, String> mergedCSS_;

  private Map<String, Set<String>> portletThemes_;

  private boolean cacheResource_;

  public SkinService() {
    portalSkins_ = new HashMap<String, SkinConfig>() ;
    skinConfigs_ = new HashMap<String, SkinConfig>(20);
    availableSkins_ = new HashSet<String>(5);
    cacheResource_ = !"true".equals(System
        .getProperty("exo.product.developing"));
    mergedCSS_ = new HashMap<String, String>();
  }

  public void addCategoryTheme(String categoryName) {
    if (portletThemes_ == null)
      portletThemes_ = new HashMap<String, Set<String>>();
    if (!portletThemes_.containsKey(categoryName))
      portletThemes_.put(categoryName, new HashSet<String>());
  }  

  public void addPortalSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addPortalSkin(module, skinName, cssPath, scontext, false) ;
  }

  public void addPortalSkin(String module, String skinName, String cssPath,
      ServletContext scontext, boolean isPrimary) {
    availableSkins_.add(skinName) ;
    String key = module + "$" + skinName;
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null || isPrimary) {
      portalSkins_.put(key, new SkinConfig(module, cssPath));
      mergeCSS(cssPath, scontext);
    }
  }

  public void addPortalSkin(String module,String skinName, String cssPath, String cssData) {
    String key = module + "$" + skinName;
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null) {
      portalSkins_.put(key, new SkinConfig(module, cssPath));      
    }
    mergedCSS_.put(cssPath, cssData);
  }

  /**
   * 
   * @param module
   * @param skinName
   * @param cssPath
   */
  public void addSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addSkin(module, skinName, cssPath, scontext, false);
  }

  public void addSkin(String module, String skinName, String cssPath,
      ServletContext scontext, boolean isPrimary) {
    availableSkins_.add(skinName);
    String key = module + "$" + skinName;
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null || isPrimary) {
      skinConfigs_.put(key, new SkinConfig(module, cssPath));
      mergeCSS(cssPath, scontext);
    }
  }

  public void addSkin(String module, String skinName, String cssPath, String cssData) {
    availableSkins_.add(skinName);
    String key = module + "$" + skinName;
    SkinConfig skinConfig = skinConfigs_.get(key);    
    if (skinConfig == null) {      
      skinConfigs_.put(key, new SkinConfig(module, cssPath));      
    }
    mergedCSS_.put(cssPath,cssData);
  }

  public void addTheme(String categoryName, List<String> themesName) {
    if (portletThemes_ == null)
      portletThemes_ = new HashMap<String, Set<String>>();
    if (!portletThemes_.containsKey(categoryName))
      portletThemes_.put(categoryName, new HashSet<String>());
    Set<String> catThemes = portletThemes_.get(categoryName);
    for (String theme : themesName)
      catThemes.add(theme);
  }

  /**
   * TODO: should return a collection or list This method should return the
   * availables skin in the service
   * 
   * @return
   */
  public Iterator<String> getAvailableSkins() {
    return availableSkins_.iterator();
  }

  public String getMergedCSS(String cssPath) {
    return mergedCSS_.get(cssPath);
  }

  public Collection<SkinConfig> getPortalSkins(String skinName) {
    Set<String> keys = portalSkins_.keySet();
    Collection<SkinConfig> portalSkins = new ArrayList<SkinConfig>() ;
    for(String key : keys) {
      if(key.endsWith("$" + skinName)) portalSkins.add(portalSkins_.get(key)) ;
    }
    return portalSkins ;
  }

  public Map<String, Set<String>> getPortletThemes() {
    return portletThemes_;
  }

  public SkinConfig getSkin(String key) {
    return skinConfigs_.get(key);
  }

  public SkinConfig getSkin(String module, String skinName) {
    SkinConfig config = skinConfigs_.get(module + "$" + skinName) ;
    if(config == null) skinConfigs_.get(module + "$Default") ;
    return config;
  }

  public void invalidatePortalSkinCache(String portalName, String skinName) {
    String key = portalName + "$" + skinName;
    skinConfigs_.remove(key);
  }

  public void remove(String key) throws Exception {
    skinConfigs_.remove(key);
  }

  public void remove(String module, String skinName) throws Exception {
    String key = module + "$" + skinName;
    if (skinName.length() == 0)
      key = module + "$Default";
    skinConfigs_.remove(key);
  }

  public void setPortletThemes(Map<String, Set<String>> portletThemes_) {
    this.portletThemes_ = portletThemes_;
  }

  public int size() {
    return skinConfigs_.size();
  }

  /**
   * This method is only called in production environment where all the css for the
   * portlets displayed in the portal canvas are merged into as single CSS file
   */
//public SkinConfig getPortalSkin(String portalName,
//String skinName, List<String> portletInPortal) {
//String key = portalName + "$" + skinName;
//SkinConfig portalSkinConfig = skinConfigs_.get(key);
//if(portalSkinConfig == null) {
////manage the portlet in portal merge and generate the css Path
//StringBuffer buffer = new StringBuffer();
//for (String module : portletInPortal) {
//String portletKey = module + "$" + skinName;
//SkinConfig portletConfig = skinConfigs_.get(portletKey);
//if(portletConfig != null) {
//String portletCSS = mergedCSS_.get(portletConfig.getCSSPath());
//if(portletCSS != null) {
//buffer.append(portletCSS);
//}
//}

//}
//String cssPath = CSS_SERVLET_URL + "/" + key + ".css";
//mergedCSS_.put(cssPath, buffer.toString());
//portalSkinConfig = new SkinConfig(portalName, cssPath, false);
//skinConfigs_.put(key, portalSkinConfig);
//}
//return portalSkinConfig;
//}


  private void mergeCSS(String cssPath, ServletContext scontext) {
    if (cacheResource_) {
      String relativeCSSPath = cssPath.substring(cssPath.indexOf("/", 2));
      Pattern pattern = Pattern.compile(REGEXP);
      StringBuffer sB = new StringBuffer();

      String resolvedPath = relativeCSSPath.substring(0, relativeCSSPath
          .lastIndexOf("/") + 1);
      String includedPath = relativeCSSPath.substring(relativeCSSPath
          .lastIndexOf("/") + 1);
      processMergeRecursively(pattern, sB, scontext, resolvedPath, includedPath);
      mergedCSS_.put(cssPath, sB.toString());
    }
  }

  private void processMergeRecursively(Pattern pattern, StringBuffer sB,
      ServletContext scontext, String basePath, String pathToResolve) {
    String resolvedPath = basePath.substring(0, basePath.lastIndexOf("/") + 1)
    + pathToResolve;

    String line = "";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(scontext
          .getResourceAsStream(resolvedPath)));
      try {
        while ((line = reader.readLine()) != null) {
          Matcher matcher = pattern.matcher(line);
          if (matcher.find()) {
            String includedPath = "";
            if (line.contains("url('") || line.contains("url(\"")) {
              includedPath = line.substring("@import url(".length() + 1, line
                  .indexOf(")") - 1);
            } else {
              includedPath = line.substring("@import url(".length(), line
                  .indexOf(")"));
            }
            if(includedPath.startsWith("/")) {
              String targetedContextName = includedPath.substring(includedPath.indexOf("/"), 
                  includedPath.indexOf("/", 2));
              String targetedResolvedPath = includedPath.substring(includedPath.indexOf("/", 2), 
                  includedPath.lastIndexOf("/") + 1);
              String targetedIncludedPath = includedPath.substring(includedPath
                  .lastIndexOf("/") + 1);      
              ServletContext targetedContext = scontext.getContext(targetedContextName);

              StringBuffer tempSB = new StringBuffer();
              processMergeRecursively(pattern, tempSB, targetedContext, 
                  targetedResolvedPath, targetedIncludedPath );
              sB.append(tempSB);
            } else 
              processMergeRecursively(pattern, sB, scontext, resolvedPath, includedPath);
          } else {
            String rootContext = resolvedPath.substring(0, resolvedPath
                .lastIndexOf("/") + 1);
            String rootURL = "/" + scontext.getServletContextName()
            + rootContext;
            rewriteLine(line, rootURL, sB);
          }
        }
      } catch (Exception ex) {
        log.error("Problem while processing line : " + line, ex);
      } finally {
        try {
          reader.close();
        } catch (Exception ex) {
        }
      }
    } catch (Exception e) {
      log.error("Problem while merging CSS : " + resolvedPath, e);
    }

  }

  private void rewriteLine(String line, String basePath, StringBuffer sB) {
    Pattern backgroundPattern = Pattern.compile(BACKGROUND_REGEXP);
    Matcher matcher = backgroundPattern.matcher(line);
    if ((!matcher.find()) || line.contains("url(/") || line.contains("url('/")
        || line.contains("url(\"/")) {
      sB.append(line + "\n");
      return;
    }
    int firstIndex = line.indexOf("url(") + "url(".length();
    int lastIndex = line.indexOf(")");
    sB.append(line.substring(0, firstIndex));
    String urlToRewrite = "";
    if (line.contains("url('") || line.contains("url(\"")) {
      urlToRewrite = line.substring(firstIndex + 1, lastIndex - 1);
    } else {
      urlToRewrite = line.substring(firstIndex, lastIndex);
    }
    sB.append(basePath + urlToRewrite);
    sB.append(line.substring(lastIndex) + "\n");

  }
}