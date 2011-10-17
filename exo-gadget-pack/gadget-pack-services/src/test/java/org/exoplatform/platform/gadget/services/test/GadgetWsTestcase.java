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
package org.exoplatform.platform.gadget.services.test;

import junit.framework.TestCase;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.test.BasicTestCase;

public class GadgetWsTestcase extends TestCase {

	protected final static String REPO_NAME = "repository";

	protected final static String COLLABORATION_WS = "collaboration";

	protected static PortalContainer container;

	protected static RepositoryService repositoryService;
	
	protected static ResourceLauncher launcher;	

	static {
		try {
			container = PortalContainer.getInstance();
			repositoryService = getService(RepositoryService.class);
			RequestHandler handler = getService(RequestHandler.class);
			launcher = new ResourceLauncher(handler);			
		} catch (Exception e) {
			fail("Failed to init GadgetWsTestcase: " + e.getMessage());
		}
	}
	
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Failed to setup GadgetWsTestcase: " + e.getMessage());
		}
	}

	protected static <T> T getService(Class<T> clazz) {
		return clazz.cast(container.getComponentInstanceOfType(clazz));
	}
}