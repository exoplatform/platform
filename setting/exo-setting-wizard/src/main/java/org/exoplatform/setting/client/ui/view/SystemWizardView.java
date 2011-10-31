package org.exoplatform.setting.client.ui.view;

import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.client.ui.model.SystemInfoWizardModel;
import org.exoplatform.setting.shared.WizardClientUtility;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class SystemWizardView extends WizardView {
  
  private Grid systemInfoOptions;
  private SystemInfoWizardModel model;
  
  public SystemWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
    
    model = (SystemInfoWizardModel) getModel();
  }

  @Override
  protected String getWizardTitle() {
    return constants.systemInfoTitle();
  }

  @Override
  protected String getWizardDescription() {
    return constants.systemInfoDescription();
  }

  @Override
  protected Widget buildStepToolbar() {
    
    Grid gridToolbar = new Grid(1, 2);
    gridToolbar.setWidth("100%");
    gridToolbar.getColumnFormatter().setWidth(0, "100%");
    
    // Add language form
    ListBox languages = new ListBox();
    languages.addItem(constants.english());
    languages.setValue(0, "en");
    languages.addItem(constants.francais());
    languages.setValue(1, "fr");
    languages.setWidth("150px");
    languages.setSelectedIndex(getIndexByLanguagesValue(languages, Window.Location.getParameter("locale")));
    languages.addChangeHandler(new ChangeHandler() {

      // Change language
      public void onChange(ChangeEvent arg0) {
        ListBox lgs = (ListBox) arg0.getSource();
        String newLanguage = lgs.getValue(lgs.getSelectedIndex());
        String oldLanguage = Window.Location.getParameter("locale");
        String newUrl = WizardClientUtility.buildLocaleUrl(Window.Location.getHref(), Window.Location.getQueryString(), oldLanguage, newLanguage);
        Window.Location.replace(newUrl);
      }
    });
    Grid gridLanguages = new Grid(1, 2);
    gridLanguages.setHTML(0, 0, constants.chooseLanguage());
    gridLanguages.setWidget(0, 1, languages);
    
    gridToolbar.setWidget(0, 0, gridLanguages);
    gridToolbar.setWidget(0, 1, prepareNextButton(constants.start()));
    
    return gridToolbar;
  }

  @Override
  protected Widget buildStepContent() {

    Map<String, String> datas = model.getSystemInfoOptions();
    
    systemInfoOptions = new Grid(datas.size(), 3);
    systemInfoOptions.setCellSpacing(6);
    
    if(datas != null) {
      int i = 0;
      for(Map.Entry<String, String> entry : datas.entrySet()) {
        systemInfoOptions.setHTML(i, 0, "<b>" + entry.getKey() + ": </b>");
        systemInfoOptions.setHTML(i, 1, entry.getValue());
        i++;
      }
    }

    return systemInfoOptions;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    return null;
  }
    
  
  /**
   * Return an index corresponding of the value
   * @param languages
   * @param language
   * @return
   */
  public int getIndexByLanguagesValue(ListBox languages, String language) {
    if(languages != null) {
      for(int i=0; i<languages.getItemCount(); i++) {
        if(languages.getValue(i).equals(language)) {
          return i;
        }
      }
    }
    return 0;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub
    
  }
  
  
}
