package org.exoplatform.setting.client.ui.controller;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.service.WizardService;
import org.exoplatform.setting.client.service.WizardServiceAsync;
import org.exoplatform.setting.client.ui.model.DatabaseIdmWizardModel;
import org.exoplatform.setting.client.ui.model.DatabaseJcrWizardModel;
import org.exoplatform.setting.client.ui.model.LdapConfigWizardModel;
import org.exoplatform.setting.client.ui.model.SystemInfoWizardModel;
import org.exoplatform.setting.client.ui.model.WizardModel;
import org.exoplatform.setting.client.ui.view.ApplyWizardView;
import org.exoplatform.setting.client.ui.view.ChatWizardView;
import org.exoplatform.setting.client.ui.view.FileSetupWizardView;
import org.exoplatform.setting.client.ui.view.IdmDBWizardView;
import org.exoplatform.setting.client.ui.view.IdmSetupWizardView;
import org.exoplatform.setting.client.ui.view.JcrDBWizardView;
import org.exoplatform.setting.client.ui.view.LdapWizardView;
import org.exoplatform.setting.client.ui.view.MailWizardView;
import org.exoplatform.setting.client.ui.view.SetupTypeWizardView;
import org.exoplatform.setting.client.ui.view.SummaryWizardView;
import org.exoplatform.setting.client.ui.view.SuperUserWizardView;
import org.exoplatform.setting.client.ui.view.SystemWizardView;
import org.exoplatform.setting.client.ui.view.WebsiteWizardView;
import org.exoplatform.setting.client.ui.view.WizardDialogBox;
import org.exoplatform.setting.client.ui.view.WizardView;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class SetupWizardController {

  // Create a remote service proxy to talk to the server-side Wizard service.
  private final WizardServiceAsync wizardService = GWT.create(WizardService.class);
  
  private int currentScreenDisplayed;
  
  // GUI elements
  private LinkedList<WizardView> views;
  private LinkedHashMap<Integer, WizardModel> models;
  private LinkedList<Integer> loadedModels;
  private int nbModels = 0;
  private int nbViews = 0;
  private WizardDialogBox messageDialogBox;
  
  // Client Mode to show/hide some screens
  private SetupWizardMode setupWizardMode;
  
  // Screens datas
  private Map<SetupWizardData, String> setupWizardDatas;
  
  // Debug
  private static Boolean isDebugActivated = true;
  private static Integer firstScreenNumber = 0;
  
  
  public void start() {
    
    // Call to server properties
    initDebug();
    initFirstScreenNumber();
    
    // Create the dialog box
    messageDialogBox = new WizardDialogBox();
    
    // Initialize setup mode
    setupWizardMode = SetupWizardMode.ADVANCED;
    
    // Initialize datas
    setupWizardDatas = new LinkedHashMap<SetupWizardData, String>();

    // Models init
    models = new  LinkedHashMap<Integer, WizardModel>();
    models.put(0, new SystemInfoWizardModel(this, 0));
    models.put(3, new DatabaseJcrWizardModel(this, 3));
    models.put(5, new DatabaseIdmWizardModel(this, 5));
    models.put(6, new LdapConfigWizardModel(this, 6));
    
    // Views init
    views = new LinkedList<WizardView>();
    views.add(new SystemWizardView(this, 0, SetupWizardMode.STANDARD));
    views.add(new SetupTypeWizardView(this, 1, SetupWizardMode.STANDARD));
    views.add(new SuperUserWizardView(this, 2, SetupWizardMode.ADVANCED));
    views.add(new JcrDBWizardView(this, 3, SetupWizardMode.STANDARD));
    views.add(new IdmSetupWizardView(this, 4, SetupWizardMode.STANDARD));
    views.add(new IdmDBWizardView(this, 5, SetupWizardMode.STANDARD));
    views.add(new LdapWizardView(this, 6, SetupWizardMode.STANDARD));
    views.add(new FileSetupWizardView(this, 7, SetupWizardMode.STANDARD));
    views.add(new MailWizardView(this, 8, SetupWizardMode.STANDARD));
    views.add(new ChatWizardView(this, 9, SetupWizardMode.ADVANCED));
    views.add(new WebsiteWizardView(this, 10, SetupWizardMode.ADVANCED));
    views.add(new SummaryWizardView(this, 11, SetupWizardMode.STANDARD));
    views.add(new ApplyWizardView(this, 12, SetupWizardMode.STANDARD));
    
    nbModels = models.size();
    nbViews = views.size();
    
    executeModels();
  }



  /*=======================================================================
   * GUI methods
   *======================================================================*/

  /**
   * This method need to be called by all models. When all models are loaded
   * So, views are built and displayed.
   */
  public void fireModelLoaded(int screenNumber) {
    if(loadedModels == null) {
      loadedModels = new LinkedList<Integer>();
    }
    loadedModels.add(screenNumber);
    
    // All models are loaded, so we can display Setup Wizard
    if(loadedModels.size() == nbModels) {
      buildViews();
      displaySetupWizard();
    }
  }
  
  /**
   * Load all datas needed by views
   */
  private void executeModels() {
    if(models != null) {
      for(Map.Entry<Integer, WizardModel> entry : models.entrySet()) {
        entry.getValue().initDatas();
      }
    }
  }
  
  /**
   * Build views and add in HTML
   */
  private void buildViews() {
    for(WizardView view : views) {
      view.build();
      RootPanel.get("mainBlock").add(view);
    }
  }
  
  public WizardModel getModel(int screenNumber) {
    return models.get(screenNumber);
  }
  
  /**
   * First displaying of SetupWizard
   */
  private void displaySetupWizard() {
    // Hide loading
    RootPanel.get("loadingBlock").setVisible(false);
    
    // Display mainBlock
    RootPanel.get("mainBlock").setVisible(true);
    
    // Display mainBlock
    RootPanel.get("stepBlock").setVisible(true);
    
    // Display First Screen
    displayScreen(firstScreenNumber);
  }
  
  /**
   * Display screen #index 
   * 
   * @param index
   */
  public void displayScreen(int index) {
    
    // In case of setup mode is STANDARD, ADVANCED screens are not displayed, we try to display next/previous screen
    if(! isDebugActivated() && 
       setupWizardMode.equals(SetupWizardMode.STANDARD) && 
       views.get(index).getMode().equals(SetupWizardMode.ADVANCED)) {
      
      // Compute toStep
      int toStep = (currentScreenDisplayed < index) ? index + 1 : index - 1;
      if(toStep < views.size() && toStep >= 0) {
        displayScreen(toStep);
      }
    }
    else {
      if(index < 0 || index >= views.size()) {
        displayError("Screen #" + index + " doesn't exist.");
      }
      else {
        // Hide all screens
        for(WizardView view : views) {
          view.hide();
        }
        
        currentScreenDisplayed = index;
        
        WizardView activeView = views.get(index);
        activeView.executeOnDisplay();
        activeView.display();
      }
    }
  }
  
  /**
   * Display an error in a dialog box
   * @param error
   */
  public void displayError(String error) {
    messageDialogBox.displayError(error);
  }
  
  /**
   * Display a message in a dialog box
   * @param message
   */
  public void displayMessage(String message) {
    messageDialogBox.displayMessage(message);
  }


  /*=======================================================================
   * Controller methods
   *======================================================================*/
  
  /**
   * Stores datas
   * @param datas
   */
  public void storeDatas(Map<SetupWizardData, String> datas, int toStep) {

    if(datas != null && datas.size() > 0) {
      // Add datas
      setupWizardDatas.putAll(datas);
    }
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
  
  public int getNbViews() {
    return nbViews;
  }
  
  public boolean isDebugActivated() {
    return isDebugActivated.booleanValue();
  }


  /*=======================================================================
   * Server methods (between client and server)
   *======================================================================*/
  
  /**
   * Call to server to get all properties displayed to user
   * @return
   */
  public Map<String, String> getSystemInfoProperties(AsyncCallback<Map<String, String>> callback) {
    wizardService.getSystemProperties(callback);
    
    return null;
  }
  
  /**
   * Call to server to get all datasources installed on server
   * @return
   */
  public Map<String, String> getDatasources(AsyncCallback<List<String>> callback) {
    wizardService.getDatasources(callback);
    
    return null;
  }
  
  /**
   * Call to server to get all properties displayed to user
   * @return
   */
  public String saveDatas(AsyncCallback<String> callback) {
    wizardService.saveDatas(setupWizardDatas, callback);
    
    return null;
  }
  
  /**
   * Call to server to get all properties displayed to user
   * @return
   */
  public String startPlatform(AsyncCallback<String> callback) {
    wizardService.startPlatform(callback);
    
    return null;
  }
  
  /**
   * Call to the server to know if debug is activated or not
   */
  private void initDebug() {
    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
      public void onFailure(Throwable arg0) {
        displayError("Cannot access to service");
      }
      public void onSuccess(Boolean arg0) {
        isDebugActivated = arg0;
      }
    };
    wizardService.getDebugActivation(callback);
  }
  
  /**
   * Call to the server to know if debug is activated or not
   */
  private void initFirstScreenNumber() {
    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
      public void onFailure(Throwable arg0) {
        displayError("Cannot access to service");
      }
      public void onSuccess(Integer arg0) {
        firstScreenNumber = arg0;
      }
    };
    wizardService.getFirstScreenNumber(callback);
  }
 
}


