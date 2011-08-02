package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.WizardModule;
import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.i18n.WizardConstants;

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
  protected WizardModule gui;
  
  // Current stepNumber
  protected int stepNumber;

  // i18n constants
  protected WizardConstants constants = GWT.create(WizardConstants.class);
  
  /**
   * Initialization
   * @param gui
   * @param title
   * @param description
   * @param stepNumber
   */
  public WizardView(WizardModule gui, int stepNumber) {
    
    this.gui = gui;
    this.stepNumber = stepNumber;

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
  

  /*=======================================================================
   * Screen Factory
   *======================================================================*/
  
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
    this.initScreen();
    this.setVisible(true);
  }
  
  public void hide() {
    this.setVisible(false);
  }
  

  /*=======================================================================
   * Framework abstract methods (need to be redefined by user)
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
   * Data storing is to redefine
   * @return
   */
  protected abstract void storeDatas(int toStep);
  
  /**
   * Data field verifying
   * @return
   */
  protected abstract void verifyDatas() throws InvalidWizardViewFieldException;
  
  /**
   * Called before screen displaying
   * @return
   */
  public abstract void initScreen();
  
  
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
  
  /***
   * Constructs a next button with text string
   * @param text
   * @return build button
   */
  protected Button prepareNextButton(String text) {
    return prepareButton(text, stepNumber + 1);
  }
  
  /***
   * Constructs a button with text string and with step target
   * <p>
   * Verify datas only on clik "Next" button
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
        try {
          if(stepNumber < toStep) {
            verifyDatas();
          }
          storeDatas(toStep);
        } catch (InvalidWizardViewFieldException e) {
          gui.displayError(e.getWizardMessage());
        }
      }
    });
    return button;
  }
}
