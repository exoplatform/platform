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
import java.io.UnsupportedEncodingException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.Orientation;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.commons.utils.Safe;
import org.exoplatform.commons.utils.CharEncoder;
import org.exoplatform.commons.utils.CharsetCharEncoder;

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

  private Map<SkinKey, SkinConfig> portalSkins_ ;

  private Map<SkinKey, SkinConfig> skinConfigs_;

  private HashSet<String> availableSkins_;

  private Map<String, String> ltCache;
  private Map<String, String> rtCache;

  private Map<String, Set<String>> portletThemes_;

  private Map<String, SimpleResourceContext> contexts;

  public SkinServiceImpl() {
    portalSkins_ = new HashMap<SkinKey, SkinConfig>() ;
    skinConfigs_ = new HashMap<SkinKey, SkinConfig>(20);
    availableSkins_ = new HashSet<String>(5);
    ltCache = new ConcurrentHashMap<String, String>();
    rtCache = new ConcurrentHashMap<String, String>();
    contexts = new HashMap<String, SimpleResourceContext>();
    portletThemes_ = new HashMap<String, Set<String>>();
  }

  /**
	 * Create a new portlet theme category
	 * @param categoryName portlet theme category
	 */
  public void addCategoryTheme(String categoryName) {
    if (!portletThemes_.containsKey(categoryName))
      portletThemes_.put(categoryName, new HashSet<String>());
  }

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link javax.servlet.ServletContext}
   */
  public void addPortalSkin(String module, String skinName, String cssPath, ServletContext scontext) {
    addPortalSkin(module, skinName, cssPath, scontext, false) ;
  }

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link javax.servlet.ServletContext}
   * @param overwrite
   */
  public void addPortalSkin(String module, String skinName, String cssPath,
      ServletContext scontext, boolean overwrite) {

    // Triggers a put if absent
    getResourceContext(scontext);

    availableSkins_.add(skinName) ;
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null || overwrite) {
    	skinConfig = new SimpleSkin(module, skinName, cssPath) ;
			portalSkins_.put(key, skinConfig);
    }
  }

  public void addPortalSkin(String module, String skinName, String cssPath, String cssData) {
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = portalSkins_.get(key);
    if (skinConfig == null) {
      portalSkins_.put(key, new SimpleSkin(module, skinName, cssPath));
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

  private static final char[][] table = new char[256][];

  static {
    char[] a = "0123456789ABCDEF".toCharArray();
    for (int b = 0;b < 256;b++) {
      int b1 = (b & 0xF0) >> 4;
      int b2 = b & 0x0F;
      table[b] = new char[]{a[b1],a[b2]};
    }
  }

  private String decode(String s) {
    try {
      s = s.replace("_", "%2F");
      return URLDecoder.decode(s, "UTF8");
    }
    catch (UnsupportedEncodingException e) {
      throw new Error(e);
    }
  }

  private void encode(Appendable appendable, String s) throws IOException {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isLetter(c)) {
        appendable.append(c);
      } else {
        switch (c) {
          case 'A':
          case '.':
          case '-':
          case '*':
            appendable.append(c);
            break;
          case ' ':
            appendable.append('+');
            break;
          case '/':
            appendable.append('_');
            break;
          default:
            CharEncoder encoder = CharsetCharEncoder.getUTF8();
            byte[] bytes = encoder.encode(c);
            appendable.append('%');
            for (byte b : bytes) {
              for (char cc : table[b]) {
                appendable.append(cc);
              }
            }
        }
      }
    }
  }

  public Skin merge(Collection<SkinConfig> skins) {
    TreeMap<String, SkinConfig> urlSkins = new TreeMap<String, SkinConfig>();
    for (SkinConfig skin : skins) {
      urlSkins.put(skin.getCSSPath(), skin);
    }

    //
    final StringBuilder builder = new StringBuilder();
    builder.append("/portal/resource");

    //
    try {
      for (SkinConfig cfg : urlSkins.values()) {
        StringBuilder encodedName = new StringBuilder();
        encode(encodedName, cfg.getName());
        StringBuilder encodedModule = new StringBuilder();
        encode(encodedModule, cfg.getModule());
        builder.append("/").append(encodedName).append("/").append(encodedModule);
      }
    }
    catch (IOException e) {
      throw new Error(e);
    }

    //
    return new Skin() {
      public String getId() {

        // todo
        return "FOO";
      }

      public SkinURL createURL() {
        return new SkinURL() {

          Orientation orientation;

          public void setOrientation(Orientation orientation) {
            this.orientation = orientation;
          }

          @Override
          public String toString() {
            Orientation o = orientation == null ? Orientation.LT : orientation;
            return builder.toString() + "/style" + SkinServiceImpl.getSuffix(o);
          }
        };
      }
    };
  }

  public void addSkin(String module, String skinName, String cssPath, ServletContext scontext, boolean isPrimary) {

    // Triggers a put if absent
    getResourceContext(scontext);

    availableSkins_.add(skinName);
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null || isPrimary) {
    	skinConfig = new SimpleSkin(module, skinName, cssPath);
    	skinConfigs_.put(key, skinConfig);
    }
  }

  public void addSkin(String module, String skinName, String cssPath, String cssData) {
    availableSkins_.add(skinName);
    SkinKey key = new SkinKey(module, skinName);
    SkinConfig skinConfig = skinConfigs_.get(key);
    if (skinConfig == null) {
      skinConfigs_.put(key, new SimpleSkin(module, skinName, cssPath));
    }
    ltCache.put(cssPath, cssData);
    rtCache.put(cssPath, cssData);
  }

  private SimpleResourceContext getResourceContext(ServletContext servletContext) {
    String key = "/" + servletContext.getServletContextName();
    SimpleResourceContext ctx = contexts.get(key);
    if (ctx == null) {
      ctx = new SimpleResourceContext(key, servletContext);
      contexts.put(ctx.getContextPath(), ctx);
    }
    return ctx;
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
    String css;

    //
    Orientation orientation = Orientation.LT;
    if (cssPath.endsWith("-lt.css")) {
      cssPath = cssPath.substring(0, cssPath.length() - "-lt.css".length()) + ".css";
    } else if (cssPath.endsWith("-rt.css")) {
      cssPath = cssPath.substring(0, cssPath.length() - "-rt.css".length()) + ".css";
      orientation = Orientation.RT;
    }

    // Try cache first
    if (!PropertyManager.isDevelopping()) {
      Map<String, String> cache = orientation == Orientation.LT ? ltCache : rtCache;
      css = cache.get(cssPath);
      if (css == null) {
        css = processCSS(cssPath, orientation, true);
        cache.put(cssPath, css);
      }
    } else {
      css = processCSS(cssPath, orientation, false);
    }

    //
    return css;
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

  public void setPortletThemes(Map<String, Set<String>> portletThemes_) {
    this.portletThemes_ = portletThemes_;
  }

  public int size() {
    return skinConfigs_.size();
  }

  private Resource getResource(final String resourcePath) {
      if (resourcePath.startsWith("/portal/resource/") && resourcePath.endsWith("/style.css")) {
        return new Resource(new ResourceResolver() {
          public Reader resolve(String path) {
            StringBuffer sb = new StringBuffer();
            String encoded = resourcePath.substring("/portal/resource".length() + 1, resourcePath.length() - "/style.css".length());
            String blah[] = encoded.split("/");
            for (int i = 0; i < blah.length; i += 2) {
              String name = decode(blah[i]);
              String module = decode(blah[i + 1]);
              SkinKey key = new SkinKey(module, name);
              SkinConfig skin = skinConfigs_.get(key);
              if (skin != null) {
                sb.append("@import url(").append(skin.getCSSPath()).append(");").append("\n");
              }
            }
            return new StringReader(sb.toString());
          }
        }, resourcePath);
      } else {
        int i1 = resourcePath.indexOf("/", 2);
        String targetedContextPath = resourcePath.substring(0, i1);
        SimpleResourceContext servletContext = contexts.get(targetedContextPath);
        return servletContext.getResource(resourcePath.substring(i1));
      }
  }

  private String processCSS(String cssPath, Orientation orientation, boolean merge) {
    Resource skin = getResource(cssPath);
    StringBuffer sB = new StringBuffer();
    processCSSRecursively(sB, merge, skin, orientation);
    return sB.toString();
  }

  private void processCSSRecursively(
      StringBuffer sB,
      boolean merge,
      Resource skin,
      Orientation orientation) {

    // The root URL for the entry
    String basePath = skin.getContextPath() + skin.getParentPath();

    //
    String line = "";
    try {
      Reader tmp = skin.read();
      BufferedReader reader = new BufferedReader(tmp);
      try {
        while ((line = reader.readLine()) != null) {
          Matcher matcher = IMPORT_PATTERN.matcher(line);
          if (matcher.find()) {
            String includedPath = matcher.group(2);
            if(includedPath.startsWith("/")) {
            	if(merge) {
                Resource ssskin = getResource(includedPath);
                processCSSRecursively(sB, merge, ssskin, orientation);
            	} else {
                sB.append(matcher.group(1)).
                   append(includedPath.substring(0, includedPath.length() - ".css".length())).
                   append(getSuffix(orientation)).
                   append(matcher.group(3)).
                   append("\n");
            	}
            } else {
            	if(merge) {
                String path = skin.getContextPath() + skin.getParentPath() + includedPath;
                Resource ssskin = getResource(path);
            		processCSSRecursively(sB, merge, ssskin, orientation);
            	} else {
              	sB.append(matcher.group(1));
              	sB.append(basePath);
              	sB.append(includedPath.substring(0, includedPath.length() - ".css".length()));
              	sB.append(getSuffix(orientation));
              	sB.append(matcher.group(3));
            	}
            }
          } else {
            if (orientation == null || wantInclude(line, orientation)) {
              append(line, basePath, sB);
            }
          }
        }
      } catch (Exception ex) {
        log.error("Problem while processing line : " + line, ex);
      } finally {
        Safe.close(reader);
      }
    } catch (Exception e) {
      log.error("Problem while merging CSS : " + skin.getResourcePath(), e);
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

  private void append(String line, String basePath, StringBuffer sB) {
    // Rewrite background url pattern
    Matcher matcher = BACKGROUND_PATTERN.matcher(line);
    if (matcher.find() && !matcher.group(2).startsWith("\"/") && !matcher.group(2).startsWith("'/") && !matcher.group(2).startsWith("/")) {
      sB.append(matcher.group(1)).append(basePath).append(matcher.group(2)).append(matcher.group(3)).append('\n');
    } else {
      sB.append(line).append('\n');
    }
  }

  static String getSuffix(Orientation orientation) {
    return suffixMap.get(orientation);
  }
}