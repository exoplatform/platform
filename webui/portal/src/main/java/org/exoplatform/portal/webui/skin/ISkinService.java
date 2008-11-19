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
import org.exoplatform.commons.utils.PropertyManager;

public interface ISkinService {

  String getCSS(String cssPath);

  void addTheme(String categoryName, List<String> themesName);

  Map<String, Set<String>> getPortletThemes();

  /**
   * Returns an iterator over the available skins
   *
   * @return the available skins
   * @deprecated use getAvailableSkinNames()
   */
  Iterator<String> getAvailableSkins();

  /**
   * Get the names of all the currently registered skins.
   *
   * @return an unmodifiable Set of the currently registered skins
   */
  Set<String> getAvailableSkinNames();
  Collection<SkinConfig> getPortalSkins(String skinName);

  SkinConfig getSkin(String module, String skinName);

  String getMergedCSS(String cssPath);

  void addPortalSkin(
    String module,
    String skinName,
    String cssPath,
    ServletContext scontext);

  void addPortalSkin(String module, String skinName, String cssPath, String cssData);

  /**
   * Register the stylesheet for a portal Skin.
   * @param module skin module identifier
   * @param skinName skin name
   * @param cssPath path uri to the css file. This is relative to the root context, use leading '/'
   * @param scontext the webapp's {@link javax.servlet.ServletContext}
   * @param isPrimary set to true to override an existing portal skin config
   */
  void addPortalSkin(
    String module,
    String skinName,
    String cssPath,
    ServletContext scontext,
    boolean isPrimary);
}