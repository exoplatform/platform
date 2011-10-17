package org.exoplatform.platform.gadget.services.ExoScriptingConsole;

import org.exoplatform.platform.gadget.services.test.GadgetServiceTestcase;

public class ExoScriptingConsoleTest extends GadgetServiceTestcase{
	public void testRun(){
		try {
			ExoScriptingConsole console = new ExoScriptingConsole("ECMAScript");
			console.getVariables().put("x", 5);
			assertTrue(console.getVariables().containsKey("x"));
			assertEquals(console.run("println(x+1)"), "6\n");
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
