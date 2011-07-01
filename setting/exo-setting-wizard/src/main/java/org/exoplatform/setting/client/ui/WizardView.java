package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.WizardGui;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
    
/**
 * Represents a Wizard View
 * <p>
 * Each wizard view is a step into Web Application Setup Wizard
 * 
 * @author Clement
 *
 */
public abstract class WizardView extends HorizontalPanel {
  
  protected WizardGui gui;
  
  public WizardView(WizardGui gui, String title, String description) {
    
    this.gui = gui;

    // Construct a dock panel
    DockPanel dock = new DockPanel();
    dock.setStyleName("cw-DockPanel");
    dock.add(buildHeader(title), DockPanel.NORTH);
    dock.add(buildDescription(description), DockPanel.NORTH);
    dock.add(buildToolbar(), DockPanel.SOUTH);
    dock.add(buildContent(), DockPanel.CENTER);

    // Configure Main panel
    setSpacing(5);
    setSize("100%", "500px");
    setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
    setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
    
    // Add constructed view
    add(dock);
  }

  protected Widget buildHeader(String title) {
    Label uiTitle = new Label();
    uiTitle.setText(title);
    uiTitle.setStylePrimaryName("blockHeader");
    return uiTitle;
  }

  protected Widget buildDescription(String description) {
    HTML desc = new HTML(description);
    desc.setStylePrimaryName("blockDescription");
    return desc;
  }

  protected Widget buildToolbar() {
    HorizontalPanel uiToolbar = new HorizontalPanel();
    uiToolbar.setStylePrimaryName("blockAction");
    uiToolbar.setWidth("100%");
    uiToolbar.setHorizontalAlignment(ALIGN_RIGHT);
    uiToolbar.setSpacing(2);
    uiToolbar.add(buildStepToolbar());
    return uiToolbar;
  }
  
  protected Widget buildContent() {
    ScrollPanel uiContent = new ScrollPanel();
    uiContent.setWidget(buildStepContent());
    uiContent.setStylePrimaryName("blockContent");
    uiContent.setSize("600px", "300px");
    return uiContent;
  }
  
  // Methods to redefine
  protected abstract Widget buildStepToolbar();
  protected abstract Widget buildStepContent();
  
}
