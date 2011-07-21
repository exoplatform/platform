package org.exoplatform.setting.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.i18n.WizardConstants;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.client.service.WizardServiceAsync;
import org.exoplatform.setting.client.ui.ApplySettingsWizardView;
import org.exoplatform.setting.client.ui.SetupTypeWizardView;
import org.exoplatform.setting.client.ui.SummaryWizardView;
import org.exoplatform.setting.client.ui.SuperUserWizardView;
import org.exoplatform.setting.client.ui.SystemInfoWizardView;
import org.exoplatform.setting.client.ui.WizardDialogBox;
import org.exoplatform.setting.client.ui.WizardView;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
  
  // GUI elements
  private LinkedList<WizardView> views;
  private WizardDialogBox errorDialogBox;
  
  // Client Mode to show/hide some screens
  private SetupWizardMode setupWizardMode;
  
  // Screen datas
  private Map<SetupWizardData, String> setupWizardDatas;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    
    // Create the dialog box
    errorDialogBox = new WizardDialogBox();
    
    // Initialize setup mode
    setupWizardMode = SetupWizardMode.STANDARD;
    
    // Initialize datas
    setupWizardDatas = new HashMap<SetupWizardData, String>();

    // Views init
    views = new LinkedList<WizardView>();
    views.add(new SystemInfoWizardView(this, 0));
    views.add(new SetupTypeWizardView(this, 1));
    views.add(new SuperUserWizardView(this, 2));
    views.add(new SummaryWizardView(this, 3));
    views.add(new ApplySettingsWizardView(this, 4));
    
    // Add views in HTML
    for(Widget view : views) {
      RootPanel.get("mainBlock").add(view);
    }
        
    // Display only first screen
    displayScreen(0);
  }
  
  /**
   * Display screen #index 
   * 
   * @param index
   */
  private void displayScreen(int index) {
    
    if(index < 0 || index >= views.size()) {
      displayError("Screen #" + index + " doesn't exist.");
    }
    else {
      
      // Fetch all screens to set visible false
      for(WizardView view : views) {
        view.hide();
      }
      
      WizardView activeView = views.get(index);
      activeView.display();
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
  public void storeDatas(Map<SetupWizardData, String> datas, int toStep) {
    
    /*AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

      public void onFailure(Throwable arg0) {
        displayError(arg0.getMessage());
      }

      public void onSuccess(Integer arg0) {
        displayScreen(arg0);
      }
    };
    
    // Call service to store datas
    wizardService.storeDatas(datas, toStep, callback);*/
    
    if(datas != null && datas.size() > 0) {
      // Rajoute les données
      setupWizardDatas.putAll(datas);
    }
    
    displayScreen(toStep);
  }

  public SetupWizardMode getSetupWizardMode() {
    return setupWizardMode;
  }

  public void setSetupWizardMode(SetupWizardMode setupWizardMode) {
    this.setupWizardMode = setupWizardMode;
  }
  
  public Map<SetupWizardData, String> getSetupWizardDatas() {
    return this.setupWizardDatas;
  }
}
