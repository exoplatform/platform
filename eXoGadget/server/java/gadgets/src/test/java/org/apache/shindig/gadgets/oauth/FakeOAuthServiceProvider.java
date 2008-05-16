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

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;

import org.apache.shindig.gadgets.ContentFetcher;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FakeOAuthServiceProvider implements ContentFetcher {

  public final static String SP_HOST = "http://www.example.com";
  
  public final static String REQUEST_TOKEN_URL =
      SP_HOST + "/request?param=foo";
  public final static String ACCESS_TOKEN_URL = 
      SP_HOST + "/access";
  public final static String APPROVAL_URL =
      SP_HOST + "/authorize";
  public final static String RESOURCE_URL = 
      SP_HOST + "/data";
  
  public final static String CONSUMER_KEY = "consumer";
  public final static String CONSUMER_SECRET = "secret";
  
  private static class TokenState {
    String tokenSecret;
    OAuthConsumer consumer;
    State state;
    String userData;
    
    enum State {
      PENDING,
      APPROVED,
    } 
    
    public TokenState(String tokenSecret, OAuthConsumer consumer) {
      this.tokenSecret = tokenSecret;
      this.consumer = consumer;
      this.state = State.PENDING;
      this.userData = null;
    }
    
    public static TokenState makeAccessTokenState(String tokenSecret,
        OAuthConsumer consumer) {
      TokenState s = new TokenState(tokenSecret, consumer);
      s.setState(State.APPROVED);
      return s;
    }

    public void setState(State state) {
      this.state = state;
    }
    
    public State getState() {
      return state;
    }
    
    public String getSecret() {
      return tokenSecret;
    }
    
    public void setUserData(String userData) {
      this.userData = userData;
    }
    
    public String getUserData() {
      return userData;
    }
  }
  
  /**
   * Table of OAuth access tokens
   */
  private final HashMap<String, TokenState> tokenState;
  private final OAuthValidator validator;
  private final OAuthConsumer consumer;
  
  public FakeOAuthServiceProvider() {
    OAuthServiceProvider provider = new OAuthServiceProvider(
        REQUEST_TOKEN_URL, APPROVAL_URL, ACCESS_TOKEN_URL);
    consumer = new OAuthConsumer(
        null, CONSUMER_KEY, CONSUMER_SECRET, provider);
    tokenState = new HashMap<String, TokenState>();
    validator = new SimpleOAuthValidator();
  }
  
  public RemoteContent fetch(RemoteContentRequest request)
      throws GadgetException {
    String url = request.getUri().toASCIIString();
    try {
      if (url.startsWith(REQUEST_TOKEN_URL)) {
        return handleRequestTokenUrl(request);
      } else if (url.startsWith(ACCESS_TOKEN_URL)) {
        return handleAccessTokenUrl(request);
      } else if (url.startsWith(RESOURCE_URL)){
        return handleResourceUrl(request);
      }
    } catch (Exception e) {
      throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR,
          "Problem with request for URL " + url, e);
    }
    throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR,
        "Unexpected request for " + url);
  }

  private RemoteContent handleRequestTokenUrl(RemoteContentRequest request)
      throws Exception {
    OAuthMessage message = parseMessage(request);
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    message.validateMessage(accessor, validator);
    String requestToken = Crypto.getRandomString(16);
    String requestTokenSecret = Crypto.getRandomString(16);
    tokenState.put(
        requestToken, new TokenState(requestToken, accessor.consumer));
    String resp = OAuth.formEncode(OAuth.newList(
        "oauth_token", requestToken,
        "oauth_token_secret", requestToken));
    return new RemoteContent(resp);
  }

  // Loosely based off net.oauth.OAuthServlet, and even more loosely related
  // to the OAuth specification
  private OAuthMessage parseMessage(RemoteContentRequest request) {
    String method = request.getMethod();
    if (!method.equals("GET")) {
      throw new RuntimeException("Only GET supported for now");
    }
    ParsedUrl url = new ParsedUrl(request.getUri().toASCIIString());
    List<OAuth.Parameter> params = new ArrayList<OAuth.Parameter>();
    params.addAll(url.getParsedQuery());
    String aznHeader = request.getHeader("Authorization");
    if (aznHeader != null) {
      for (OAuth.Parameter p : OAuthMessage.decodeAuthorization(aznHeader)) {
        if (!p.getKey().equalsIgnoreCase("realm")) {
          params.add(p);
        }
      }
    }
    return new OAuthMessage(method, url.getLocation(), params);
  }
  
  /**
   * Utility class for parsing OAuth URLs.
   */
  private static class ParsedUrl {
    String location = null;
    String query = null;
    List<OAuth.Parameter> decodedQuery = null;
    
    public ParsedUrl(String url) {
      int queryIndex = url.indexOf('?');
      if (queryIndex != -1) {
        query = url.substring(queryIndex+1, url.length());
        location = url.substring(0, queryIndex);
      } else {
        location = url;
      }
    }
    
    public String getLocation() {
      return location;
    }
    
    public String getRawQuery() {
      return query;
    }
    
    public List<OAuth.Parameter> getParsedQuery() {
      if (decodedQuery == null) {
        if (query != null) {
          decodedQuery = OAuth.decodeForm(query);
        } else {
          decodedQuery = new ArrayList<OAuth.Parameter>();
        }
      }
      return decodedQuery;
    }
    
    public String getQueryParam(String name) {
      for (OAuth.Parameter p : getParsedQuery()) {
        if (p.getKey().equals(name)) {
          return p.getValue();
        }
      }
      return null;
    }
  }
  
  /**
   * Used to fake a browser visit to approve a token.
   * @param url
   * @throws Exception
   */
  public void browserVisit(String url) throws Exception {
    ParsedUrl parsed = new ParsedUrl(url);
    String requestToken = parsed.getQueryParam("oauth_token");
    TokenState state = tokenState.get(requestToken);
    state.setState(TokenState.State.APPROVED);
    // Not part of the OAuth spec, just a handy thing for testing.
    state.setUserData(parsed.getQueryParam("user_data"));
  }

  private RemoteContent handleAccessTokenUrl(RemoteContentRequest request)
      throws Exception {
    OAuthMessage message = parseMessage(request);
    String requestToken = message.getParameter("oauth_token");
    TokenState state = tokenState.get(requestToken);
    if (state.getState() != TokenState.State.APPROVED) {
      throw new Exception("Token not approved");
    }
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    accessor.requestToken = requestToken;
    accessor.tokenSecret = state.tokenSecret;
    message.validateMessage(accessor, validator);
    String accessToken = Crypto.getRandomString(16);
    String accessTokenSecret = Crypto.getRandomString(16);
    state.tokenSecret = accessTokenSecret;
    tokenState.put(accessToken, state);
    tokenState.remove(requestToken);
    String resp = OAuth.formEncode(OAuth.newList(
        "oauth_token", accessToken,
        "oauth_token_secret", accessTokenSecret));
    return new RemoteContent(resp);
  }

  private RemoteContent handleResourceUrl(RemoteContentRequest request)
      throws Exception {
    OAuthMessage message = parseMessage(request);
    String accessToken = message.getParameter("oauth_token");
    TokenState state = tokenState.get(accessToken);
    if (state.getState() != TokenState.State.APPROVED) {
      throw new Exception("Token not approved");
    }
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    accessor.accessToken = accessToken;
    accessor.tokenSecret = state.getSecret();
    message.validateMessage(accessor, validator);
    return new RemoteContent("User data is " + state.getUserData());
  }

}
