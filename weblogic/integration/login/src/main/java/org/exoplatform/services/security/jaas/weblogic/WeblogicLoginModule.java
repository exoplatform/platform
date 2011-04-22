package org.exoplatform.services.security.jaas.weblogic;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.jaas.DefaultLoginModule;

import weblogic.security.principal.WLSGroupImpl;
import weblogic.security.principal.WLSUserImpl;

public class WeblogicLoginModule extends DefaultLoginModule {

	/**
	 * The name of the option to use in order to specify the name of the portal
	 * container
	 */
	private static final String OPTION_PORTAL_CONTAINER_NAME = "portalContainerName";

	/**
	 * The default name of the portal container
	 */
	private static final String DEFAULT_PORTAL_CONTAINER_NAME = "portal";

	/**
	 * Logger.
	 */
	protected Log log = ExoLogger.getLogger("WeblogicLoginModule");

	public WeblogicLoginModule() {
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void afterInitialize() {
        super.afterInitialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean commit() throws LoginException {
		if (log.isDebugEnabled()) {
			log.debug("In commit of WeblogicLoginModule.");
		}

		if (super.commit()) {

			Set<Principal> principals = subject.getPrincipals();

			// username principal
			// must be the first principal...
			// Used by the SetCurrentIdentityFilter to retrieve the user (via
			// httpRequest.getRemoteUser())
			// principals.add(new UserPrincipal(identity.getUserId()));
			principals.add(new WLSUserImpl(identity.getUserId()));

			for (String role : identity.getRoles()) {
				principals.add(new WLSGroupImpl(role));
			}

			return true;
		} else {
			return false;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean abort() throws LoginException {
		if (log.isDebugEnabled()) {
			log.debug("In abort of WeblogicLoginModule.");
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean logout() throws LoginException {
		if (log.isDebugEnabled()) {
			log.debug("In logout of WeblogicLoginModule.");
		}

		return super.logout();
	}
}
