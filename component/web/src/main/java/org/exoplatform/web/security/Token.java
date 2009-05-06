/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.web.security;

/**
 * Created by The eXo Platform SAS
 * Author : Tan Pham Dinh
 *          tan.pham@exoplatform.com
 * May 6, 2009  
 */
public class Token {

  public static String      EXPIRE_MILI = "expirationMilis";

  public static String      USERNAME    = "userName";

  public static String      PASSWORD    = "password";

  /** . */
  private final long        expirationTimeMillis;

  /** . */
  private final Credentials payload;

  public Token(long expirationTimeMillis, Credentials payload) {
    this.expirationTimeMillis = expirationTimeMillis;
    this.payload = payload;
  }

  public long getExpirationTimeMillis() {
    return expirationTimeMillis;
  }

  public Credentials getPayload() {
    return payload;
  }
}
