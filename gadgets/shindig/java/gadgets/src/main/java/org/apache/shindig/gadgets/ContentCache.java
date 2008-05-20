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
package org.apache.shindig.gadgets;

import java.net.URI;

/**
 * Cache of RemoteContent keyed by URI/RemoteContentRequest
 */
public interface ContentCache {

  public RemoteContent getContent(URI uri);

  public RemoteContent getContent(RemoteContentRequest request);

  public void addContent(URI uri, RemoteContent content);

  public void addContent(RemoteContentRequest request, RemoteContent content);

  public RemoteContent removeContent(URI uri);

  public RemoteContent removeContent(RemoteContentRequest request);
}
