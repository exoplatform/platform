/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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