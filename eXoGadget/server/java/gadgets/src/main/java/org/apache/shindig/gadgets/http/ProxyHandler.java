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
package org.apache.shindig.gadgets.http;

import org.apache.shindig.gadgets.ContentFetcher;
import org.apache.shindig.gadgets.ContentFetcherFactory;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.gadgets.GadgetTokenDecoder;
import org.apache.shindig.gadgets.LockedDomainService;
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.gadgets.oauth.OAuthRequestParams;
import org.apache.shindig.gadgets.spec.Auth;
import org.apache.shindig.gadgets.spec.Preload;
import org.apache.shindig.util.InputStreamConsumer;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyHandler {
  public static final String UNPARSEABLE_CRUFT = "throw 1; < don't be evil' >";
  public static final String POST_DATA_PARAM = "postData";
  public static final String METHOD_PARAM = "httpMethod";
  public static final String SECURITY_TOKEN_PARAM = "st";
  public static final String HEADERS_PARAM = "headers";
  public static final String NOCACHE_PARAM = "nocache";
  public static final String SIGN_VIEWER = "signViewer";
  public static final String SIGN_OWNER = "signOwner";
  public static final String URL_PARAM = "url";
  private static final String REFRESH_PARAM = "refresh";

  private static final Logger logger = 
      Logger.getLogger(ProxyHandler.class.getPackage().getName());


  private final GadgetTokenDecoder gadgetTokenDecoder;

  private final static Set<String> DISALLOWED_RESPONSE_HEADERS
      = new HashSet<String>();
  static {
    DISALLOWED_RESPONSE_HEADERS.add("set-cookie");
    DISALLOWED_RESPONSE_HEADERS.add("content-length");
    DISALLOWED_RESPONSE_HEADERS.add("content-encoding");
    DISALLOWED_RESPONSE_HEADERS.add("etag");
    DISALLOWED_RESPONSE_HEADERS.add("last-modified");
    DISALLOWED_RESPONSE_HEADERS.add("accept-ranges");
    DISALLOWED_RESPONSE_HEADERS.add("vary");
  }

  // This isn't a final field because we want to support optional injection.
  // This is a limitation of Guice, but this workaround...works.
  private ContentFetcherFactory contentFetcherFactory;
  private final LockedDomainService domainLocker;

  @Inject
  public void setContentFetcher(ContentFetcherFactory contentFetcherFactory) {
    this.contentFetcherFactory = contentFetcherFactory;
  }

  @Inject
  public ProxyHandler(ContentFetcherFactory contentFetcherFactory,
                      GadgetTokenDecoder gadgetTokenDecoder,
                      LockedDomainService lockedDomainService) {
    this.contentFetcherFactory = contentFetcherFactory;
    this.gadgetTokenDecoder = gadgetTokenDecoder;
    this.domainLocker = lockedDomainService;
  }

  /**
   * Retrieves a file and returns it as a json response.
   *
   * @param request
   * @param response
   * @throws IOException
   * @throws GadgetException
   */
  public void fetchJson(HttpServletRequest request,
                        HttpServletResponse response)
      throws IOException, GadgetException {

    // Build up the request to make
    RemoteContentRequest rcr = buildRemoteContentRequest(request);

    // Build the chain of fetchers that will handle the request
    ContentFetcher fetcher = getContentFetcher(request, response);

    // Do the fetch
    RemoteContent results = fetcher.fetch(rcr);

    // Serialize the response
    String output = serializeJsonResponse(request, results);

    // Find and set the refresh interval
    int refreshInterval = 0;

    if (request.getParameter(REFRESH_PARAM) != null) {
      refreshInterval =  Integer.valueOf(request.getParameter(REFRESH_PARAM));
    }
    HttpUtil.setCachingHeaders(response, refreshInterval);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json; charset=utf-8");
    response.setHeader("Content-Disposition", "attachment;filename=p.txt");
    response.getWriter().write(output);
  }

  /**
   * Generate a remote content request based on the parameters
   * sent from the client.
   * @throws GadgetException
   */
  @SuppressWarnings("unchecked")
  private RemoteContentRequest buildRemoteContentRequest(
      HttpServletRequest request) throws GadgetException {
    try {
      String encoding = request.getCharacterEncoding();
      if (encoding == null) {
        encoding = "UTF-8";
      }

      URI url = validateUrl(request.getParameter(URL_PARAM));
      String method = request.getMethod();
      Map<String, List<String>> headers = null;
      byte[] postBody = null;

      if ("POST".equals(method)) {
        method = getParameter(request, METHOD_PARAM, "GET");
        postBody = getParameter(request, POST_DATA_PARAM, "").getBytes();

        String headerData = request.getParameter(HEADERS_PARAM);
        if (headerData == null || headerData.length() == 0) {
          headers = Collections.emptyMap();
        } else {
          // We actually only accept single key value mappings now.
          headers = new TreeMap<String, List<String>>(
              String.CASE_INSENSITIVE_ORDER);
          String[] headerList = headerData.split("&");
          for (String header : headerList) {
            String[] parts = header.split("=");
            if (parts.length != 2) {
              throw new GadgetException(
                  GadgetException.Code.INTERNAL_SERVER_ERROR,
              "malformed header specified");
            }
            headers.put(URLDecoder.decode(parts[0], encoding),
                Arrays.asList(URLDecoder.decode(parts[1], encoding)));
          }
        }
      } else {
        postBody = null;
        headers = new TreeMap<String, List<String>>(
            String.CASE_INSENSITIVE_ORDER);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
          String header = headerNames.nextElement();
          headers.put(header, Collections.list(request.getHeaders(header)));
        }
      }

      removeUnsafeHeaders(headers);

      RemoteContentRequest.Options options =
        new RemoteContentRequest.Options();
      options.ignoreCache = "1".equals(request.getParameter(NOCACHE_PARAM));
      if (request.getParameter(SIGN_VIEWER) != null) {
        options.viewerSigned = Boolean
            .parseBoolean(request.getParameter(SIGN_VIEWER));
      }
      if (request.getParameter(SIGN_OWNER) != null) {
        options.ownerSigned = Boolean
            .parseBoolean(request.getParameter(SIGN_OWNER));
      }

      return new RemoteContentRequest(
          method, url, headers, postBody, options);
    } catch (UnsupportedEncodingException e) {
      throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e);
    }
  }

  /**
   * At the moment our fetcher chain is short.  In the future we might add
   * additional layers for things like caching or throttling.
   *
   * Whatever we do needs to return a reference to the OAuthFetcher, if it is
   * present, so we can pull data out as needed.
   */
  private ContentFetcher getContentFetcher(HttpServletRequest request,
      HttpServletResponse response) throws GadgetException {

    String authzType = getParameter(request, Preload.AUTHZ_ATTR, "");
    Auth auth = Auth.parse(authzType);
    switch (auth) {
      case NONE:
        return contentFetcherFactory.get();
      case SIGNED:
        return contentFetcherFactory
            .getSigningFetcher(extractAndValidateToken(request));
      case AUTHENTICATED:
        return contentFetcherFactory.getOAuthFetcher(
            extractAndValidateToken(request), new OAuthRequestParams(request));
      default:
        return contentFetcherFactory.get();
    }
  }

  /**
   * Format a response as JSON, including additional JSON inserted by
   * chained content fetchers.
   */
  private String serializeJsonResponse(HttpServletRequest request,
      RemoteContent results) {
    try {
      JSONObject resp = new JSONObject();
      
      resp.put("body", results.getResponseAsString());
      resp.put("rc", results.getHttpStatusCode());

      // Merge in additional response data
      for (Map.Entry<String, String> entry : results.getMetadata().entrySet()) {
        resp.put(entry.getKey(), entry.getValue());
      }
      // Use raw param as key as URL may have to be decoded
      String originalUrl = request.getParameter(URL_PARAM);
      JSONObject json = new JSONObject().put(originalUrl, resp);
      return UNPARSEABLE_CRUFT + json.toString();
    } catch (JSONException e) {
      return "";
    }
  }

  /**
   * This is called for embedding images inline in gadgets, e.g. via img src
   * links created with IG_Embed
   *
   * @param request
   * @param response
   * @throws IOException
   * @throws GadgetException
   */
  public void fetch(HttpServletRequest request,
                    HttpServletResponse response)
      throws IOException, GadgetException {

    String host = request.getHeader("Host");
    if (!domainLocker.embedCanRender(host)) {
      // Force embedded images and the like to their own domain to avoid XSS
      // in gadget domains.
      String msg = "Embed request for url " +
          getParameter(request, URL_PARAM, "") +
          " made to wrong domain " + host;
      logger.info(msg);
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER, msg);
    }
    
    if (request.getHeader("If-Modified-Since") != null) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    }

    RemoteContentRequest rcr = buildRemoteContentRequest(request);
    RemoteContent results = contentFetcherFactory.get().fetch(rcr);

    // Default interval of 1 hour
    int refreshInterval = 60 * 60;
    if (request.getParameter(REFRESH_PARAM) != null) {
      refreshInterval =  Integer.valueOf(request.getParameter(REFRESH_PARAM));
    }

    int status = results.getHttpStatusCode();
    response.setStatus(status);
    if (status == HttpServletResponse.SC_OK) {
      Map<String, List<String>> headers = HttpUtil.enforceCachePolicy(
          results.getAllHeaders(), refreshInterval * 1000L, false);

      if (request.getParameter(REFRESH_PARAM) != null) {
        refreshInterval =  Integer.valueOf(request.getParameter(REFRESH_PARAM));
      }
      else {
        if (headers.get("Cache-Control") == null) {
                // Cache for 1 hour by default for static files.
               refreshInterval = 60 * 60;
        }
      }
      HttpUtil.setCachingHeaders(response, refreshInterval);
      for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
        String name = entry.getKey();
        List<String> values = entry.getValue();
        if (name != null && values != null
            && !DISALLOWED_RESPONSE_HEADERS.contains(name.toLowerCase())) {
          for (String value : values) {
            response.addHeader(name, value);
          }
        }
      }
      response.getOutputStream().write(
          InputStreamConsumer.readToByteArray(results.getResponse()));
      response.getOutputStream().flush() ;
    }
  }

  /**
   * Removes unsafe headers from the header set.
   * @param headers
   */
  private void removeUnsafeHeaders(Map<String, List<String>> headers) {
    // Host must be removed.
    final String[] badHeaders = new String[] {
        // No legitimate reason to over ride these.
        // TODO: We probably need to test variations as well.
        "Host", "Accept", "Accept-Encoding"
    };
    for (String bad : badHeaders) {
      headers.remove(bad);
    }
  }

  /**
   * Validates that the given url is valid for this request.
   *
   * @param urlToValidate
   * @return A URL object of the URL
   * @throws GadgetException if the URL fails security checks or is malformed.
   */
  public URI validateUrl(String urlToValidate) throws GadgetException {
    if (urlToValidate == null) {
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
          "url parameter is missing.");
    }
    try {
      URI url = new URI(urlToValidate);
      if (!"http".equals(url.getScheme()) && !"https".equals(url.getScheme())) {
        throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
            "Invalid request url scheme; only " +
            "\"http\" and \"https\" supported.");
      }
      if (url.getPath() == null || url.getPath().length() == 0) {
        // Forcibly set the path to "/" if it is empty
        url = new URI(url.getScheme(),
                      url.getUserInfo(),
                      url.getHost(),
                      url.getPort(),
                      "/", url.getQuery(),
                      url.getFragment());
      }
      return url;
    } catch (URISyntaxException use) {
      throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
          "url parameter is not a valid url.");
    }
  }

  /**
   * @param request
   * @return A valid token for the given input.
   * @throws GadgetException
   */
  private GadgetToken extractAndValidateToken(HttpServletRequest request)
      throws GadgetException {
    String token = getParameter(request, SECURITY_TOKEN_PARAM, "");
    return gadgetTokenDecoder.createToken(token);
  }

  /**
   * Extracts the first parameter from the parameter map with the given name.
   * @param request
   * @param name
   * @return The parameter, if found, or defaultValue
   */
  private static String getParameter(HttpServletRequest request,
                                     String name, String defaultValue) {
    String ret = request.getParameter(name);
    return ret == null ? defaultValue : ret;
  }
}
