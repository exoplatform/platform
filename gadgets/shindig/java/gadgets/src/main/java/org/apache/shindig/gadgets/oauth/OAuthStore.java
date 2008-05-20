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
import net.oauth.OAuthServiceProvider;

/**
 * Interface to an OAuth Data Store. A shindig gadget server can act as an
 * OAuth consumer, using OAuth tokens to talk to OAuth service providers on
 * behalf of the gadgets it is proxying requests for. An OAuth consumer needs
 * to permanently store gadgets it has collected, and retrieve the
 * appropriate tokens when proxying a request for a gadget.
 *
 * An OAuth Data Store stores three things:
 *  (1) information about OAuth service providers, including the three
 *      URLs that define OAuth providers (defined in OAuthStore.ProviderInfo)
 *  (2) information about consumer keys and secrets that gadgets might have
 *      negotiated with OAuth service providers, or that containers might have
 *      negotiated on behalf of the gadgets. This information is bound to
 *      the service provider it pertains to and can only be stored if the
 *      corresponding service provider information is already stored in the
 *      OAuth store (defined in OAuthStore.ConsumerKeyAndSecret).
 *  (3) OAuth access tokens and their corresponding token secrets. (defined
 *      in OAuthStore.TokenInfo).
 *
 *  Note that we do not store request tokens in the OAuth store.
 */
public interface OAuthStore {

  /**
   * Set information about a service provider, as it is known to a certain
   * gadget.
   *
   * @param providerKey the gadget that wants to use the service provider, and
   *                    a nickName under which the gadget will refer to this
   *                    service provider in the future.
   * @param providerInfo the three URLs that define this provider. Also
   *                     contains the httpMethod used to access the service
   *                     provider. (Technically, OAuth allows different methods
   *                     for the different URLs, but the Java OAuth library
   *                     doesn't support that. So we're also just storing one
   *                     preference here.) Also contains the signature type
   *                     (RSA, HMAC, or PLAINTEXT) to be
   *                     used for this service provider. (Again, OAuth allows
   *                     different signature types for each of the three
   *                     URLs, but we just assume they're all the same, and
   *                     provide just one parameter to specify signature type.)
   * @throws OAuthStoreException if there was a problem accessing the
   *                                    store.
   */
  public void setOAuthServiceProviderInfo(ProviderKey providerKey,
                                          ProviderInfo providerInfo)
      throws OAuthStoreException;

  /**
   * Gets information about a service provider (access, request, and authorize
   * URLs with access methods and signature types), given a gadget description
   * and a service name.
   *
   * @param providerKey the gadget that wants to use the service provider, and
   *                    a nickName under which the gadget refers to this
   *                    service provider.
   * @return the provider information.
   * @throws OAuthStoreException
   * @throws OAuthNoDataException
   */
  public ProviderInfo getOAuthServiceProviderInfo(ProviderKey providerKey)
      throws OAuthStoreException, OAuthNoDataException;

  /**
   * If we or a gadget negotiate a separate consumer key and secret with a
   * service provider, use this method to store it. The "secret" can either
   * be a consumer secret in the strict OAuth sense, or it can be a
   * PKCS8-then-Base64 encoded private key that we'll be using with this
   * service provider. Even if we never set a consumer secret, an implementation
   * of this interface may still be able to return useful OAuthAccessors in
   * the getOAuthAccessor method, e.g. by returning a fallback private key and
   * default consumer key, which could be used with service providers that we
   * haven't negotiated a consumer secret with.
   *
   * @param providerKey the gadget that wants to use the service provider, and
   *                    a nickName under which the gadget will refer to this
   *                    service provider in the future.
   * @param keyAndSecret the consumer key and secret. If the secret is an RSA
   *                     private key, it must be PKCS8-then-Base64 encoded.
   * @throws OAuthStoreException if something goes wrong accessing the
   *                                   data store.
   * @throws OAuthNoDataException if no provider info can be found for the
   *                              provider specified in the providerKey.
   */
  public void setOAuthConsumerKeyAndSecret(ProviderKey providerKey,
                                           ConsumerKeyAndSecret keyAndSecret)
      throws OAuthStoreException, OAuthNoDataException;

