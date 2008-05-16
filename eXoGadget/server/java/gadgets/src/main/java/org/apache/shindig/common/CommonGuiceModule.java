/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.common;

import org.apache.shindig.gadgets.BasicContentCache;
import org.apache.shindig.gadgets.BasicGadgetTokenDecoder;
import org.apache.shindig.gadgets.BasicRemoteContentFetcher;
import org.apache.shindig.gadgets.ContentCache;
import org.apache.shindig.gadgets.ContentFetcher;
import org.apache.shindig.gadgets.GadgetTokenDecoder;

import com.google.inject.AbstractModule;

/**
 * Provides social api component injection
 */
public class CommonGuiceModule extends AbstractModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    // TODO: These classes should be moved into the common package.
    // Once that happens then this common guice module can also move to
    // java/common.
    bind(ContentFetcher.class).to(BasicRemoteContentFetcher.class);
    bind(GadgetTokenDecoder.class).to(BasicGadgetTokenDecoder.class);
    bind(ContentCache.class).to(BasicContentCache.class);
  }
}
