/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import org.exoplatform.services.log.Log;
import org.exoplatform.commons.utils.ExceptionUtil;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.Input;
import org.exoplatform.services.portletcontainer.pci.RenderInput;
import org.exoplatform.services.portletcontainer.pci.RenderOutput;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputIconSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.ExpressionValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
import org.exoplatform.webui.organization.UIListPermissionSelector;
import org.exoplatform.webui.organization.UIListPermissionSelector.EmptyIteratorValidator;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 8, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
                   lifecycle = UIFormLifecycle.class,
                   template = "system:/groovy/portal/webui/portal/UIPortletForm.gtmpl",
                   events = {
                     @EventConfig(listeners = UIPortletForm.SaveActionListener.class),
                     @EventConfig(listeners = UIPortletForm.CloseActionListener.class, phase = Phase.DECODE)
                   }
  ),
  @ComponentConfig(
                   id = "PortletPermission",
                   type = UIFormInputSet.class,
                   lifecycle = UIContainerLifecycle.class
  )
})
public class UIPortletForm extends UIFormTabPane {	
	private static Log log = ExoLogger.getLogger("portal:UIPortletForm");
	
  private UIPortlet uiPortlet_;
  private UIComponent backComponent_;
  private static final String FIELD_THEME = "Theme"; 
  private static final String FIELD_PORTLET_PREF = "PortletPref";
  
  @SuppressWarnings("unchecked")
  public UIPortletForm() throws Exception {
  	super("UIPortletForm");
  	UIFormInputSet uiPortletPrefSet = new UIFormInputSet(FIELD_PORTLET_PREF).setRendered(false);
  	addUIFormInput(uiPortletPrefSet);
    UIFormInputSet uiSettingSet = new UIFormInputSet("PortletSetting");
  	uiSettingSet.
      addUIFormInput(new UIFormStringInput("id", "id", null).
                     addValidator(MandatoryValidator.class).setEditable(false)).
      addUIFormInput(new UIFormStringInput("windowId", "windowId", null).setEditable(false)).
    	addUIFormInput(new UIFormStringInput("title", "title", null).
                     addValidator(StringLengthValidator.class, 3, 60)).
  		addUIFormInput(new UIFormStringInput("width", "width", null).
                     addValidator(ExpressionValidator.class, "(^([1-9]\\d*)px$)?",
                         "UIPortletForm.msg.InvalidWidthHeight")).
  		addUIFormInput(new UIFormStringInput("height", "height", null).
  		               addValidator(ExpressionValidator.class, "(^([1-9]\\d*)px$)?", 
                         "UIPortletForm.msg.InvalidWidthHeight")).
  		addUIFormInput(new UIFormCheckBoxInput("showInfoBar", "showInfoBar", false)).
  		addUIFormInput(new UIFormCheckBoxInput("showPortletMode", "showPortletMode", false)).
    	addUIFormInput(new UIFormCheckBoxInput("showWindowState", "showWindowState", false)).
      addUIFormInput(new UIFormTextAreaInput("description", "description", null).
                     addValidator(StringLengthValidator.class, 0, 255));
    addUIFormInput(uiSettingSet);    
    UIFormInputIconSelector uiIconSelector = new UIFormInputIconSelector("Icon", "icon");
    addUIFormInput(uiIconSelector);
    
    UIFormInputThemeSelector uiThemeSelector = new UIFormInputThemeSelector(FIELD_THEME, null);
    SkinService skinService = getApplicationComponent(SkinService.class);
    uiThemeSelector.getChild(UIItemThemeSelector.class).setValues(skinService.getPortletThemes());
    addUIFormInput(uiThemeSelector);
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIAccessPermission", "accessPermissions");
    uiListPermissionSelector.addValidator(EmptyIteratorValidator.class) ;
    UIFormInputSet uiPermissionSet = createUIComponent(UIFormInputSet.class, "PortletPermission", null);
    uiPermissionSet.addChild(uiListPermissionSelector);
    addUIFormInput(uiPermissionSet);
  }
  
