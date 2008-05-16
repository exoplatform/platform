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

import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.RenderingContext;
import org.apache.shindig.gadgets.UserPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Extracts context from JSON input.
 */
public class JsonRpcGadgetContext extends GadgetContext {
  private final URI url;
  @Override
  public URI getUrl() {
    if (url == null) {
      return super.getUrl();
    }
    return url;
  }

  private final Integer moduleId;
  @Override
  public int getModuleId() {
    if (moduleId == null) {
      return super.getModuleId();
    }
    return moduleId;
  }


  private final Locale locale;
  @Override
  public Locale getLocale() {
    if (locale == null) {
      return super.getLocale();
    }
    return locale;
  }

  /**
   * @param obj
   * @return The locale, if appropriate parameters are set, or null.
   */
  private static Locale getLocale(JSONObject obj) {
    String language = obj.optString("language");
    String country = obj.optString("country");
    if (language == null || country == null) {
      return null;
    }
    return new Locale(language, country);
  }

  private final RenderingContext renderingContext;
  @Override
  public RenderingContext getRenderingContext() {
    if (renderingContext == null) {
      return super.getRenderingContext();
    }
    return renderingContext;
  }

  private final Boolean ignoreCache;
  @Override
  public boolean getIgnoreCache() {
    if (ignoreCache == null) {
      return super.getIgnoreCache();
    }
    return ignoreCache;
  }

  private final String container;
  @Override
  public String getContainer() {
    if (container == null) {
      return super.getContainer();
    }
    return container;
  }

  private final Boolean debug;
  @Override
  public boolean getDebug() {
    if (debug == null) {
      return super.getDebug();
    }
    return debug;
  }

  private final String view;
  @Override
  public String getView() {
    if (view == null) {
      return super.getView();
    }
    return view;
  }

  private final UserPrefs userPrefs;
  @Override
  public UserPrefs getUserPrefs() {
    if (userPrefs == null) {
      return super.getUserPrefs();
    }
    return userPrefs;
  }

  /**
   * @param json
   * @return UserPrefs, if any are set for this request.
   * @throws JSONException
   */
  @SuppressWarnings("unchecked")
  private static UserPrefs getUserPrefs(JSONObject json) throws JSONException {
    JSONObject prefs = json.optJSONObject("prefs");
    if (prefs == null) {
      return null;
    }
    Map<String, String> p = new HashMap<String, String>();
    Iterator i = prefs.keys();
    while (i.hasNext()) {
      String key = (String)i.next();
      p.put(key, prefs.getString(key));
    }
    return new UserPrefs(p);
  }

  /**
   *
   * @param json
   * @return URL from the request, or null if not present
   * @throws JSONException
   */
  private static URI getUrl(JSONObject json) throws JSONException {
    try {
      String url = json.getString("url");
      return new URI(url);
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /**
   * @param json
   * @return module id from the request, or null if not present
   * @throws JSONException
   */
  private static Integer getModuleId(JSONObject json) throws JSONException {
    if (json.has("moduleId")) {
      return Integer.valueOf(json.getInt("moduleId"));
    }
    return null;
  }

  /**
   * @param context
   * @param gadget
   * @throws JSONException
   */
  public JsonRpcGadgetContext(JSONObject context, JSONObject gadget)
      throws JSONException {
    url = getUrl(gadget);
    moduleId = getModuleId(gadget);
    userPrefs = getUserPrefs(gadget);

    locale = getLocale(context);
    view = context.optString("view");
    ignoreCache = context.optBoolean("ignoreCache");
    container = context.optString("container");
    debug = context.optBoolean("debug");
    renderingContext = RenderingContext.METADATA;
  }
}
