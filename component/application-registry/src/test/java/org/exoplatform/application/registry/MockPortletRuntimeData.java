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
package org.exoplatform.application.registry;

import org.exoplatform.services.portletcontainer.monitor.CachedData;
import org.exoplatform.services.portletcontainer.monitor.PortletRequestMonitorData;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
class MockPortletRuntimeData implements PortletRuntimeData {
  
  public String getCacheScope() {
    // TODO Auto-generated method stub
    return null;
  }

  private String portletAppName;
  private String portletName;

  public MockPortletRuntimeData(String appName , String name) {
    this.portletAppName = appName;
    this.portletName = name;
  }

  public String getPortletAppName() { return portletAppName;  }

  public String getPortletName() { return portletName; }

  public boolean isInitialized() { return false; }

  public long getInitializationTime() { return 0; }

  public long getLastAccessTime() { return 0; }

  public long getLastFailureAccessTime() { return 0; }

  public long getLastInitFailureAccessTime() { return 0; }

  @SuppressWarnings("unused")
  public void setLastInitFailureAccessTime(long lastInitFailureAccessTime) {
  }

  public long getUnavailabilityPeriod() { return 0; }

  @SuppressWarnings("unused")
  public boolean isDataCached(String key, boolean isCacheGlobal) { return false; }

  @SuppressWarnings("unused")
  public CachedData getCachedData(String key, boolean isCacheGlobal) { return null; }

  public int getCacheExpirationPeriod() { return 0; }

  public PortletRequestMonitorData[] getPortletRequestMonitorData() {
    return new PortletRequestMonitorData[0];
  }

}