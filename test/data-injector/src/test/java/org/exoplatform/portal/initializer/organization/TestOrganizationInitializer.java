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
package org.exoplatform.portal.initializer.organization;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SAS Author : Trong.Tran
 * @author Trong.Tran
 */

public class TestOrganizationInitializer extends BasicTestCase {

	protected OrganizationService orgSer_ ;
	
  public TestOrganizationInitializer(String s) {
    super(s);
  }

  protected void setUp() throws Exception {
  	//super.setUp();
    //PortalContainer pContainer = PortalContainer.getInstance();
    //orgSer_ = (OrganizationService) pContainer.getComponentInstanceOfType(OrganizationService.class);

  }
  
  public void testGroupInitializer() throws Exception {
  	//GroupHandler groupHandler = orgSer_.getGroupHandler();
  	//Group test = groupHandler.findGroupById("/africa");
  	//assertNotNull(test);
  }
}
