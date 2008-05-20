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

import com.google.inject.Injector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;

/**
 * Supports DI for servlets. Can't handle ctor injection since
 * the servlet spec requires configuration being done in init().
 */
public abstract class InjectedServlet extends HttpServlet {

 @Override public void init(ServletConfig config) throws ServletException {
   super.init(config);
   ServletContext context = config.getServletContext();
   Injector injector = (Injector)
       context.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE);
   if (injector == null) {
     throw new UnavailableException(
         "Guice Injector not found! Make sure you registered " +
         GuiceServletContextListener.class.getName() + " as a listener");
   }
   injector.injectMembers(this);
 }
}