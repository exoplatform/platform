/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.gadgets;

import org.apache.shindig.gadgets.spec.GadgetSpec;

import java.net.URI;
import java.util.Locale;

/**
 * Bundles together context data for the current request with server config
 * data.
 */
public class GadgetContext {
  /**
   * @return The url for this gadget.
   */
  public URI getUrl() {
    return null;
  }

  /**
   * @return The module id for this request.
   */
  public int getModuleId() {
    return 0;
  }

  /**
   * @return The locale for this request.
   */
  public Locale getLocale() {
    return GadgetSpec.DEFAULT_LOCALE;
  }

  /**
   * @return The rendering context for this request.
   */
  public RenderingContext getRenderingContext() {
    return RenderingContext.GADGET;
  }

  /**
   * @return Whether or not to bypass caching behavior for the current request.
   */
  public boolean getIgnoreCache() {
    return false;
  }

  /**
   * @return The container of the current request.
   */
  public String getContainer() {
    return ContainerConfig.DEFAULT_CONTAINER;
  }

  /**
   * @return Whether or not to show debug output.
   */
  public boolean getDebug() {
    return false;
  }

  /**
   * @return Name of view to show
   */
  public String getView() {
    return GadgetSpec.DEFAULT_VIEW;
  }

  /**
   * @return The user prefs for the current request.
   */
  public UserPrefs getUserPrefs() {
    return UserPrefs.EMPTY;
  }

  /**
   * @return The token associated with this request
   */
  @SuppressWarnings("unused")
  public GadgetToken getToken() throws GadgetException {
    return null;
  }
}
