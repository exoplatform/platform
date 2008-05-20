/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.shindig.social.abdera;

import static org.easymock.EasyMock.expect;

import org.apache.abdera.protocol.server.impl.RouteManager;

import org.junit.Test;

public class RouteManagerTest extends SocialApiProviderTestFixture {

  private RouteManager rm;

  @Override
  public void setUp(){
    rm = provider.getRouteManager();
  }

  @Test
  public void testRouteManagerResolve() {
    mockAndResolve("people/x/@all");
    mockAndResolve("people/x/@friends");
    mockAndResolve("people/x/y");
    mockAndResolve("people/x/@all/y");

    mockAndResolve("activities/x/@self");
    mockAndResolve("activities/x/@friends");
    mockAndResolve("activities/x/y");
    mockAndResolve("activities/x/@self/y");

    mockAndResolve("appdata/x/friends/y");
    mockAndResolve("appdata/x/self/y");
  }

  private void mockAndResolve(String path){
    expect(request.getTargetPath()).andReturn(base + path);
    org.easymock.EasyMock.replay(request);
    assertNotNull("path = " + path, rm.resolve(request));
    org.easymock.EasyMock.reset(request);
  }
}
