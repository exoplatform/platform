/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.shindig.social;

import org.apache.shindig.gadgets.http.GuiceServletContextListener;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

public class JettyServer {
  //TODO such a hack. why have to specify the provider when using the Servlet?
  public static final int DEFAULT_PORT = 9002;
  public static final String PROVIDER_NAME =
      "org.apache.shindig.social.abdera.SocialApiProvider";
  public static final String GUICE_MODULES =
      "org.apache.shindig.common.CommonGuiceModule:" +
      "org.apache.shindig.gadgets.http.HttpGuiceModule:" +
      "org.apache.shindig.social.SocialApiGuiceModule";

  private final int port;
  private Server server;

  public JettyServer() {
    this(DEFAULT_PORT);
  }

  public JettyServer(int port) {
    this.port = port;
  }

  public void start(Class<? extends Provider> _class, String mapBase)
      throws Exception {
    server = new Server(port);
    if (mapBase == null) {
      mapBase = "/*";
    }
    Context context = new Context(server, "/", Context.SESSIONS);
    ServletHolder servletHolder = new ServletHolder(new AbderaServlet());
    servletHolder.setInitParameter(ServiceManager.PROVIDER, _class.getName());
    context.addServlet(servletHolder, mapBase);
    server.start();
  }

  public void start(Servlet servlet, String mapBase) throws Exception {
    server = new Server(port);
    if (mapBase == null) {
      mapBase = "/*";
    }
    Context context = new Context(server, "/", Context.SESSIONS);
    context.addEventListener(new GuiceServletContextListener());
    Map<String, String> initParams = new HashMap<String, String>();
    initParams.put(GuiceServletContextListener.MODULES_ATTRIBUTE,
        GUICE_MODULES);
    context.setInitParams(initParams);
    ServletHolder servletHolder = new ServletHolder(servlet);
    servletHolder.setInitParameter(ServiceManager.PROVIDER, PROVIDER_NAME);
    context.addServlet(servletHolder, mapBase);
    server.start();
  }
  public void stop() throws Exception {
    server.stop();
  }

}
