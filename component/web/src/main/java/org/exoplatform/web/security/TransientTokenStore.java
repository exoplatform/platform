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

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.web.login.CookieTokenService;
import org.exoplatform.web.login.InitiateLoginServlet;

/**
 * A trivial in memory implementation of the token store. Tokens are evicted during their access which means that
 * there is not background task to evict the invalid tokens.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TransientTokenStore implements TokenStore {
  
  CookieTokenService service ;
  
  /** . */
  private final ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<String, Token>();

  /** . */
  private final Random random = new Random();
  
  public TransientTokenStore() {
    PortalContainer container = PortalContainer.getInstance() ;
    service = (CookieTokenService) container.getComponentInstanceOfType(CookieTokenService.class) ;
  }

  public String createToken(long validityMillis, Credentials credentials) {
    if (validityMillis < 0) {
      throw new IllegalArgumentException();
    }
    if (credentials == null) {
      throw new NullPointerException();
    }
    String tokenId = InitiateLoginServlet.COOKIE_NAME + random.nextInt();
    long expirationTimeMillis = System.currentTimeMillis() + validityMillis;
    try {
      service.saveToken(tokenId, new Token(expirationTimeMillis, credentials)) ;
    } catch (Exception e) {}
    return tokenId;
  }

  public Credentials validateToken(String tokenKey, boolean remove) {
    if (tokenKey == null) {
      throw new NullPointerException();
    }

    //
    Token token;
    try {
      if (remove) {
        token = service.deleteToken(tokenKey) ;
      } else {
        token = service.getToken(tokenKey) ;
      }

      if (token != null) {
        boolean valid = token.getExpirationTimeMillis() > System.currentTimeMillis();
        if (valid) {
          return token.getPayload();
        } else if (!remove) {
          service.deleteToken(tokenKey) ;
        }
      }
    } catch (Exception e) {}

    return null;
  }
  
  static public class Token {
    
    public static String EXPIRE_MILI = "expirationMilis" ;
    public static String USERNAME = "userName" ;
    public static String PASSWORD = "password" ;
    /** . */
    private final long expirationTimeMillis;
    
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
}

