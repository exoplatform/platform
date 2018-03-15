/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.oauth.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.oauth.filter.OAuthAbstractFilter;
import org.exoplatform.oauth.service.OAuthRegistrationServices;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.services.organization.UserStatus;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.image.ImageUtils;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.model.AvatarAttachment;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.security.oauth.spi.AccessTokenContext;
import org.gatein.security.oauth.spi.OAuthPrincipal;
import org.gatein.security.oauth.spi.OAuthProviderType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OAuthRegistrationServicesImpl implements OAuthRegistrationServices {
  private static Logger log = LoggerFactory.getLogger(OAuthRegistrationServicesImpl.class);

    private final List<String> registerOnFly;

    private final OrganizationService orgService;
    private final IdentityManager identityManager;

    public OAuthRegistrationServicesImpl(InitParams initParams, OrganizationService orgService, IdentityManager identityManager) {
        ValueParam onFly = initParams.getValueParam("registerOnFly");
        String onFlyValue = onFly == null ? "" : onFly.getValue();
        if(onFlyValue != null && !onFlyValue.isEmpty()) {
            registerOnFly = Arrays.asList(onFlyValue.split(","));
        } else {
            registerOnFly = Collections.EMPTY_LIST;
        }

        this.orgService = orgService;
        this.identityManager = identityManager;
    }

    @Override
    public boolean isRegistrationOnFly(OAuthProviderType<? extends AccessTokenContext> oauthProviderType) {
        return registerOnFly.contains(oauthProviderType.getKey());
    }

    @Override
    public User detectGateInUser(HttpServletRequest request, OAuthPrincipal<? extends AccessTokenContext> principal) {
      OAuthProviderType providerType = principal.getOauthProviderType();
      User gtnUser = providerType.getOauthPrincipalProcessor().convertToGateInUser(principal);

      String email = gtnUser.getEmail();
      String username = gtnUser.getUserName();

      User foundUser = null;

      try {
        UserHandler userHandler = orgService.getUserHandler();
        Query query = null;
        ListAccess<User> users = null;

        //Find user by username
        if(username != null) {
          query = new Query();
          query.setUserName(username);
          users = userHandler.findUsersByQuery(query, UserStatus.ANY);
          if(users != null && users.getSize() > 0) {
            foundUser = users.load(0, 1)[0];
          }
        }

        //Find by email
        if(foundUser == null && email != null && !email.isEmpty()) {
          query = new Query();
          query.setEmail(email);
          users = userHandler.findUsersByQuery(query, UserStatus.ANY);
          if(users != null && users.getSize() > 0) {
            foundUser = users.load(0, 1)[0];
          }
        }

        // find recent user logged in
        Cookie[] cookies = request.getCookies();
        if (foundUser == null && cookies != null && cookies.length > 0) {
          for (Cookie cookie : cookies) {
            if (OAuthAbstractFilter.COOKIE_LAST_LOGIN.equals(cookie.getName())) {
              username = cookie.getValue();
              if(username != null && username.length() > 0) {
                query = new Query();
                query.setUserName(username);
                users = userHandler.findUsersByQuery(query, UserStatus.ANY);
                if(users != null && users.getSize() > 0) {
                  foundUser = users.load(0, 1)[0];
                }
              }
              break;
            }
          }
        }

      } catch (Exception ex) {
        log.error("Exception when trying to detect user: ", ex);
      }


      return foundUser;
    }

    @Override
    public User createGateInUser(OAuthPrincipal<? extends AccessTokenContext> principal) {
      OAuthProviderType providerType = principal.getOauthProviderType();
      User user = providerType.getOauthPrincipalProcessor().convertToGateInUser(principal);
      user.setPassword(randomPassword(16));

      if (orgService instanceof ComponentRequestLifecycle) {
        RequestLifeCycle.begin((ComponentRequestLifecycle)orgService);
      }
      try {
        orgService.getUserHandler().createUser(user, true);

        //User profile
        UserProfileHandler profileHandler = orgService.getUserProfileHandler();

        UserProfile newUserProfile = profileHandler.findUserProfileByName(user.getUserName());
        if (newUserProfile == null) {
          newUserProfile = orgService.getUserProfileHandler().createUserProfileInstance(user.getUserName());
        }

        newUserProfile.setAttribute(providerType.getUserNameAttrName(), principal.getUserName());
        profileHandler.saveUserProfile(newUserProfile, true);

        //
        String avatar = principal.getAvatar();
        if (avatar != null && !avatar.trim().isEmpty()) {
          int WIDTH = 200;
          int HEIGHT = 200;
          String fileName = FilenameUtils.getBaseName(avatar);
          MimeTypeResolver mimeTypeResolver = new MimeTypeResolver();
          String mimeType = mimeTypeResolver.getMimeType(avatar);
          if (!mimeType.toLowerCase().contains("image")) {
            mimeType = "image/png";
          }

          try {
            URL url = new URL(avatar);
            InputStream is = url.openStream();
            if (is != null) {
              AvatarAttachment avatarAttachment = ImageUtils.createResizedAvatarAttachment(is, WIDTH, HEIGHT, null, fileName, mimeType, null);
              if (avatarAttachment == null) {
                avatarAttachment = new AvatarAttachment(null, fileName, mimeType, is, null, System.currentTimeMillis());
              }
              is.close();

              Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user.getUserName(), true);
              Profile p = identity.getProfile();

              p.setProperty(Profile.AVATAR, avatarAttachment);
              Map<String, Object> props = p.getProperties();
              for (String key : props.keySet()) {
                if (key.startsWith(Profile.AVATAR + ImageUtils.KEY_SEPARATOR)) {
                  p.removeProperty(key);
                }
              }

              identityManager.updateProfile(p);
            }
          } catch (MalformedURLException ex) {
            log.warn("Can not fetch Avatar of oauth user because the URL is invalid: " + avatar, ex);
          } catch (IOException ex) {
            log.warn("Can not fetch Avatar at URL: " + avatar, ex);
          } catch (Exception ex) {
            // This exception may be thrown by the AvatarAttachment constructor
            log.warn("Can not fetch Avatar at URL: " + avatar, ex);
          }
        }

      } catch (Exception ex) {
        if (log.isErrorEnabled()) {
          log.error("Exception when trying to create user: " + user.getUserName() + " on-fly", ex);
        }
        user = null;
      } finally {
        if (orgService instanceof ComponentRequestLifecycle) {
          RequestLifeCycle.end();
        }
      }

      return user;
    }

    private String randomPassword(int length) {
      final String CHAR_ENABLED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

      char[] chars = new char[length];
      Random random = new Random();
      int rand;
      final int len = CHAR_ENABLED.length();
      for (int i = 0; i < length; i++) {
        rand = random.nextInt(len) % len;
        chars[i] = CHAR_ENABLED.charAt(rand);
      }
      return new String(chars);
    }
}
