/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.social;

import org.apache.shindig.gadgets.http.GuiceServletContextListener;

import com.google.inject.Injector;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Superclass for all servlets related to processing of REST api.
 * The reason for its existence is to init Guice Injection.
 * Since this has AbderaServlet to extend Abdera Servlet,
 * it cannot extend InjectedServlet like GadgetDataServlet does.
 *
 * Injection is also a little different because of Abdera is in the middle.
 * instead of injecting (this) as the GadgetServlet does,
 * here the Provider is injected.
 */
public class RestServerServlet extends AbderaServlet {
  private static Logger logger =
      Logger.getLogger(RestServerServlet.class.getName());

  @Override public void init() {
    // Abdera provider stuff
    manager = createServiceManager();
    provider = createProvider();
  }

  @Override
  protected Provider createProvider() {
    Provider provider = manager.newProvider(getProperties(getServletConfig()));
    try {
      initGuice(getServletConfig(), provider);
    } catch (ServletException e) {
      logger.severe(e.getMessage());
      e.printStackTrace();
      return null;
    }
    return provider;
  }

  protected void initGuice(ServletConfig config, Provider provider)
      throws ServletException {
    ServletContext context = config.getServletContext();
    Injector injector = (Injector)
        context.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);
    if (injector == null) {
      throw new UnavailableException(
          "Guice Injector not found! Make sure you registered " +
          GuiceServletContextListener.class.getName() + " as a listener");
    }
    injector.injectMembers(provider);
    // all providers should implement initialize() so injection could happen
    try {
      Method m = provider.getClass().getMethod("initialize", new Class<?>[0]);
      m.invoke(provider, new Object[0]);
    } catch (IllegalArgumentException e) {
        logger.severe(e.getMessage());
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        logger.severe(e.getMessage());
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        logger.severe(e.getMessage());
        e.printStackTrace();
    } catch (SecurityException e) {
      logger.severe(e.getMessage());
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      logger.severe(e.getMessage());
      e.printStackTrace();
    }
  }
}