  /**
   * Stores an access token.
   * @param tokenKey a structure uniquely identifying the key under which this
   *                 access token should be retrievable: a userId, a gadgetId,
   *                 a moduleId (in case there are more than one gadget of the
   *                 same type on a page), a tokenName (which distinguishes
   *                 this token from others that the same gadget might hold for
   *                 the same service provider) and a serviceName (which is the
   *                 same as the service name in the ProviderKey structure).
   * @param tokenInfo an access token and token secret.
   * @throws OAuthStoreException if an error occurs talking to the
   *                                   data store.
   */
  public void setTokenAndSecret(TokenKey tokenKey, TokenInfo tokenInfo)
      throws OAuthStoreException;

  /**
   * Retrieve an OAuthAccessor that is ready to sign OAuthMessages for
   * resource access.
   * @param tokenKey a structure uniquely identifying the token: a userId,
   *                 a gadgetId, a moduleId (in case there are more than one
   *                 gadget of the same type on a page), a tokenName (which
   *                 distinguishes this token from others that the same gadget
   *                 might hold for the same service provider) and a serviceName
   *                 (which is the same as the service name in the ProviderKey
   *                 structure).
   * @return an OAuthAccessor object than can be passed to an OAuthMessage.sign
   *         method.
   * @throws OAuthNoDataException if the token couldn't be found
   * @throws OAuthStoreException if an error occurred accessing the data
   *                                   store.
   */
  public AccessorInfo getOAuthAccessor(TokenKey tokenKey)
      throws OAuthNoDataException, OAuthStoreException;


  //////////////////////////////////////////////////////////////////////////////
  // Auxiliary types needed to work with this interface
  //////////////////////////////////////////////////////////////////////////////

  public static enum HttpMethod { GET, POST }
  public static enum SignatureType {HMAC_SHA1, RSA_SHA1, PLAINTEXT}
  public static enum KeyType { HMAC_SYMMETRIC, RSA_PRIVATE }
  public static enum OAuthParamLocation {
    AUTH_HEADER,
    POST_BODY,
    URI_QUERY
  }

  public static class AccessorInfo {
    OAuthAccessor accessor;
    HttpMethod httpMethod;
    SignatureType signatureType;
    OAuthParamLocation paramLocation;

    public OAuthParamLocation getParamLocation() {
      return paramLocation;
    }
    public void setParamLocation(OAuthParamLocation paramLocation) {
      this.paramLocation = paramLocation;
    }
    public OAuthAccessor getAccessor() {
      return accessor;
    }
    public void setAccessor(OAuthAccessor accessor) {
      this.accessor = accessor;
    }
    public HttpMethod getHttpMethod() {
      return httpMethod;
    }
    public void setHttpMethod(HttpMethod httpMethod) {
      this.httpMethod = httpMethod;
    }
    public SignatureType getSignatureType() {
      return signatureType;
    }
    public void setSignatureType(SignatureType signatureType) {
      this.signatureType = signatureType;
    }
  }

  public static class ConsumerKeyAndSecret {
    private String consumerKey;
    private String consumerSecret;
    private KeyType keyType;

    public ConsumerKeyAndSecret(String key, String secret, KeyType type) {
      consumerKey = key;
      consumerSecret = secret;
      keyType = type;
    }
    public String getConsumerKey() {
      return consumerKey;
    }
    public String getConsumerSecret() {
      return consumerSecret;
    }
    public KeyType getKeyType() {
      return keyType;
    }
  }

  public static class ProviderKey {
    private String gadgetUri;
    private String serviceName;

