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

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class CompositeResourceResolver implements ResourceResolver {

  /** . */
  private final Map<SkinKey, SkinConfig> skins;

  public CompositeResourceResolver(Map<SkinKey, SkinConfig> skins) {
    this.skins = skins;
  }

  public Resource resolve(String path) {
    if (path.startsWith("/portal/resource/") && path.endsWith(".css")) {
      final StringBuffer sb = new StringBuffer();
      String encoded = path.substring("/portal/resource/".length());
      String blah[] = encoded.split("/");
      int len = (blah.length >> 1) << 1;
      for (int i = 0; i < len; i += 2) {
        String name = Codec.decode(blah[i]);
        String module = Codec.decode(blah[i + 1]);
        SkinKey key = new SkinKey(module, name);
        SkinConfig skin = skins.get(key);
        if (skin != null) {
          sb.append("@import url(").append(skin.getCSSPath()).append(");").append("\n");
        }
      }
      return new Resource(path) {
        @Override
        public Reader read() {
          return new StringReader(sb.toString());
        }
      };
    } else {
      return null;
    }
  }
}
