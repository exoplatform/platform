/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.api.mail;

import java.util.List;

/**
 * API for eXo Mail. Allows applications to view and edit eXo Mail content.
 */
public interface MailService {

  
  /**
   * Get the number of unread messages
   * @param owner of the MailBox
   * @return number of unread messages in INBOX of the default account
   */
  int getUnreadCount(String owner);
  
  /**
   * Get last messages for a given owner
   * @param owner owner of the mailbox
   * @param maximupm number of messages to return
   * @return List of conversations in descending chronological order
   */
  List<Conversation> getLastConversations(String owner, int limit);

  /**
   * Mark conversations as read
   * @param owner owner of the mailbox
   * @param conversationIds IDs of conversations
   */
  void markRead(String owner, String... conversationIds);
  
  /**
   * Mark conversations as unread
   * @param owner owner of the mailbox
   * @param conversationIds IDs of conversations
   */
  void markUnread(String owner, String... conversationIds);
  
  /**
   * Delete conversations
   * @param owner owner of the mailbox
   * @param conversationIds IDs of conversations
   */
  void delete(String owner, String... conversationIds);
  
  
}
