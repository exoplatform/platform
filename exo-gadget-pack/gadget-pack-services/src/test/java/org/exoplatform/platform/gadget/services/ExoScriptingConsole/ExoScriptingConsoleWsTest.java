package org.exoplatform.platform.gadget.services.ExoScriptingConsole;

import org.exoplatform.platform.gadget.services.test.GadgetServiceTestcase;
import org.exoplatform.services.rest.impl.ContainerResponse;

public class ExoScriptingConsoleWsTest extends GadgetServiceTestcase {
	public void testGetLanguagesWs(){
		try{
			ContainerResponse cres = launcher.service("GET", "/console-manager/languages/", "", null, null, null);

			assertEquals(200, cres.getStatus());
			assertNotNull(cres.getEntity());
		}catch(Exception e){
			fail(e.getMessage());
		}
	}
}
