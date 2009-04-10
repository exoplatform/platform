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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import javax.servlet.ServletContext;

/**
 * The skin service.
 * 
 * @deprecated Since 2.6, this class has been moved to the component.portal module.
 * that helps it will be available in large scope in Portal
 */
@Deprecated
public interface SkinService {

  /**
   * Merge several skins into one single skin.
   *
   * @param skins the skins to merge
   * @return the merged skin
   */
  Skin merge(Collection<SkinConfig> skins);

  /**
   * Retrieves the css or return null if no css can be located.
   *
   * @param cssPath the path
   * @return the css
   */
  String getCSS(String cssPath);

  /**
   * Add a resource resolver to plug external resolvers.
   *
   * @param resolver a resolver to add
   */
  void addResourceResolver(ResourceResolver resolver);

  void renderCSS(ResourceRenderer renderer, String path) throws IOException, RenderingException;

  void addTheme(String categoryName, List<String> themesName);

  Map<String, Set<String>> getPortletThemes();

  /**
   * Returns an iterator over the available skins
   *
   * @return the available skins
   * @deprecated use getAvailableSkinNames()
   */
  Iterator<String> getAvailableSkins();

  void invalidatePortalSkinCache(String portalName, String skinName);

  /**
   * Get the names of all the currently registered skins.
   *
   * @return an unmodifiable Set of the currently registered skins
   */
  Set<String> getAvailableSkinNames();
  
  Collection<SkinConfig> getPortalSkins(String skinName);

  SkinConfig getSkin(String module, String skinName);

  String getMergedCSS(String cssPath);

  void addSkin(String module, String skinName, String cssPath, String cssData);

  void addPortalSkin(
    String module,
    String skinName,
    String cssPath,
    ServletContext scontext);

  void addPortalSkin(
    String module,
    String skinName,
    String cssPath,
    String cssData);

  /**
   * Register the stylesheet for a portal Skin.
   *
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link javax.servlet.ServletContext}
   * @param overwrite if any previous skin should be replaced by that one
   */
  void addPortalSkin(
    String module,
    String skinName,
    String cssPath,
    ServletContext scontext,
    boolean overwrite);
}