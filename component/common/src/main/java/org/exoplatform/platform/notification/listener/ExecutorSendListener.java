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
package org.exoplatform.platform.notification.listener;

import java.util.List;
import java.util.concurrent.Callable;

import org.exoplatform.commons.api.notification.NotificationMessage;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.notification.MessageInfo;
import org.exoplatform.platform.notification.provider.NotificationProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.mail.Message;

public class ExecutorSendListener implements Callable<NotificationMessage>{
  private static final Log LOG = ExoLogger.getExoLogger(ExecutorSendListener.class);
  
  private static ExecutorSendListener  instance;
  private NotificationMessage  message;
  
  @Override
  public NotificationMessage call() throws Exception {
    // process send email notification
    processSendEmailNotifcation();
    return message;
  }
  
  public static ExecutorSendListener getInstance(NotificationMessage message) {
    if (instance == null) {
      synchronized (ExecutorSendListener.class) {
        if (instance == null) {
          instance = new ExecutorSendListener();
        }
      }
    }
    instance.message = message;

    return instance;
  }

  private NotificationProviderService getNotificationProviderService(String providerType) {
    List<NotificationProviderService> profiderSerives = PortalContainer.getInstance()
                                                      .getComponentInstancesOfType(NotificationProviderService.class);
    for (NotificationProviderService providerService : profiderSerives) {
      if (providerService.getSupportType().contains(message.getProviderType())) {
        return providerService;
      }
    }
    return null;
  }

  private void processSendEmailNotifcation() {
    NotificationProviderService providerService = getNotificationProviderService(message.getProviderType());
   
    MessageInfo messageInfo =  providerService.buildMessageInfo(message, null);
    if(messageInfo != null) {
      Message message_ = messageInfo.makeEmailNotification();
      
      MailService mailService = (MailService) PortalContainer.getInstance().getComponentInstanceOfType(MailService.class);
      
      try {
        mailService.sendMessage(message_);
      } catch (Exception e) {
        LOG.error("Send email error!", e);
      }
    }
    
    LOG.info("Process send email notification successfully ... " + message.getProviderType());
    
  }
}
