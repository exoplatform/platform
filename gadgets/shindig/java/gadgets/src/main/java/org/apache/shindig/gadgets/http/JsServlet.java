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
package org.apache.shindig.gadgets.http;

import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.GadgetFeature;
import org.apache.shindig.gadgets.GadgetFeatureFactory;
import org.apache.shindig.gadgets.GadgetFeatureRegistry;
import org.apache.shindig.gadgets.GadgetTokenDecoder;
import org.apache.shindig.gadgets.JsLibrary;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple servlet serving up JavaScript files by their registered aliases.
 * Used by type=URL gadgets in loading JavaScript resources.
 */
public class JsServlet extends InjectedServlet {

  private GadgetFeatureRegistry registry;
  @Inject
  public void setRegistry(GadgetFeatureRegistry registry) {
    this.registry = registry;
  }

  private GadgetTokenDecoder tokenDecoder;
  @Inject
  public void setRegistry(GadgetTokenDecoder tokenDecoder) {
    this.tokenDecoder = tokenDecoder;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    // If an If-Modified-Since header is ever provided, we always say
    // not modified. This is because when there actually is a change,
    // cache busting should occur.
    if (req.getHeader("If-Modified-Since") != null &&
        req.getParameter("v") != null) {
      resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    }

    // Use the last component as filename; prefix is ignored
    String uri = req.getRequestURI();
    // We only want the file name part. There will always be at least 1 slash
    // (the server root), so this is always safe.
    String resourceName = uri.substring(uri.lastIndexOf('/') + 1);
    if (resourceName.endsWith(".js")) {
      // Lop off the suffix for lookup purposes
      resourceName = resourceName.substring(
          0, resourceName.length() - ".js".length());
    }

    Set<String> needed = new HashSet<String>();
    if (resourceName.contains(":")) {
      needed.addAll(Arrays.asList(resourceName.split(":")));
    } else {
      needed.add(resourceName);
    }

    Set<GadgetFeatureRegistry.Entry> found
        = new HashSet<GadgetFeatureRegistry.Entry>();
    Set<String> dummy = new HashSet<String>();

    registry.getIncludedFeatures(needed, found, dummy);
    StringBuilder jsData = new StringBuilder();

    // Probably incorrect to be using a context here...
    GadgetContext context = new HttpGadgetContext(req, tokenDecoder);
    Set<String> features = new HashSet<String>(found.size());
    do {
      for (GadgetFeatureRegistry.Entry entry : found) {
        if (!features.contains(entry.getName()) &&
            features.containsAll(entry.getDependencies())) {
          features.add(entry.getName());
          GadgetFeatureFactory factory = entry.getFeature();
          GadgetFeature feature = factory.create();
          for (JsLibrary lib : feature.getJsLibraries(context)) {
            if (!lib.getType().equals(JsLibrary.Type.URL)) {
              if (context.getDebug()) {
                jsData.append(lib.getDebugContent());
              } else {
                jsData.append(lib.getContent());
              }
              jsData.append(";\n");
            }
          }
        }
      }
    } while (features.size() != found.size());

    if (jsData.length() == 0) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    if (req.getParameter("v") != null) {
      // Versioned files get cached indefinitely
      HttpUtil.setCachingHeaders(resp);
    } else {
      // Unversioned files get cached for 1 hour.
      HttpUtil.setCachingHeaders(resp, 60 * 60);
    }
    resp.setContentType("text/javascript; charset=utf-8");
    resp.setContentLength(jsData.length());
    resp.getOutputStream().write(jsData.toString().getBytes());
  }
}
