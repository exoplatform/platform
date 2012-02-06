/*
 * Copyright (C) 2003-2007 eXo Platform.
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
package org.exoplatform.platform.gadget.services.newspaces;

import java.util.Date;

/**
 * 
 * @author <a href="tungdt@exoplatform.com">Do Thanh Tung </a>
 * @version $Revision$
 */
public class IntranetSpace {
  
  /** The display name. */
  private String displayName;
  
  /** The description. */
  private String description;
  
  /** The avartarURL */
  private String avatarURL;
  
  /** The url. */
  private String url;
  
  /** the created date */
  private Date createdDate;
  
  /** isMember= true if user is member of space */
  private Boolean isMember = false;
  
  /** isPendingUser= true if user is PendingUser of space */
  private Boolean isPendingUser = false;
  
  /** isInvitedUser= true if user is InvitedUser of space */
  private Boolean isInvitedUser = false;
  
  /** the registration type of the space */
  private String registration;
  
  /** the visibility type of the space */
  private String visibility;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAvatarURL() {
    return avatarURL;
  }

  public void setAvatarURL(String avatarURL) {
    this.avatarURL = avatarURL;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Boolean getIsMember() {
    return isMember;
  }

  public void setIsMember(Boolean isMember) {
    this.isMember = isMember;
  }

  public Boolean getIsPendingUser() {
    return isPendingUser;
  }

  public void setIsPendingUser(Boolean isPendingUser) {
    this.isPendingUser = isPendingUser;
  }

  public Boolean getIsInvitedUser() {
    return isInvitedUser;
  }

  public void setIsInvitedUser(Boolean isInvitedUser) {
    this.isInvitedUser = isInvitedUser;
  }

  public String getRegistration() {
    return registration;
  }

  public void setRegistration(String registration) {
    this.registration = registration;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  
}