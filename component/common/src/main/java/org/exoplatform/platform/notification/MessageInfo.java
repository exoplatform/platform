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
package org.exoplatform.platform.notification;

import org.exoplatform.services.mail.Message;

public class MessageInfo {
  private String from;

  private String to;

  private String body;

  private String subject = "";

  private String foodter;

  public MessageInfo() {
  }

  /**
   * @return the from
   */
  public String getFrom() {
    return from;
  }

  /**
   * @param from the from to set
   */
  public MessageInfo setFrom(String from) {
    this.from = from;
    return this;
  }

  /**
   * @return the to
   */
  public String getTo() {
    return to;
  }

  /**
   * @param to the to to set
   */
  public MessageInfo setTo(String to) {
    this.to = to;
    return this;
  }

  /**
   * @return the body
   */
  public String getBody() {
    return body;
  }

  /**
   * @param body the body to set
   */
  public MessageInfo setBody(String body) {
    this.body = body;
    return this;
  }

  /**
   * @return the header
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param header the header to set
   */
  public MessageInfo setSubject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * @return the foodter
   */
  public String getFoodter() {
    return foodter;
  }

  /**
   * @param foodter the foodter to set
   */
  public MessageInfo setFoodter(String foodter) {
    this.foodter = foodter;
    return this;
  }
  
  
  public Message makeEmailNotification() {
    Message message = new Message();
    message.setMimeType("text/html");
    message.setFrom(from);
    message.setTo(to);
    message.setSubject(subject);
    message.setBody(body);
    return message;
  }

  public String makeNotification() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("{ ")
           .append("subject: '").append(subject).append("', ")
           .append("from: '").append(from).append("', ")
           .append("to: '").append(to).append("', ")
           .append("body: '").append(body).append("' ")
           .append("}");
    return builder.toString();
  }

}
