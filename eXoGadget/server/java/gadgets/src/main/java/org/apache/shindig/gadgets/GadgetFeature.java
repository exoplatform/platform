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

import java.util.Collections;
import java.util.List;

/**
 * Base interface providing Gadget Server's primary extensibility mechanism.
 *
 * During processing of a {@code Gadget}, a tree of {@code GadgetFeature}
 * objects is constructed based on the &lt;Require&gt; and &lt;Optional&gt;
 * tags declared in its {@code GadgetSpec}, and the dependencies registered
 * for these in {@code GadgetFeatureRegistry}.
 *
 * Each {@code GadgetFeature}'s process method is called - potentially
 * in parallel with many others whose dependencies have also been satisfied.
 *
 * To extend the Gadget Server's feature set, simply implement this interface
 * and register your class with {@code GadgetFeatureRegistry}, indicating
 * which other {@code GadgetFeature} features are needed before yours can
 * operate successfully.
 *
 * Each feature <i>must</i> be instantiable by a no-argument constructor,
 * and will <i>always</i> be instantiated this way. As such, it is recommended
 * not to define a constructor for a feature at all.
 */
public abstract class GadgetFeature {

  /**
   * Performs processing required to handle this feature.
   * By default this does nothing.
   *
   * Only invoked if isJsOnly is false.
   *
   * @param gadget
   * @param context
   * @throws GadgetException
   */
  @SuppressWarnings("unused")
  public void process(Gadget gadget, GadgetContext context)
      throws GadgetException {
    // By default we do nothing.
  }

  /**
   * This is used by various consumers to retrieve all javascript libraries
   * that this feature uses without necessarily processing them.
   * This is primarily used by features that simply pass-through libraries.
   *
   * @param context
   * @return A list of all libraries needed by this feature for the request.
   */
  public List<JsLibrary> getJsLibraries(GadgetContext context) {
    return Collections.emptyList();
  }

  /**
   * @return True if this feature only exists to satisfy javascript dependencies
   *     if this is true, there is no need to run prepare or process, and it
   *     can be run serially.
   */
  public boolean isJsOnly() {
    return false;
  }
}
