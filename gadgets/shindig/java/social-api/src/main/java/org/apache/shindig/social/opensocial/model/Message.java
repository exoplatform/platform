/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.opensocial.model;

import org.apache.shindig.social.AbstractGadgetData;

/**
 *
 * Base interface for all message objects.
 *
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Message.html
 *
 */
public final class Message extends AbstractGadgetData {

  public static enum Field {
    BODY("body"),
    TITLE("title"),
    TYPE("type");

    private final String jsonString;

    private Field(String jsonString) {
      this.jsonString = jsonString;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }
  }

  private String body;
  private String title;
  private Type type;

  public enum Type {
    /* An email */
    EMAIL("EMAIL"),
    /* A short private message */
    NOTIFICATION("NOTIFICATION"),
    /* A message to a specific user that can be seen only by that user */
    PRIVATE_MESSAGE("PRIVATE_MESSAGE"),
    /* A message to a specific user that can be seen by more than that user */
    PUBLIC_MESSAGE("PUBLIC_MESSAGE");

    private final String jsonString;

    private Type(String jsonString) {
      this.jsonString = jsonString;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }
  }

  public Message(String initBody, String initTitle, Type initType) {
    this.body = initBody;
    this.title = initTitle;
    this.type = initType;
  }

  /**
   * Gets the main text of the message.
   * @return the main text of the message
   */
  public String getBody() {
    return this.body;
  }

  /**
   * Sets the main text of the message.
   * HTML attributes are allowed and are sanitized by the container
   * @param newBody the main text of the message
   */
  public void setBody(String newBody) {
    this.body = newBody;
  }

  /**
   * Gets the title of the message
   * @return the title of the message
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title of the message
   * HTML attributes are allowed and are sanitized by the container.
   * @param newTitle the title of the message
   */
  public void setTitle(String newTitle) {
    this.title = newTitle;
  }

  /**
   * Gets the type of the message, as specified by opensocial.Message.Type
   * @return the type of message (enum Message.Type)
   */
  public Type getType() {
    return type;
  }

  /**
   * Sets the type of the message, as specified by opensocial.Message.Type
   * @param newType the type of message (enum Message.Type)
   */
  public void setType(Type newType) {
    this.type = newType;
  }

  /**
   * TODO implement either a standard 'sanitizing' facility or
   * define an interface that can be set on this class so
   * others can plug in their own.
   * @param htmlStr String to be sanitized.
   * @return the sanitized HTML String
   */
  public String sanitizeHTML(String htmlStr) {
    return htmlStr;
  }
}
