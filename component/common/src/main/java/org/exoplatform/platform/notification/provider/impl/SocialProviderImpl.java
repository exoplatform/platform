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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.api.notification.NotificationMessage;
import org.exoplatform.commons.api.notification.Provider;
import org.exoplatform.commons.api.notification.service.ProviderService;
import org.exoplatform.platform.notification.LinkProviderUtils;
import org.exoplatform.platform.notification.MessageInfo;
import org.exoplatform.platform.notification.provider.AbstractNotificationProvider;
import org.exoplatform.portal.webui.util.Util;
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
    NewUserJoinSocialIntranet, ReceiceConnectionRequest,
    ActivityLikeProvider;
    public static List<String> toValues() {
      List<String> list = new ArrayList<String>();
      for (PROVIDER_TYPE elm : PROVIDER_TYPE.values()) {
        list.add(elm.name());
      }
      return list;
    }
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
  public MessageInfo buildMessageInfo(NotificationMessage message) {
    MessageInfo messageInfo = new MessageInfo();

    //
    messageInfo.setFrom(getFrom(message)).setTo(getTo(message));

    //
    Provider provider = providerService.getProvider(message.getProviderType());
    String language = getLanguage(message);
    String body = getTemplate(provider, language);
    String subject = getSubject(provider, language);

    PROVIDER_TYPE type = PROVIDER_TYPE.valueOf(message.getProviderType());
    switch (type) {
      case ActivityMentionProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getIdentity(activity.getPosterId(), true);
        messageInfo.setSubject(subject.replace("$user-who-mentionned", identity.getProfile().getFullName()))
                   .setBody(body.replace("$user-who-mentionned", identity.getProfile().getFullName())
                                .replace("$post", activity.getTitle())
                                .replace("$replyAction", LinkProviderUtils.getReplyActivityUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser()))
                                .replace("$viewAction", LinkProviderUtils.getViewFullDiscussionUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser())));
        break;
      }
      case ActivityCommentProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        ExoSocialActivity parentActivity = activityManager.getParentActivity(activity);
        Identity identity = identityManager.getIdentity(activity.getPosterId(), true);
        messageInfo.setSubject(subject.replace("$other_user_name", identity.getProfile().getFullName()))
                   .setBody(body.replace("$other_user_name", identity.getProfile().getFullName())
                                .replace("$activity_comment", activity.getTitle())
                                .replace("$original_activity_message", parentActivity.getTitle())
                                .replace("$replyAction", LinkProviderUtils.getReplyActivityUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser()))
                                .replace("$viewAction", LinkProviderUtils.getViewFullDiscussionUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser())));
        break;
      }
      case ActivityLikeProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getIdentity(getFrom(message), true);
        messageInfo.setSubject(subject.replace("$other_user_name", identity.getProfile().getFullName()))
                   .setBody(body.replace("$other_user_name", identity.getProfile().getFullName())
                                .replace("$activity", activity.getTitle())
                                .replace("$replyAction", LinkProviderUtils.getReplyActivityUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser()))
                                .replace("$viewAction", LinkProviderUtils.getViewFullDiscussionUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser())));
        break;
      }
      case ActivityPostProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getIdentity(activity.getPosterId(), true);
        messageInfo.setSubject(subject.replace("$other_user_name", identity.getProfile().getFullName()))
                   .setBody(body.replace("$other_user_name", identity.getProfile().getFullName())
                                .replace("$activity_message", activity.getTitle())
                                .replace("$replyAction", LinkProviderUtils.getReplyActivityUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser()))
                                .replace("$viewAction", LinkProviderUtils.getViewFullDiscussionUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser())));
        break;
      }
      case ActivityPostSpaceProvider: {
        String activityId = message.getOwnerParameter().get(ACTIVITY_ID);
        ExoSocialActivity activity = activityManager.getActivity(activityId);
        Identity identity = identityManager.getIdentity(activity.getPosterId(), true);
        Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, activity.getStreamOwner(), true);
        messageInfo.setSubject(subject.replace("$other_user_name", identity.getProfile().getFullName()).replace("$space-name", spaceIdentity.getProfile().getFullName()))
                   .setBody(body.replace("$other_user_name", identity.getProfile().getFullName())
                                .replace("$activity_message", activity.getTitle())
                                .replace("$space-name", spaceIdentity.getProfile().getFullName())
                                .replace("$replyAction", LinkProviderUtils.getReplyActivityUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser()))
                                .replace("$viewAction", LinkProviderUtils.getViewFullDiscussionUrl(activity.getId(), Util.getPortalRequestContext().getRemoteUser())));
        break;
      }
      case InvitedJoinSpace: {
        String spaceId = message.getOwnerParameter().get(SPACE_ID);
        Space space = spaceService.getSpaceById(spaceId);
        messageInfo.setSubject(subject.replace("$space-name", space.getPrettyName()))
                   .setBody(body.replace("$space-name", space.getPrettyName())
                                .replace("$space-avatar-url", space.getAvatarUrl())
                                .replace("$acceptAction", LinkProviderUtils.getAcceptInvitationToJoinSpaceUrl(space.getId(), getTo(message)))
                                .replace("$ignoreAction", LinkProviderUtils.getIgnoreInvitationToJoinSpaceUrl(space.getId(), getTo(message))));
        break;
      }
      case RequestJoinSpace: {
        String spaceId = message.getOwnerParameter().get(SPACE_ID);
        Space space = spaceService.getSpaceById(spaceId);
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, getFrom(message), true);
        Profile userProfile = identity.getProfile();
        messageInfo.setSubject(subject.replace("$space-name", space.getPrettyName()).replace("$user-name", userProfile.getFullName()))
                   .setBody(body.replace("$space-name", space.getPrettyName())
                                .replace("$user-name", userProfile.getFullName())
                                .replace("$user-avatar-url", userProfile.getAvatarUrl())
                                .replace("$validateAction", LinkProviderUtils.getValidateRequestToJoinSpaceUrl(space.getId(), identity.getRemoteId())));
        break;
      }
      case NewUserJoinSocialIntranet: {
        
        break;
      }
      case ReceiceConnectionRequest: {
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, getFrom(message), true);
        Profile userProfile = identity.getProfile();
        messageInfo.setSubject(subject.replace("$user-name", userProfile.getFullName()))
                   .setBody(body.replace("$user-name", userProfile.getFullName())
                                .replace("$user-avatar-url", userProfile.getAvatarUrl())
                                .replace("$confirmAction", LinkProviderUtils.getConfirmInvitationToConnectUrl(getFrom(message), getTo(message)))
                                .replace("$ignoreAction", LinkProviderUtils.getIgnoreInvitationToConnectUrl(getFrom(message), getTo(message))));
        break;
      }
    }

    return messageInfo;
  }
  
  @Override
  public List<String> getSupportType() {
    return PROVIDER_TYPE.toValues();
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
