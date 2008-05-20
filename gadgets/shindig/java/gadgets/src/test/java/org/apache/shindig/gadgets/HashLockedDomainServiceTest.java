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

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.shindig.gadgets.HashLockedDomainService.GadgetReader;

public class HashLockedDomainServiceTest extends EasyMockTestCase {

  HashLockedDomainService domainLocker;
  Gadget gadget;
  FakeSpecReader wantsLocked = new FakeSpecReader(
      true, "http://somehost.com/somegadget.xml");
  FakeSpecReader noLocked = new FakeSpecReader(
      false, "http://somehost.com/somegadget.xml");
  ContainerConfig containerEnabledConfig;
  ContainerConfig containerRequiredConfig;
  
  /**
   * Mocked out spec reader, rather than mocking the whole
   * Gadget object.
   */
  public static class FakeSpecReader extends GadgetReader {
    private boolean wantsLockedDomain;
    private String gadgetUrl;
    
    public FakeSpecReader(boolean wantsLockedDomain, String gadgetUrl) {
      this.wantsLockedDomain = wantsLockedDomain;
      this.gadgetUrl = gadgetUrl;
    }
    
    @Override
    protected boolean gadgetWantsLockedDomain(Gadget gadget) {
      return wantsLockedDomain;
    }
    
    @Override
    protected String getGadgetUrl(Gadget gadget) {
      return gadgetUrl;
    }
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    JSONObject json = new JSONObject();
    json.put("gadgets.container",
             new JSONArray().put(ContainerConfig.DEFAULT_CONTAINER));
    json.put("gadgets.lockedDomainRequired", true);
    json.put("gadgets.lockedDomainSuffix", "-a.example.com:8080");
    containerRequiredConfig  = new ContainerConfig(null);
    containerRequiredConfig.loadFromString(json.toString());
    
    json.put("gadgets.lockedDomainRequired", false);
    containerEnabledConfig = new ContainerConfig(null);
    containerEnabledConfig.loadFromString(json.toString());
    gadget = mock(Gadget.class);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testDisabledGlobally() {
    domainLocker = new HashLockedDomainService(
        containerRequiredConfig, "embed.com", false);
    assertTrue(domainLocker.embedCanRender("anywhere.com"));
    assertTrue(domainLocker.embedCanRender("embed.com"));
    assertTrue(domainLocker.gadgetCanRender("embed.com", gadget, "default"));
    
    domainLocker = new HashLockedDomainService(
        containerEnabledConfig, "embed.com", false);
    assertTrue(domainLocker.embedCanRender("anywhere.com"));
    assertTrue(domainLocker.embedCanRender("embed.com"));
    assertTrue(domainLocker.gadgetCanRender("embed.com", gadget, "default"));    
  }
  
  public void testEnabledForGadget() {
    domainLocker = new HashLockedDomainService(
        containerEnabledConfig, "embed.com", true);
    assertFalse(domainLocker.embedCanRender("anywhere.com"));
    assertTrue(domainLocker.embedCanRender("embed.com"));
    domainLocker.setSpecReader(wantsLocked);
    assertFalse(domainLocker.gadgetCanRender(
        "www.example.com", gadget, "default"));
    assertTrue(domainLocker.gadgetCanRender(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        gadget,
        "default"));
    String target = domainLocker.getLockedDomainForGadget(
        wantsLocked.getGadgetUrl(gadget), "default");
    assertEquals(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        target);
  }
  
  public void testNotEnabledForGadget() {
    domainLocker = new HashLockedDomainService(
        containerEnabledConfig, "embed.com", true);
    domainLocker.setSpecReader(noLocked);
    assertTrue(domainLocker.gadgetCanRender(
        "www.example.com", gadget, "default"));
    assertFalse(domainLocker.gadgetCanRender(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        gadget,
        "default"));
    assertFalse(domainLocker.gadgetCanRender(
        "foo-a.example.com:8080",
        gadget,
        "default"));
    assertFalse(domainLocker.gadgetCanRender(
        "foo-a.example.com:8080",
        gadget,
        "othercontainer"));
    String target = domainLocker.getLockedDomainForGadget(
        wantsLocked.getGadgetUrl(gadget), "default");
    assertEquals(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        target);    
  }
  
  public void testRequiredForContainer() {
    domainLocker = new HashLockedDomainService(
        containerRequiredConfig, "embed.com", true);
    domainLocker.setSpecReader(noLocked);
    assertFalse(domainLocker.gadgetCanRender(
        "www.example.com", gadget, "default"));
    assertTrue(domainLocker.gadgetCanRender(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        gadget,
        "default"));
    String target = domainLocker.getLockedDomainForGadget(
        wantsLocked.getGadgetUrl(gadget), "default");
    assertEquals(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        target);
  }
  
  public void testMissingConfig() throws Exception {
    JSONObject json = new JSONObject();
    json.put("gadgets.container",
             new JSONArray().put(ContainerConfig.DEFAULT_CONTAINER));
    ContainerConfig containerMissingConfig  = new ContainerConfig(null);
    containerMissingConfig.loadFromString(json.toString());
    domainLocker = new HashLockedDomainService(
        containerMissingConfig, "embed.com", false);
    domainLocker.setSpecReader(wantsLocked);
    assertTrue(domainLocker.gadgetCanRender(
        "www.example.com", gadget, "default"));
  }
  
  public void testMultiContainer() throws Exception {
    JSONObject json = new JSONObject();
    json.put("gadgets.container",
             new JSONArray()
             .put(ContainerConfig.DEFAULT_CONTAINER)
             .put("other"));
    json.put("gadgets.lockedDomainRequired", true);
    json.put("gadgets.lockedDomainSuffix", "-a.example.com:8080");
    ContainerConfig inheritsConfig  = new ContainerConfig(null);
    inheritsConfig.loadFromString(json.toString());
    domainLocker = new HashLockedDomainService(
        inheritsConfig, "embed.com", true);
    domainLocker.setSpecReader(wantsLocked);
    assertFalse(domainLocker.gadgetCanRender(
        "www.example.com", gadget, "other"));
    assertTrue(domainLocker.gadgetCanRender(
        "8uhr00296d2o3sfhqilj387krjmgjv3v-a.example.com:8080",
        gadget,
        "other"));
  }
}
