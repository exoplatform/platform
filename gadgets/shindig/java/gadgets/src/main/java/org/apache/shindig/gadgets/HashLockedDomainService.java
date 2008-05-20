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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shindig.gadgets.spec.Feature;
import org.apache.shindig.util.Base32;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Locked domain implementation based on sha1.
 * 
 * The generated domain takes the form:
 *
 * base32(sha1(gadget url)).
 * 
 * Other domain locking schemes are possible as well.
 */
public class HashLockedDomainService implements LockedDomainService {
  
  private final ContainerConfig config;

  private final String embedHost;

  private final boolean enabled;
  
  private final Set<String> suffixes;
  
  private GadgetReader gadgetReader = new GadgetReader();
  
  public static final String LOCKED_DOMAIN_REQUIRED_KEY =
      "gadgets.lockedDomainRequired";

  public static final String LOCKED_DOMAIN_SUFFIX_KEY =
      "gadgets.lockedDomainSuffix";
  
  /**
   * Create a LockedDomainService
   * @param config per-container configuration
   * @param embedHost host name to use for embedded content
   * @param enabled whether this service should do anything at all.
   */
  @Inject
  public HashLockedDomainService(
      ContainerConfig config,
      @Named("locked-domain.embed-host")String embedHost,
      @Named("locked-domain.enabled")boolean enabled) {
    this.config = config;
    this.embedHost = embedHost;
    this.enabled = enabled;
    suffixes = new HashSet<String>();
    Set<String> containers = config.getContainers();
    if (enabled) {
      for (String container : containers) {
        String suffix = config.get(container, LOCKED_DOMAIN_SUFFIX_KEY);
        suffixes.add(suffix);
      }
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getEmbedHost() {
    return embedHost;
  }

  public boolean embedCanRender(String host) {
    return (!enabled || host.endsWith(embedHost));
  }

  public boolean gadgetCanRender(String host, Gadget gadget, String container) {
    if (!enabled) {
      return true;
    }
    // Gadgets can opt-in to locked domains, or they can be enabled globally
    // for a particular container
    if (gadgetReader.gadgetWantsLockedDomain(gadget) ||
        containerWantsLockedDomain(container)) {
      String neededHost = getLockedDomainForGadget(
          gadgetReader.getGadgetUrl(gadget), container);
      return (neededHost.equals(host));    
    }
    // Make sure gadgets that don't ask for locked domain aren't allowed
    // to render on one.
    return !gadgetUsingLockedDomain(host, gadget);
  }
  
  // Simple class for dependency injection, so we don't need a full-fledged
  // Gadget mock for these test cases
  static class GadgetReader {
    protected boolean gadgetWantsLockedDomain(Gadget gadget) {
      Map<String, Feature> prefs =
        gadget.getSpec().getModulePrefs().getFeatures();
      return prefs.containsKey("locked-domain");      
    }
    
    protected String getGadgetUrl(Gadget gadget) {
      return gadget.getContext().getUrl().toString();
    }
  }
  
  // For testing only
  void setSpecReader(GadgetReader gadgetReader) {
    this.gadgetReader = gadgetReader;
  }
  
  private boolean containerWantsLockedDomain(String container) {
    String required = config.get(
        container, LOCKED_DOMAIN_REQUIRED_KEY);
    return ("true".equals(required));
  }
  
  private boolean gadgetUsingLockedDomain(String host, Gadget gadget) {
    for (String suffix : suffixes) {
      if (host.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }
  
  public String getLockedDomainForGadget(String gadget, String container) {
    String suffix = config.get(container, LOCKED_DOMAIN_SUFFIX_KEY);
    if (suffix == null) {
      throw new IllegalStateException(
          "Cannot redirect to locked domain if it is not configured");
    }
    byte[] sha1 = DigestUtils.sha(gadget);
    String hash = new String(Base32.encodeBase32(sha1));
    return hash + suffix;
  }
}
