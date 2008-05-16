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

import org.apache.shindig.gadgets.DefaultGuiceModule;

import com.google.inject.Scopes;

import java.util.Properties;

/**
 * Provides http component injection on top of existing components.
 */
public class HttpGuiceModule extends DefaultGuiceModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    super.configure();
    bind(ProxyHandler.class).in(Scopes.SINGLETON);
    bind(JsonRpcHandler.class).in(Scopes.SINGLETON);
    bind(GadgetRenderingTask.class);
    bind(UrlGenerator.class).in(Scopes.SINGLETON);
  }

  public HttpGuiceModule(Properties properties) {
    super(properties);
  }

  public HttpGuiceModule() {
    super();
  }
}
