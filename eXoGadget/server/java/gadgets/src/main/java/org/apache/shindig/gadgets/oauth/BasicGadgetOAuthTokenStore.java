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
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.util.ResourceLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class BasicGadgetOAuthTokenStore extends GadgetOAuthTokenStore {

  /** default location for consumer keys and secrets */
  private static final String OAUTH_CONFIG = "config/oauth.json";

  private static final String CONSUMER_SECRET_KEY = "consumer_secret";
  private static final String CONSUMER_KEY_KEY = "consumer_key";
  private static final String KEY_TYPE_KEY = "key_type";

  public BasicGadgetOAuthTokenStore(OAuthStore store) {
    super(store);
  }

  public void initFromConfigFile(ContentFetcher fetcher)
  throws GadgetException {
    // Read our consumer keys and secrets from config/oauth.js
    // This actually involves fetching gadget specs
    try {
      String oauthConfigStr = ResourceLoader.getContent(OAUTH_CONFIG);

      JSONObject oauthConfigs = new JSONObject(oauthConfigStr);

      for (Iterator<?> i = oauthConfigs.keys(); i.hasNext();) {
        String url = (String) i.next();
        URI gadgetUri = new URI(url);
        storeProviderInfos(fetcher, gadgetUri);

        JSONObject oauthConfig = oauthConfigs.getJSONObject(url);
        storeConsumerInfos(gadgetUri, oauthConfig);
      }
    } catch (IOException e) {
      throw new GadgetException(GadgetException.Code.OAUTH_STORAGE_ERROR, e);
    } catch (JSONException e) {
      throw new GadgetException(GadgetException.Code.OAUTH_STORAGE_ERROR, e);
    } catch (URISyntaxException e) {
      throw new GadgetException(GadgetException.Code.OAUTH_STORAGE_ERROR, e);
    }
  }

  private void storeProviderInfos(ContentFetcher fetcher, URI gadgetUri)
      throws GadgetException {
    RemoteContentRequest request = RemoteContentRequest.getRequest(
        gadgetUri, false);
    RemoteContent response = fetcher.fetch(request);
    GadgetSpec spec
        = new GadgetSpec(gadgetUri, response.getResponseAsString());
    storeServiceInfoFromGadgetSpec(gadgetUri, spec);
  }

  private void storeConsumerInfos(URI gadgetUri, JSONObject oauthConfig)
  throws JSONException, OAuthStoreException {

    for (String serviceName : JSONObject.getNames(oauthConfig)) {
      JSONObject consumerInfo = oauthConfig.getJSONObject(serviceName);
      storeConsumerInfo(gadgetUri, serviceName, consumerInfo);
    }
  }

  private void storeConsumerInfo(URI gadgetUri,
      String serviceName, JSONObject consumerInfo)
  throws JSONException, OAuthStoreException {

    String consumerSecret = consumerInfo.getString(CONSUMER_SECRET_KEY);
    String consumerKey = consumerInfo.getString(CONSUMER_KEY_KEY);
    String keyTypeStr = consumerInfo.getString(KEY_TYPE_KEY);
    OAuthStore.KeyType keyType = OAuthStore.KeyType.HMAC_SYMMETRIC;

    if (keyTypeStr.equals("RSA_PRIVATE")) {
      keyType = OAuthStore.KeyType.RSA_PRIVATE;
    }

    OAuthStore.ConsumerKeyAndSecret kas = new OAuthStore.ConsumerKeyAndSecret(
        consumerKey, consumerSecret, keyType);

    storeConsumerKeyAndSecret(gadgetUri, serviceName, kas);
  }

}
