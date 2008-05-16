/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shindig.gadgets;

import org.apache.shindig.util.Crypto;
import org.apache.shindig.util.TimeSource;

import net.oauth.OAuth;
import net.oauth.OAuth.Parameter;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.signature.RSA_SHA1;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implements signed fetch based on the OAuth request signing algorithm.
 *
 * Subclasses can override signMessage to use their own crypto if they don't
 * like the oauth.net code for some reason.
 *
 * Instances of this class are only accessed by a single thread at a time,
 * but instances may be created by multiple threads.
 */
public class SigningFetcher extends ChainedContentFetcher {

  protected static final String OPENSOCIAL_OWNERID = "opensocial_owner_id";

  protected static final String OPENSOCIAL_VIEWERID = "opensocial_viewer_id";

  protected static final String OPENSOCIAL_APPID = "opensocial_app_id";

  protected static final String XOAUTH_PUBLIC_KEY
      = "xoauth_signature_publickey";

  protected static final Pattern ALLOWED_PARAM_NAME
      = Pattern.compile("[-:\\w~!@$*()_\\[\\]:,./]+");

  protected final TimeSource clock = new TimeSource();

  /**
   * Authentication token for the user and gadget making the request.
   */
  protected final GadgetToken authToken;

  /**
   * Private key we pass to the OAuth RSA_SHA1 algorithm.  This can be a
   * PrivateKey object, or a PEM formatted private key, or a DER encoded byte
   * array for the private key.  (No, really, they accept any of them.)
   */
  protected final Object privateKeyObject;

  /**
   * The name of the key, included in the fetch to help with key rotation.
   */
  protected final String keyName;

  /**
   *  The cache to fetch results in. 
   */
  protected final ContentCache cache;

  /**
   * Constructor for subclasses that don't want this code to use their
   * keys.
   */
  protected SigningFetcher(ContentCache cache,
      ContentFetcher next, GadgetToken authToken) {
    this(cache, next, authToken, null, null);
  }

  /**
   * Constructor based on signing with the given PrivateKey object.
   *
   * @param authToken verified gadget security token
   * @param keyName name of the key to include in the request
   * @param privateKey the key to use for the signing
   */
  public static SigningFetcher makeFromPrivateKey(ContentCache cache,
      ContentFetcher next, GadgetToken authToken,
      String keyName, PrivateKey privateKey) {
    return new SigningFetcher(cache, next, authToken, keyName, privateKey);
  }

  /**
   * Constructor based on signing with the given PrivateKey object.
   *
   * @param authToken verified gadget security token
   * @param keyName name of the key to include in the request
   * @param privateKey base64 encoded private key
   */
  public static SigningFetcher makeFromB64PrivateKey(ContentCache cache,
      ContentFetcher next,
      GadgetToken authToken, String keyName, String privateKey) {
    return new SigningFetcher(cache, next, authToken, keyName, privateKey);
  }

  /**
   * Constructor based on signing with the given PrivateKey object.
   *
   * @param authToken verified gadget security token
   * @param keyName name of the key to include in the request
   * @param privateKey DER encoded private key
   */
  public static SigningFetcher makeFromPrivateKeyBytes(
      ContentCache cache, ContentFetcher next,
      GadgetToken authToken, String keyName,
      byte[] privateKey) {
    return new SigningFetcher(cache, next, authToken, keyName, privateKey);
  }

  protected SigningFetcher(ContentCache cache, ContentFetcher next,
      GadgetToken authToken, String keyName, Object privateKeyObject) {
    super(next);
    this.cache = cache;
    this.authToken = authToken;
    this.keyName = keyName;
    this.privateKeyObject = privateKeyObject;
  }

