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

import org.apache.shindig.social.Mandatory;
import org.apache.shindig.social.AbstractGadgetData;

import java.util.Map;
import java.util.List;
import java.util.Date;

public class Activity extends AbstractGadgetData {

  public static enum Field {
    APP_ID("appId"),
    BODY("body"),
    BODY_ID("bodyId"),
    EXTERNAL_ID("externalId"),
    ID("id"),
    LAST_UPDATED("updated"),
    MEDIA_ITEMS("mediaItems"),
    POSTED_TIME("postedTime"),
    PRIORITY("priority"),
    STREAM_FAVICON_URL("streamFaviconUrl"),
    STREAM_SOURCE_URL("streamSourceUrl"),
    STREAM_TITLE("streamTitle"),
    STREAM_URL("streamUrl"),
    TEMPLATE_PARAMS("templateParams"),
    TITLE("title"),
    TITLE_ID("titleId"),
    URL("url"),
    USER_ID("userId");

    private final String jsonString;

    private Field(String jsonString) {
      this.jsonString = jsonString;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }
  }

  private String appId;
  private String body;
  private String bodyId;
  private String externalId;
  private String id;
  private Date updated;
  private List<MediaItem> mediaItems;
  private Long postedTime;
  private Float priority;
  private String streamFaviconUrl;
  private String streamSourceUrl;
  private String streamTitle;
  private String streamUrl;
  private Map<String, String> templateParams;
  private String title;
  private String titleId;
  private String url;
  private String userId;

  public Activity(String id, String userId) {
    this.id = id;
    this.userId = userId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBodyId() {
    return bodyId;
  }

  public void setBodyId(String bodyId) {
    this.bodyId = bodyId;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  @Mandatory
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public List<MediaItem> getMediaItems() {
    return mediaItems;
  }

  public void setMediaItems(List<MediaItem> mediaItems) {
    this.mediaItems = mediaItems;
  }

  public Long getPostedTime() {
    return postedTime;
  }

  public void setPostedTime(Long postedTime) {
    this.postedTime = postedTime;
  }

  public Float getPriority() {
    return priority;
  }

  public void setPriority(Float priority) {
    this.priority = priority;
  }

  public String getStreamFaviconUrl() {
    return streamFaviconUrl;
  }

  public void setStreamFaviconUrl(String streamFaviconUrl) {
    this.streamFaviconUrl = streamFaviconUrl;
  }

  public String getStreamSourceUrl() {
    return streamSourceUrl;
  }

  public void setStreamSourceUrl(String streamSourceUrl) {
    this.streamSourceUrl = streamSourceUrl;
  }

  public String getStreamTitle() {
    return streamTitle;
  }

  public void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
  }

  public String getStreamUrl() {
    return streamUrl;
  }

  public void setStreamUrl(String streamUrl) {
    this.streamUrl = streamUrl;
  }

  public Map<String, String> getTemplateParams() {
    return templateParams;
  }

  public void setTemplateParams(Map<String, String> templateParams) {
    this.templateParams = templateParams;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitleId() {
    return titleId;
  }

  public void setTitleId(String titleId) {
    this.titleId = titleId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Mandatory
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
