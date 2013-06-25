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
import org.exoplatform.platform.notification.MessageInfo;
import org.exoplatform.platform.notification.Provider;
import org.exoplatform.platform.notification.provider.AbstractNotificationProvider;
import org.exoplatform.platform.notification.provider.ProviderManager;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;


public class SocialProviderImpl extends AbstractNotificationProvider {
  public static final String ACTIVITY_ID = "activityId";

  public static final String SPACE_ID    = "activityId";

  public static final String IDENTITY_ID = "activityId";
  
  ProviderManager providerManager;

  ActivityManager activityManager;
  IdentityManager identityManager;
  SpaceService spaceService;
  
  public SocialProviderImpl(ActivityManager activityManager, IdentityManager identityManager,
                         SpaceService spaceService, ProviderManager providerManager, OrganizationService organizationService) {
    this.providerManager = providerManager;
    this.activityManager = activityManager;
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.organizationService = organizationService;
  }

  
  @Override
  protected String processBody(String body) {
    // TODO replace parameter
    return body;
  }
  
  @Override
  public MessageInfo buildMessageInfo(NotificationMessage message, String language) {
    MessageInfo messageInfo = new MessageInfo();
    Provider provider = providerManager.getProvier(message.getProviderType());
    if (language == null || language.length() == 0) {
      language = getLanguage(message);
    }
    String body = getTemplate(provider, language);

    messageInfo.setSubject(getSubject(provider, language))
               .setBody(processBody(body))
               .setFrom(getFrom(message))
               .setTo(getTo(message));

    return messageInfo;
  }
  
  @Override
  public List<String> getSupportType() {
    return Arrays.asList("ActivityMentionProvider", "ActivityCommentProvider",
                          "ActivityPostProvider", "ActivityPostSpaceProvider");
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
