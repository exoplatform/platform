package org.exoplatform.platform.gadget.services.ForumStatistics;

import org.exoplatform.platform.gadget.services.test.GadgetServiceTestcase;
import org.exoplatform.services.rest.impl.ContainerResponse;

public class ForumStatisticsWsTest extends GadgetServiceTestcase {
	public void testForumStatistics() {
		try{
			ContainerResponse cres = launcher.service("GET", "/forumsService/forums/statistic/", "", null, null, null);

			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	public void testForumWeeklyStatistics() {
		try{
			ContainerResponse cres = launcher.service("GET", "/forumsService/forums/weeklystatistic/", "", null, null, null);

			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	public void testForumToprate() {
		try {
			ContainerResponse cres = launcher.service("GET", "/forumsService/forums/toprate/5", "", null, null, null);

			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}	  
}
