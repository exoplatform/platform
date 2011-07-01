package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.WizardGui;
import org.exoplatform.setting.client.WizardUtility;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class SetupWizardView extends WizardView {
  
  
  public SetupWizardView(WizardGui gui) {
    super(gui,
          "Step 0 - Setup", 
          "We have detected <br /> the following environement on your server.");
  }

  @Override
  protected Widget buildStepToolbar() {
    
    FlowPanel panel = new FlowPanel();
    
    Button buttonNext = new Button();
    buttonNext.setText("Next");
    buttonNext.getElement().setId("toStep1");
    
    // Button event
    buttonNext.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Button clickedButton = (Button) event.getSource();
        int toStepId = WizardUtility.getToStepId(clickedButton.getElement().getId());
        gui.displayScreen(toStepId);
      }
    });
    
    panel.add(buttonNext);
    
    return panel;
  }

  @Override
  protected Widget buildStepContent() {
    return new HTML("Step 0");
  }
  
}
