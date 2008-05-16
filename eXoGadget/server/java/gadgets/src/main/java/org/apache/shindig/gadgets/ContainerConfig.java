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

package org.apache.shindig.gadgets;

import org.apache.shindig.util.ResourceLoader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a container configuration.
 *
 * See config/container.js for an example configuration.
 *
 * We use a cascading model, so you only have to specify attributes in
 * your config that you actually want to change.
 */
public class ContainerConfig {
  private final Map<String, JSONObject> config;
  public static final String DEFAULT_CONTAINER = "default";
  public static final String CONTAINER_KEY = "gadgets.container";
  private static final Logger logger
      = Logger.getLogger("org.apache.shindig.gadgets");

  /**
   * @return The set of all containers that are currently registered.
   */
  public Set<String> getContainers() {
    return Collections.unmodifiableSet(config.keySet());
  }

  /**
   * Fetches a configuration parameter as a JSON object, array, string, or
   * number, ensuring that it can be safely passed to javascript without any
   * additional filtering.
   *
   * @param container
   * @param parameter The value to fetch. May be specified as an x-path like
   *     object reference such as "gadgets/features/views".
   * @return A configuration parameter as a JSON object or null if not set or
   *     can't be interpreted as JSON.
   */
  public Object getJson(String container, String parameter) {
    JSONObject data = config.get(container);
    if (data == null) {
      return null;
    }
    if (parameter == null) {
      return data;
    }

    try {
      for (String param : parameter.split("/")) {
        Object next = data.get(param);
        if (next instanceof JSONObject) {
          data = (JSONObject)next;
        } else {
          return next;
        }
      }
      return data;
    } catch (JSONException e) {
      logger.log(Level.WARNING, "Failed to get JSON config", e);
      return null;
    }
  }

  /**
   * Attempts to fetch a parameter for the given container, or the default
   * container if the specified container is not supported.
   *
   * @param container
   * @param parameter
   * @return A configuration parameter as a string, or null if not set.
   */
  public String get(String container, String parameter) {
    Object data = getJson(container, parameter);
    return data == null ? null : data.toString();
  }

  /**
   * @param container
   * @param parameter
   * @return A configuration parameter as a JSON object or null if not set or
   *     can't be interpreted as JSON.
   */
  public JSONObject getJsonObject(String container, String parameter) {
    Object data = getJson(container, parameter);
    if (data instanceof JSONObject) {
      return (JSONObject)data;
    }
    return null;
  }

  /**
   * @param container
   * @param parameter
   * @return A configuration parameter as a JSON object or null if not set or
   *     can't be interpreted as JSON.
   */
  public JSONArray getJsonArray(String container, String parameter) {
    Object data = getJson(container, parameter);
    if (data instanceof JSONArray) {
      return (JSONArray)data;
    }
    return null;
  }

  /**
   * Loads containers from directories recursively.
   *
   * Only files with a .js or .json extension will be loaded.
   *
   * @param files The files to examine.
   * @throws GadgetException
   */
  private void loadFiles(File[] files) throws GadgetException {
    try {
      for (File file : files) {
        logger.info("Reading container config: " + file.getName());
        if (file.isDirectory()) {
          loadFiles(file.listFiles());
        } else if (file.getName().endsWith(".js") ||
                   file.getName().endsWith(".json")) {
          loadFromString(ResourceLoader.getContent(file));
        }
      }
    } catch (IOException e) {
      throw new GadgetException(GadgetException.Code.INVALID_PATH, e);
    }
  }
 
  /**
   * Loads resources recursively.
   * @param files The base paths to look for container.xml
   * @throws GadgetException
   */
  private void loadResources(String[] files)  throws GadgetException {
    try {
      for (String entry : files) {
        logger.info("Reading container config: " + entry);
        String content = ResourceLoader.getContent(entry);
        loadFromString(content);
      }
    } catch (IOException e) {
      throw new GadgetException(GadgetException.Code.INVALID_PATH, e);
    }
  }

