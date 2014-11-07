package org.exoplatform.platform.security;

import org.apache.poi.util.CommonsLogger;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;
import org.exoplatform.services.security.PasswordEncrypter;


public class PasswordEncrypterUserListener extends UserEventListener {

  private static final Log LOG = ExoLogger.getLogger(PasswordEncrypterUserListener.class);

  private PasswordEncrypter     passwordEncrypter;

  private OrganizationService   organizationService;

  public PasswordEncrypterUserListener(PasswordEncrypter passwordEncrypter,
                                       OrganizationService organizationService) {
    this.passwordEncrypter = passwordEncrypter;
    this.organizationService = organizationService;
  }

  @Override
  public void preSave(User user, boolean isNew) throws Exception {
    if (passwordEncrypter != null && user.getPassword() != null) {
      CommonsUtils.startRequest(organizationService);
      try {
        User persistedUser = organizationService.getUserHandler().findUserByName(user.getUserName());
        if (persistedUser == null || persistedUser.getPassword() == null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Encrypting password for a new user " + user.getUserName());
          }
          String encodedPassword = new String(passwordEncrypter.encrypt(user.getPassword().getBytes()));
          user.setPassword(encodedPassword);
        } else if (!user.getPassword().equals(persistedUser.getPassword())) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Encrypting changed password for user " + user.getUserName());
          }
          String encodedPassword = new String(passwordEncrypter.encrypt(user.getPassword().getBytes()));
          user.setPassword(encodedPassword);
        } else {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Nothing to encrypt for user " + user.getUserName() + ": password no changed.");
          }
        }
      } finally {
        CommonsUtils.endRequest(organizationService);
      }
    }
  }
}
