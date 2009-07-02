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
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.exoplatform.services.log.Log;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.Orientation;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.commons.utils.Safe;

@Managed
@NameTemplate({
  @Property(key = "view", value = "portal"),
  @Property(key = "service", value = "management"),
  @Property(key="type", value="skin")
})
@ManagedDescription("Skin service")
public class SkinServiceImpl implements SkinService {

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
  private static final Pattern IMPORT_PATTERN = Pattern.compile("(@import\\s+" + "url" + LEFT_P + "['\"]?" + ")([^'\"]+.css)(" + "['\"]?" + RIGHT_P + "\\s*;)");

  /** Immutable and therefore thread safe. */
  private static final Pattern BACKGROUND_PATTERN = Pattern.compile("(background.*:.*url" + LEFT_P + "['\"]?" + ")([^'\"]+)(" + "['\"]?" + RIGHT_P + ".*;)");

  /** Immutable and therefore thread safe. */
  private static final Pattern LT = Pattern.compile("/\\*\\s*orientation=lt\\s*\\*/");

  /** Immutable and therefore thread safe. */
  private static final Pattern RT = Pattern.compile("/\\*\\s*orientation=rt\\s*\\*/");

  /** One month caching. */
  private static final int ONE_MONTH = 2592000;

  /** One hour caching. */
  private static final int ONE_HOUR = 3600;

  private final Map<SkinKey, SkinConfig> portalSkins_ ;
  private final Map<SkinKey, SkinConfig> skinConfigs_;
  private final HashSet<String> availableSkins_;
  private final Map<String, String> ltCache;
  private final Map<String, String> rtCache;
  private final Map<String, Set<String>> portletThemes_;
  private final MainResourceResolver mainResolver;

  /**
   * An id used for caching request. The id life cycle is the same than the class instance because
   * we consider css will change until server is restarted. Of course this only applies for the
   * developing mode set to false.
   */
  final String id = Long.toString(System.currentTimeMillis());

  public SkinServiceImpl() {
    portalSkins_ = new LinkedHashMap<SkinKey, SkinConfig>() ;
    skinConfigs_ = new LinkedHashMap<SkinKey, SkinConfig>(20);
    availableSkins_ = new HashSet<String>(5);
    ltCache = new ConcurrentHashMap<String, String>();
    rtCache = new ConcurrentHashMap<String, String>();
    portletThemes_ = new HashMap<String, Set<String>>();
    mainResolver = new MainResourceResolver(skinConfigs_);
  }

  public void addCategoryTheme(String categoryName) {
    if (!portletThemes_.containsKey(categoryName))
      portletThemes_.put(categoryName, new HashSet<String>());
  }

