package org.exoplatform.setting.client.ui.view;

import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class SummaryWizardView extends WizardView {
  
  private Grid summary = new Grid();
  
  public SummaryWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.setupSummary();
  }

  @Override
  protected String getWizardDescription() {
    return constants.weAreReady();
  }

  @Override
  protected Widget buildStepToolbar() {
    
    Grid gridToolbar = new Grid(1, 3);
    gridToolbar.setWidth("100%");
    gridToolbar.getColumnFormatter().setWidth(0, "100%");
    gridToolbar.setWidget(0, 0, prepareExportButton());
    gridToolbar.setWidget(0, 1, preparePreviousButton());
    gridToolbar.setWidget(0, 2, prepareNextButton(constants.apply()));
    
    return gridToolbar;
  }

  @Override
  protected Widget buildStepContent() {

    summary = new Grid();
    summary.setCellSpacing(6);
    
    return summary;
  }
  
  /***
   * Constructs a button with text string and with step target
   * @param text
   * @param toStep
   * @return build button
   */
  protected Button prepareExportButton() {
    Button button = new Button();
    button.setText("Export");
    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        // Todo Export
        controller.displayMessage(constants.notYetImplemented());
      }
    });
    return button;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    return null;
  }

  @Override
  public void executeOnDisplay() {
    // Display all user's choices
    Map<SetupWizardData, String> datas = controller.getSetupWizardDatas();

    summary.resize(datas.size(), 3);
    
    if(datas != null) {
      int i = 0;
      for(Map.Entry<SetupWizardData, String> entry : datas.entrySet()) {
        summary.setHTML(i, 0, "<b>" + entry.getKey() + ": </b>");
        summary.setHTML(i, 1, entry.getValue());
        i++;
      }
    }
    
  }
  
}
