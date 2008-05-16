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
package org.apache.shindig.gadgets.oauth;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;

import junit.framework.TestCase;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;

import java.util.HashMap;
import java.util.Map;

public class BasicOAuthStoreTest extends TestCase {

  private IMocksControl control;
  private Map<OAuthStore.ProviderKey, BasicOAuthStore.ProviderInfo>
      mockProviders;
  private Map<OAuthStore.TokenKey, OAuthStore.TokenInfo> mockTokens;
  private BasicOAuthStore noDefaultStore;
  private BasicOAuthStore withDefaultStore;
  private String defaultKey = "defaultkey";
  private String defaultSecret = "defaultsecret";  // not quite a PKCS8-encoded
                                                   // RSA key, but that's ok

  @Override
  @SuppressWarnings("unchecked")
  protected void setUp() throws Exception {
    control = EasyMock.createStrictControl();
    mockProviders = control.createMock(
        new HashMap<OAuthStore.ProviderKey,
                    BasicOAuthStore.ProviderInfo>().getClass());
    mockTokens = control.createMock(
        new HashMap<OAuthStore.TokenKey, OAuthStore.TokenInfo>().getClass());

    noDefaultStore = new BasicOAuthStore();
    noDefaultStore.setHashMapsForTesting(mockProviders, mockTokens);

    withDefaultStore = new BasicOAuthStore(defaultKey, defaultSecret);
    withDefaultStore.setHashMapsForTesting(mockProviders, mockTokens);
  }

  public void testGetOAuthAccessor() throws Exception {
    OAuthStore.TokenKey tokenKey = new OAuthStore.TokenKey();
    tokenKey.setGadgetUri("http://foo.bar.com/gadget.xml");
    tokenKey.setModuleId(2348709L);
    tokenKey.setUserId("testuser");
    tokenKey.setServiceName("testservice");
    tokenKey.setTokenName("testtoken");

    OAuthStore.TokenInfo tokenInfo = new OAuthStore.TokenInfo("accesstoken",
                                                              "tokensecret");

    OAuthStore.ProviderKey provKey = new OAuthStore.ProviderKey();
    provKey.setGadgetUri(tokenKey.getGadgetUri());
    provKey.setServiceName(tokenKey.getServiceName());

    OAuthServiceProvider provider = new OAuthServiceProvider(
        "request", "authorize", "access");

    BasicOAuthStore.ProviderInfo info = new BasicOAuthStore.ProviderInfo();

    info.setHttpMethod(OAuthStore.HttpMethod.GET);
    info.setSignatureType(OAuthStore.SignatureType.HMAC_SHA1);
    info.setProvider(provider);
    info.setParamLocation(OAuthStore.OAuthParamLocation.AUTH_HEADER);

    ////////////////////////////////////////////////////////////////////////////
    // first, the case where we don't have a consumer key/secret

    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(info).once();
    expect(mockTokens.get(tokenKey)).andReturn(tokenInfo);

    control.replay();

    OAuthStore.AccessorInfo accessorInfo =
        withDefaultStore.getOAuthAccessor(tokenKey);

    control.verify();

    OAuthAccessor accessor = accessorInfo.getAccessor();

    assertSame(info.getHttpMethod(), accessorInfo.getHttpMethod());
    assertSame(OAuthStore.OAuthParamLocation.AUTH_HEADER,
               accessorInfo.getParamLocation());

    assertEquals("accesstoken", accessor.accessToken);
    assertEquals("tokensecret", accessor.tokenSecret);
    assertEquals(defaultKey, accessor.consumer.consumerKey);
    assertNull(accessor.consumer.consumerSecret);
    assertEquals(defaultSecret, accessor.consumer
        .getProperty(RSA_SHA1.PRIVATE_KEY));
    assertEquals("access", accessor.consumer.serviceProvider.accessTokenURL);
    assertEquals("request", accessor.consumer.serviceProvider.requestTokenURL);
    assertEquals("authorize",
                 accessor.consumer.serviceProvider.userAuthorizationURL);

    // now for the one that doesn't have default keys set
    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(info).once();
    expect(mockTokens.get(tokenKey)).andStubReturn(tokenInfo);

    control.replay();

    try {
      accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);
      fail("expected exception, but didn't get it");
    } catch (OAuthNoDataException e) {
      // this is expected
    }

    control.verify();

    ////////////////////////////////////////////////////////////////////////////
    // now the case where we have negotiated an HMAC consumer secret