  public UIComponent getBackComponent() { return backComponent_; }
  public void setBackComponent(final UIComponent uiComp) throws Exception {
    backComponent_ = uiComp;
  }
  
  public UIPortlet getUIPortlet() { return uiPortlet_; }

  public boolean hasEditMode() {
    return uiPortlet_.getSupportModes().contains("edit");
  }
  
  public String getEditModeContent() {
    StringBuilder portletContent = new StringBuilder();
    try {
      PortalRequestContext prcontext = (PortalRequestContext) WebuiRequestContext.getCurrentInstance();
      prcontext.setFullRender(true);
      PortletContainerService portletContainer = uiPortlet_.getApplicationComponent(PortletContainerService.class);
      RenderInput input = new RenderInput();
      String baseUrl = new StringBuilder(prcontext.getRequestURI()).append(
          "?" + PortalRequestContext.UI_COMPONENT_ID).append("=").append(
          uiPortlet_.getId()).toString();
      input.setBaseURL(baseUrl);
      input.setUserAttributes(new HashMap<String, String>());
      
      input.setPortletMode(PortletMode.EDIT);
      input.setWindowState(uiPortlet_.getCurrentWindowState());
      input.setMarkup("text/html");
      input.setTitle(uiPortlet_.getTitle());
      input.setInternalWindowID(uiPortlet_.getExoWindowID());
      input.setRenderParameters(getRenderParameterMap(uiPortlet_));
      input.setPublicParamNames(uiPortlet_.getPublicRenderParamNames());
      RenderOutput output = portletContainer.render(prcontext.getRequest(), prcontext.getResponse(), input);
      portletContent.setLength(0);
      portletContent.append(output.getContent());
    } catch (Throwable ex) {
      portletContent.append("This portlet encountered an error and could not be displayed.");
      log.error("The portlet " + uiPortlet_.getName() + " could not be loaded. Check if properly deployed.", ExceptionUtil.getRootCause(ex));
    }
    return portletContent.toString();
  }
  
  public void setValues(final UIPortlet uiPortlet) throws Exception {
  	uiPortlet_ = uiPortlet;
    invokeGetBindingBean(uiPortlet_);
    String icon = uiPortlet.getIcon();
    
    if (icon == null || icon.length() < 0) { icon = "PortletIcon"; }
    getChild(UIFormInputIconSelector.class).setSelectedIcon(icon);
    getChild(UIFormInputThemeSelector.class).getChild(UIItemThemeSelector.class).setSelectedTheme(uiPortlet.getSuitedTheme(null));
    WebuiRequestContext contextres = WebuiRequestContext.getCurrentInstance();
    ResourceBundle res = contextres.getApplicationResourceBundle();
    if (hasEditMode()) {
      uiPortlet.setCurrentPortletMode(PortletMode.EDIT);
    } else {
      ExoWindowID windowID = uiPortlet.getExoWindowID();
      Input input = new Input();
      input.setInternalWindowID(windowID);
      PortletContainerService pcServ = getApplicationComponent(PortletContainerService.class);
      PortletPreferences preferences = pcServ.getPortletPreferences(input);
      if (preferences != null) {
        UIFormInputSet uiPortletPrefSet = getChildById(FIELD_PORTLET_PREF);
        uiPortletPrefSet.getChildren().clear();
        Enumeration<String> prefNames = preferences.getNames();
  
        while (prefNames.hasMoreElements()) {
          String name = prefNames.nextElement();
          if (!preferences.isReadOnly(name)) {
            UIFormStringInput templateStringInput = new UIFormStringInput(name, null, preferences.getValue(name, "value"));
            templateStringInput.setLabel(res.getString("UIPortletForm.tab.label.Template"));
            uiPortletPrefSet.addUIFormInput(templateStringInput);
          }
        }
        if (uiPortletPrefSet.getChildren().size() > 0) {
          uiPortletPrefSet.setRendered(true);
          setSelectedTab(FIELD_PORTLET_PREF);
          return;
        }
      }
      setSelectedTab("PortletSetting");
    } 
  }
  
