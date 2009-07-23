/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.webui.portal;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.PortalProperties;
import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.portal.webui.application.UIApplicationList;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainerList;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UITabPane;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 * thanhtungty@gmail.com Jun 10, 2009
 */
@ComponentConfigs({
	@ComponentConfig(
			template = "app:/groovy/portal/webui/portal/UIPortalComposer.gtmpl",
			events = { 
					@EventConfig(listeners = UIPortalComposer.ViewPropertiesActionListener.class),
					@EventConfig(listeners = UIPortalComposer.AbortActionListener.class),
					@EventConfig(listeners = UIPortalComposer.FinishActionListener.class),
					@EventConfig(listeners = UIPortalComposer.SwitchModeActionListener.class)
			}
	),
	@ComponentConfig(
			id = "UIPortalComposerTab",
			type = UITabPane.class,
			template = "app:/groovy/portal/webui/portal/UIPortalComposerContent.gtmpl",
			events = {@EventConfig(listeners = UIPortalComposer.SelectTabActionListener.class)}
	)
})
public class UIPortalComposer extends UIContainer {
  @Deprecated
	private boolean isBlockEditMode;
  @Deprecated
	private boolean isPortletEditType;

	public UIPortalComposer() throws Exception {
		UITabPane uiTabPane = addChild(UITabPane.class, "UIPortalComposerTab", null);
		uiTabPane.addChild(UIApplicationList.class, null, null).setRendered(true);
		uiTabPane.addChild(UIContainerList.class, null, null);
		uiTabPane.setSelectedTab(1);
		setBlockEditMode(true);
		setPortletEditType(true);
	}

	@Deprecated
	public boolean isBlockEditMode() {
		return this.isBlockEditMode;
	}

	@Deprecated
	public void setBlockEditMode(boolean blockEditMode) {
		this.isBlockEditMode = blockEditMode;
	}

	@Deprecated
	public boolean isPortletEditType() {
		return this.isPortletEditType;
	}

	@Deprecated
	public void setPortletEditType(boolean editType) {
		this.isPortletEditType = editType;
	}

	public void save() throws Exception {
		UIPortal uiPortal = Util.getUIPortal();     
		UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);    

