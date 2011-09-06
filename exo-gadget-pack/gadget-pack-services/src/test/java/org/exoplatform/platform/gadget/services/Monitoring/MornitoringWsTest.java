package org.exoplatform.platform.gadget.services.Monitoring;

import org.exoplatform.platform.gadget.services.test.GadgetWsTestcase;
import org.exoplatform.services.rest.impl.ContainerResponse;

public class MornitoringWsTest extends GadgetWsTestcase {
	public void testMemory() {
		try{
			ContainerResponse cres = launcher.service("GET", "/monitoring/memory", "", null, null, null);

			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		} catch(Exception e){
			fail(e.getMessage());
		}
	}

	public void testCaches() {
		try{
			ContainerResponse cres = launcher.service("GET", "/monitoring/caches", "", null, null, null);

			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		}catch(Exception e){
			fail(e.getMessage());
		}
	}	
}