  private void savePreferences() throws Exception {
    UIFormInputSet uiPortletPrefSet = getChildById(FIELD_PORTLET_PREF);
    List<UIFormStringInput> uiFormInputs = new ArrayList<UIFormStringInput>(3);
    uiPortletPrefSet.findComponentOfType(uiFormInputs, UIFormStringInput.class);
    if (uiFormInputs.size() < 1) { return; }
    Input input = new Input();
    input.setInternalWindowID(uiPortlet_.getExoWindowID());
    Map<String, String> pers = new HashMap<String, String>();
    for(UIFormStringInput ele : uiFormInputs) { pers.put(ele.getName(), ele.getValue()); }
    PortletContainerService preferences = getApplicationComponent(PortletContainerService.class);
    preferences.setPortletPreference(input, pers);
  }
  
  
  private Map<String, String[]> getRenderParameterMap(UIPortlet uiPortlet) {
    Map<String, String[]> renderParams = uiPortlet.getRenderParametersMap();

    if (renderParams == null) {
      renderParams = new HashMap<String, String[]>();
      uiPortlet.setRenderParametersMap(renderParams);
    }
    
    /*
     *  handle public params to only get the one supported by the targeted portlet
     */ 
    Map<String, String[]> allParams = new HashMap<String, String[]>(renderParams);
    allParams.putAll(uiPortlet.getPublicParameters());
    
    return allParams;
  }
  
	static public class SaveActionListener extends EventListener<UIPortletForm> {
    public void execute(final Event<UIPortletForm> event) throws Exception {
      UIPortletForm uiPortletForm = event.getSource();
      UIPortlet uiPortlet = uiPortletForm.getUIPortlet();
      UIFormInputIconSelector uiIconSelector = uiPortletForm.getChild(UIFormInputIconSelector.class);
      uiPortletForm.invokeSetBindingBean(uiPortlet);
      if (uiIconSelector.getSelectedIcon().equals("Default")) { 
        uiPortlet.setIcon("PortletIcon"); 
      } else { uiPortlet.setIcon(uiIconSelector.getSelectedIcon()); }
      UIFormInputThemeSelector uiThemeSelector = uiPortletForm.getChild(UIFormInputThemeSelector.class);
      uiPortlet.putSuitedTheme(null, uiThemeSelector.getChild(UIItemThemeSelector.class).getSelectedTheme());
      uiPortletForm.savePreferences();
      UIMaskWorkspace uiMaskWorkspace = uiPortletForm.getParent();
      uiMaskWorkspace.setUIComponent(null);
      if (uiPortletForm.hasEditMode()) {
        uiPortlet.setCurrentPortletMode(PortletMode.VIEW);
      }
      
      String width = uiPortletForm.getUIStringInput("width").getValue();
      if (width == null || width.length() == 0) { 
        uiPortlet.setWidth(null); 
      } else { uiPortlet.setWidth(width); }
      
      String height = uiPortletForm.getUIStringInput("height").getValue();
      if (height == null || height.length() == 0) { 
        uiPortlet.setHeight(null); 
      } else { uiPortlet.setHeight(height); }
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      pcontext.addUIComponentToUpdateByAjax(uiMaskWorkspace);
      UIPortalApplication uiPortalApp = uiPortlet.getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      Util.showComponentLayoutMode(UIPortlet.class);  
    }
  }
	
  public static class CloseActionListener extends EventListener<UIPortletForm> {
    public void execute(final Event<UIPortletForm> event) throws Exception {
      UIPortletForm uiPortletForm = event.getSource();
      UIPortlet uiPortlet = uiPortletForm.getUIPortlet();
      if (uiPortletForm.hasEditMode()) { uiPortlet.setCurrentPortletMode(PortletMode.VIEW); }
      UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      //add by Pham Dinh Tan
      UIMaskWorkspace uiMaskWorkspace = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
      uiMaskWorkspace.setUIComponent(null);
      uiMaskWorkspace.setWindowSize(-1, -1);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWorkspace);
      pcontext.setFullRender(true);
      Util.showComponentLayoutMode(UIPortlet.class);  
    }
  }
  
}