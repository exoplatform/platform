package org.exoplatform.platform.security;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;
import org.exoplatform.services.security.PasswordEncrypter;

public class PasswordEncrypterUserListener extends UserEventListener {

  private PasswordEncrypter passwordEncrypter;
  private OrganizationService organizationService;

  public PasswordEncrypterUserListener(PasswordEncrypter passwordEncrypter, OrganizationService organizationService) {
    this.passwordEncrypter = passwordEncrypter;
    this.organizationService = organizationService;
  }

  @Override
  public void preSave(User user, boolean isNew) throws Exception {
    if (passwordEncrypter != null && user.getPassword() != null) {
      User persistedUser = organizationService.getUserHandler().findUserByName(user.getUserName());
      if (persistedUser == null || persistedUser.getPassword() == null || !user.getPassword().equals(persistedUser.getPassword())) {
        String encodedPassword = new String(passwordEncrypter.encrypt(user.getPassword().getBytes()));
        user.setPassword(encodedPassword);
      }
    }
  }
}