  public RemoteContent fetch(RemoteContentRequest request)
      throws GadgetException {

    try {
      RemoteContentRequest cacheableRequest = makeCacheableRequest(request);
      RemoteContent result = cache.getContent(cacheableRequest);
      if (result != null) {
        return result;
      }

      RemoteContentRequest signedRequest = signRequest(request);
      // Signed requests are not externally cacehable
      signedRequest.getOptions().ignoreCache = true;
      result = nextFetcher.fetch(signedRequest);

      // Try and cache the response
      cache.addContent(cacheableRequest, result);

      return result;
    } catch (GadgetException e) {
      throw e;
    } catch (Exception e) {
      throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e);
    }
  }

  private RemoteContentRequest makeCacheableRequest(
      RemoteContentRequest request)
      throws IOException, URISyntaxException, RequestSigningException {
    // Create a request without the OAuth params which includes the
    // OpenSocial ones and see if we can find it in the cache
    URI resource = request.getUri();
    String query = resource.getRawQuery();
    List<Parameter> cacheableParams = sanitize(OAuth.decodeForm(query));
    addOpenSocialParams(request.getOptions(), cacheableParams);
    addOAuthNonTemporalParams(cacheableParams);
    String cacheableQuery = OAuth.formEncode(cacheableParams);
    URL url = new URL(
          resource.getScheme(),
          resource.getHost(),
          resource.getPort(),
          resource.getRawPath() + "?" + cacheableQuery);
    RemoteContentRequest cacheableRequest =
        new RemoteContentRequest(url.toURI(), request);
    return cacheableRequest;
  }

  private RemoteContentRequest signRequest(RemoteContentRequest req)
      throws GadgetException {
    try {
      // Parse the request into parameters for OAuth signing, stripping out
      // any OAuth or OpenSocial parameters injected by the client
      URI resource = req.getUri();
      String query = resource.getRawQuery();
      resource = removeQuery(resource);
      List<OAuth.Parameter> queryParams = sanitize(OAuth.decodeForm(query));
      String postStr = req.getPostBodyAsString();
      List<OAuth.Parameter> postParams = sanitize(OAuth.decodeForm(postStr));
      List<OAuth.Parameter> msgParams = new ArrayList<OAuth.Parameter>();
      msgParams.addAll(queryParams);
      msgParams.addAll(postParams);

      addOpenSocialParams(req.getOptions(), msgParams);

      addOAuthParams(msgParams);

      // Build and sign the OAuthMessage; note that the resource here has
      // no query string, the parameters are all in msgParams
      OAuthMessage message
          = new OAuthMessage(req.getMethod(), resource.toString(), msgParams);

      // Sign the message, this may jump into a subclass
      signMessage(message);

      // Rebuild the query string, including all of the parameters we added.
      // We have to be careful not to copy POST parameters into the query.
      // If post and query parameters share a name, they end up being removed
      // from the query.
      HashSet<String> forPost = new HashSet<String>();
      for (OAuth.Parameter param : postParams) {
        forPost.add(param.getKey());
      }
      List<Map.Entry<String, String>> newQuery =
        new ArrayList<Map.Entry<String, String>>();
      for (Map.Entry<String, String> param : message.getParameters()) {
        if (! forPost.contains(param.getKey())) {
          newQuery.add(param);
        }
      }
      // Careful here; the OAuth form encoding scheme is slightly different than
      // the normal form encoding scheme, so we have to use the OAuth library
      // formEncode method.  The java.net.URI code makes it difficult to insert
      // a pre-encoded query string, so we use URL instead.
      String finalQuery = OAuth.formEncode(newQuery);
      URL url = new URL(
          resource.getScheme(),
          resource.getHost(),
          resource.getPort(),
          resource.getRawPath() + "?" + finalQuery);
      return new RemoteContentRequest(url.toURI(), req);
    } catch (GadgetException e) {
      throw e;
    } catch (Exception e) {
      throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e);
    }
  }

  private URI removeQuery(URI resource) throws URISyntaxException {
    return new URI(
        resource.getScheme(),
        null, // user info
        resource.getHost(),
        resource.getPort(),
        resource.getRawPath(),
        null, // query
        null); // fragment
  }


  private void addOpenSocialParams(RemoteContentRequest.Options options,
      List<Parameter> msgParams) {
    String owner = authToken.getOwnerId();
    if (owner != null && options.ownerSigned) {
      msgParams.add(new OAuth.Parameter(OPENSOCIAL_OWNERID, owner));
    }

    String viewer = authToken.getViewerId();
    if (viewer != null && options.viewerSigned) {
      msgParams.add(new OAuth.Parameter(OPENSOCIAL_VIEWERID, viewer));
    }

    String app = authToken.getAppId();
    if (app != null) {
      msgParams.add(new OAuth.Parameter(OPENSOCIAL_APPID, app));
    }

  }

  private void addOAuthParams(List<Parameter> msgParams) {
    addOAuthNonTemporalParams(msgParams);

    String nonce = Long.toHexString(Crypto.rand.nextLong());
    msgParams.add(new OAuth.Parameter(OAuth.OAUTH_NONCE, nonce));

    String timestamp = Long.toString(clock.currentTimeMillis()/1000L);
    msgParams.add(new OAuth.Parameter(OAuth.OAUTH_TIMESTAMP, timestamp));
  }

  private void addOAuthNonTemporalParams(List<Parameter> msgParams) {
    // Add the params which are not used for nonce, timestamp or anything
    // else which varies quickly over time
    msgParams.add(new OAuth.Parameter(OAuth.OAUTH_TOKEN, ""));

    // Add the OAuth params which are not
    String domain = authToken.getDomain();
    if (domain != null) {
      msgParams.add(new OAuth.Parameter(OAuth.OAUTH_CONSUMER_KEY, domain));
    }

    if (keyName != null) {
      msgParams.add(new OAuth.Parameter(XOAUTH_PUBLIC_KEY, keyName));
    }

    msgParams.add(new OAuth.Parameter(OAuth.OAUTH_SIGNATURE_METHOD,
        OAuth.RSA_SHA1));
  }

  /**
   * Sign a message and append the oauth signature parameter to the message
   * object.
   *
   * @param message the message to sign
   *
   * @throws Exception because the OAuth libraries require it.
   */
  protected void signMessage(OAuthMessage message) throws Exception {
    OAuthConsumer consumer = new OAuthConsumer(null, null, null, null);
    consumer.setProperty(RSA_SHA1.PRIVATE_KEY, privateKeyObject);
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    message.sign(accessor);
  }

  /**
   * Strip out any owner or viewer id passed by the client.
 * @throws RequestSigningException 
   */
  private List<Parameter> sanitize(List<Parameter> params)
      throws RequestSigningException {
    ArrayList<Parameter> list = new ArrayList<Parameter>();
    for (Parameter p : params) {
      String name = p.getKey();
      if (allowParam(name)) {
        list.add(p);
      } else {
    	  throw new RequestSigningException("invalid parameter name " + name);
      }
    }
    return list;
  }

  private boolean allowParam(String paramName) {
    String canonParamName = paramName.toLowerCase();
    return (!(canonParamName.startsWith("oauth") ||
        canonParamName.startsWith("xoauth") ||
        canonParamName.startsWith("opensocial")) &&
        ALLOWED_PARAM_NAME.matcher(canonParamName).matches());
  }
}
