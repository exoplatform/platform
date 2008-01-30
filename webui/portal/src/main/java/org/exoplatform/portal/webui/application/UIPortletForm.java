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
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.skin.SkinService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.services.portletcontainer.PCConstants;
import org.exoplatform.services.portletcontainer.helper.PortletWindowInternal;
import org.exoplatform.services.portletcontainer.pci.ActionInput;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.Input;
import org.exoplatform.services.portletcontainer.pci.model.ExoPortletPreferences;
import org.exoplatform.services.portletcontainer.pci.model.Portlet;
import org.exoplatform.services.portletcontainer.persistence.PortletPreferencesPersister;
import org.exoplatform.services.portletcontainer.plugins.pc.PortletApplicationsHolder;
import org.exoplatform.services.portletcontainer.plugins.pc.portletAPIImp.PortletPreferencesImp;
import org.exoplatform.services.portletcontainer.plugins.pc.portletAPIImp.persistenceImp.PersistenceManager;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.UIFormInputIconSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 8, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormTabPane.gtmpl",
    events = {
      @EventConfig(listeners = UIPortletForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)   
public class UIPortletForm extends UIFormTabPane {	
  
	private UIPortlet uiPortlet_ ;
  private UIComponent backComponent_ ;
  final static private String FIELD_THEME = "Theme" ; 
  final static private String FIELD_PORTLET_PREF = "PortletPref" ;
  
  @SuppressWarnings("unchecked")
  public UIPortletForm() throws Exception {//InitParams initParams
  	super("UIPortletForm");
  	UIFormInputSet uiPortletPrefSet = new UIFormInputSet(FIELD_PORTLET_PREF) ;
  	addUIFormInput(uiPortletPrefSet) ;
    UIFormInputSet uiSettingSet = new UIFormInputSet("PortletSetting") ;
  	uiSettingSet.
      addUIFormInput(new UIFormStringInput("id", "id", null).
                     addValidator(EmptyFieldValidator.class).setEditable(false)).
      addUIFormInput(new UIFormStringInput("windowId", "windowId", null).setEditable(false)).
    	addUIFormInput(new UIFormStringInput("title", "title", null)).
  		addUIFormInput(new UIFormStringInput("width", "width", null)).
  		addUIFormInput(new UIFormStringInput("height", "height", null)).
  		addUIFormInput(new UIFormCheckBoxInput("showInfoBar", "showInfoBar", false)).
  		addUIFormInput(new UIFormCheckBoxInput("showPortletMode", "showPortletMode", false)).
    	addUIFormInput(new UIFormCheckBoxInput("showWindowState", "showWindowState", false)).
      addUIFormInput(new UIFormTextAreaInput("description", "description", null));
    addUIFormInput(uiSettingSet);    
    setSelectedTab(uiSettingSet.getId()) ;
    UIFormInputIconSelector uiIconSelector = new UIFormInputIconSelector("Icon", "icon") ;
    addUIFormInput(uiIconSelector) ;
    
    UIFormInputThemeSelector uiThemeSelector = new UIFormInputThemeSelector(FIELD_THEME, null) ;
    SkinService skinService = getApplicationComponent(SkinService.class) ;
    uiThemeSelector.getChild(UIItemThemeSelector.class).setValues(skinService.getPortletThemes()) ;
    addUIFormInput(uiThemeSelector) ;
  }
  
  public UIComponent getBackComponent() { return backComponent_ ; }
  public void setBackComponent(UIComponent uiComp) throws Exception {
    backComponent_ = uiComp;
  }
  
  public UIPortlet getUIPortlet() { return uiPortlet_; }
  
  @SuppressWarnings("unchecked")
  public void setValues(UIPortlet uiPortlet) throws Exception {
  	uiPortlet_ = uiPortlet;
    invokeGetBindingBean(uiPortlet_) ;
    String icon = uiPortlet.getIcon();
    
    if( icon == null || icon.length() < 0) icon = "PortletIcon" ;
    getChild(UIFormInputIconSelector.class).setSelectedIcon(icon);
    getChild(UIFormInputThemeSelector.class).getChild(UIItemThemeSelector.class).setSelectedTheme(uiPortlet.getSuitedTheme(null)) ;
    
    ExoWindowID windowID = uiPortlet.getExoWindowID();
    Input input = new Input() ;
    input.setInternalWindowID(windowID) ;
    PortletApplicationsHolder holder = getApplicationComponent(PortletApplicationsHolder.class) ;
    Portlet pDatas = holder.getPortletMetaData(windowID.getPortletApplicationName(), windowID.getPortletName());
    ExoPortletPreferences defaultPrefs = pDatas.getPortletPreferences();
    PersistenceManager manager = getApplicationComponent(PersistenceManager.class) ;
    PortletWindowInternal windowInfos = manager.getWindow(input, defaultPrefs);
    PortletPreferences preferences = windowInfos.getPreferences();
    buidPreferenceInputs(preferences) ;
  }

  private void savePreferences() throws Exception {
    UIFormInputSet uiPortletPrefSet = getChildById(FIELD_PORTLET_PREF) ;
    List<UIFormStringInput> uiFormInputs = new ArrayList<UIFormStringInput>(3) ;
    uiPortletPrefSet.findComponentOfType(uiFormInputs, UIFormStringInput.class) ;
    if(uiFormInputs.size() < 1) return ;
    ExoWindowID windowID = uiPortlet_.getExoWindowID();
    Input input = new Input() ;
    input.setInternalWindowID(windowID) ;
    PortletApplicationsHolder holder = getApplicationComponent(PortletApplicationsHolder.class) ;
    Portlet pDatas = holder.getPortletMetaData(windowID.getPortletApplicationName(), windowID.getPortletName());
    ExoPortletPreferences defaultPrefs = pDatas.getPortletPreferences();
    PersistenceManager manager = getApplicationComponent(PersistenceManager.class) ;
    PortletWindowInternal windowInfos = manager.getWindow(input, defaultPrefs);
    PortletPreferencesImp preferences = (PortletPreferencesImp) windowInfos.getPreferences();
    for(UIFormStringInput ele : uiFormInputs) {
      preferences.setValue(ele.getName(), ele.getValue()) ;
    }
    preferences.setMethodCalledIsAction(PCConstants.actionInt) ;
    preferences.store() ;
  }
  
  private void buidPreferenceInputs(PortletPreferences preferences) {
    UIFormInputSet uiPortletPrefSet = getChildById(FIELD_PORTLET_PREF) ;
    uiPortletPrefSet.getChildren().clear() ;
    Enumeration<String> prefNames = preferences.getNames() ;
    if(!prefNames.hasMoreElements()) {
      uiPortletPrefSet.setRendered(false) ;
      return ;
    }
    while(prefNames.hasMoreElements()) {
      String name = prefNames.nextElement() ;
      if(!preferences.isReadOnly(name)) {
        uiPortletPrefSet.addUIFormInput(new UIFormStringInput(name, null, preferences.getValue(name, "value"))) ;
      }
    }    
  }
  
  
	static public class SaveActionListener extends EventListener<UIPortletForm> {
    public void execute(Event<UIPortletForm> event) throws Exception {      
      UIPortletForm uiPortletForm = event.getSource() ;
      UIPortlet uiPortlet = uiPortletForm.getUIPortlet() ;
      UIFormInputIconSelector uiIconSelector = uiPortletForm.getChild(UIFormInputIconSelector.class);
      uiPortletForm.invokeSetBindingBean(uiPortlet) ;
      if(uiIconSelector.getSelectedIcon().equals("Default")) uiPortlet.setIcon("PortletIcon") ;
      else uiPortlet.setIcon(uiIconSelector.getSelectedIcon());
      UIFormInputThemeSelector uiThemeSelector = uiPortletForm.getChild(UIFormInputThemeSelector.class) ;
      uiPortlet.putSuitedTheme(null, uiThemeSelector.getChild(UIItemThemeSelector.class).getSelectedTheme()) ;
      uiPortletForm.savePreferences() ;
      UIMaskWorkspace uiMaskWorkspace = uiPortletForm.getParent();
      uiMaskWorkspace.setUIComponent(null);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      pcontext.addUIComponentToUpdateByAjax(uiMaskWorkspace);
      UIPortalApplication uiPortalApp = uiPortlet.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      Util.showComponentLayoutMode(UIPortlet.class);  
    }
  }
  
}
