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
package org.exoplatform.portal.skin;

import org.exoplatform.services.resources.Orientation;
import org.exoplatform.commons.utils.PropertyManager;

import java.util.Collection;
import java.util.TreeMap;
import java.io.IOException;

/**
 * A composite skin.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class CompositeSkin implements Skin {

  /** . */
  private final SkinService service;

  /** . */
  private final String id;

  /** . */
  private final String urlPrefix;

  CompositeSkin(SkinService service, Collection<SkinConfig> skins) {
    TreeMap<String, SkinConfig> urlSkins = new TreeMap<String, SkinConfig>();
    for (SkinConfig skin : skins) {
      urlSkins.put(skin.getCSSPath(), skin);
    }

    //
    final StringBuilder builder = new StringBuilder();
    builder.append("/portal/resource");

    //
    final StringBuilder id = new StringBuilder();

    //
    try {
      for (SkinConfig cfg : urlSkins.values()) {
        StringBuilder encodedName = new StringBuilder();
        Codec.encode(encodedName, cfg.getName());
        StringBuilder encodedModule = new StringBuilder();
        Codec.encode(encodedModule, cfg.getModule());

        //
        id.append(encodedName).append(encodedModule);
        builder.append("/").append(encodedName).append("/").append(encodedModule);
      }
    }
    catch (IOException e) {
      throw new Error(e);
    }

    //
    this.service = service;
    this.id = id.toString();
    this.urlPrefix = builder.toString();
  }

  public String getId() {
    return id;
  }

  public SkinURL createURL() {
    return new SkinURL() {

      Orientation orientation;

      public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
      }

      @Override
      public String toString() {
        return urlPrefix +
          "/" + (PropertyManager.isDevelopping() ? "style" : service.id) +
          service.getSuffix(orientation);
      }
    };
  }
}
