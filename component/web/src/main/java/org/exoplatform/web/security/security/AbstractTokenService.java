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
package org.exoplatform.web.security.security;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.Token;
import org.exoplatform.web.security.TokenStore;
import org.picocontainer.Startable;

/**
 * Created by The eXo Platform SAS Author : liem.nguyen ncliam@gmail.com Jun 5,
 * 2009
 */
@Managed
@NameTemplate( { @Property(key = "service", value = "auth_token"),
    @Property(key = "name", value = "{Name}") })
@ManagedDescription("Token Service")
public abstract class AbstractTokenService implements Startable, TokenStore {

  protected final String     SERVICE_CONFIG = "SERVICE_CONFIG";

  protected static final int DELAY_TIME    = 600;

  protected final Random     random        = new Random();

  protected String           name;

  protected long             validityMillis;

  @SuppressWarnings("unchecked")
  public AbstractTokenService(InitParams initParams) {
    List<String> params = initParams.getValuesParam(SERVICE_CONFIG).getValues();
    this.name = params.get(0);
    long configValue = new Long(params.get(1));
    this.validityMillis = TimeoutEnum.valueOf(params.get(2)).toMilisecond(configValue);
  }

  public void start() {
    // start a thread, garbage expired cookie token every [DELAY_TIME]
    final AbstractTokenService service = this;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleWithFixedDelay(new Runnable() {
      public void run() {
        try {
          service.cleanExpiredTokens();
        } catch (RepositoryException re) {
          System.out.println("Error occur when delete expired cookie token");
        }
      }
    }, 0, DELAY_TIME, TimeUnit.SECONDS);

  }

  public void stop() {
    // do nothing
  }

  @SuppressWarnings("unchecked")
  public static <T extends AbstractTokenService> T getInstance(Class<T> classType) {    
     PortalContainer container = PortalContainer.getInstance() ;
     return (T) container.getComponentInstanceOfType(classType) ;
  }

  public Credentials validateToken(String tokenKey, boolean remove) {
    if (tokenKey == null) {
      throw new NullPointerException();
    }

    Token token;
    try {
      if (remove) {
        token = this.deleteToken(tokenKey);
      } else {
        token = this.getToken(tokenKey);
      }

      if (token != null) {
        boolean valid = token.getExpirationTimeMillis() > System.currentTimeMillis();
        if (valid) {
          return token.getPayload();
        } else if (!remove) {
          this.deleteToken(tokenKey);
        }
      }
    } catch (Exception e) {
    }

    return null;
  }

  @Managed
  @ManagedDescription("Clean all tokens are expired")
  public void cleanExpiredTokens() throws PathNotFoundException, RepositoryException {
    String[] ids = getAllTokens();
    for (String s : ids) {
      Token token = getToken(s);
      if (token.isExpired()) {
        deleteToken(s);
      }
    }
  }

  @Managed
  @ManagedDescription("Get period time of expired token")
  public long getExpiredPeriodTime() {
    return validityMillis;
  }

  @Managed
  @ManagedName("Name")
  @ManagedDescription("The token service name")
  public String getName() {
    return name;
  }

  @Managed
  @ManagedDescription("get a token by id")
  public abstract Token getToken(String id) throws PathNotFoundException, RepositoryException;

  @Managed
  @ManagedDescription("Delete a token by id")
  public abstract Token deleteToken(String id) throws PathNotFoundException, RepositoryException;

  @Managed
  @ManagedDescription("The list of all tokens")
  public abstract String[] getAllTokens();

  @Managed
  @ManagedDescription("The number of tokens")
  public abstract long getNumberTokens() throws Exception;

  private enum TimeoutEnum {
    SECOND(1000), MINUTE(1000 * 60), HOUR(1000 * 60 * 60), DAY(1000 * 60 * 60 * 24);

    private long multiply;

    private TimeoutEnum(long multiply) {
      this.multiply = multiply;
    }

    public long toMilisecond(long configValue) {
      return configValue * multiply;
    }
  }
}
