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
package org.exoplatform.platform.migration.common.aio.object;

import java.util.ArrayList;

public class PortalConfig {

  final public static String USER_TYPE = "user";
  final public static String GROUP_TYPE = "group";
  final public static String PORTAL_TYPE = "portal";

  private String name;
  private String locale;

  private String[] accessPermissions;
  private String editPermission;

  private String skin;
  private String title;

  private Container portalLayout;

  private String creator;
  private String modifier;

  private transient boolean modifiable;

  public PortalConfig() {
    portalLayout = new Container();
  }

  public String getName() {
    return name;
  }

  public void setName(String s) {
    name = s;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String s) {
    locale = s;
  }

  public String[] getAccessPermissions() {
    return accessPermissions;
  }

  public void setAccessPermissions(String[] s) {
    accessPermissions = s;
  }

  public String getEditPermission() {
    return editPermission;
  }

  public void setEditPermission(String editPermission) {
    this.editPermission = editPermission;
  }

  public String getSkin() {
    return skin;
  }

  public void setSkin(String s) {
    skin = s;
  }

  public Container getPortalLayout() {
    return portalLayout;
  }

  public void setPortalLayout(Container container) {
    portalLayout = container;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public void setModifiable(boolean b) {
    modifiable = b;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String s) {
    creator = s;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String s) {
    modifier = s;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String value) {
    title = value;
  }

  static public class PortalConfigSet {
    private ArrayList<PortalConfig> portalConfigs;

    public ArrayList<PortalConfig> getPortalConfigs() {
      return portalConfigs;
    }

    public void setPortalConfigs(ArrayList<PortalConfig> list) {
      portalConfigs = list;
    }
  }

}