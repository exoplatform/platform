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
  
  public static final String DEFAULT_SKIN = "Default";

  private static final String SKIN_KEY_SEP = "$";

  private final static String REGEXP = "@import url(.*).*;";

  private final static String BACKGROUND_REGEXP = "background.*:.*url(.*).*;";

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

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link ServletContext}
   */
  public void addPortalSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addPortalSkin(module, skinName, cssPath, scontext, false) ;
  }

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link ServletContext}
   * @param isPrimary set to true to override an existing portal skin config
   */
  public void addPortalSkin(String module, String skinName, String cssPath,
      ServletContext scontext, boolean isPrimary) {
    availableSkins_.add(skinName) ;
    String key = skinConfigKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null || isPrimary) {
      portalSkins_.put(key, new SkinConfig(module, cssPath));
      mergeCSS(cssPath, scontext);
    }
  }

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param cssData the actual css code for the skin
   */
  public void addPortalSkin(String module,String skinName, String cssPath, String cssData) {
    String key = skinConfigKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null) {
      portalSkins_.put(key, new SkinConfig(module, cssPath));      
    }
    mergedCSS_.put(cssPath, cssData);
  }

  /**
   * Register a portlet stylesheet for a Skin.
   * @param module skin module. Typically of the form 'portletAppName/portletName' .
   * @param skinName Name of the skin
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link ServletContext}
   */
  public void addSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addSkin(module, skinName, cssPath, scontext, false);
  }

  /**
   * Register a portlet stylesheet for a Skin.
   * @param module skin module. Typically of the form 'portletAppName/portletName' .
   * @param skinName Name of the skin
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link ServletContext}
   * @param isPrimary  set to true to override an existing portlet skin config
   */
  public void addSkin(String module, String skinName, String cssPath,
      ServletContext scontext, boolean isPrimary) {
    availableSkins_.add(skinName);
    String key = skinConfigKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null || isPrimary) {
      skinConfigs_.put(key, new SkinConfig(module, cssPath));
      mergeCSS(cssPath, scontext);
    }
  }

  /**
   * Register a portlet stylesheet for a Skin.
   * @param module skin module. Typically of the form 'portletAppName/portletName' .
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param cssData the actual css code for the skin
   */  
  public void addSkin(String module, String skinName, String cssPath, String cssData) {
    availableSkins_.add(skinName);
    String key = skinConfigKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);    
    if (skinConfig == null) {      
      skinConfigs_.put(key, new SkinConfig(module, cssPath));      
    }
    mergedCSS_.put(cssPath,cssData);
  }

  /**
   * Register multiple portlet themes
   * @param categoryName portlet theme category
   * @param themesName names of the themes
   */
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
   * Create a new portlet theme category
   * @param categoryName portlet theme category
   */
  public void addCategoryTheme(String categoryName) {
    if (portletThemes_ == null)
      portletThemes_ = new HashMap<String, Set<String>>();
    if (!portletThemes_.containsKey(categoryName))
      portletThemes_.put(categoryName, new HashSet<String>());
  }  
  

  /**
   * Get all registered portlet themes
   * @return
   */
  public Map<String, Set<String>> getPortletThemes() {
    return portletThemes_;
  }
  
  /**
   * Set all portlet themes in one call
   * @param portletThemes keys are theme category, values are theme names
   */
  public void setPortletThemes(Map<String, Set<String>> portletThemes) {
    this.portletThemes_ = portletThemes;
  }
  

  /**
   * @deprecated use getAvailableSkinNames() 
   */
  public Iterator<String> getAvailableSkins() {
    return availableSkins_.iterator();
  }
  
  /**
   * Get names of all the currently registered skins.
   * @return a Set of the currently registered skins
   */
  public Set<String> getAvailableSkinNames() {
    return availableSkins_;
  }

  /**
   * Get the merged css content for a given cssPath
   * @param cssPath
   * @return
   */
  public String getMergedCSS(String cssPath) {
    return mergedCSS_.get(cssPath);
  }

  /**
   * Get all portal skin configurations for a given skin
   * @param skinName name of the skin
   * @return all configs that have been registered for this skinName
   */
  public Collection<SkinConfig> getPortalSkins(String skinName) {
    Set<String> keys = portalSkins_.keySet();
    Collection<SkinConfig> portalSkins = new ArrayList<SkinConfig>() ;
    for(String key : keys) {
      if(key.endsWith(SKIN_KEY_SEP + skinName)) portalSkins.add(portalSkins_.get(key)) ;
    }
    return portalSkins ;
  }

  /**
   * Get a skin configuration for a given Skin
   * @param module skin module such as registered in {@link #addSkin(String, String, String, ServletContext)}
   * @param skinName skin name
   * @return the skin configuration or, if not found try to find the default skin
   */
  public SkinConfig getSkin(String module, String skinName) {
    SkinConfig config = skinConfigs_.get(skinConfigKey(module, skinName)) ;
    if(config == null) skinConfigs_.get(skinConfigKey(module, DEFAULT_SKIN)) ;
    return config;
  }

  /**
   * Get the skin configuration by ID
   * @param moduleId Identifier of the skin. In the form of module$skinName
   * @see #addSkin(String, String, String, ServletContext)
   * @return
   */
  public SkinConfig getSkin(String moduleId) {
    return skinConfigs_.get(moduleId);
  } 
  
  /**
   * Unregister a skin.
   * @param module skin module such as registered in {@link #addSkin(String, String, String, ServletContext)}
   * @param skinName name of the skin. If empty 'Default' will be used.
   * @throws Exception
   */
  public void remove(String module, String skinName) throws Exception {
    String key = skinConfigKey(module, skinName);
    if (skinName == null || skinName.length() == 0) 
      key = skinConfigKey(module, DEFAULT_SKIN);
    skinConfigs_.remove(key);
  }

  /**
   * Remove a skin configuration by key.
   * @param moduleId identifier of the module. In the form of module$skinName
   * @return
   */  
  public void remove(String moduleId) throws Exception {
    skinConfigs_.remove(moduleId);
  }
  
  /**
   * @deprecated use {@link #remove(String, String)}
   */
  public void invalidatePortalSkinCache(String portalName, String skinName) {
    String key = skinConfigKey(portalName, skinName);
    skinConfigs_.remove(key);
  }

  /**
   * Get the configurations skins
   * @return number of registered skin configs
   */
  public int size() {
    return skinConfigs_.size();
  }
  
  private String skinConfigKey(String module, String skinName) {
    return module + SKIN_KEY_SEP + skinName;
  }

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