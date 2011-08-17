package org.exoplatform.setting.client.ui.view;

import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.i18n.WizardConstants;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.client.ui.model.WizardModel;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
    
/**
 * This is a view
 * 
 * @author Clement
 *
 */
public abstract class WizardView extends HorizontalPanel {
  
  // Contains principal controller
  protected SetupWizardController controller;
  
  // Current stepNumber
  protected int stepNumber;
  
  // Wizard Mode
  protected SetupWizardMode mode;

  // i18n constants
  protected WizardConstants constants = GWT.create(WizardConstants.class);
  
  /**
   * Initialization
   * @param gui
   * @param title
   * @param description
   * @param stepNumber
   */
  public WizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    
    this.controller = controller;
    this.stepNumber = stepNumber;
    this.mode = mode;
  }
  

  /*=======================================================================
   * Screen Factory
   *======================================================================*/
  
  /**
   * Build a view
   */
  public void build() {
    // Construct a dock panel
    DockPanel dock = new DockPanel();
    dock.setStyleName("cw-DockPanel");
    dock.add(buildHeader(), DockPanel.NORTH);
    dock.add(buildDescription(), DockPanel.NORTH);
    dock.add(buildToolbar(), DockPanel.SOUTH);
    dock.add(buildContent(), DockPanel.CENTER);

    // Configure Main panel
    setSpacing(5);
    setSize("100%", "500px");
    setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
    setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
    
    DecoratorPanel decPanel = new DecoratorPanel();
    decPanel.setWidget(dock);
    
    // Add constructed view
    add(decPanel);
  }
  
  /**
   * Header creation
   * @param title
   * @return
   */
  protected Widget buildHeader() {
    Label uiTitle = new Label();
    uiTitle.setText(getWizardTitle());
    uiTitle.setStylePrimaryName("blockHeader");
    return uiTitle;
  }

  /**
   * Description block creation
   * @param description
   * @return
   */
  protected Widget buildDescription() {
    HTML desc = new HTML(getWizardDescription());
    desc.setHeight("40px");
    desc.setStylePrimaryName("blockDescription");
    return desc;
  }

  /**
   * Skeleton of toolbar creation
   * @return
   */
  protected Widget buildToolbar() {
    HorizontalPanel uiToolbar = new HorizontalPanel();
    uiToolbar.setStylePrimaryName("blockAction");
    uiToolbar.setWidth("100%");
    uiToolbar.setHeight("60px");
    uiToolbar.setHorizontalAlignment(ALIGN_RIGHT);
    uiToolbar.setVerticalAlignment(ALIGN_MIDDLE);
    uiToolbar.setSpacing(10);
    uiToolbar.add(buildStepToolbar());
    return uiToolbar;
  }
  
  /**
   * Skeleton of content creation
   * @return
   */
  protected Widget buildContent() {
    ScrollPanel uiContent = new ScrollPanel();
    uiContent.setWidget(buildStepContent());
    uiContent.setStylePrimaryName("blockContent");
    uiContent.setSize("600px", "300px");
    return uiContent;
  }
  
  /**
   * This method display a screen after his initialization
   */
  public void display() {
    this.setVisible(true);
  }
  
  public void hide() {
    this.setVisible(false);
  }
  

  /*=======================================================================
   * GUI abstract methods (need to be redefined)
   *======================================================================*/

  /**
   * Redefine Wizard title
   * @return
   */
  protected abstract String getWizardTitle();

  /**
   * Redefine Wizard description
   * @return
   */
  protected abstract String getWizardDescription();
  
  /**
   * Toolbar creation is to redefine
   * @return
   */
  protected abstract Widget buildStepToolbar();
  
  /**
   * Step content creation is to redefine
   * @return
   */
  protected abstract Widget buildStepContent();

  /**
   * Store data after screen validation
   * @return
   */
  public abstract Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException;
  
  
  /*=======================================================================
   * Button factory
   *======================================================================*/

  /**
   * Constructs a standard previous button
   * <p>
   * If current step is the first step, so we return null
   * @return
   */
  protected Button preparePreviousButton() {
    return preparePreviousButton(constants.previous());
  }
  
  /**
   * Constructs a standard previous button with text string
   * <p>
   * If current step is the first step, so return null
   * @param text
   * @return build button
   */
  protected Button preparePreviousButton(String text) {
    return prepareButton(text, stepNumber - 1);
  }

  /**
   * Constructs a standard next button
   * @return build button
   */
  protected Button prepareNextButton() {
    return prepareNextButton(constants.next());
  }
  
  /**
   * Constructs a next button with text string
   * @param text
   * @return build button
   */
  protected Button prepareNextButton(String text) {
    return prepareButton(text, stepNumber + 1);
  }
  
  /**
   * Constructs a button with text string and with step target
   * <p>
   * Verify datas only on click "Next" button
   * <p>
   * store datas if
   * @param text
   * @param toStep
   * @return build button
   */
  protected Button prepareButton(String text, final int toStep) {
    Button button = new Button();
    button.setText(text);
    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        if(stepNumber < toStep) {
          try {
            // Ask to verify datas
            Map<SetupWizardData, String> datas = verifyDatas(toStep);
            // Ask to controller to store these datas
            controller.storeDatas(datas, toStep);
            controller.displayScreen(toStep);
          }
          catch (InvalidWizardViewFieldException e) {
            controller.displayError(e.getWizardMessage());
          }
        }
        else {
          controller.displayScreen(toStep);
        }
      }
    });
    return button;
  }

  
  /*=======================================================================
   * API methods
   *======================================================================*/
  
  /**
   * Get the current model loaded
   * @return
   */
  protected WizardModel getModel() {
    return controller.getModel(stepNumber);
  }

  public SetupWizardMode getMode() {
    return mode;
  }
}
