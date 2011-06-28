package org.exoplatform.setting.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 */
public class GwtTestwizard extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.exoplatform.setting.wizardJUnit";
  }

  /**
   * This test will send a request to the server using the greetServer method in
   * GreetingService and verify the response.
   */
  public void testGreetingService() {
    /*// Create the service that we will test.
    WizardServiceAsync greetingService = GWT.create(WizardService.class);
    ServiceDefTarget target = (ServiceDefTarget) greetingService;
    target.setServiceEntryPoint(GWT.getModuleBaseURL() + "wizard/greet");

    // Since RPC calls are asynchronous, we will need to wait for a response
    // after this test method returns. This line tells the test runner to wait
    // up to 10 seconds before timing out.
    delayTestFinish(10000);

    // Send a request to the server.
    greetingService.greetServer("GWT User", new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        // The request resulted in an unexpected error.
        fail("Request failure: " + caught.getMessage());
      }

      public void onSuccess(String result) {
        // Verify that the response is correct.
        assertTrue(result.startsWith("Hello, GWT User!"));

        // Now that we have received a response, we need to tell the test runner
        // that the test is complete. You must call finishTest() after an
        // asynchronous test finishes successfully, or the test will time out.
        finishTest();
      }
    });*/
  }


}