    public String getGadgetUri() {
      return gadgetUri;
    }
    public void setGadgetUri(String gadgetUri) {
      this.gadgetUri = gadgetUri;
    }
    public String getServiceName() {
      return serviceName;
    }
    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result =
          prime * result + ((gadgetUri == null) ? 0 : gadgetUri.hashCode());
      result =
          prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final ProviderKey other = (ProviderKey) obj;
      if (gadgetUri == null) {
        if (other.gadgetUri != null) return false;
      } else if (!gadgetUri.equals(other.gadgetUri)) return false;
      if (serviceName == null) {
        if (other.serviceName != null) return false;
      } else if (!serviceName.equals(other.serviceName)) return false;
      return true;
    }
  }

  public static class ProviderInfo {
    private OAuthServiceProvider provider;
    private HttpMethod httpMethod;
    private SignatureType signatureType;
    private OAuthParamLocation paramLocation;

    // this can be null if we have not negotiated a consumer key and secret
    // yet with the provider, or if we decided that we want to use a global
    // public key
    private ConsumerKeyAndSecret keyAndSecret;

    public OAuthParamLocation getParamLocation() {
      return paramLocation;
    }
    public void setParamLocation(OAuthParamLocation paramLocation) {
      this.paramLocation = paramLocation;
    }
    public ConsumerKeyAndSecret getKeyAndSecret() {
      return keyAndSecret;
    }
    public void setKeyAndSecret(ConsumerKeyAndSecret keyAndSecret) {
      this.keyAndSecret = keyAndSecret;
    }
    public OAuthServiceProvider getProvider() {
      return provider;
    }
    public void setProvider(OAuthServiceProvider provider) {
      this.provider = provider;
    }
    public HttpMethod getHttpMethod() {
      return httpMethod;
    }
    public void setHttpMethod(HttpMethod httpMethod) {
      this.httpMethod = httpMethod;
    }
    public SignatureType getSignatureType() {
      return signatureType;
    }
    public void setSignatureType(SignatureType signatureType) {
      this.signatureType = signatureType;
    }
  }

  public static class TokenKey {
    private String userId;
    private String gadgetUri;
    private long moduleId;
    private String tokenName;
    private String serviceName;

    public String getUserId() {
      return userId;
    }
    public void setUserId(String userId) {
      this.userId = userId;
    }
    public String getGadgetUri() {
      return gadgetUri;
    }
    public void setGadgetUri(String gadgetUri) {
      this.gadgetUri = gadgetUri;
    }
    public long getModuleId() {
      return moduleId;
    }
    public void setModuleId(long moduleId) {
      this.moduleId = moduleId;
    }
    public String getTokenName() {
      return tokenName;
    }
    public void setTokenName(String tokenName) {
      this.tokenName = tokenName;
    }
    public String getServiceName() {
      return serviceName;
    }
    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result =
          prime * result + ((gadgetUri == null) ? 0 : gadgetUri.hashCode());
      result = prime * result + (int) (moduleId ^ (moduleId >>> 32));
      result =
          prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
      result =
          prime * result + ((tokenName == null) ? 0 : tokenName.hashCode());
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final TokenKey other = (TokenKey) obj;
      if (gadgetUri == null) {
        if (other.gadgetUri != null) return false;
      } else if (!gadgetUri.equals(other.gadgetUri)) return false;
      if (moduleId != other.moduleId) return false;
      if (serviceName == null) {
        if (other.serviceName != null) return false;
      } else if (!serviceName.equals(other.serviceName)) return false;
      if (tokenName == null) {
        if (other.tokenName != null) return false;
      } else if (!tokenName.equals(other.tokenName)) return false;
      if (userId == null) {
        if (other.userId != null) return false;
      } else if (!userId.equals(other.userId)) return false;
      return true;
    }
  }

  public static class TokenInfo {
    private String accessToken;
    private String tokenSecret;
    public TokenInfo(String token, String secret) {
      accessToken = token;
      tokenSecret = secret;
    }
    public String getAccessToken() {
      return accessToken;
    }
    public String getTokenSecret() {
      return tokenSecret;
    }
  }
}
