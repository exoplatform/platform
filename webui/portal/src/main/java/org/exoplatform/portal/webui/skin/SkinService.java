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
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.Orientation;

public class SkinService {

  protected static Log log = ExoLogger.getLogger("portal.SkinService");

  private static final Map<Orientation, String> suffixMap = new EnumMap<Orientation, String>(Orientation.class);

  static
  {
    suffixMap.put(Orientation.LT, "-lt.css");
    suffixMap.put(Orientation.RT, "-rt.css");
    suffixMap.put(Orientation.TL, "-lt.css");
    suffixMap.put(Orientation.TR, "-lt.css");
  }

  private static final String LEFT_P = "\\(";
  private static final String RIGHT_P = "\\)";

  /** Immutable and therefore thread safe. */
  private static final Pattern IMPORT_PATTERN = Pattern.compile("(@import\\s+" + "url" + LEFT_P + "['\"]?" + ")([^'\"]+)(" + "['\"]?" + RIGHT_P + "\\s*;)");

  /** Immutable and therefore thread safe. */
  private static final Pattern BACKGROUND_PATTERN = Pattern.compile("(background\\s+:\\s+url" + LEFT_P + "['\"]?" + ")([^'\"]+)(" + "['\"]?" + RIGHT_P + "\\s*;)");

  /** Immutable and therefore thread safe. */
  private static final Pattern LT = Pattern.compile("/\\*\\s*orientation=lt\\s*\\*/");

  /** Immutable and therefore thread safe. */
  private static final Pattern RT = Pattern.compile("/\\*\\s*orientation=rt\\s*\\*/");

  private Map<SkinKey, SkinConfig> portalSkins_ ;

  private Map<SkinKey, SkinConfig> skinConfigs_;

  private HashSet<String> availableSkins_;

  private Map<String, String> cssCache_;

  private Map<String, Set<String>> portletThemes_;

  private boolean cacheResource_;

  public SkinService() {
    portalSkins_ = new HashMap<SkinKey, SkinConfig>() ;
    skinConfigs_ = new HashMap<SkinKey, SkinConfig>(20);
    availableSkins_ = new HashSet<String>(5);
    cacheResource_ = !"true".equals(System
        .getProperty("exo.product.developing"));
    cssCache_ = new HashMap<String, String>();
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
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param orientation Orientation
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link ServletContext}
   */
  public void addPortalSkin(String module, String skinName, Orientation orientation, String cssPath, ServletContext scontext) {
    addPortalSkin(module, skinName, orientation, cssPath, scontext, false) ;
  }

