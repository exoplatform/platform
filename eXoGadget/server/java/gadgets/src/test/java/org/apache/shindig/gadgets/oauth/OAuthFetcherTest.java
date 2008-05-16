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
package org.apache.shindig.gadgets.oauth;

import junit.framework.TestCase;

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.gadgets.BasicGadgetToken;
import org.apache.shindig.gadgets.ContentFetcher;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.gadgets.oauth.OAuthStore.HttpMethod;
import org.apache.shindig.gadgets.oauth.OAuthStore.OAuthParamLocation;
import org.apache.shindig.gadgets.oauth.OAuthStore.SignatureType;
import org.apache.shindig.util.BasicBlobCrypter;
import org.apache.shindig.util.BlobCrypter;
import org.apache.shindig.util.BlobCrypterException;

import java.net.URI;

/**
 * Primitive test of the main code paths in OAuthFetcher.
 * 
 * This is a fairly crappy regression test, so if you find yourself wanting
 * to modify this code, you should probably write additional test cases first.
 */
public class OAuthFetcherTest extends TestCase {

  private GadgetOAuthTokenStore tokenStore;
  private BlobCrypter blobCrypter;
  private FakeOAuthServiceProvider serviceProvider;
  
  public static final String SERVICE_NAME = "test";
  public static final String GADGET_URL = "http://www.example.com/gadget.xml";
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    serviceProvider = new FakeOAuthServiceProvider();
    tokenStore = getOAuthStore();
    blobCrypter = new BasicBlobCrypter("abcdefghijklmnop".getBytes());
  }
  
  /**
   * Builds a nicely populated fake token store.
   */
  public static GadgetOAuthTokenStore getOAuthStore() {
    BasicOAuthStore base = new BasicOAuthStore();
    
    OAuthStore.ProviderKey providerKey = new OAuthStore.ProviderKey();
    providerKey.setGadgetUri(GADGET_URL);
    providerKey.setServiceName(SERVICE_NAME);
    
    OAuthStore.ProviderInfo providerInfo = new OAuthStore.ProviderInfo();
    OAuthServiceProvider provider = new OAuthServiceProvider(
        FakeOAuthServiceProvider.REQUEST_TOKEN_URL,
        FakeOAuthServiceProvider.APPROVAL_URL,
        FakeOAuthServiceProvider.ACCESS_TOKEN_URL);
    providerInfo.setProvider(provider);
    providerInfo.setHttpMethod(HttpMethod.GET);
    OAuthStore.ConsumerKeyAndSecret kas = new OAuthStore.ConsumerKeyAndSecret(
        FakeOAuthServiceProvider.CONSUMER_KEY,
        FakeOAuthServiceProvider.CONSUMER_SECRET,
        OAuthStore.KeyType.HMAC_SYMMETRIC);
    providerInfo.setKeyAndSecret(kas);
    providerInfo.setParamLocation(OAuthParamLocation.AUTH_HEADER);
    providerInfo.setSignatureType(SignatureType.HMAC_SHA1);
    
    base.setOAuthServiceProviderInfo(providerKey, providerInfo);
    return new BasicGadgetOAuthTokenStore(base);
  }
  
  /**
   * Builds a nicely populated gadget token.
   */
  public static GadgetToken getGadgetToken(String owner, String viewer)
      throws Exception {
    return new BasicGadgetToken(owner, viewer, "app", "container.com",
        GADGET_URL, "0");
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public ContentFetcher getFetcher(
      GadgetToken authToken, OAuthRequestParams params) throws GadgetException {
    OAuthFetcher fetcher = new OAuthFetcher(
        tokenStore, blobCrypter, serviceProvider, authToken, params);
    fetcher.init();
    return fetcher;
  }

  
  public void testOAuthFlow() throws Exception {
    ContentFetcher fetcher;
    RemoteContentRequest request;
    RemoteContent response;
    
    fetcher = getFetcher(
        getGadgetToken("owner", "owner"),
        new OAuthRequestParams(SERVICE_NAME, null, null));
    request = new RemoteContentRequest(
        new URI(FakeOAuthServiceProvider.RESOURCE_URL));
    response = fetcher.fetch(request);
    String clientState = response.getMetadata().get("oauthState");
    assertNotNull(clientState);
    String approvalUrl = response.getMetadata().get("approvalUrl");
    assertNotNull(approvalUrl);
    
    serviceProvider.browserVisit(approvalUrl + "&user_data=hello-oauth");
    
    fetcher = getFetcher(
        getGadgetToken("owner", "owner"),
        new OAuthRequestParams(SERVICE_NAME, null, clientState));
    request = new RemoteContentRequest(
        new URI(FakeOAuthServiceProvider.RESOURCE_URL));
    response = fetcher.fetch(request);
    assertEquals("User data is hello-oauth", response.getResponseAsString());
    
    fetcher = getFetcher(
        getGadgetToken("owner", "somebody else"),
        new OAuthRequestParams(SERVICE_NAME, null, null));
    request = new RemoteContentRequest(
        new URI(FakeOAuthServiceProvider.RESOURCE_URL));
    response = fetcher.fetch(request);
    assertEquals("User data is hello-oauth", response.getResponseAsString());
    
    fetcher = getFetcher(
        getGadgetToken("somebody else", "somebody else"),
        new OAuthRequestParams(SERVICE_NAME, null, null));
    request = new RemoteContentRequest(
        new URI(FakeOAuthServiceProvider.RESOURCE_URL));
    response = fetcher.fetch(request);
    clientState = response.getMetadata().get("oauthState");
    assertNotNull(clientState);
    approvalUrl = response.getMetadata().get("approvalUrl");
    assertNotNull(approvalUrl);
    
    serviceProvider.browserVisit(approvalUrl + "&user_data=somebody%20else");
    
    fetcher = getFetcher(
        getGadgetToken("somebody else", "somebody else"),
        new OAuthRequestParams(SERVICE_NAME, null, clientState));
    request = new RemoteContentRequest(
        new URI(FakeOAuthServiceProvider.RESOURCE_URL));
    response = fetcher.fetch(request);
    assertEquals("User data is somebody else", response.getResponseAsString());
  }

}
