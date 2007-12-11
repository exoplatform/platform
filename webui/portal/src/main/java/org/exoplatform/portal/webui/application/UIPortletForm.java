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

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.skin.SkinService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
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
  
  @SuppressWarnings("unchecked")
  public UIPortletForm() throws Exception {//InitParams initParams
  	super("UIPortletForm");
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
    
    /*TODO: modify tungnd to fixed icon of portlets*/
    //String[] arrayWindowId = uiPortlet.getWindowId().split("/");
    //String portletName = arrayWindowId[arrayWindowId.length-2] ;
    //String defaultIcon = portletName.substring(0, portletName.lastIndexOf("Portlet")) ;
    /*end modify*/
    
    if( icon == null || icon.length() < 0) icon = "PortletIcon" ;
    getChild(UIFormInputIconSelector.class).setSelectedIcon(icon);
    getChild(UIFormInputThemeSelector.class).getChild(UIItemThemeSelector.class).setSelectedTheme(uiPortlet.getSuitedTheme(null)) ;
  }
  
	static public class SaveActionListener extends EventListener<UIPortletForm> {
    public void execute(Event<UIPortletForm> event) throws Exception {      
      UIPortletForm uiPortletForm = event.getSource() ;
      UIPortlet uiPortlet = uiPortletForm.getUIPortlet() ;
      UIFormInputIconSelector uiIconSelector = uiPortletForm.getChild(UIFormInputIconSelector.class);
      uiPortlet.setIcon(uiIconSelector.getSelectedIcon());
      uiPortletForm.invokeSetBindingBean(uiPortlet) ;
      UIFormInputThemeSelector uiThemeSelector = uiPortletForm.getChild(UIFormInputThemeSelector.class) ;
      uiPortlet.putSuitedTheme(null, uiThemeSelector.getChild(UIItemThemeSelector.class).getSelectedTheme()) ;
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
