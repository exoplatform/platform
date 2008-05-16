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
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetServer;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.ModulePrefs;
import org.apache.shindig.gadgets.spec.UserPref;
import org.apache.shindig.gadgets.spec.View;

import com.google.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

/**
 * Processes JSON-RPC requests by retrieving all necessary meta data in parallel
 * and coalescing into a single output JSON construct.
 */
public class JsonRpcHandler {

  private final Executor executor;
  private GadgetServer server;
  private UrlGenerator urlGenerator;

  /**
   * Processes a JSON request.
   *
   * @param request Original JSON request
   * @return The JSON response.
   */
  public JSONObject process(JSONObject request)
      throws RpcException, JSONException {
    JSONObject response = new JSONObject();

    // Dispatch a separate thread for each gadget that we wish to render.
    CompletionService<JSONObject> processor =
      new ExecutorCompletionService<JSONObject>(executor);

    List<GadgetContext> gadgets;

    JSONObject requestContext = request.getJSONObject("context");
    JSONArray requestedGadgets = request.getJSONArray("gadgets");

    // Process all JSON first so that we don't wind up with hanging threads if
    // a JSONException is thrown.
    gadgets = new ArrayList<GadgetContext>(requestedGadgets.length());
    for (int i = 0, j = requestedGadgets.length(); i < j; ++i) {
      GadgetContext context = new JsonRpcGadgetContext(
          requestContext, requestedGadgets.getJSONObject(i));
      gadgets.add(context);
    }

    for (GadgetContext context : gadgets) {
      processor.submit(new Job(context));
    }

    int numJobs = gadgets.size();
    do {
      try {
        JSONObject gadget = processor.take().get();
        response.append("gadgets", gadget);
      } catch (InterruptedException e) {
        throw new RpcException("Processing interrupted", e);
      } catch (ExecutionException ee) {
        if (!(ee.getCause() instanceof RpcException)) {
          throw new RpcException("Processing interrupted", ee);
        }
        RpcException e = (RpcException)ee.getCause();
        // Just one gadget failed; mark it as such.
        try {
          GadgetContext context = e.getContext();

          if (context == null) {
            throw e;
          }

          JSONObject errorObj = new JSONObject();
          errorObj.put("url", context.getUrl())
                  .put("moduleId", context.getModuleId());
          if (e.getCause() instanceof GadgetException) {
            GadgetException gpe = (GadgetException)e.getCause();
            errorObj.append("errors", gpe.getMessage());
          } else {
            errorObj.append("errors", e.getMessage());
          }
          response.append("gadgets", errorObj);
        } catch (JSONException je) {
          throw new RpcException("Unable to write JSON", je);
        }
      } catch (JSONException e) {
        throw new RpcException("Unable to write JSON", e);
      } finally {
        numJobs--;
      }
    } while (numJobs > 0);
    return response;
  }

  private class Job implements Callable<JSONObject> {
    private final GadgetContext context;
    public JSONObject call() throws RpcException {
      try {
        Gadget gadget = server.processGadget(context);

        JSONObject gadgetJson = new JSONObject();

        GadgetSpec spec = gadget.getSpec();
        ModulePrefs prefs = spec.getModulePrefs();

        // TODO: modularize response fields based on requested items.
        JSONObject views = new JSONObject();
        for (View view : spec.getViews().values()) {
          views.put(view.getName(), new JSONObject()
               // .put("content", view.getContent())
               .put("type", view.getType().toString().toLowerCase())
               .put("quirks", view.getQuirks()));
        }

        // Features.
        Set<String> feats = prefs.getFeatures().keySet();
        String[] features = feats.toArray(new String[feats.size()]);

        JSONObject userPrefs = new JSONObject();

        // User pref specs
        for (UserPref pref : spec.getUserPrefs()) {
          JSONObject up = new JSONObject()
              .put("displayName", pref.getDisplayName())
              .put("type", pref.getDataType().toString().toLowerCase())
              .put("default", pref.getDefaultValue())
              .put("enumValues", pref.getEnumValues());
          userPrefs.put(pref.getName(), up);
        }

        gadgetJson.put("iframeUrl", urlGenerator.getIframeUrl(gadget))
                  .put("url", gadget.getContext().getUrl().toString())
                  .put("moduleId", gadget.getContext().getModuleId())
                  .put("title", prefs.getTitle())
                  .put("titleUrl", prefs.getTitleUrl().toString())
                  .put("views", views)
                  .put("features", features)
                  .put("userPrefs", userPrefs)
                  // extended meta data
                  .put("directoryTitle", prefs.getDirectoryTitle())
                  .put("thumbnail", prefs.getThumbnail().toString())
                  .put("screenshot", prefs.getScreenshot().toString())
                  .put("author", prefs.getAuthor())
                  .put("authorEmail", prefs.getAuthorEmail())
                  .put("authorAffiliation", prefs.getAuthorAffiliation())
                  .put("authorLocation", prefs.getAuthorLocation())
                  .put("authorPhoto", prefs.getAuthorPhoto())
                  .put("authorAboutme", prefs.getAuthorAboutme())
                  .put("authorQuote", prefs.getAuthorQuote())
                  .put("authorLink", prefs.getAuthorLink())
                  .put("categories", prefs.getCategories())
                  .put("screenshot", prefs.getScreenshot().toString())
                  .put("height", prefs.getHeight())
                  .put("width", prefs.getWidth())
                  .put("showStats", prefs.getShowStats())
                  .put("showInDirectory", prefs.getShowInDirectory())
                  .put("singleton", prefs.getSingleton())
                  .put("scaling", prefs.getScaling())
                  .put("scrolling", prefs.getScrolling());
        return gadgetJson;
      } catch (GadgetException e) {
        throw new RpcException(context, e);
      } catch (JSONException e) {
        // Shouldn't be possible
        throw new RpcException(context, e);
      }
    }
    public Job(GadgetContext context) {
      this.context = context;
    }
  }

  @Inject
  public JsonRpcHandler(Executor executor, GadgetServer server,
      UrlGenerator iframeUrlGenerator) {
    this.executor = executor;
    this.server = server;
    this.urlGenerator = iframeUrlGenerator;
  }
}
