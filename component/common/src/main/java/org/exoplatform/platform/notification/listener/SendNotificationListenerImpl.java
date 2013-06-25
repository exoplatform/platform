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

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.exoplatform.commons.api.notification.NotificationMessage;
import org.exoplatform.commons.api.notification.service.NotificationServiceListener;

public class SendNotificationListenerImpl implements NotificationServiceListener<NotificationMessage> {

  private ExecutorService executor;
  
  public SendNotificationListenerImpl() {
    executor = Executors.newFixedThreadPool(1);
  }


  @Override
  public void processListener(NotificationMessage message) {
    CompletionService<NotificationMessage> cs = new ExecutorCompletionService<NotificationMessage>(executor);
    cs.submit(ExecutorSendListener.getInstance(message));
  }

}
