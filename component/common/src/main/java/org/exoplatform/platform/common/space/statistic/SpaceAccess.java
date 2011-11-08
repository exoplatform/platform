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
package org.exoplatform.platform.common.space.statistic;

import org.chromattic.api.annotations.FormattedBy;
import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.NamingPrefix;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;
import org.chromattic.ext.format.BaseEncodingObjectFormatter;

@PrimaryType(name = "plf:spaceaccess")
@FormattedBy(BaseEncodingObjectFormatter.class)
@NamingPrefix("plf")
public abstract class SpaceAccess {

  @Name
  public abstract String getName();

  @Property(name = "plf:mostAccessedSpaces")
  public abstract String[] getMostAccessedSpaces();
  public abstract void setMostAccessedSpaces(String[] mostAccessedSpaces);
}