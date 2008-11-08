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

import org.exoplatform.services.resources.Orientation;

/**
 * A key for skin config lookup.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class SkinKey {

  private final String base;
  private final String name;
  private final Orientation orientation;
  private final int hashCode;

  /**
   * Creates a new skin key.
   *
   * @param base the skin base
   * @param name the skin name
   * @throws IllegalArgumentException if any argument is null
   */
  public SkinKey(String base, String name) {
    this(base, name, Orientation.LT);
  }

  /**
   * Creates a new skin key.
   *
   * @param base the skin base
   * @param name the skin name
   * @param orientation the skin orientation
   * @throws IllegalArgumentException if any argument is null
   */
  public SkinKey(String base, String name, Orientation orientation) throws IllegalArgumentException {
    if (base == null) {
      throw new IllegalArgumentException("No null base accepted");
    }
    if (name == null) {
      throw new IllegalArgumentException("No null skin name accepted");
    }
    if (orientation == null) {
      throw new IllegalArgumentException("No null orientation accepted");
    }

    //
    this.base = base;
    this.name = name;
    this.orientation = orientation;
    this.hashCode = (base.hashCode() * 41 + name.hashCode()) * 41 + orientation.hashCode();
  }

  public String getBase() {
    return base;
  }

  public String getName() {
    return name;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public int hashCode() {
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof SkinKey) {
      SkinKey that = (SkinKey)obj;
      return that.orientation.equals(orientation) && that.base.equals(base) && that.name.equals(name);
    }
    return false;
  }

  public String toString() {
    return "SkinKey[base=" + base + ",name=" + name + ",orientation=" + orientation + "]";
  }
}
