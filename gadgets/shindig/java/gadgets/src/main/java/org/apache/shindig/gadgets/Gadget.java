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
import org.apache.shindig.gadgets.spec.MessageBundle;
import org.apache.shindig.gadgets.spec.Preload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Intermediary representation of all state associated with processing
 * of a single gadget request.
 *
 * This class is constructed by an immutable base {@code GadgetSpec},
 * and is modified in parallel by a number of {@code GadgetFeature}
 * processors, in an order defined by their dependencies, in
 * {@code GadgetServer}.
 */
public class Gadget {
  private final GadgetContext context;
  public GadgetContext getContext() {
    return context;
  }

  private final GadgetSpec spec;
  public GadgetSpec getSpec() {
    return spec;
  }

  private final MessageBundle messageBundle;
  public MessageBundle getMessageBundle() {
    return messageBundle;
  }

  private final List<JsLibrary> jsLibraries;
  public List<JsLibrary> getJsLibraries() {
    return jsLibraries;
  }

  private final Map<Preload, Future<RemoteContent>> preloads
      = new HashMap<Preload, Future<RemoteContent>>();
  public Map<Preload, Future<RemoteContent>> getPreloadMap() {
    return preloads;
  }

  /**
   * @param context
   * @param spec
   * @param messageBundle
   * @param jsLibraries
   */
  public Gadget(GadgetContext context, GadgetSpec spec,
      MessageBundle messageBundle, List<JsLibrary> jsLibraries) {
    this.context = context;
    this.spec = spec;
    this.messageBundle = messageBundle;
    this.jsLibraries = jsLibraries;
  }
}