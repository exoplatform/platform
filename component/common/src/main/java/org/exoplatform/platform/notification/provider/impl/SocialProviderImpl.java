/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.notification.provider.impl;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.api.notification.NotificationMessage;
import org.exoplatform.commons.api.notification.Provider;
import org.exoplatform.commons.api.notification.service.ProviderService;
import org.exoplatform.platform.notification.MessageInfo;
import org.exoplatform.platform.notification.provider.AbstractNotificationProvider;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;


public class SocialProviderImpl extends AbstractNotificationProvider {
  public static final String ACTIVITY_ID = "activityId";

  public static final String SPACE_ID    = "spaceId";

  public static final String IDENTITY_ID = "identityId";
  
  ProviderService providerService;

  ActivityManager activityManager;
  IdentityManager identityManager;
  SpaceService spaceService;
  
  public enum PROVIDER_TYPE {
    ActivityMentionProvider, ActivityCommentProvider,
    ActivityPostProvider, ActivityPostSpaceProvider,
    InvitedJoinSpace, RequestJoinSpace,
    NewUserJoinSocialIntranet, ReceiceConnectionRequest;
  }
  
  public SocialProviderImpl(ActivityManager activityManager, IdentityManager identityManager,
                         SpaceService spaceService, ProviderService providerService, OrganizationService organizationService) {
    this.providerService = providerService;
    this.activityManager = activityManager;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.organizationService = organizationService;
  }
  
  @Override
  public MessageInfo processMessage(String body, String subject, NotificationMessage message) {
    MessageInfo messageInfo = new MessageInfo();
    
    PROVIDER_TYPE type = PROVIDER_TYPE.valueOf(message.getProviderType());
    switch (type) {
      case ActivityMentionProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, activity.getPosterId(), true);
        messageInfo.setBody(body.replace("@user-who-mentionned", identity.getRemoteId()).replace("@post", activity.getTitle()))
                   .setSubject(subject.replace("@user-who-mentionned", identity.getRemoteId()));
        break;
      }
      case ActivityCommentProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        ExoSocialActivity parentActivity = activityManager.getParentActivity(activity);
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, activity.getPosterId(), true);
        messageInfo.setBody(body.replace("$other_user_name", identity.getRemoteId()).replace("@activity_comment", activity.getTitle()).replace("@original_activity_message", parentActivity.getTitle()))
                   .setSubject(subject.replace("$other_user_name", identity.getRemoteId()));
        break;
      }
      case ActivityPostProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, activity.getPosterId(), true);
        messageInfo.setBody(body.replace("$other_user_name", identity.getRemoteId()).replace("@activity_message", activity.getTitle()))
                   .setSubject(subject.replace("$other_user_name", identity.getRemoteId()));
        break;
      }
      case ActivityPostSpaceProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, activity.getPosterId(), true);
        Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, activity.getStreamOwner(), true);
        messageInfo.setBody(body.replace("$other_user_name", identity.getRemoteId()).replace("@activity_message", activity.getTitle()).replace("$space-name", spaceIdentity.getRemoteId()))
                   .setSubject(subject.replace("$other_user_name", identity.getRemoteId()).replace("$space-name", spaceIdentity.getRemoteId()));
        break;
      }
      case InvitedJoinSpace: {
        String spaceId = message.getOwnerParameter().get(SPACE_ID);
        Space space = spaceService.getSpaceById(spaceId);
        messageInfo.setSubject(subject.replace("$space-name", space.getPrettyName()))
                   .setBody(body.replace("$space-name", space.getPrettyName()).replace("$space-avatar-url", space.getAvatarUrl()));
        break;
      }
      case RequestJoinSpace: {
        String spaceId = message.getOwnerParameter().get(SPACE_ID);
        Space space = spaceService.getSpaceById(spaceId);
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, getFrom(message), true);
        Profile userProfile = identity.getProfile();
        messageInfo.setSubject(subject.replace("$space-name", space.getPrettyName()).replace("$user-name", userProfile.getFullName()))
                   .setBody(body.replace("$space-name", space.getPrettyName()).replace("$user-name", userProfile.getFullName()).replace("$user-avatar-url", userProfile.getAvatarUrl()));
        break;
      }
      case NewUserJoinSocialIntranet: {
        
        break;
      }
      case ReceiceConnectionRequest: {
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, getFrom(message), true);
        Profile userProfile = identity.getProfile();
        messageInfo.setSubject(subject.replace("$user-name", userProfile.getFullName()))
                   .setBody(body.replace("$user-name", userProfile.getFullName()).replace("$user-avatar-url", userProfile.getAvatarUrl()));
        break;
      }
    }
    
    return messageInfo;
  }
  
  @Override
  public MessageInfo buildMessageInfo(NotificationMessage message, String language) {
    Provider provider = providerService.getProvider(message.getProviderType());
    if (language == null || language.length() == 0) {
      language = getLanguage(message);
    }
    String body = getTemplate(provider, language);
    String subject = getSubject(provider, language);
    
    MessageInfo messageInfo = processMessage(body, subject, message);
    messageInfo.setFrom(getFrom(message))
               .setTo(getTo(message));

    return messageInfo;
  }
  
  @Override
  public List<String> getSupportType() {
    return Arrays.asList("ActivityMentionProvider", "ActivityCommentProvider",
                          "ActivityPostProvider", "ActivityPostSpaceProvider",
                          "InvitedJoinSpace", "RequestJoinSpace",
                          "NewUserJoinSocialIntranet", "ReceiceConnectionRequest");
  }

  public String getActivityId(NotificationMessage message) {
    return message.getOwnerParameter().get(ACTIVITY_ID);
  }

  public String getSpaceId(NotificationMessage message) {
    return message.getOwnerParameter().get(SPACE_ID);
  }

  public String getIdentityId(NotificationMessage message) {
    return message.getOwnerParameter().get(IDENTITY_ID);
  }

}
