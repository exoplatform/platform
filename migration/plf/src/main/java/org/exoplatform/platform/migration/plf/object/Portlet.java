/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.migration.plf.object;

/**
 * The transient state of an application when it has not yet been stored in the database.
 * 
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Portlet {
  private String applicationName = null;
  private String portletName = null;
  private PortletPreferences preferences = null;

  public Portlet() {
    preferences = new PortletPreferences();
  }

  public Portlet(String applicationName, String portletName) {
    this.applicationName = applicationName;
    this.portletName = portletName;
    preferences = new PortletPreferences();
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getPortletName() {
    return this.portletName;
  }

  public void setPortletName(String portletName) {
    this.portletName = portletName;
  }

  public PortletPreferences getPreferences() {
    return this.preferences;
  }

  public void setPreferences(PortletPreferences preferences) {
    this.preferences = preferences;
  }
}
