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
package org.exoplatform.portal.application;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
enum ImageType {

  GIF("gif"),
  PNG("png"),
  JPG("jpg");

  private final char x,y,z;
  private final char X,Y,Z;
  private final String format;
  private final String mimeType;

  ImageType(String id) {
    this.x = id.charAt(0);
    this.y = id.charAt(1);
    this.z = id.charAt(2);
    this.X = Character.toUpperCase(x);
    this.Y = Character.toUpperCase(y);
    this.Z = Character.toUpperCase(z);
    this.format = id;
    this.mimeType = "image/" + id;
  }

  public String getFormat() {
    return format;
  }

  public boolean matches(String s) {
    int len = s.length();
    if (len-- > 4)
    {
      char c = s.charAt(len--);
      if (c == z || c == Z) {
        char b = s.charAt(len--);
        if (b == y || b == Y) {
          char a = s.charAt(len);
          if (a == x || a == X) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public String getMimeType() {
    return mimeType;
  }
}
