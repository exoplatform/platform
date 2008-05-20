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
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.gadgets.GadgetTokenDecoder;
import org.apache.shindig.gadgets.RenderingContext;
import org.apache.shindig.gadgets.UserPrefs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Implements GadgetContext using an HttpServletRequest
 */
public class HttpGadgetContext extends GadgetContext {

  public static final String USERPREF_PARAM_PREFIX = "up_";

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
   * @param req
   * @return The ignore cache setting, if appropriate params are set, or null.
   */
  private static URI getUrl(HttpServletRequest req) {
    String url = req.getParameter("url");
    if (url == null) {
      return null;
    }
    try {
      return new URI(url);
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /**
   * @param req
   * @return module id, if specified
   */
  private static Integer getModuleId(HttpServletRequest req) {
    String mid = req.getParameter("mid");
    if (mid == null) {
      return null;
    }
    return Integer.parseInt(mid);
  }


  /**
   * @param req
   * @return The locale, if appropriate parameters are set, or null.
   */
  private static Locale getLocale(HttpServletRequest req) {
    String language = req.getParameter("lang");
    String country = req.getParameter("country");
    if (language == null && country == null) {
      return null;
    } else if (language == null) {
      language = "all";
    } else if (country == null) {
      country = "ALL";
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

  /**
   * @param req
   * @return The rendering context, if appropriate params are set, or null.
   */
  private static RenderingContext getRenderingContext(HttpServletRequest req) {
    String c = req.getParameter("c");
    if (c == null) {
      return null;
    }
    return c.equals("1") ? RenderingContext.CONTAINER : RenderingContext.GADGET;
  }

  private final Boolean ignoreCache;
  @Override
  public boolean getIgnoreCache() {
    if (ignoreCache == null) {
      return super.getIgnoreCache();
    }
    return ignoreCache;
  }

  /**
   * @param req
   * @return The ignore cache setting, if appropriate params are set, or null.
   */
  private static Boolean getIgnoreCache(HttpServletRequest req) {
    String ignoreCache = req.getParameter("nocache");
    if (ignoreCache == null) {
      return null;
    } else if ("0".equals(ignoreCache)) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  private final String container;
  @Override
  public String getContainer() {
    if (container == null) {
      return super.getContainer();
    }
    return container;
  }

  /**
   * @param req
   * @return The container, if set, or null.
   */
  private static String getContainer(HttpServletRequest req) {
    String container = req.getParameter("container");
    if (container == null) {
      // The parameter used to be called 'synd' FIXME: schedule removal
      container = req.getParameter("synd");
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

  /**
   * @param req
   * @return Debug setting, if set, or null.
   */
  private static Boolean getDebug(HttpServletRequest req) {
    String debug = req.getParameter("debug");
    if (debug == null) {
      return null;
    } else if ("0".equals(debug)) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  private final String view;
  @Override
  public String getView() {
    if (view == null) {
      return super.getView();
    }
    return view;
  }

  /**
   * @param req
   * @return The view, if specified, or null.
   */
  private static String getView(HttpServletRequest req) {
    return req.getParameter("view");
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
   * @param req
   * @return UserPrefs, if any are set for this request.
   */
  @SuppressWarnings("unchecked")
  private static UserPrefs getUserPrefs(HttpServletRequest req) {
    Map<String, String> prefs = new HashMap<String, String>();
    Enumeration<String> paramNames = req.getParameterNames();
    if (paramNames == null) {
      return null;
    }
    while (paramNames.hasMoreElements()) {
      String paramName = paramNames.nextElement();
      if (paramName.startsWith(USERPREF_PARAM_PREFIX)) {
        String prefName = paramName.substring(USERPREF_PARAM_PREFIX.length());
        String escapedParam =
        prefs.put(prefName, req.getParameter(paramName));
      }
    }
    return new UserPrefs(prefs);
  }

  private final String tokenString;
  private final GadgetTokenDecoder tokenDecoder;
  @Override
  public GadgetToken getToken() throws GadgetException {
    if (tokenString == null || tokenString.length() == 0) {
      return super.getToken();
    } else {
      return tokenDecoder.createToken(tokenString);
    }
  }

  public HttpGadgetContext(HttpServletRequest request,
      GadgetTokenDecoder tokenDecoder) {
    url = getUrl(request);
    moduleId = getModuleId(request);
    locale = getLocale(request);
    renderingContext = getRenderingContext(request);
    ignoreCache = getIgnoreCache(request);
    container = getContainer(request);
    debug = getDebug(request);
    view = getView(request);
    userPrefs = getUserPrefs(request);
    tokenString = request.getParameter(ProxyHandler.SECURITY_TOKEN_PARAM);
    this.tokenDecoder = tokenDecoder;
  }
}