  /**
   * Returns names on the json object. Used instead of JSONObject.getNames to
   * provide backwards compatibility with older JSON releases.
   *
   * @param obj
   * @return An array of all keys in the object.
   */
  private static String[] getNames(JSONObject obj) throws JSONException {
    JSONArray arr = obj.names();
    List<String> items = new ArrayList<String>(arr.length());
    for (int i = 0, j = arr.length(); i < j; ++i) {
      items.add(i, arr.getString(i));
    }

    return items.toArray(new String[items.size()]);
  }

  /**
   * Merges two JSON objects together (recursively), with values from "merge"
   * replacing values from "base".
   *
   * @param base
   * @param merge
   * @return The merged object.
   * @throws JSONException if the two objects can't be merged for some reason.
   */
  private JSONObject mergeObjects(JSONObject base, JSONObject merge)
      throws JSONException {
    // Clone the initial object (JSONObject doesn't support "clone").

    JSONObject clone = new JSONObject(base, getNames(base));
    // Walk parameter list for the merged object and merge recursively.
    String[] fields = getNames(merge);
    for (String field : fields) {
      Object existing = clone.opt(field);
      Object update = merge.get(field);
      if (existing == null || update == null) {
        // It's new custom config, not referenced in the prototype, or
        // it's removing a pre-configured value.
        clone.put(field, update);
      } else {
        // Merge if object type is JSONObject.
        if (update instanceof JSONObject &&
            existing instanceof JSONObject) {
          clone.put(field, mergeObjects((JSONObject)existing,
                                        (JSONObject)update));
        } else {
          // Otherwise we just overwrite it.
          clone.put(field, update);
        }
      }
    }
    return clone;
  }

  /**
   * Processes a container file.
   * @param json
   */
  public void loadFromString(String json) throws GadgetException {
    try {
      JSONObject contents = new JSONObject(json);
      JSONArray containers = contents.getJSONArray(CONTAINER_KEY);
      JSONObject defaultContainer = config.get(DEFAULT_CONTAINER);
      if (defaultContainer == null) {
        if (DEFAULT_CONTAINER.equals(containers.get(0))) {
          defaultContainer = contents;
          config.put(DEFAULT_CONTAINER, contents);
        } else {
          throw new GadgetException(GadgetException.Code.INVALID_CONFIG,
                                    "No default config registered");
        }
      }
      for (int i = 0, j = containers.length(); i < j; ++i) {
        // Copy the default object and produce a new one.
        String container = containers.getString(i);
        if (!DEFAULT_CONTAINER.equals(container)) {
          config.put(container, mergeObjects(defaultContainer, contents));
        }
      }
    } catch (JSONException e) {
      throw new GadgetException(GadgetException.Code.INVALID_CONFIG, e);
    }
  }

  /**
   * Loads containers from the specified resource. Follows the same rules
   * as {@code JsFeatureLoader.loadFeatures} for locating resources.
   * This call is not thread safe, so you should only call loadContainers()
   * from within the GadgetServerConfig.
   *
   * @param path
   */
  public void loadContainers(String path) throws GadgetException {
    try {
      if (path.startsWith("res://")) {
        path = path.substring(6);
        logger.info("Loading resources from: " + path);
        if (path.endsWith(".txt")) {
          loadResources(ResourceLoader.getContent(path).split("[\r\n]+"));
        } else {
          loadResources(new String[]{path});
        }
      } else {
        logger.info("Loading files from: " + path);
        File file = new File(path);
        loadFiles(new File[]{file});
      }
    } catch (IOException e) {
      throw new GadgetException(GadgetException.Code.INVALID_PATH, e);
    }
  }

  /**
   * Creates a new, empty configuration.
   */
  @Inject
  public ContainerConfig(@Named("containers.default") String containers)
      throws GadgetException {
    config = new HashMap<String, JSONObject>();
    if (containers != null) {
      loadContainers(containers);
    }
  }
}
