package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.i18n.WizardConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Represents a dialog box to display error
 * @author Clement
 *
 */
public class WizardDialogBox extends DialogBox {
  
  private HTML dialogDetails;

  protected WizardConstants constants = GWT.create(WizardConstants.class);

  public WizardDialogBox() {
    // Create a dialog box and set the caption text
    this.setGlassEnabled(true);
    this.setAnimationEnabled(true);
    //this.setModal(false);
    this.ensureDebugId("cwDialogBox");
    this.setText(constants.message());
  
    // Create a table to layout the content
    VerticalPanel dialogContents = new VerticalPanel();
    dialogContents.setSpacing(10);
    dialogContents.setSize("400px", "100px");
    this.setWidget(dialogContents);
  
    // Add some text to the top of the dialog
    dialogDetails = new HTML("");
    dialogDetails.setStyleName("dialogDetails");
    dialogContents.add(dialogDetails);
    dialogContents.setCellHorizontalAlignment(dialogDetails, HasHorizontalAlignment.ALIGN_LEFT);
  
    // Add a close button at the bottom of the dialog
    Button closeButton = new Button(
        constants.ok(), new ClickHandler() {
          public void onClick(ClickEvent event) {
            hide();
          }
        });
    dialogContents.add(closeButton);
    dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
    dialogContents.setCellVerticalAlignment(closeButton, HasVerticalAlignment.ALIGN_BOTTOM);
  }
  
  public void displayError(String error) {
    this.setText(constants.error());
    displayMessage(error);
  }
  
  public void displayMessage(String message) {
    dialogDetails.setHTML(message);
    this.center();
    this.show();
  }
}
