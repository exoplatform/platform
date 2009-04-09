package org.exoplatform.portal.gadget.core;

import org.apache.shindig.gadgets.oauth.OAuthModule;
import org.apache.shindig.gadgets.oauth.OAuthFetcherConfig;
import org.apache.shindig.gadgets.oauth.OAuthStore;
import org.apache.shindig.common.ContainerConfig;
import org.apache.shindig.common.crypto.BlobCrypter;
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


  @Override
  protected void configure() {
    // Used for encrypting client-side OAuth state.
    bind(BlobCrypter.class).annotatedWith(Names.named(OAuthFetcherConfig.OAUTH_STATE_CRYPTER))
        .toProvider(OAuthCrypterProvider.class);

    // Used for persistent storage of OAuth access tokens.
    bind(OAuthStore.class).toProvider(ExoOAuthStoreProvider.class);
  }

  public static class ExoOAuthStoreProvider extends OAuthStoreProvider {
    @Inject
    public ExoOAuthStoreProvider(ContainerConfig config) {
      super(config.get(ContainerConfig.DEFAULT_CONTAINER, SIGNING_KEY_FILE), config.get(ContainerConfig.DEFAULT_CONTAINER, SIGNING_KEY_NAME));
    }
  }
}
