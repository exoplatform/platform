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

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of the {@link OAuthStore} interface. We use a
 * in-memory hash map. If initialized with a private key, then the store will
 * return an OAuthAccessor in {@code getOAuthAccessor} that uses that private
 * key if no consumer key and secret could be found.
 */
public class BasicOAuthStore implements OAuthStore {

  /**
   * HashMap of provider and consumer information. Maps ProviderKeys (i.e.
   * nickname of a service provider and the gadget that uses that nickname) to
   * {@link OAuthStore.ProviderInfo}s.
   */
  private Map<ProviderKey, ProviderInfo> providers =
      new HashMap<ProviderKey, ProviderInfo>();

  /**
   * HashMap of token information. Maps TokenKeys (i.e. gadget id, token
   * nickname, module id, etc.) to TokenInfos (i.e. access token and token
   * secrets).
   */
  private Map<TokenKey, TokenInfo> tokens = new HashMap<TokenKey, TokenInfo>();

  /**
   * The default consumer key to be used if no consumer key could be found in
   * the store.
   */
  private String defaultConsumerKey;

  /**
   * The default consumer "secret" to be used when no secret could be found in
   * store. This <b>must</b> be a PKCS8-then-Base64-encoded RSA private key.
   * (Can also be null.)
   */
  private String defaultConsumerSecret;

  /**
   * Public constructor. Makes an OAuthStoreImpl that doesn't have a fallback
   * RSA key to sign outgoing requests. When no consumer secret can be found,
   * {@code getOAuthAccessor} will throw an exception.
   */
  public BasicOAuthStore() {
    this(null, null);
  }

  /**
   * Public constructor. Makes an OAuthStoreImpl with a fallback
   * RSA key to sign outgoing requests. When no consumer secret can be found,
   * {@code getOAuthAccessor} will create an OAuthAccessor that uses the
   * fallback RSA private key.
   *
   * @param consumerKey the consumer key to be used when no consumer key
   *        can be found in the oauth store for a specific
   *        {@code getOAuthAccessor} request.
   * @param privateKey the RSA private key to be used when no consumer secret
   *        can be found in the oauth store for a specific
   *        {@code getOAuthAccessor} request.
   */
  public BasicOAuthStore(String consumerKey, String privateKey) {
    defaultConsumerKey = consumerKey;
    defaultConsumerSecret = privateKey;
  }

  void setHashMapsForTesting(
      Map<ProviderKey, ProviderInfo> providers,
      Map<TokenKey, TokenInfo> tokens) {
    this.providers = providers;
    this.tokens = tokens;
  }

  /**
   * {@inheritDoc}
   */
  public AccessorInfo getOAuthAccessor(TokenKey tokenKey)
      throws OAuthNoDataException {

    ProviderKey provKey = new ProviderKey();
    provKey.setGadgetUri(tokenKey.getGadgetUri());
    provKey.setServiceName(tokenKey.getServiceName());

    AccessorInfo result = getOAuthAccessor(provKey);

    TokenInfo accessToken = tokens.get(tokenKey);

    if (accessToken != null) {
      result.getAccessor().accessToken = accessToken.getAccessToken();
      result.getAccessor().tokenSecret = accessToken.getTokenSecret();
    }

    return result;
  }

  private AccessorInfo getOAuthAccessor(ProviderKey providerKey)
      throws OAuthNoDataException {

    ProviderInfo provInfo = providers.get(providerKey);

    if (provInfo == null) {
      throw new OAuthNoDataException("provider info was null in oauth store");
    }

    AccessorInfo result = new AccessorInfo();
    result.setHttpMethod(provInfo.getHttpMethod());
    result.setParamLocation(provInfo.getParamLocation());

    ConsumerKeyAndSecret consumerKeyAndSecret = provInfo.getKeyAndSecret();

    if (consumerKeyAndSecret == null) {
      if (defaultConsumerKey == null || defaultConsumerSecret == null) {
        // if we don't have a fallback key and secret, that's all we can do
        throw new OAuthNoDataException("ConsumerKeyAndSecret " +
                                       "was null in oauth store");
      } else {
        // we'll use the fallback key and secret.
        consumerKeyAndSecret =
          new ConsumerKeyAndSecret(defaultConsumerKey,
              defaultConsumerSecret,
              OAuthStore.KeyType.RSA_PRIVATE);
      }
    }

    OAuthServiceProvider oauthProvider = provInfo.getProvider();

    if (oauthProvider == null) {
      throw new OAuthNoDataException("OAuthService provider " +
      "was null in oauth store");
    }

    boolean usePublicKeyCrypto =
      (consumerKeyAndSecret.getKeyType() == OAuthStore.KeyType.RSA_PRIVATE);

    OAuthConsumer consumer = usePublicKeyCrypto
                             ? new OAuthConsumer(null,  // no callback URL
                                 consumerKeyAndSecret.getConsumerKey(),
                                 null,
                                 oauthProvider)
                             : new OAuthConsumer(null,  // no callback URL
                                 consumerKeyAndSecret.getConsumerKey(),
                                 consumerKeyAndSecret.getConsumerSecret(),
                                 oauthProvider);

    if (usePublicKeyCrypto) {
      consumer.setProperty(RSA_SHA1.PRIVATE_KEY,
                           consumerKeyAndSecret.getConsumerSecret());
      result.setSignatureType(OAuthStore.SignatureType.RSA_SHA1);
    } else {
      result.setSignatureType(OAuthStore.SignatureType.HMAC_SHA1);
    }

    result.setAccessor(new OAuthAccessor(consumer));

    return result;
  }


  /**
   * {@inheritDoc}
   */
  public ProviderInfo getOAuthServiceProviderInfo(ProviderKey providerKey)
      throws OAuthNoDataException {
    ProviderInfo provInfo = providers.get(providerKey);

    if (provInfo == null) {
      throw new OAuthNoDataException("provider info was null in oauth store");
    }

    return provInfo;
  }

  /**
   * {@inheritDoc}
   */
  public void setOAuthConsumerKeyAndSecret(ProviderKey providerKey,
                                           ConsumerKeyAndSecret keyAndSecret)
      throws OAuthNoDataException {

    ProviderInfo provData = providers.get(providerKey);

    if (provData == null) {
      throw new OAuthNoDataException("could not find provider data for token");
    }

    provData.setKeyAndSecret(keyAndSecret);
  }

  /**
   * {@inheritDoc}
   */
  public void setOAuthServiceProviderInfo(ProviderKey providerKey,
                                          ProviderInfo providerInfo) {
      providers.put(providerKey, providerInfo);
  }

  /**
   * {@inheritDoc}
   */
  public void setTokenAndSecret(TokenKey tokenKey, TokenInfo tokenInfo) {
    tokens.put(tokenKey, tokenInfo);
  }
}
