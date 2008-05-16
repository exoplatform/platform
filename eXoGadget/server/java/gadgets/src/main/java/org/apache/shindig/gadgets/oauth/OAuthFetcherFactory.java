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

import org.apache.shindig.gadgets.ContentFetcher;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.util.BasicBlobCrypter;
import org.apache.shindig.util.BlobCrypter;
import org.apache.shindig.util.Crypto;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Produces OAuth content fetchers for input tokens.
 */
public class OAuthFetcherFactory {

  /** used to encrypt state stored on the client */
  protected BlobCrypter oauthCrypter;

  /** persistent storage for OAuth tokens */
  protected GadgetOAuthTokenStore tokenStore;

  private static final Logger logger
      = Logger.getLogger(OAuthFetcherFactory.class.getName());

  /**
   * Initialize the OAuth factory with a default implementation of
   * BlobCrypter and consumer keys/secrets read from oauth.js
   */
  @Inject
  public OAuthFetcherFactory(@OAuthConfigFetcher ContentFetcher fetcher) {
    try {
      this.oauthCrypter = new BasicBlobCrypter(
          Crypto.getRandomBytes(BasicBlobCrypter.MASTER_KEY_MIN_LEN));

      BasicGadgetOAuthTokenStore basicStore =
          new BasicGadgetOAuthTokenStore(new BasicOAuthStore());
      basicStore.initFromConfigFile(fetcher);
      tokenStore = basicStore;
    } catch (Throwable t) {
      // Since this happens at startup, we don't want to kill the server just
      // because we can't initialize the OAuth config.
      logger.log(Level.WARNING, "Failed to initialize OAuth", t);
    }
  }

  /**
   * Creates an OAuthFetcherFactory based on prepared crypter and token store.
   *
   * @param oauthCrypter used to wrap client side state
   * @param tokenStore used as interface to persistent token store.
   */
  protected OAuthFetcherFactory(
      BlobCrypter oauthCrypter,
      GadgetOAuthTokenStore tokenStore) {
    this.oauthCrypter = oauthCrypter;
    this.tokenStore = tokenStore;
  }

  /**
   * Produces an OAuthFetcher that will sign requests and delegate actual
   * network retrieval to the {@code nextFetcher}
   *
   * @param nextFetcher The fetcher that will fetch real content
   * @param token The gadget token used to identity the user and gadget
   * @param params The parsed parameters the gadget requested
   * @return The oauth fetcher.
   * @throws GadgetException
   */
  public OAuthFetcher getOAuthFetcher(
      ContentFetcher nextFetcher,
      GadgetToken token,
      OAuthRequestParams params) throws GadgetException {
    OAuthFetcher fetcher = new OAuthFetcher(
        tokenStore, oauthCrypter, nextFetcher, token, params);
    fetcher.init();
    return fetcher;
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  public @interface OAuthConfigFetcher {
  }
}
