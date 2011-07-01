package org.exoplatform.setting.client;

import java.util.LinkedList;

import org.exoplatform.setting.client.i18n.WizardConstants;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.client.service.WizardServiceAsync;
import org.exoplatform.setting.client.ui.SetupTypeWizardView;
import org.exoplatform.setting.client.ui.SetupWizardView;
import org.exoplatform.setting.client.ui.WizardView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * <p>
 * This is principal controller
 */
public class WizardGui implements EntryPoint {
  
  // Create a remote service proxy to talk to the server-side Wizard service.
  private final WizardServiceAsync wizardService = GWT.create(WizardService.class);

  // I18n messages & constants
  private WizardConstants constants = GWT.create(WizardConstants.class);
  
  private LinkedList<WizardView> views;
  private Label errorMsgLabel = new Label();

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

    // Error message label init
    errorMsgLabel.setStyleName("errorMessage");
    errorMsgLabel.setVisible(false);

    // Views init
    views = new LinkedList<WizardView>();
    views.add(new SetupWizardView(this));
    views.add(new SetupTypeWizardView(this));
    
    RootPanel.get("mainBlock").add(errorMsgLabel);
    for(Widget view : views) {
      RootPanel.get("mainBlock").add(view);
    }
        
    // Display first screen
    displayScreen(0);
  }
  
  /**
   * Display screen #index 
   * 
   * @param index
   */
  public void displayScreen(int index) {
    
    if(index < 0 || index >= views.size()) {
      displayError("Screen doesn't exist.");
    }
    else {
      hideError();
      
      // Fetch all screens to set visible false
      for(Widget view : views) {
        view.setVisible(false);
      }
      
      Widget activeView = views.get(index);
      activeView.setVisible(true);
    }
  }
  
  public void storeDatas() {
    // TODO
  }
  
  private void displayError(String errorMsg) {
    errorMsgLabel.setText("Error: " + errorMsg);
    errorMsgLabel.setVisible(true);
  }
  
  private void hideError() {
    errorMsgLabel.setText("");
    errorMsgLabel.setVisible(false);
  }
}
