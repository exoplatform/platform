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
package org.apache.shindig.gadgets.http;

import com.google.inject.Guice;
import com.google.inject.Module;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Creates a global guice injector and stores it in a servlet context parameter
 * for injecting servlets.
 */
public class GuiceServletContextListener implements ServletContextListener {
  public static final String INJECTOR_ATTRIBUTE = "guice-injector";
  public static final String MODULES_ATTRIBUTE = "guice-modules";

  public void contextInitialized(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    String moduleNames = context.getInitParameter(MODULES_ATTRIBUTE);
    List<Module> modules = new LinkedList<Module>();
    if (moduleNames != null) {
      for (String moduleName : moduleNames.split(":")) {
        try {
          modules.add((Module)Class.forName(moduleName).newInstance());
        } catch (InstantiationException e) {
          throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    context.setAttribute(INJECTOR_ATTRIBUTE, Guice.createInjector(modules));
  }

  public void contextDestroyed(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    context.removeAttribute(INJECTOR_ATTRIBUTE);
  }
}