  public void addPortalSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addPortalSkin(module, skinName, cssPath, scontext, false) ;
  }

  public void addPortalSkin(String module, String skinName, String cssPath,
      ServletContext scontext, boolean overwrite) {

    // Triggers a put if absent
    mainResolver.registerContext(scontext);

    availableSkins_.add(skinName) ;
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null || overwrite) {
    	skinConfig = new SimpleSkin(this, module, skinName, cssPath) ;
			portalSkins_.put(key, skinConfig);
    }
  }

  public void addPortalSkin(String module, String skinName, String cssPath, String cssData) {
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null) {
      portalSkins_.put(key, new SimpleSkin(this, module, skinName, cssPath));
    }
    ltCache.put(cssPath, cssData);
    rtCache.put(cssPath, cssData);
  }

  public void addSkin(
    String module,
    String skinName,
    String cssPath,
    ServletContext scontext) {
    addSkin(module, skinName, cssPath, scontext, false);
  }

  public Skin merge(Collection<SkinConfig> skins) {
    return new CompositeSkin(this, skins);
  }

  public void addResourceResolver(ResourceResolver resolver) {
    mainResolver.resolvers.addIfAbsent(resolver);
  }

  public void addSkin(String module, String skinName, String cssPath, ServletContext scontext, boolean overwrite) {

    // Triggers a put if absent
    mainResolver.registerContext(scontext);

    availableSkins_.add(skinName);
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null || overwrite) {
    	skinConfig = new SimpleSkin(this, module, skinName, cssPath);
    	skinConfigs_.put(key, skinConfig);
    }
  }

  public void addSkin(String module, String skinName, String cssPath, String cssData) {
    availableSkins_.add(skinName);
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null) {
      skinConfigs_.put(key, new SimpleSkin(this, module, skinName, cssPath));
    }
    ltCache.put(cssPath, cssData);
    rtCache.put(cssPath, cssData);
  }


  public void addTheme(String categoryName, List<String> themesName) {
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
   * @return the css
   */
  public String getCSS(String cssPath) {
    try {
      final StringBuilder sb = new StringBuilder();
      renderCSS(new ResourceRenderer() {
        public Appendable getAppendable() {
          return sb;
        }
        public void setExpiration(long seconds) {

        }
      }, cssPath);
      return sb.toString();
    }
    catch (IOException e) {
      log.error("Error while rendering css " + cssPath, e);
      return null;
    }
    catch (RenderingException e) {
      log.error("Error while rendering css " + cssPath, e);
      return null;
    }
  }

  public void renderCSS(ResourceRenderer renderer, String path) throws RenderingException, IOException {
    Orientation orientation = Orientation.LT;
    if (path.endsWith("-lt.css")) {
      path = path.substring(0, path.length() - "-lt.css".length()) + ".css";
    } else if (path.endsWith("-rt.css")) {
      path = path.substring(0, path.length() - "-rt.css".length()) + ".css";
      orientation = Orientation.RT;
    }

    // Try cache first
    if (!PropertyManager.isDevelopping()) {

      if (path.startsWith("/portal/resource")) {
        renderer.setExpiration(ONE_MONTH);
      } else {
        renderer.setExpiration(ONE_HOUR);
      }

      //
      Map<String, String> cache = orientation == Orientation.LT ? ltCache : rtCache;
      String css = cache.get(path);
      if (css == null) {
        StringBuilder sb = new StringBuilder();
        processCSS(sb, path, orientation, true);
        css = sb.toString();
        cache.put(path, css);
      }
      renderer.getAppendable().append(css);
    } else {
      processCSS(renderer.getAppendable(), path, orientation, false);
    }
  }

  public String getMergedCSS(String cssPath) {
    return ltCache.get(cssPath);
  }

  public Collection<SkinConfig> getPortalSkins(String skinName) {
    Set<SkinKey> keys = portalSkins_.keySet();
    Collection<SkinConfig> portalSkins = new ArrayList<SkinConfig>() ;
    for(SkinKey key : keys) {
      if(key.getName().equals(skinName)) portalSkins.add(portalSkins_.get(key)) ;
    }
    return portalSkins ;
  }

  public Map<String, Set<String>> getPortletThemes() {
    return portletThemes_;
  }

  public SkinConfig getSkin(String module, String skinName) {
    SkinConfig config = skinConfigs_.get(new SkinKey(module, skinName)) ;
    if(config == null) skinConfigs_.get(new SkinKey(module, "Default")) ;
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

  public int size() {
    return skinConfigs_.size();
  }

  private void processCSS(Appendable appendable, String cssPath, Orientation orientation, boolean merge) throws RenderingException, IOException {
    Resource skin = mainResolver.resolve(cssPath);
    processCSSRecursively(appendable, merge, skin, orientation);
  }

  private void processCSSRecursively(
      Appendable appendable,
      boolean merge,
      Resource skin,
      Orientation orientation) throws RenderingException, IOException {

    // The root URL for the entry
    String basePath = skin.getContextPath() + skin.getParentPath();

    //
    String line = "";
    Reader tmp = skin.read();
    if (tmp == null) {
      throw new RenderingException("No skin resolved for path " + skin.getResourcePath());
    }
    BufferedReader reader = new BufferedReader(tmp);
    try {
      while ((line = reader.readLine()) != null) {
        Matcher matcher = IMPORT_PATTERN.matcher(line);
        if (matcher.find()) {
          String includedPath = matcher.group(2);
          if(includedPath.startsWith("/")) {
            if(merge) {
              Resource ssskin = mainResolver.resolve(includedPath);
              processCSSRecursively(appendable, merge, ssskin, orientation);
            } else {
              appendable.append(matcher.group(1)).
                 append(includedPath.substring(0, includedPath.length() - ".css".length())).
                 append(getSuffix(orientation)).
                 append(matcher.group(3)).
                 append("\n");
            }
          } else {
            if(merge) {
              String path = skin.getContextPath() + skin.getParentPath() + includedPath;
              Resource ssskin = mainResolver.resolve(path);
              processCSSRecursively(appendable, merge, ssskin, orientation);
            } else {
              appendable.append(matcher.group(1));
              appendable.append(basePath);
              appendable.append(includedPath.substring(0, includedPath.length() - ".css".length()));
              appendable.append(getSuffix(orientation));
              appendable.append(matcher.group(3));
            }
          }
        } else {
          if (orientation == null || wantInclude(line, orientation)) {
            append(line, basePath, appendable);
          }
        }
      }
    } finally {
      Safe.close(reader);
    }
  }

  /**
   * Filter what if it's annotated with the alternative orientation.
   *
   * @param line the line to include
   * @param orientation the orientation
   * @return true if the line is included
   */
  private boolean wantInclude(String line, Orientation orientation) {
    Pattern orientationPattern = orientation == Orientation.LT ? RT : LT;
    Matcher matcher2 = orientationPattern.matcher(line);
    return !matcher2.find();
  }

  private void append(String line, String basePath, Appendable appendable) throws IOException {
    // Rewrite background url pattern
    Matcher matcher = BACKGROUND_PATTERN.matcher(line);
    if (matcher.find() && !matcher.group(2).startsWith("\"/") && !matcher.group(2).startsWith("'/") && !matcher.group(2).startsWith("/")) {
      appendable.append(matcher.group(1)).append(basePath).append(matcher.group(2)).append(matcher.group(3)).append('\n');
    } else {
      appendable.append(line).append('\n');
    }
  }

  String getSuffix(Orientation orientation) {
    if (orientation == null) {
      orientation = Orientation.LT;
    }
    return suffixMap.get(orientation);
  }
  
  @Managed
  @ManagedDescription ("The list of registered skins identifiers")
  public String[] getSkinList() {
	// get all available skin
    List<String> availableSkin = new ArrayList<String>() ;
    for(String skin : availableSkins_) {
    	availableSkin.add(skin) ;
    }
    // sort skin name asc
    Collections.sort(availableSkin);
    
    return availableSkin.toArray(new String[availableSkin.size()]);
  }
  
  @Managed
  @ManagedDescription ("Reload all skins")
  public void reloadSkins() {
	// remove all ltCache, rtCache
    ltCache.clear();
    rtCache.clear();
  }
  
  @Managed
  @ManagedDescription ("Reload a specified skin")
  public void reloadSkin(@ManagedDescription("The skin id") @ManagedName("skinId") String skinId) {
	  ltCache.remove(skinId);
	  rtCache.remove(skinId);
  }
}