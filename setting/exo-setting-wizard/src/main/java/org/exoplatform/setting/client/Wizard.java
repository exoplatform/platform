package org.exoplatform.setting.client;

import org.exoplatform.setting.client.i18n.WizardConstants;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.client.service.WizardServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Wizard implements EntryPoint {
  
  //The message displayed to the user when the server cannot be reached or returns an error.
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  // Create a remote service proxy to talk to the server-side Wizard service.
  private final WizardServiceAsync wizardService = GWT.create(WizardService.class);

  // I18n messages & constants
  private WizardConstants constants = GWT.create(WizardConstants.class);
  
  private Button testButton = new Button("Test");

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {

    Window.setTitle(constants.setupWizard());
    RootPanel.get("appTitle").add(new Label(constants.setupWizard()));
    
    RootPanel.get("mainBlock").add(testButton);
  }
}
