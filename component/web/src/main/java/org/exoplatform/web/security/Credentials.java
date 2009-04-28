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
package org.exoplatform.web.security;

/**
 * An immutable object that contains a username and a password.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Credentials {

  /** . */
  private final String username;

  /** . */
  private final String password;

  /**
   * Construct a new instance.
   *
   * @param username the username value
   * @param password the password value
   * @throws NullPointerException if any argument is null
   */
  public Credentials(String username, String password) throws NullPointerException {
    if (username == null) {
      throw new NullPointerException("Username is null");
    }
    if (password == null) {
      throw new NullPointerException("Password is null");
    }
    this.username = username;
    this.password = password;
  }

  /**
   * Returns the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }
}