  public void addPortalSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addPortalSkin(module, skinName, Orientation.LT, cssPath, scontext) ;
    addPortalSkin(module, skinName, Orientation.RT, cssPath, scontext) ;
  }

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param orientation Orientation
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link ServletContext}
   * @param isPrimary set to true to override an existing portal skin config
   */
  /**
   * todo: check we can remove
   */
  public void addPortalSkin(String module, String skinName, Orientation orientation, String cssPath,
      ServletContext scontext, boolean isPrimary) {
    availableSkins_.add(skinName) ;
    SkinKey key = new SkinKey(module, skinName, orientation);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null || isPrimary) {
    	skinConfig = new SkinConfig(module, cssPath, orientation) ;
			portalSkins_.put(key, skinConfig);
      cacheCSS(skinConfig, scontext);
    }
  }

  /**
   * todo: check we can remove
   */
  public void addPortalSkin(String module, String skinName, String cssPath, String cssData) {
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null) {
      portalSkins_.put(key, new SkinConfig(module, cssPath));      
    }
    cssCache_.put(cssPath, cssData);
  }

  public void addSkin(
    String module,
    String skinName,
    Orientation orientation,
    String cssPath,
    ServletContext scontext) {
    addSkin(module, skinName, orientation, cssPath, scontext, false);
  }

  /**
   * 
   * @param module
   * @param skinName
   * @param cssPath
   */
  public void addSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addSkin(module, skinName, Orientation.LT, cssPath, scontext);
    addSkin(module, skinName, Orientation.RT, cssPath, scontext);
  }

  /**
   * todo: check we can remove
   */
  public void addSkin(String module, String skinName, Orientation orientation, String cssPath, ServletContext scontext, boolean isPrimary) {
    availableSkins_.add(skinName);
    SkinKey key = new SkinKey(module, skinName, orientation);
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null || isPrimary) {
    	skinConfig = new SkinConfig(module, cssPath, orientation);
    	skinConfigs_.put(key, skinConfig);
      cacheCSS(skinConfig, scontext);
    }
  }

  /**
   * todo: check we can remove
   */
  public void addSkin(String module, String skinName, String cssPath, String cssData) {
    availableSkins_.add(skinName);
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);    
    if (skinConfig == null) {      
      skinConfigs_.put(key, new SkinConfig(module, cssPath));      
    }
    cssCache_.put(cssPath,cssData);
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
   * @deprecated use getAvailableSkinNames()
   */
  public Iterator<String> getAvailableSkins() {
    return availableSkins_.iterator();
  }
	/**
	 * Get names of all the currently registered skins.
	 * @return an unmodifiable Set of the currently registered skins
	 */
	public Set<String> getAvailableSkinNames() {
		return availableSkins_;
	}
  /**
   * Return the CSS content of the file specified by the given URI.
   *
   * @param cssPath path of the css to find
   * @param scontext the related servlet context
   * @return the css
   */
  public String getCSS(String cssPath, ServletContext scontext) {
    String css = null;

    // Try cache first
    if (cacheResource_) {
      css = cssCache_.get(cssPath);
    }

    //
    if (css == null) {
      Orientation orientation = Orientation.LT;
      if (cssPath.endsWith("-lt.css")) {
        cssPath = cssPath.substring(0, cssPath.length() - "-lt.css".length()) + ".css";
      } else if (cssPath.endsWith("-rt.css")) {
        cssPath = cssPath.substring(0, cssPath.length() - "-rt.css".length()) + ".css";
        orientation = Orientation.RT;
      }
      css = processCSS(cssPath, orientation, scontext, false);
    }

    //
    return css;
  }

  public String getMergedCSS(String cssPath) {
    return cssCache_.get(cssPath);
  }

  public Collection<SkinConfig> getPortalSkins(String skinName) {
    return getPortalSkins(skinName, Orientation.LT);
  }

  public Collection<SkinConfig> getPortalSkins(String skinName, Orientation orientation) {
    Set<SkinKey> keys = portalSkins_.keySet();
    Collection<SkinConfig> portalSkins = new ArrayList<SkinConfig>() ;
    for(SkinKey key : keys) {
      if(key.getName().equals(skinName) && key.getOrientation() == orientation) portalSkins.add(portalSkins_.get(key)) ;
    }
    return portalSkins ;
  }

  public Map<String, Set<String>> getPortletThemes() {
    return portletThemes_;
  }

  public SkinConfig getSkin(String module, String skinName) {
    return getSkin(module, skinName, Orientation.LT);
  }

  public SkinConfig getSkin(String module, String skinName, Orientation orientation) {
    SkinConfig config = skinConfigs_.get(new SkinKey(module, skinName, orientation)) ;
    if(config == null) skinConfigs_.get(new SkinKey(module, "Default", orientation)) ;
    return config;
  }

  public void invalidatePortalSkinCache(String portalName, String skinName) {
    SkinKey key = new SkinKey(portalName, skinName);
    skinConfigs_.remove(key);
  }

  public void remove(String module, String skinName) throws Exception {
    SkinKey key;
    if (skinName.length() == 0)
      key = new SkinKey(module, "Default");
    else
      key = new SkinKey(module, skinName);
    skinConfigs_.remove(key);
  }

  public void setPortletThemes(Map<String, Set<String>> portletThemes_) {
    this.portletThemes_ = portletThemes_;
  }

  public int size() {
    return skinConfigs_.size();
  }

  private void cacheCSS(SkinConfig config, ServletContext scontext) {
    if (cacheResource_) {
      String css = processCSS(config.getCSSPath(), config.getOrientation(), scontext, true);
      cssCache_.put(config.getVirtualCSSPath(), css);
    }
  }

  private String processCSS(String cssPath, Orientation orientation, ServletContext scontext, boolean merge) {
    String relativeCSSPath = cssPath.substring(cssPath.indexOf("/", 2));
    String resolvedPath = relativeCSSPath.substring(0, relativeCSSPath.lastIndexOf("/") + 1);
    String includedPath = relativeCSSPath.substring(relativeCSSPath.lastIndexOf("/") + 1);
    StringBuffer sB = new StringBuffer();
    processCSSRecursively(sB, merge, scontext, resolvedPath, includedPath, orientation);
    return sB.toString();
  }

  private void processCSSRecursively(
      StringBuffer sB,
      boolean merge,
      ServletContext scontext,
      String basePath,
      String pathToResolve,
      Orientation orientation) {
    String resolvedPath = basePath.substring(0, basePath.lastIndexOf("/") + 1) + pathToResolve;
    String rootContext = resolvedPath.substring(0, resolvedPath.lastIndexOf("/") + 1);
    String rootURL = "/" + scontext.getServletContextName() + rootContext;
    String line = "";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(scontext.getResourceAsStream(resolvedPath)));
      try {
        while ((line = reader.readLine()) != null) {
          Matcher matcher = IMPORT_PATTERN.matcher(line);
          if (matcher.find()) {
            String includedPath = matcher.group(2);
            if (merge) {
              if(includedPath.startsWith("/")) {
                int i1 = 0;
                int i2 = includedPath.indexOf("/", 2);
                int i3 = includedPath.lastIndexOf("/") + 1;
                String targetedContextName = includedPath.substring(i1, i2);
                String targetedResolvedPath = includedPath.substring(i2, i3);
                String targetedIncludedPath = includedPath.substring(i3);
                ServletContext targetedContext = scontext.getContext(targetedContextName);
                processCSSRecursively(sB, merge, targetedContext, targetedResolvedPath, targetedIncludedPath, orientation);
              } else
                processCSSRecursively(sB, merge, scontext, resolvedPath, includedPath, orientation);
            } else {
              sB.append(matcher.group(1));
              sB.append(rootURL);
              sB.append(includedPath.substring(0, includedPath.length() - ".css".length()));
              sB.append(getSuffix(orientation));
              sB.append(matcher.group(3));
            }
          } else {
            append(line, rootURL, sB, orientation);
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

  private void append(String line, String basePath, StringBuffer sB, Orientation orientation) {

    // Filter what if it's annotated with the alternative orientation
    Pattern orientationPattern = orientation == Orientation.LT ? RT : LT;
    Matcher matcher2 = orientationPattern.matcher(line);
    if (matcher2.find()) {
      return;
    }

    // Rewrite background url pattern
    Matcher matcher = BACKGROUND_PATTERN.matcher(line);
    if (matcher.find() && !matcher.group(2).startsWith("\"") && !matcher.group(2).startsWith("'")) {
      sB.append(matcher.group(1)).append(basePath).append(matcher.group(2)).append(matcher.group(3)).append('\n');
    }
    else {
      sB.append(line).append('\n');
    }
  }

  static String getSuffix(Orientation orientation) {
    return suffixMap.get(orientation);
  }


/*
  private void processCSSRecursively(StringBuffer sB, boolean merge,
      ServletContext scontext, String basePath, String pathToResolve) {
    String resolvedPath = basePath.substring(0, basePath.lastIndexOf("/") + 1) + pathToResolve;
    String rootContext = resolvedPath.substring(0, resolvedPath.lastIndexOf("/") + 1);
    String rootURL = "/" + scontext.getServletContextName() + rootContext;
    String line = "";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(scontext.getResourceAsStream(resolvedPath)));
      try {
        while ((line = reader.readLine()) != null) {
          Matcher matcher = IMPORT_PATTERN.matcher(line);
          if (matcher.find()) {
            String includedPath = matcher.group(1);
            if(includedPath.startsWith("/")) {
              int i1 = 0;
              int i2 = includedPath.indexOf("/", 2);
              int i3 = includedPath.lastIndexOf("/") + 1;
              String targetedContextName = includedPath.substring(i1, i2);
              String targetedResolvedPath = includedPath.substring(i2, i3);
              String targetedIncludedPath = includedPath.substring(i3);
              ServletContext targetedContext = scontext.getContext(targetedContextName);
              processCSSRecursively(sB, merge, targetedContext, targetedResolvedPath, targetedIncludedPath );
            } else 
              processCSSRecursively(sB, merge, scontext, resolvedPath, includedPath);
          } else {
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
    Matcher matcher = BACKGROUND_PATTERN.matcher(line);
    if (!matcher.find() || matcher.group(2).startsWith("\"") || matcher.group(2).startsWith("'")) {
      sB.append(line).append('\n');
      return;
    }
    sB.append(matcher.group(1));
    sB.append(basePath).append(matcher.group(2));
    sB.append(matcher.group(3)).append('\n');
  }
*/
}