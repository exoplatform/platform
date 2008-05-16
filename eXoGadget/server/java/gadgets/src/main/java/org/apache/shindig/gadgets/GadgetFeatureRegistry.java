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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Maintains a registry of all {@code GadgetFeature} types supported by
 * a given Gadget Server installation.
 *
 * To register a feature:
 * GadgetFeatureRegistry registry = // get your global registry
 * registry.register("my-feature", null, new MyFeatureFactory());
 */
public class GadgetFeatureRegistry {
  private final Map<String, Entry> features;
  private final Map<String, Entry> core;

  // Caches the transitive dependencies to enable faster lookups.
  private final Map<Set<String>, Set<Entry>> transitiveDeps
      = new HashMap<Set<String>, Set<Entry>>();

  private boolean graphComplete = false;

  private final static Logger logger
      = Logger.getLogger("org.apache.shindig.gadgets");

  /**
   * Creates a new feature registry and loads the specified features.
   *
   * @param contentFetcher
   * @param featureFiles
   * @throws GadgetException
   */
  @Inject
  public GadgetFeatureRegistry(@Named("features.default") String featureFiles,
      ContentFetcher contentFetcher) throws GadgetException {
    features = new HashMap<String, Entry>();
    core = new HashMap<String, Entry>();
    if (featureFiles != null) {
      JsFeatureLoader loader = new JsFeatureLoader(contentFetcher);
      loader.loadFeatures(featureFiles, this);
    }
  }

  /**
   * Register a {@code GadgetFeature} identified by {@code name} which
   * depends on other {@code GadgetFeature}s listed in {@code deps}
   * completing before this one does.
   *
   * Names are freeform, but it is strongly suggested that they are
   * namespaced, optionally (yet often usefully) in Java package-notation ie.
   * 'org.example.FooFeature'
   *
   * May never be invoked after calling getIncludedFeatures.
   *
   * @param name Name of the feature to register, ideally using the conventions
   *     described
   * @param deps List of strings indicating features on which {@code feature}
   *     depends to operate correctly, which need to process the {@code Gadget}
   *     before it does
   * @param feature Class implementing the feature
   */
  public Entry register(String name, List<String> deps,
                        GadgetFeatureFactory feature) {
    if (graphComplete) {
      throw new IllegalStateException("registerFeatures should never be " +
          "invoked after calling getIncludedFeatures");
    }
    logger.info("Registering feature: " + name + " with deps " + deps);
    Entry entry = new Entry(name, deps, feature, this);
    if (isCore(entry)) {
      core.put(name, entry);
      for (Entry e : features.values()) {
        e.deps.add(name);
      }
    } else {
      entry.deps.addAll(core.keySet());
    }
    features.put(name, entry);
    return entry;
  }

  /**
   * @param entry
   * @return True if the entry is "core" (a dependency of all other features)
   */
  private boolean isCore(Entry entry) {
    return entry.name.startsWith("core");
    }

  /**
   * @return All registered features.
   */
  public Map<String, Entry> getAllFeatures() {
    return Collections.unmodifiableMap(features);
  }

  /**
   * Attempts to retrieve all the {@code GadgetFeature} classes specified
   * in the {@code needed} list. Those that are found are returned in
   * {@code resultsFound}, while the names of those that are missing are
   * populated in {@code resultsMissing}.
   * @param needed Set of names identifying features to retrieve
   * @param resultsFound Set of feature entries found
   * @param resultsMissing Set of feature identifiers that could not be found
   * @return True if all features were retrieved
   */
  public boolean getIncludedFeatures(Set<String> needed,
                                     Set<Entry> resultsFound,
                                     Set<String> resultsMissing) {
    graphComplete = true;
    if (needed.size() == 0) {
      // Shortcut for gadgets that don't have any explicit dependencies.
      resultsFound.addAll(core.values());
      return true;
    }
    // We use the cache only for situations where all needed are available.
    // if any are missing, the result won't be cached.
    Set<Entry> cache = transitiveDeps.get(needed);
    if (cache != null) {
      resultsFound.addAll(cache);
      return true;
    } else {
      resultsFound.addAll(core.values());
      for (String featureName : needed) {
        Entry entry = features.get(featureName);
        if (entry == null) {
          resultsMissing.add(featureName);
        } else {
          addEntryToSet(resultsFound, entry);
        }
      }

      if (resultsMissing.size() == 0) {
        // Store to cache
        transitiveDeps.put(
            Collections.unmodifiableSet(new HashSet<String>(needed)),
            Collections.unmodifiableSet(new HashSet<Entry>(resultsFound)));
        return true;
      }
    }
    return false;
  }

  /**
   * Recursively add all dependencies.
   * @param results
   * @param entry
   */
  private void addEntryToSet(Set<Entry> results, Entry entry) {
    for (String dep : entry.deps) {
      addEntryToSet(results, features.get(dep));
    }
    results.add(entry);
  }

  /**
   * Fetches an entry by name.
   * @param name
   * @return The entry, or null if it does not exist.
   */
  Entry getEntry(String name) {
    return features.get(name);
  }

  /**
   * Ties together a {@code GadgetFeature} with its name and dependencies.
   */
  public static class Entry {
    private final String name;
    private final Set<String> deps;
    private final Set<String> readDeps;
    private final GadgetFeatureFactory feature;

    private Entry(String name,
                  List<String> deps,
                  GadgetFeatureFactory feature,
                  GadgetFeatureRegistry registry)
        throws IllegalStateException {
      this.name = name;
      this.deps = new HashSet<String>();
      this.readDeps = Collections.unmodifiableSet(this.deps);
      if (deps != null) {
        this.deps.addAll(deps);
      }
      this.feature = feature;
    }

    /**
     * @return Name identifier
     */
    public String getName() {
      return name;
    }

    /**
     * @return List of identifiers on which feature depends
     */
    public Set<String> getDependencies() {
      return readDeps;
    }

    @Override
    public boolean equals(Object rhs) {
      if (rhs == this) {
        return true;
      }
      if (rhs instanceof Entry) {
        Entry entry = (Entry)rhs;
        return name.equals(entry.name);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    /**
     * @return Class implementing the feature
     */
    public GadgetFeatureFactory getFeature() {
      return feature;
    }
  }
}
