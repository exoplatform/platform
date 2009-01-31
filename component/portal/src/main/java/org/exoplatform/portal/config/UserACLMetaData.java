/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.portal.config;

/**
 * A metadata class to describe security configuration.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class UserACLMetaData {

  /** . */
  private String superUser;

  /** . */
  private String guestsGroups;

  /** . */
  private String accessControlWorkspace;

  /** . */
  private String navigationCreatorMembershipType;

  /** . */
  private String portalCreateGroups;

  public String getSuperUser() {
    return superUser;
  }

  public void setSuperUser(String superUser) {
    this.superUser = superUser;
  }

  public String getGuestsGroups() {
    return guestsGroups;
  }

  public void setGuestsGroups(String guestsGroups) {
    this.guestsGroups = guestsGroups;
  }

  public String getAccessControlWorkspace() {
    return accessControlWorkspace;
  }

  public void setAccessControlWorkspace(String accessControlWorkspace) {
    this.accessControlWorkspace = accessControlWorkspace;
  }

  public String getNavigationCreatorMembershipType() {
    return navigationCreatorMembershipType;
  }

  public void setNavigationCreatorMembershipType(String navigationCreatorMembershipType) {
    this.navigationCreatorMembershipType = navigationCreatorMembershipType;
  }

  public String getPortalCreateGroups() {
    return portalCreateGroups;
  }

  public void setPortalCreateGroups(String portalCreateGroups) {
    this.portalCreateGroups = portalCreateGroups;
  }
}
