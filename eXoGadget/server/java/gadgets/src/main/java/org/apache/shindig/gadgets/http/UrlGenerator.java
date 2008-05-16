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

import org.apache.shindig.gadgets.Gadget;
import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.GadgetFeature;
import org.apache.shindig.gadgets.GadgetFeatureFactory;
import org.apache.shindig.gadgets.GadgetFeatureRegistry;
import org.apache.shindig.gadgets.JsLibrary;
import org.apache.shindig.gadgets.ContainerConfig;
import org.apache.shindig.gadgets.UserPrefs;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.UserPref;
import org.apache.shindig.gadgets.spec.View;
import org.apache.shindig.util.HashUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Generates urls for various public entrypoints
 */
public class UrlGenerator {

  private final String jsPrefix;
  private final String iframePrefix;
  private final String jsChecksum;
  private final ContainerConfig containerConfig;
  private final static Pattern ALLOWED_FEATURE_NAME
      = Pattern.compile("[0-9a-zA-Z\\.\\-]+");

  /**
   * @param features The list of features that js is needed for.
   * @return The url for the bundled javascript that includes all referenced
   *    feature libraries.
   */
  public String getBundledJsUrl(Collection<String> features,
      GadgetContext context) {
    return jsPrefix + getBundledJsParam(features, context);
  }

  /**
   * @param features
   * @param context
   * @return The bundled js parameter for type=url gadgets.
   */
  public String getBundledJsParam(Collection<String> features,
      GadgetContext context) {
    StringBuilder buf = new StringBuilder();
    boolean first = false;
    for (String feature : features) {
      if (ALLOWED_FEATURE_NAME.matcher(feature).matches()) {
        if (!first) {
          first = true;
        } else {
          buf.append(':');
        }
        buf.append(feature);
      }
    }
    buf.append(".js?v=").append(jsChecksum)
       .append("&container=").append(context.getContainer())
       .append("&debug=").append(context.getDebug() ? "1" : "0");
    return buf.toString();
  }

  /**
   * Generates iframe urls for meta data service.
   * Use this rather than generating your own urls by hand.
   *
   * @param gadget
   * @return The generated iframe url.
   */
  public String getIframeUrl(Gadget gadget) {
    StringBuilder buf = new StringBuilder();
    GadgetContext context = gadget.getContext();
    GadgetSpec spec = gadget.getSpec();
    try {
      String url = context.getUrl().toString();
      View view = HttpUtil.getView(gadget, containerConfig);
      View.ContentType type;
      if (view == null) {
        type = View.ContentType.HTML;
      } else {
        type = view.getType();
      }
      switch (type) {
        case URL:
          // type = url
          buf.append(view.getHref());
          if (url.indexOf('?') == -1) {
            buf.append('?');
          } else {
            buf.append('&');
          }
          break;
        case HTML:
        default:
          buf.append(iframePrefix)
             .append("url=")
             .append(URLEncoder.encode(url, "UTF-8"))
             .append("&");
          break;
      }
      buf.append("container=").append(context.getContainer());
      if (context.getModuleId() != 0) {
        buf.append("&mid=").append(context.getModuleId());
      }
      if (context.getIgnoreCache()) {
        buf.append("&nocache=1");
      } else {
        buf.append("&v=").append(spec.getChecksum());
      }

      buf.append("&lang=").append(context.getLocale().getLanguage());
      buf.append("&country=").append(context.getLocale().getCountry());

      UserPrefs prefs = context.getUserPrefs();
      for (UserPref pref : gadget.getSpec().getUserPrefs()) {
        String name = pref.getName();
        String value = prefs.getPref(name);
        if (value == null) {
          value = pref.getDefaultValue();
        }
        buf.append("&up_").append(URLEncoder.encode(pref.getName(), "UTF-8"))
           .append("=").append(URLEncoder.encode(value, "UTF-8"));
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 Not supported!", e);
    }
    return buf.toString();
  }

  @Inject
  public UrlGenerator(@Named("urls.iframe.prefix") String iframePrefix,
                      @Named("urls.js.prefix") String jsPrefix,
                      GadgetFeatureRegistry registry,
                      ContainerConfig containerConfig) {
    this.iframePrefix = iframePrefix;
    this.jsPrefix = jsPrefix;
    this.containerConfig = containerConfig;

    StringBuilder jsBuf = new StringBuilder();
    for (Map.Entry<String, GadgetFeatureRegistry.Entry> entry :
        registry.getAllFeatures().entrySet()) {
      GadgetFeatureFactory factory = entry.getValue().getFeature();
      GadgetFeature feature = factory.create();
      for (JsLibrary library : feature.getJsLibraries(null)) {
        jsBuf.append(library.getContent());
      }
    }
    jsChecksum = HashUtil.checksum(jsBuf.toString().getBytes());
  }
}
