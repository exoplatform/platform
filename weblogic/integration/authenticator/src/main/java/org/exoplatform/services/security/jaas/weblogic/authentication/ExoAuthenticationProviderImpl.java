package org.exoplatform.services.security.jaas.weblogic.authentication;

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import weblogic.management.security.ProviderMBean;
import weblogic.security.spi.AuthenticationProviderV2;
import weblogic.security.spi.IdentityAsserterV2;
import weblogic.security.spi.PrincipalValidator;
import weblogic.security.spi.SecurityServices;

/**
 * eXo Weblogic Authenticator
 * @author Thomas
 *
 */
public class ExoAuthenticationProviderImpl implements AuthenticationProviderV2 {

    private String description;
    private LoginModuleControlFlag controlFlag;
    private String loginModuleClass;
    
    public ExoAuthenticationProviderImpl() {
    	description = "eXoPlatform authentication provider";
    }
    
	public String getDescription() {
		return description;
	}

	public void initialize(ProviderMBean providerMBean, SecurityServices securityServices) {

		ExoAuthenticatorMBean exoAuthenticatorMBean = (ExoAuthenticatorMBean) providerMBean;

        loginModuleClass = exoAuthenticatorMBean.getLoginModuleClass();

		if (!exoAuthenticatorMBean.getDescription().equals("")) {
			description = (new StringBuilder()).append(
					exoAuthenticatorMBean.getDescription()).append("\n")
					.append(exoAuthenticatorMBean.getVersion()).toString();
		}
		
		String strControlFlag = exoAuthenticatorMBean.getControlFlag();
		if (strControlFlag.equalsIgnoreCase("REQUIRED")) {
			controlFlag = LoginModuleControlFlag.REQUIRED;
		} else if (strControlFlag.equalsIgnoreCase("OPTIONAL")) {
			controlFlag = LoginModuleControlFlag.OPTIONAL;
		} else if (strControlFlag.equalsIgnoreCase("REQUISITE")) {
			controlFlag = LoginModuleControlFlag.REQUISITE;
		} else if (strControlFlag.equalsIgnoreCase("SUFFICIENT")) {
			controlFlag = LoginModuleControlFlag.SUFFICIENT;
		} else {
			throw new IllegalArgumentException((new StringBuilder()).append(
					"invalid flag value").append(strControlFlag).toString());
		}
	}

    
	public AppConfigurationEntry getAssertionModuleConfiguration() {
		return null;
	}

	public IdentityAsserterV2 getIdentityAsserter() {
		return null;
	}

	public AppConfigurationEntry getLoginModuleConfiguration() {
        return new AppConfigurationEntry(this.loginModuleClass, controlFlag, new HashMap());
	}

	public PrincipalValidator getPrincipalValidator() {
		return null;
	}
	
	public void shutdown() {
	}

}
