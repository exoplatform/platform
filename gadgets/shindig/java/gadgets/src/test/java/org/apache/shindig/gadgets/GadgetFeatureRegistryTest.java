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

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GadgetFeatureRegistryTest extends TestCase {
  private GadgetFeatureRegistry registry;
  private static final GadgetFeatureFactory DUMMY_FEATURE
      = new GadgetFeatureFactory() {
          // Dummy feature.
          public GadgetFeature create() {
            return new GadgetFeature() {};
          }
        };

  private static final String FEATURE_NAME = "feature";
  private static final String DEP_NAME = "dependency";
  private static final String[] FEATURE_LIST = new String[] {
    "feature0", "feature1", "feature2", "feature3"
  };
  private static final String UNREGISTERED_FEATURE = "unregistered";
  @Override
  public void setUp() throws Exception {
    // TODO: Add a mock fetcher here and add tests for retrieving remote files
    registry = new GadgetFeatureRegistry(null, null);
  }


  public void testDependencyChain() throws Exception {
    registry.register(FEATURE_NAME, Arrays.asList(DEP_NAME), DUMMY_FEATURE);
    registry.register(DEP_NAME, null, DUMMY_FEATURE);

    GadgetFeatureRegistry.Entry entry = registry.getEntry(FEATURE_NAME);
    // Object comparison is OK here.
    assertEquals(DUMMY_FEATURE, entry.getFeature());
    assertEquals(DEP_NAME, entry.getDependencies().iterator().next());
  }

  public void testGetAllFeatures() throws Exception {
    for (String feature : FEATURE_LIST) {
      registry.register(feature, Arrays.asList(DEP_NAME), DUMMY_FEATURE);
    }

    Map<String, GadgetFeatureRegistry.Entry> entries
        = registry.getAllFeatures();

    for (String feature : FEATURE_LIST) {
      GadgetFeatureRegistry.Entry entry = entries.get(feature);
      assertNotNull(entry);
      assertEquals(feature, entry.getName());
      assertEquals(DEP_NAME, entry.getDependencies().iterator().next());
    }
  }

  public void testGetIncluded() throws Exception {
    Set<String> requested = new HashSet<String>();
    for (String feature : FEATURE_LIST) {
      registry.register(feature, Arrays.asList(DEP_NAME), DUMMY_FEATURE);
      requested.add(feature);
    }

    registry.register(DEP_NAME, null, DUMMY_FEATURE);

    requested.add(UNREGISTERED_FEATURE);

    Set<GadgetFeatureRegistry.Entry> found
        = new HashSet<GadgetFeatureRegistry.Entry>();
    Set<String> missing = new HashSet<String>();
    registry.getIncludedFeatures(requested, found, missing);

    assertEquals(1, missing.size());
    assertEquals(UNREGISTERED_FEATURE, missing.iterator().next());

    for (String feature : FEATURE_LIST) {
      boolean contains = false;
      for (GadgetFeatureRegistry.Entry entry : found) {
        if (entry.getName().equals(feature)) {
          contains = true;
        }
      }
      if (!contains) {
        fail("Feature " + feature + " not included in needed set.");
      }
    }

    boolean contains = false;
    for (GadgetFeatureRegistry.Entry entry : found) {
      if (entry.getName().equals(DEP_NAME)) {
        contains = true;
      } else if (entry.getName().equals(UNREGISTERED_FEATURE)) {
        fail("Unregistered dependency included in needed set.");
      }
    }
    if (!contains) {
      fail("Transitive dependency " + DEP_NAME + " not included in needed set");
    }

    assertEquals(UNREGISTERED_FEATURE, missing.iterator().next());
  }
}
