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

import net.oauth.OAuthServiceProvider;

import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.spec.Feature;
import org.apache.shindig.gadgets.spec.GadgetSpec;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Higher-level interface that allows callers to store and retrieve
 * OAuth-related data directly from {@code GadgetSpec}s, {@code GadgetContext}s,
 * etc. See {@link OAuthStore} for a more detailed explanation of the OAuth
 * Data Store.
 */
public class GadgetOAuthTokenStore {

  /**
   * Internal class used to communicate results of parsing the gadget spec
   * between methods.
   */
  static class GadgetInfo {
    private String serviceName;
    private OAuthStore.ProviderInfo providerInfo;

    public String getServiceName() {
      return serviceName;
    }
    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }
    public OAuthStore.ProviderInfo getProviderInfo() {
      return providerInfo;
    }
    public void setProviderInfo(OAuthStore.ProviderInfo providerInfo) {
      this.providerInfo = providerInfo;
    }
  }

  // name of the OAuth feature in the gadget spec
  public static final String OAUTH_FEATURE = "oauth";

  // name of the Param that identifies the service name
  public static final String SERVICE_NAME = "service_name";

  // name of the Param that identifies the access URL
  public static final String ACCESS_URL = "access_url";
  // name of the optional Param that identifies the HTTP method for access URL
  public static final String ACCESS_HTTP_METHOD = "access_method";

  // name of the Param that identifies the request URL
  public static final String REQUEST_URL = "request_url";
  // name of the optional Param that identifies the HTTP method for request URL
  public static final String REQUEST_HTTP_METHOD = "request_method";

  // name of the Param that identifies the user authorization URL
  public static final String AUTHORIZE_URL = "authorize_url";

  // name of the Param that identifies the location of OAuth parameters
  public static final String OAUTH_PARAM_LOCATION = "param_location";

  public static final String AUTH_HEADER = "auth_header";
  public static final String POST_BODY   = "post_body";
  public static final String URI_QUERY = "uri_query";

  public static final String DEFAULT_OAUTH_PARAM_LOCATION = AUTH_HEADER;

  // we use POST if no HTTP method is specified for access and request URLs
  // (user authorization always uses GET)
  private static final String DEFAULT_HTTP_METHOD = "POST";

  private static final Logger log =
      Logger.getLogger(GadgetOAuthTokenStore.class.getName());

  private OAuthStore store;

  /**
   * Public constructor.
   *
   * @param store an {@link OAuthStore} that can store and retrieve OAuth
   *              tokens, as well as information about service providers.
   */
  public GadgetOAuthTokenStore(OAuthStore store) {
    this.store = store;
  }

  /**
   * Parses a gadget spec and stores the service provider information found
   * in the spec into the OAuth store. The spec passed in <b>must</b> require
   * "oauth" as a feature. It is an error to pass in a spec that does not
   * require oauth.
   *
   * @param gadgetUrl the URL of the gadget
   * @param spec the parsed GadgetSpec of the gadget.
   * @throws OAuthStoreException if there is a problem talking to the
   *                             backend store.
   * @throws GadgetException if the gadget spec doesn't require oauth, or if
   *                         there are other problems processing the gadget
   *                         spec.
   */
  public void storeServiceInfoFromGadgetSpec(URI gadgetUrl,
                                             GadgetSpec spec)
      throws GadgetException {
    GadgetInfo gadgetInfo = getGadgetOAuthInfo(spec);

    OAuthStore.ProviderKey providerKey = new OAuthStore.ProviderKey();
    providerKey.setGadgetUri(gadgetUrl.toString());
    providerKey.setServiceName(gadgetInfo.getServiceName());

    store.setOAuthServiceProviderInfo(providerKey,
                                      gadgetInfo.getProviderInfo());
  }

  /**
   * Stores a negotiated consumer key and secret in the gadget store.
   * The "secret" can either be a consumer secret in the strict OAuth sense,
   * or it can be a PKCS8-then-Base64 encoded private key that we'll be using
   * with this service provider.
   *
   * @param gadgetUrl the URL of the gadget
   * @param serviceName the service provider with whom we have negotiated a
   *                    consumer key and secret.
   * @throws OAuthStoreException if there is a problem talking to the
   *                             backend store.
   * @throws OAuthNoDataException if there is no data about this service
   *                              provider stored for this gadget.
   */
  public void storeConsumerKeyAndSecret(
      URI gadgetUrl,
      String serviceName,
      OAuthStore.ConsumerKeyAndSecret keyAndSecret)
        throws OAuthStoreException, OAuthNoDataException {

    OAuthStore.ProviderKey providerKey = new OAuthStore.ProviderKey();
    providerKey.setGadgetUri(gadgetUrl.toString());
    providerKey.setServiceName(serviceName);

    store.setOAuthConsumerKeyAndSecret(providerKey, keyAndSecret);
  }

  /**
   * Stores an access token in the OAuth Data Store.
   * @param tokenKey information about the Gadget storing the token.
   * @param tokenInfo the TokenInfo to be stored in the OAuth data store.
   * @throws OAuthStoreException
   */
  public void storeTokenKeyAndSecret(OAuthStore.TokenKey tokenKey,
                                     OAuthStore.TokenInfo tokenInfo)
      throws OAuthStoreException {

    if (isEmpty(tokenKey.getGadgetUri())) {
      throw new IllegalArgumentException("found empty gadget URI in TokenKey");
    }

    if (isEmpty(tokenKey.getServiceName())) {
      throw new IllegalArgumentException("found empty service " +
      		                             "name in TokenKey");
    }

    if (isEmpty(tokenKey.getUserId())) {
      throw new IllegalArgumentException("found empty userId in TokenKey");
    }

    store.setTokenAndSecret(tokenKey, tokenInfo);
  }

  /**
   * Retrieve an OAuthAccessor that is ready to sign OAuthMessages.
   *
   * @param tokenKey information about the gadget retrieving the accessor.
   *
   * @return an OAuthAccessorInfo containing an OAuthAccessor (whic can be
   *         passed to an OAuthMessage.sign method), as well as httpMethod and
   *         signatureType fields.
   * @throws OAuthNoDataException if the token couldn't be found
   * @throws OAuthStoreException if an error occurred accessing the data
   *                             store.
   */
  public OAuthStore.AccessorInfo getOAuthAccessor(OAuthStore.TokenKey tokenKey)
      throws OAuthNoDataException, OAuthStoreException {

    if (isEmpty(tokenKey.getGadgetUri())) {
      throw new IllegalArgumentException("found empty gadget URI in TokenKey");
    }

    if (isEmpty(tokenKey.getServiceName())) {
      throw new IllegalArgumentException("found empty service " +
                                         "name in TokenKey");
    }

    if (isEmpty(tokenKey.getUserId())) {
      throw new IllegalArgumentException("found empty userId in TokenKey");
    }

    return store.getOAuthAccessor(tokenKey);
  }

  /**
   * Reads OAuth provider information out of gadget spec.
   * @param spec
   * @return a GadgetInfo
   * @throws GadgetException if some information is missing, or something else
   *                         is wrong with the spec.
   */
  static GadgetInfo getGadgetOAuthInfo(GadgetSpec spec) throws GadgetException {
    Feature oauthFeature =
        spec.getModulePrefs().getFeatures().get(OAUTH_FEATURE);

    if (oauthFeature == null) {
      String message = "gadget spec is missing oauth feature section";
      log.warning(message);
      throw new GadgetException(GadgetException.Code.MISSING_PARAMETER,
                                message);
    }

    Map<String, String> oauthParams = oauthFeature.getParams();

    String serviceName = getOAuthParameter(oauthParams, SERVICE_NAME, false);

    String requestUrl = getOAuthParameter(oauthParams, REQUEST_URL, false);
    String requestMethod = getOAuthParameter(oauthParams,
                                             REQUEST_HTTP_METHOD,
                                             true);
    if (requestMethod == null) {
      requestMethod = DEFAULT_HTTP_METHOD;
    }

    String accessUrl = getOAuthParameter(oauthParams, ACCESS_URL, false);
    String accessMethod = getOAuthParameter(
        oauthParams, 
        ACCESS_HTTP_METHOD,
        true);
    if (accessMethod == null) {
      accessMethod = DEFAULT_HTTP_METHOD;
    }

    if (!accessMethod.equalsIgnoreCase(requestMethod)) {
      String message = new StringBuilder()
          .append("HTTP methods of access and request URLs have to match. ")
          .append("access method was: ")
          .append(accessMethod)
          .append(". request method was: ")
          .append(requestMethod)
          .toString();
      log.warning(message);
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
                                message);
    }

    String authorizeUrl = getOAuthParameter(oauthParams, AUTHORIZE_URL, false);

    OAuthServiceProvider provider = new OAuthServiceProvider(requestUrl,
                                                             authorizeUrl,
                                                             accessUrl);

    OAuthStore.HttpMethod httpMethod;
    if (accessMethod.equalsIgnoreCase("GET")) {
      httpMethod = OAuthStore.HttpMethod.GET;
    } else if (accessMethod.equalsIgnoreCase("POST")) {
      httpMethod = OAuthStore.HttpMethod.POST;
    } else {
      String message = new StringBuilder()
          .append("unknown http method in gadget spec: ")
          .append(accessMethod)
          .toString();
      log.warning(message);
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
                                message);
    }

    String paramLocationStr = getOAuthParameter(oauthParams,
                                                OAUTH_PARAM_LOCATION,
                                                true);
    if (paramLocationStr == null) {
      paramLocationStr = DEFAULT_OAUTH_PARAM_LOCATION;
    }

    OAuthStore.OAuthParamLocation paramLocation;
    if (paramLocationStr.equalsIgnoreCase(POST_BODY)) {
      paramLocation = OAuthStore.OAuthParamLocation.POST_BODY;
    } else if (paramLocationStr.equalsIgnoreCase(AUTH_HEADER)) {
      paramLocation = OAuthStore.OAuthParamLocation.AUTH_HEADER;
    } else if (paramLocationStr.equalsIgnoreCase(URI_QUERY)) {
      paramLocation = OAuthStore.OAuthParamLocation.URI_QUERY;
    } else {
      String message = new StringBuilder()
          .append("unknown OAuth param location in gadget spec: ")
          .append(paramLocationStr)
          .toString();
      log.warning(message);
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
                                message);
    }

    if (httpMethod == OAuthStore.HttpMethod.GET &&
        paramLocation == OAuthStore.OAuthParamLocation.POST_BODY) {
      String message = new StringBuilder()
          .append("found incompatible param_location requirement of ")
          .append("POST_BODY and http method GET.")
          .toString();
      log.warning(message);
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
                                message);
    }

    OAuthStore.ProviderInfo provInfo = new OAuthStore.ProviderInfo();
    provInfo.setHttpMethod(httpMethod);
    provInfo.setParamLocation(paramLocation);

    // TODO: for now, we'll just set the signature type to HMAC_SHA1
    // as this will be ignored later on when retrieving consumer information.
    // There, if we find a negotiated HMAC key, we will use HMAC_SHA1. If we
    // find a negotiated RSA key, we will use RSA_SHA1. And if we find neither,
    // we may use RSA_SHA1 with a default signing key.
    provInfo.setSignatureType(OAuthStore.SignatureType.HMAC_SHA1);
    provInfo.setProvider(provider);

    GadgetInfo gadgetInfo = new GadgetInfo();
    gadgetInfo.setProviderInfo(provInfo);
    gadgetInfo.setServiceName(serviceName);

    return gadgetInfo;
  }

  /**
   * Extracts a single oauth-related parameter from a key-value map,
   * throwing an exception if the parameter could not be found (unless the
   * parameter is optional, in which case null is returned).
   *
   * @param params the key-value map from which to pull the value (parameter)
   * @param paramName the name of the parameter (key).
   * @param isOptional if it's optional, don't throw an exception if it's not
   *                   found.
   * @return the value corresponding to the key (paramName)
   * @throws GadgetException if the parameter value couldn't be found.
   */
  static String getOAuthParameter(Map<String, String> params,
                                  String paramName,
                                  boolean isOptional)
      throws GadgetException {

    String param = params.get(paramName);

    if (param == null && !isOptional) {
      String message = new StringBuilder()
          .append("parameter '")
          .append(paramName)
          .append("' missing in oauth feature section of gadget spec")
          .toString();
      log.warning(message);
      throw new GadgetException(GadgetException.Code.MISSING_PARAMETER,
                                message);
    }
    return (param == null) ? null : param.trim();
  }

  static boolean isEmpty(String string) {
    return (string == null) || (string.trim().length() == 0);
  }
}