    OAuthStore.ConsumerKeyAndSecret kas =
        new OAuthStore.ConsumerKeyAndSecret("negotiatedkey",
                                            "negotiatedsecret",
                                            OAuthStore.KeyType.HMAC_SYMMETRIC);
    info.setKeyAndSecret(kas);
    info.setParamLocation(OAuthStore.OAuthParamLocation.POST_BODY);

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(info).once();
    expect(mockTokens.get(tokenKey)).andReturn(tokenInfo);

    control.replay();

    accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);

    control.verify();

    assertSame(OAuthStore.OAuthParamLocation.POST_BODY,
               accessorInfo.getParamLocation());

    accessor = accessorInfo.getAccessor();

    assertEquals("accesstoken", accessor.accessToken);
    assertEquals("tokensecret", accessor.tokenSecret);
    assertEquals("negotiatedkey", accessor.consumer.consumerKey);
    assertEquals("negotiatedsecret", accessor.consumer.consumerSecret);
    assertEquals("access", accessor.consumer.serviceProvider.accessTokenURL);
    assertEquals("request", accessor.consumer.serviceProvider.requestTokenURL);
    assertEquals("authorize",
                 accessor.consumer.serviceProvider.userAuthorizationURL);

    // the store with fallback keys should do the same thing

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(info).once();
    expect(mockTokens.get(tokenKey)).andReturn(tokenInfo);

    control.replay();

    accessorInfo = withDefaultStore.getOAuthAccessor(tokenKey);

    control.verify();

    accessor = accessorInfo.getAccessor();

    assertEquals("accesstoken", accessor.accessToken);
    assertEquals("tokensecret", accessor.tokenSecret);
    assertEquals("negotiatedkey", accessor.consumer.consumerKey);
    assertEquals("negotiatedsecret", accessor.consumer.consumerSecret);
    assertEquals("access", accessor.consumer.serviceProvider.accessTokenURL);
    assertEquals("request", accessor.consumer.serviceProvider.requestTokenURL);
    assertEquals("authorize",
                 accessor.consumer.serviceProvider.userAuthorizationURL);

    ////////////////////////////////////////////////////////////////////////////
    // now the case where we have negotiated an RSA consumer key

    kas = new OAuthStore.ConsumerKeyAndSecret("negotiatedkey",
                                              "negotiatedsecret",
                                              OAuthStore.KeyType.RSA_PRIVATE);
    info.setKeyAndSecret(kas);

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(info).once();
    expect(mockTokens.get(tokenKey)).andReturn(tokenInfo);

    control.replay();

    accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);

    control.verify();

    accessor = accessorInfo.getAccessor();

    assertEquals("accesstoken", accessor.accessToken);
    assertEquals("tokensecret", accessor.tokenSecret);
    assertEquals("negotiatedkey", accessor.consumer.consumerKey);
    assertNull(accessor.consumer.consumerSecret);
    assertEquals("negotiatedsecret",
                 accessor.consumer.getProperty(RSA_SHA1.PRIVATE_KEY));
    assertEquals("access", accessor.consumer.serviceProvider.accessTokenURL);
    assertEquals("request", accessor.consumer.serviceProvider.requestTokenURL);
    assertEquals("authorize",
                 accessor.consumer.serviceProvider.userAuthorizationURL);

    // store with fallback keys should do the same

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(info).once();
    expect(mockTokens.get(tokenKey)).andReturn(tokenInfo);

    control.replay();

    accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);

    control.verify();

    accessor = accessorInfo.getAccessor();

    assertEquals("accesstoken", accessor.accessToken);
    assertEquals("tokensecret", accessor.tokenSecret);
    assertEquals("negotiatedkey", accessor.consumer.consumerKey);
    assertNull(accessor.consumer.consumerSecret);
    assertEquals("negotiatedsecret",
                 accessor.consumer.getProperty(RSA_SHA1.PRIVATE_KEY));
    assertEquals("access", accessor.consumer.serviceProvider.accessTokenURL);
    assertEquals("request", accessor.consumer.serviceProvider.requestTokenURL);
    assertEquals("authorize",
                 accessor.consumer.serviceProvider.userAuthorizationURL);

    ////////////////////////////////////////////////////////////////////////////
    // now, test some error conditions. Fist, that no such service exists...

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(null);
    expect(mockTokens.get(tokenKey)).andStubReturn(tokenInfo);

    control.replay();

    try {
      accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);
      fail("expected OAuthNoDataException, but didn't get it");
    } catch (OAuthNoDataException e) {
      // this is expected
    }

    control.verify();

    // same with the store with fallback keys

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andReturn(null);
    expect(mockTokens.get(tokenKey)).andStubReturn(tokenInfo);

    control.replay();

    try {
      accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);
      fail("expected OAuthNoDataException, but didn't get it");
    } catch (OAuthNoDataException e) {
      // this is expected
    }

    control.verify();

    ////////////////////////////////////////////////////////////////////////////
    // service info is there, but no token:

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andStubReturn(info);
    expect(mockTokens.get(tokenKey)).andReturn(null);

    control.replay();

    accessorInfo = noDefaultStore.getOAuthAccessor(tokenKey);

    control.verify();

    assertNull(accessorInfo.getAccessor().accessToken);
    assertNull(accessorInfo.getAccessor().requestToken);

    // same with the store with fallback keys

    control.reset();
    control.checkOrder(false);

    expect(mockProviders.get(eq(provKey))).andStubReturn(info);
    expect(mockTokens.get(tokenKey)).andReturn(null);

    control.replay();

    accessorInfo = withDefaultStore.getOAuthAccessor(tokenKey);
    assertNull(accessorInfo.getAccessor().accessToken);
    assertNull(accessorInfo.getAccessor().requestToken);

    control.verify();

  }

  public void testSetOAuthConsumerKeyAndSecret() throws Exception {
    OAuthStore.ProviderKey provKey = new OAuthStore.ProviderKey();
    provKey.setGadgetUri("http://foo.bar.com/gadget.xml");
    provKey.setServiceName("testservice");

    OAuthStore.ConsumerKeyAndSecret kas =
        new OAuthStore.ConsumerKeyAndSecret("consumerkey",
                                            "consumersecret",
                                            OAuthStore.KeyType.HMAC_SYMMETRIC);

    OAuthServiceProvider provider = new OAuthServiceProvider(
        "request", "authorize", "access");

    OAuthStore.ProviderInfo info = new OAuthStore.ProviderInfo();
    info.setHttpMethod(OAuthStore.HttpMethod.GET);
    info.setSignatureType(OAuthStore.SignatureType.HMAC_SHA1);
    info.setProvider(provider);

    expect(mockProviders.get(provKey)).andReturn(info);

    control.replay();

    noDefaultStore.setOAuthConsumerKeyAndSecret(provKey, kas);

    control.verify();

    // make sure that consumer key and secret got attached
    assertSame(info.getKeyAndSecret(), kas);

    ////////////////////////////////////////////////////////////////////////////
    // now, test some error conditions

    control.reset();

    expect(mockProviders.get(provKey)).andReturn(null);

    control.replay();

    try {
      noDefaultStore.setOAuthConsumerKeyAndSecret(provKey, kas);
      fail("expected OAuthNoDataException, but didn't get it");
    } catch (OAuthNoDataException ex) {
      // this is expected
    }

    control.verify();
  }

  public void testSetOAuthServiceProviderInfo() throws Exception {

    OAuthStore.ProviderKey provKey = new OAuthStore.ProviderKey();
    provKey.setGadgetUri("http://foo.bar.com/gadget.xml");
    provKey.setServiceName("testservice");

    OAuthStore.ConsumerKeyAndSecret kas =
        new OAuthStore.ConsumerKeyAndSecret("consumerkey",
                                            "consumersecret",
                                            OAuthStore.KeyType.HMAC_SYMMETRIC);

    OAuthServiceProvider provider = new OAuthServiceProvider(
        "request", "authorize", "access");

    BasicOAuthStore.ProviderInfo info = new BasicOAuthStore.ProviderInfo();

    info.setHttpMethod(OAuthStore.HttpMethod.GET);
    info.setSignatureType(OAuthStore.SignatureType.HMAC_SHA1);
    info.setProvider(provider);
    info.setKeyAndSecret(kas);

    expect(mockProviders.put(same(provKey), same(info))).andReturn(null);

    control.replay();

    noDefaultStore.setOAuthServiceProviderInfo(provKey, info);

    control.verify();
  }

  public void testSetTokenAndSecret() throws Exception {
    OAuthStore.TokenKey tokenKey = new OAuthStore.TokenKey();
    tokenKey.setGadgetUri("http://foo.bar.com/gadget.xml");
    tokenKey.setServiceName("testservice");
    tokenKey.setModuleId(9843243278L);
    tokenKey.setTokenName("testtoken");
    tokenKey.setUserId("test user");

    OAuthStore.TokenInfo tokenInfo =
        new OAuthStore.TokenInfo("token", "secret");


    expect(mockTokens.put(tokenKey, tokenInfo)).andReturn(null);

    control.replay();

    noDefaultStore.setTokenAndSecret(tokenKey, tokenInfo);

    control.verify();
  }
}