		PortalConfig portalConfig  = PortalDataMapper.toPortal(uiPortal);    
		UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class);     
		configService.update(portalConfig);
		uiPortalApp.getUserPortalConfig().setPortal(portalConfig) ;
		PortalRequestContext prContext = Util.getPortalRequestContext();
		String remoteUser = prContext.getRemoteUser();
		String ownerUser = prContext.getPortalOwner();   
		UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);
		if(userPortalConfig != null) {
			uiPortal.setModifiable(userPortalConfig.getPortalConfig().isModifiable());        
		} else {
			uiPortal.setModifiable(false);
		}
		LocaleConfigService localeConfigService  = uiPortalApp.getApplicationComponent(LocaleConfigService.class) ;
		LocaleConfig localeConfig = localeConfigService.getLocaleConfig(portalConfig.getLocale());
		if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
		//TODO dang.tung - change layout when portal get language from UIPortal (user and browser not support)
		//----------------------------------------------------------------------------------------------------
		String portalAppLanguage = uiPortalApp.getLocale().getLanguage();
		OrganizationService orgService = getApplicationComponent(OrganizationService.class) ;
		UserProfile userProfile = orgService.getUserProfileHandler().findUserProfileByName(remoteUser) ;
		String userLanguage = userProfile.getUserInfoMap().get("user.language");
		String browserLanguage = prContext.getRequest().getLocale().getLanguage();
		if(!portalAppLanguage.equals(userLanguage) && !portalAppLanguage.equals(browserLanguage)) {  
			uiPortalApp.setLocale(localeConfig.getLocale());
			uiPortal.refreshNavigation(localeConfig.getLocale());
		}
		//----------------------------------------------------------------------------------------------------
		uiPortalApp.setSkin(uiPortal.getSkin());
		prContext.refreshResourceBundle();
		SkinService skinService = getApplicationComponent(SkinService.class);
		skinService.invalidatePortalSkinCache(uiPortal.getName(), uiPortal.getSkin());
	}

	public void processRender(WebuiRequestContext context) throws Exception {
		super.processRender(context);
		UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
		int portalMode = uiPortalApp.getEditMode();
		if(portalMode == UIPortalApplication.NORMAL_MODE) return;
		if(portalMode % 2 != 0) Util.showComponentLayoutMode(UIPortlet.class);
		else Util.showComponentEditInViewMode(UIPortlet.class);
	}

	static public class ViewPropertiesActionListener extends EventListener<UIPortalComposer> {

		public void execute(Event<UIPortalComposer> event) throws Exception {
			UIPortal uiPortal = Util.getUIPortal();
			UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);

			UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
			uiMaskWS.createUIComponent(UIPortalForm.class, null, "UIPortalForm");
			uiMaskWS.setWindowSize(700, -1);
			event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
		}
	}

	static public class AbortActionListener extends EventListener<UIPortalComposer> {

		public void execute(Event<UIPortalComposer> event) throws Exception {
			UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
			UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);

			PortalRequestContext prContext = Util.getPortalRequestContext();  
			UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);
			configService.update(uiPortalApp.getUserPortalConfig().getPortalConfig());
			uiPortalApp.setEditMode(UIPortalApplication.NORMAL_MODE) ;

			String remoteUser = prContext.getRemoteUser();
			String ownerUser = prContext.getPortalOwner();   
			UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);

			if(userPortalConfig == null){
				HttpServletRequest request = prContext.getRequest() ;        
				String portalName = URLEncoder.encode(Util.getUIPortal().getName(),"UTF-8") ;        
				String redirect = request.getContextPath() + "/public/" + portalName + "/" ;
				prContext.getResponse().sendRedirect(redirect) ;      
			}

			UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
			PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);

			UIPortal oldUIPortal = uiWorkingWS.getChild(UIPortal.class);
			uiWorkingWS.setBackupUIPortal(oldUIPortal);
			uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
			uiWorkingWS.setRenderedChild(UIPortal.class) ;  
			PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, 
					PageNodeEvent.CHANGE_PAGE_NODE, 
					(uiPortal.getSelectedNode() != null ? uiPortal.getSelectedNode().getUri() : null)) ;
			uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;  
		}

	}

	static public class FinishActionListener extends EventListener<UIPortalComposer> {

		public void execute(Event<UIPortalComposer> event) throws Exception {
			UIPortalComposer uiComposer = event.getSource();   
			uiComposer.save();

			PortalRequestContext prContext = Util.getPortalRequestContext();
			UserPortalConfigService configService = uiComposer.getApplicationComponent(UserPortalConfigService.class);
			UserPortalConfig userPortalConfig = configService.getUserPortalConfig(prContext.getPortalOwner(), prContext.getRemoteUser());
			if(userPortalConfig == null){
				HttpServletRequest request = prContext.getRequest() ;        
				String portalName = URLEncoder.encode(Util.getUIPortal().getName(),"UTF-8") ;        
				String redirect = request.getContextPath() + "/public/" + portalName + "/" ;
				prContext.getResponse().sendRedirect(redirect) ;        
			}

			UIPortal uiPortal = Util.getUIPortal();
			UIPortalApplication uiPortalApp = Util.getUIPortalApplication() ;
			if(PortalProperties.SESSION_ALWAYS.equals(uiPortal.getSessionAlive())) uiPortalApp.setSessionOpen(true) ;
			else uiPortalApp.setSessionOpen(false) ;
			uiPortalApp.setEditMode(UIPortalApplication.NORMAL_MODE) ;
			PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, 
					PageNodeEvent.CHANGE_PAGE_NODE, 
					(uiPortal.getSelectedNode() != null ? uiPortal.getSelectedNode().getUri() : null)) ;
			uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;      
		}

	}

	static public class SelectTabActionListener extends UITabPane.SelectTabActionListener {
		public void execute(Event<UITabPane> event) throws Exception {
			super.execute(event);
			UITabPane uiTabPane = event.getSource();
			UIComponent uiComponent = uiTabPane.getChildById(uiTabPane.getSelectedTabId());
			UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
			int portalMode = uiPortalApp.getEditMode();
			if(portalMode == UIPortalApplication.NORMAL_MODE) return;
			if(portalMode > 2) portalMode -= 2;
			else portalMode += 2;
			uiPortalApp.setEditMode(portalMode);
			if(uiComponent instanceof UIApplicationList) {
				Util.showComponentLayoutMode(UIPortlet.class);
			} else if(uiComponent instanceof UIContainerList) {
				Util.showComponentLayoutMode(org.exoplatform.portal.webui.container.UIContainer.class);
			}
			event.getRequestContext().addUIComponentToUpdateByAjax(
					Util.getUIPortalApplication().getChildById(UIPortalApplication.UI_WORKING_WS_ID));
		}
	}

	static public class SwitchModeActionListener extends EventListener<UIPortalComposer> {
		public void execute(Event<UIPortalComposer> event) throws Exception {
		  UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
		  int portalMode = uiPortalApp.getEditMode();
		  if(portalMode == UIPortalApplication.NORMAL_MODE) return;
		  if(portalMode%2 == 0) --portalMode;
		  else ++portalMode;
		  uiPortalApp.setEditMode(portalMode);
		  event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalApp.getChild(UIWorkingWorkspace.class));
		  Util.getPortalRequestContext().setFullRender(true);
		}
	}
}