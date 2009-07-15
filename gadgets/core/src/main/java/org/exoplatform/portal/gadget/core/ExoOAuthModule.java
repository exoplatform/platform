package org.exoplatform.portal.gadget.core;

import org.apache.shindig.auth.AnonymousAuthenticationHandler;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.oauth.OAuthFetcherConfig;
import org.apache.shindig.gadgets.oauth.OAuthModule;
import org.apache.shindig.gadgets.oauth.OAuthRequest;
import org.apache.shindig.gadgets.oauth.OAuthStore;

import com.google.inject.Inject;
import com.google.inject.name.Names;

/**
 * Created by IntelliJ IDEA.
 * User: jeremi
 * Date: Jan 9, 2009
 * Time: 10:45:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExoOAuthModule extends OAuthModule {
  private static final String SIGNING_KEY_FILE = "gadgets.signingKeyFile";
  private static final String SIGNING_KEY_NAME = "gadgets.signingKeyName";
  private static final String CALLBACK_URL = "gadgets.signing.global-callback-url";


  @Override
  protected void configure() {
    // Used for encrypting client-side OAuth state.
    bind(BlobCrypter.class).annotatedWith(Names.named(OAuthFetcherConfig.OAUTH_STATE_CRYPTER))
        .toProvider(OAuthCrypterProvider.class);

    // Used for persistent storage of OAuth access tokens.
    bind(OAuthStore.class).toProvider(ExoOAuthStoreProvider.class);
    bind(OAuthRequest.class).toProvider(OAuthRequestProvider.class);
    
    // TODO: tung.dang add some missing implement
    bind(Boolean.class)
    .annotatedWith(Names.named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
    .toInstance(Boolean.TRUE);
  }

  public static class ExoOAuthStoreProvider extends OAuthStoreProvider {
    @Inject
    public ExoOAuthStoreProvider(ContainerConfig config) {
      //super(config.getString(ContainerConfig.DEFAULT_CONTAINER, SIGNING_KEY_FILE), config.getString(ContainerConfig.DEFAULT_CONTAINER, SIGNING_KEY_NAME));
      super(config.getString(ContainerConfig.DEFAULT_CONTAINER, SIGNING_KEY_FILE), 
            config.getString(ContainerConfig.DEFAULT_CONTAINER, SIGNING_KEY_NAME),
            config.getString(ContainerConfig.DEFAULT_CONTAINER, CALLBACK_URL));
    }
  }
  
  public static class ExoOAuthRequestProvider extends OAuthRequestProvider {
    private final HttpFetcher fetcher;
    private final OAuthFetcherConfig config;

    @Inject
    public ExoOAuthRequestProvider(HttpFetcher fetcher, OAuthFetcherConfig config) {
      super(fetcher, config);
      this.fetcher = fetcher;
      this.config = config;
    }

    public OAuthRequest get() {
      return new OAuthRequest(config, fetcher);
    }
  }
}
