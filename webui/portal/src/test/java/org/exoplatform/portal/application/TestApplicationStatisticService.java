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
package org.exoplatform.portal.application;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * Mar 09, 2009  
 */
public class TestApplicationStatisticService extends BasicTestCase {

  private ApplicationStatisticService service_;

  protected void setUp() throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    service_ = (ApplicationStatisticService)portalContainer.getComponentInstanceOfType(ApplicationStatisticService.class) ;
  }
  
  public void testInitial() {
  	assertNotNull(service_) ;
  }
  
  public void testApplicationRegistryManaged() throws Exception {
  	MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
  	assertNotNull(mbeanServer);
  	ObjectName name = new ObjectName("exo:service=ApplicationStatistic");
  	ObjectInstance mbean = mbeanServer.getObjectInstance(name);
  	String[] result = (String[]) mbeanServer.invoke(name, "list", null, null);
  	assertEquals(8, result.length) ;
  }
}
