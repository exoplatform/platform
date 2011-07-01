package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.WizardGui;
import org.exoplatform.setting.client.WizardUtility;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class SetupTypeWizardView extends WizardView {

  public SetupTypeWizardView(WizardGui gui) {
    super(gui,
          "Step 1 - Select a setup type.", 
          "No desc");
  }

  @Override
  protected Widget buildStepToolbar() {
    
    FlowPanel panel = new FlowPanel();
    
    Button buttonPrevious = new Button();
    buttonPrevious.setText("Previous");
    buttonPrevious.getElement().setId("toStep0");
    buttonPrevious.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Button clickedButton = (Button) event.getSource();
        int toStepId = WizardUtility.getToStepId(clickedButton.getElement().getId());
        gui.storeDatas();
        gui.displayScreen(toStepId);
      }
    });
    panel.add(buttonPrevious);
    
    Button buttonNext = new Button();
    buttonNext.setText("Next");
    buttonNext.getElement().setId("toStep2");
    buttonNext.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Button clickedButton = (Button) event.getSource();
        int toStepId = WizardUtility.getToStepId(clickedButton.getElement().getId());
        gui.storeDatas();
        gui.displayScreen(toStepId);
      }
    });
    panel.add(buttonNext);
    
    return panel;
  }

  @Override
  protected Widget buildStepContent() {
    return new HTML("Step 1");
  }
}
