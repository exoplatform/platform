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

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * A login module implementation that relies on the token store to check the password validity. If the token store
 * provides a valid {@link Credentials} value then password stacking is used
 * and the two entries are added in the shared state map. The first entry is keyed by <code>javax.security.auth.login.name</code>
 * and contains the {@link Credentials#getUsername()} value, the second entry is keyed by <code>javax.security.auth.login.password</code>
 * and contains the {@link Credentials#getPassword()} ()} value.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PortalLoginModule implements LoginModule {

  /** . */
  protected Subject subject;

  /** . */
  protected CallbackHandler callbackHandler;

  /** . */
  protected Map<String, ?> sharedState;

  /** . */
  protected Map<String, ?> options;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;
  }

  public boolean login() throws LoginException {

  	Callback[] callbacks = new Callback[2];
    callbacks[0] = new NameCallback("Username");
    callbacks[1] = new PasswordCallback("Password", false);

    try {
      callbackHandler.handle(callbacks);
      String password = new String(((PasswordCallback) callbacks[1]).getPassword());

      //
      Object o = TokenStore.REQUEST_STORE.validateToken(password, true);
      if(o == null) o = TokenStore.COOKIE_STORE.validateToken(password, false) ;
      //
      if (o instanceof Credentials) {
        Credentials wc = (Credentials)o;

        // Set shared state
        ((Map)sharedState).put("javax.security.auth.login.name", wc.getUsername());
        ((Map)sharedState).put("javax.security.auth.login.password", wc.getPassword());
      }
      return true;
    }
    catch (Exception e) {
      LoginException le = new LoginException();
      le.initCause(e);
      throw le;
    }
  }

  public boolean commit() throws LoginException {
    return true;
  }

  public boolean abort() throws LoginException {
    return true;
  }

  public boolean logout() throws LoginException {
    return true;
  }
}
