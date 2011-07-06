package org.exoplatform.setting.client;

import java.util.LinkedList;
import java.util.Map;

import org.exoplatform.setting.client.i18n.WizardConstants;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.client.service.WizardServiceAsync;
import org.exoplatform.setting.client.ui.SetupTypeWizardView;
import org.exoplatform.setting.client.ui.SetupWizardView;
import org.exoplatform.setting.client.ui.WizardDialogBox;
import org.exoplatform.setting.client.ui.WizardView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
  private WizardDialogBox errorDialogBox;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    
    // Create the dialog box
    errorDialogBox = new WizardDialogBox();

    // Views init
    views = new LinkedList<WizardView>();
    views.add(new SetupWizardView(this, 0));
    views.add(new SetupTypeWizardView(this, 1));
    
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
      displayError("Screen #" + index + " doesn't exist.");
    }
    else {
      
      // Fetch all screens to set visible false
      for(Widget view : views) {
        view.setVisible(false);
      }
      
      Widget activeView = views.get(index);
      activeView.setVisible(true);
    }
  }
  
  /**
   * Display an error in a dialog box
   * @param error
   */
  public void displayError(String error) {
    errorDialogBox.displayError(error);
  }
  
  /**
   * Display an error in a dialog box
   * @param error
   */
  public void displayMessage(String message) {
    errorDialogBox.displayMessage(message);
  }
  
  /**
   * Stores datas into server side
   * @param datas
   */
  public void storeDatas(Map<String, String> datas, int toStep) {
    
    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

      public void onFailure(Throwable arg0) {
        displayError(arg0.getMessage());
      }

      public void onSuccess(Integer arg0) {
        displayMessage(arg0.toString());
        //displayScreen(arg0);
      }
    };
    
    // Call service to store datas
    wizardService.storeDatas(datas, toStep, callback);
  }
}
