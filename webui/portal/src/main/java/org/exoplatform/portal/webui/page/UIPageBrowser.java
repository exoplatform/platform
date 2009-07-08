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
package org.exoplatform.portal.webui.page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.jcr.RepositoryException;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UISearch;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputItemSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UISearchForm;

@ComponentConfigs( {
		@ComponentConfig(template = "system:/groovy/portal/webui/page/UIPageBrowser.gtmpl", events = {
				@EventConfig(listeners = UIPageBrowser.DeleteActionListener.class, confirm = "UIPageBrowse.deletePage"),
				@EventConfig(listeners = UIPageBrowser.EditInfoActionListener.class),
				@EventConfig(listeners = UIPageBrowser.PreviewActionListener.class),
				@EventConfig(listeners = UIPageBrowser.AddNewActionListener.class) }),
		@ComponentConfig(id = "UIBrowserPageForm", type = UIPageForm.class, lifecycle = UIFormLifecycle.class, template = "system:/groovy/webui/form/UIFormTabPane.gtmpl", events = {
				@EventConfig(listeners = UIPageBrowser.SavePageActionListener.class, name = "Save"),
				@EventConfig(listeners = UIPageForm.ChangeOwnerTypeActionListener.class, phase = Phase.DECODE),
				@EventConfig(listeners = UIPageForm.ChangeOwnerIdActionListener.class, phase = Phase.DECODE),
				@EventConfig(listeners = UIPageForm.SelectMembershipActionListener.class, phase = Phase.DECODE),
				@EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE) }, initParams = @ParamConfig(name = "PageTemplate", value = "system:/WEB-INF/conf/uiconf/portal/webui/page/PageTemplate.groovy")),
		@ComponentConfig(type = UIFormInputSet.class, id = "PermissionSetting", template = "system:/groovy/webui/core/UITabSelector.gtmpl", events = {@EventConfig(listeners = UIFormInputSet.SelectComponentActionListener.class)}) })

public class UIPageBrowser extends UISearch {

	public static String[] BEAN_FIELD = { "pageId", "title", "accessPermissions", "editPermission" };

	public static String[] ACTIONS = { "Preview", "EditInfo", "Delete" };

	private boolean showAddNewPage = false;

	protected String pageSelectedId_;

	private static List<SelectItemOption<String>> OPTIONS = new ArrayList<SelectItemOption<String>>(3);

  static {
    WebuiRequestContext contextui = WebuiRequestContext
        .getCurrentInstance();
    ResourceBundle res = contextui.getApplicationResourceBundle();
    OPTIONS.add(new SelectItemOption<String>(res.getString("UIPageSearch.label.option.ownerType"), "ownerType"));
    OPTIONS.add(new SelectItemOption<String>(res.getString("UIPageSearch.label.option.ownerId"), "ownerId"));
    OPTIONS.add(new SelectItemOption<String>(res.getString("UIPageSearch.label.option.title"), "title"));
  }
  
	private Query<Page> lastQuery_;

	public UIPageBrowser() throws Exception {
		super(OPTIONS);
		//getChild(UISearchForm.class).setId("UIPageSearch");
		getChild(UISearchForm.class).setId("UIPageSearch");
		UIGrid uiGrid = addChild(UIGrid.class, null, null);		
		uiGrid.configure("pageId", BEAN_FIELD, ACTIONS);		
		UIPageIterator uiIterator = uiGrid.getUIPageIterator();
		addChild(uiIterator);
		uiIterator.setId("UIBrowserIterator" + hashCode());
		uiIterator.setRendered(false);
		defaultValue(null);
	}

	public Query<Page> getLastQuery() {
		return lastQuery_;
	}

	public void defaultValue(Query<Page> query) throws Exception {
		lastQuery_ = query;
		UIGrid uiGrid = findFirstComponentOfType(UIGrid.class);
		UIPageIterator pageIterator = uiGrid.getUIPageIterator();
		DataStorage service = getApplicationComponent(DataStorage.class);
		if (lastQuery_ == null){
			lastQuery_ = new Query<Page>(null, null, null, null, Page.class);
		}
		LazyPageList pagelist = null;
		try {
			pagelist = service.find(lastQuery_, new Comparator<Object>() {
				public int compare(Object obj1, Object obj2) {
					Page page1 = (Page) obj1;
					Page page2 = (Page) obj2;
					return page1.getName().compareTo(page2.getName());
				}
			});
			pagelist.setPageSize(10);
			pageIterator.setPageList(pagelist);
		} catch (RepositoryException e) {
		  pageIterator.setPageList(new ObjectPageList(new ArrayList<String>(), 0));
			UIApplication uiApp = Util.getPortalRequestContext().getUIApplication();
			uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty",	null));
			Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
			return;
		}
		if (pageIterator.getAvailable() > 0) return;
		UIApplication uiApp = Util.getPortalRequestContext().getUIApplication();
		uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null));
		Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
	}

	public void quickSearch(UIFormInputSet quickSearchInput) throws Exception {
		UIFormStringInput input = (UIFormStringInput) quickSearchInput.getChild(0);
		UIFormSelectBox select = (UIFormSelectBox) quickSearchInput.getChild(1);
		String value = input.getValue();
		String selectBoxValue = select.getValue();
		Query<Page> query = new Query<Page>(null, null, null, null, Page.class);
		if (selectBoxValue.equals("title")) query.setTitle(value) ;
		else if (selectBoxValue.equals("ownerType"))	query.setOwnerType(value);
		else if (selectBoxValue.equals("ownerId"))	query.setOwnerId(value);
		query.setName(null);
		lastQuery_ = query;
		defaultValue(lastQuery_);
		if (this.<UIComponent> getParent() instanceof UIPopupWindow) {
			UIPopupWindow popupWindow = getParent();
			popupWindow.setShow(true);
		}
	}

	public boolean isShowAddNewPage() {
		return showAddNewPage;
	}

	public void setShowAddNewPage(boolean showAddNewPage) {
		this.showAddNewPage = showAddNewPage;
	}

	public void processDecode(WebuiRequestContext context) throws Exception {
		super.processDecode(context);
		UIForm uiForm = getAncestorOfType(UIForm.class);
		String action = null;
		if (uiForm != null) {
			action = uiForm.getSubmitAction();
		} else {
			action = context.getRequestParameter(UIForm.ACTION);
		}
		if (action == null)
			return;
		Event<UIComponent> event = createEvent(action, Event.Phase.PROCESS,	context);
		if (event != null) event.broadcast();
	}

	public void advancedSearch(UIFormInputSet advancedSearchInput)
			throws Exception {
	}

	void reset() throws Exception {
		UIPageIterator uiPageIterator = getChild(UIPageIterator.class);
		int currentPage = uiPageIterator.getCurrentPage();
		defaultValue(null);
		while (currentPage > uiPageIterator.getAvailablePage())	currentPage--;
		if (currentPage > 0) uiPageIterator.setCurrentPage(currentPage);
	}

	static public class DeleteActionListener extends EventListener<UIPageBrowser> {
		public void execute(Event<UIPageBrowser> event) throws Exception {
			UIPageBrowser uiPageBrowser = event.getSource();
			PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
			String id = pcontext.getRequestParameter(OBJECTID);
			UserPortalConfigService service = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class);

			UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);
			if(service.getPage(id) == null){               
			  uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.PageNotExist", new String[]{id},1)) ;;
			  pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());    
			  return;
			}
			Page page = service.getPage(id, pcontext.getRemoteUser());
      if(page == null || !page.isModifiable()) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.delete.NotDelete", new String[]{id}, 1)) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      } 
      
      UIPageIterator pageIterator = uiPageBrowser.getChild(UIGrid.class).getUIPageIterator() ;
      int currentPage = pageIterator.getCurrentPage() ;
			service.remove(page);
			uiPageBrowser.defaultValue(uiPageBrowser.getLastQuery());
			while(currentPage > pageIterator.getAvailablePage()) currentPage-- ;
			pageIterator.setCurrentPage(currentPage) ;
			
			pcontext.addUIComponentToUpdateByAjax(uiPageBrowser);
		}
	}

	static public class EditInfoActionListener extends EventListener<UIPageBrowser> {
		public void execute(Event<UIPageBrowser> event) throws Exception {
			UIPageBrowser uiPageBrowser = event.getSource();
			UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);
			uiPortalApp.setEditting(true);
			PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
			String id = pcontext.getRequestParameter(OBJECTID);
			UserPortalConfigService service = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class);
			if(service.getPage(id) == null){               
			  uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.PageNotExist", new String[]{id},1)) ;;
			  pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());    
			  return;
			}
			Page page = service.getPage(id, pcontext.getRemoteUser()) ;    
      if(page == null || !page.isModifiable()){
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.edit.NotEditPage", new String[]{id}, 1)) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }

			UIPage uiPage = Util.toUIPage(page, uiPageBrowser);
			UIPageBody uiPageBody = uiPortalApp.findFirstComponentOfType(UIPageBody.class);
			if (uiPageBody.getUIComponent() != null)
				uiPageBody.setUIComponent(null);

			if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
				UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
				UIPageForm uiPageForm = uiMaskWS.createUIComponent(
						UIPageForm.class, "UIBrowserPageForm", "UIPageForm");
				uiPageForm.setValues(uiPage);
				uiMaskWS.setUIComponent(uiPageForm);
				uiMaskWS.setShow(true);
				pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
				return;
			}

		}
	}

	static public class PreviewActionListener extends EventListener<UIPageBrowser> {
		public void execute(Event<UIPageBrowser> event) throws Exception {
			UIPageBrowser uiPageBrowser = event.getSource();
			PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
			String id = pcontext.getRequestParameter(OBJECTID);
			UserPortalConfigService service = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class);
			UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      if(service.getPage(id) == null){               
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.PageNotExist", new String[]{id},1)) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());    
        return;
      }
      Page page = service.getPage(id, pcontext.getRemoteUser());
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.NotViewPage", new String[]{id}, 1)) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      } 
      if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-Preview", new String[] { page.getName() }));
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
		

			UIPage uiPage = uiPageBrowser.createUIComponent(event.getRequestContext(), UIPage.class, null, null);
			PortalDataMapper.toUIPage(uiPage, page);
			UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
			UIPagePreview uiPagePreview = uiToolPanel.createUIComponent(UIPagePreview.class, "UIPagePreviewWithMessage", null);
			uiPagePreview.setUIComponent(uiPage);
			uiToolPanel.setUIComponent(uiPagePreview);
			uiToolPanel.setShowMaskLayer(true);
			uiToolPanel.setRenderSibbling(UIPortalToolPanel.class);

			UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
			pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
			pcontext.setFullRender(true);
		}
	}

	static public class AddNewActionListener extends EventListener<UIPageBrowser> {
		public void execute(Event<UIPageBrowser> event) throws Exception {
			PortalRequestContext prContext = Util.getPortalRequestContext();
			UIPortalApplication uiApp = event.getSource().getAncestorOfType(
					UIPortalApplication.class);
			UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
			UIPageForm uiPageForm = uiMaskWS.createUIComponent(UIPageForm.class, "UIBrowserPageForm", "UIPageForm");
			uiMaskWS.setUIComponent(uiPageForm);
			uiMaskWS.setShow(true);
			uiPageForm.getUIStringInput("ownerType").setValue(PortalConfig.USER_TYPE);
			uiPageForm.getUIStringInput("ownerId").setValue(prContext.getRemoteUser());
			uiPageForm.removeChildById("PermissionSetting");
			uiPageForm.removeChild(UIFormInputItemSelector.class);
			UIPageTemplateOptions uiTemplateConfig = uiPageForm.createUIComponent(UIPageTemplateOptions.class, null, null);
			uiPageForm.addUIFormInput(uiTemplateConfig);
			prContext.addUIComponentToUpdateByAjax(uiMaskWS);
		}
	}

	static public class SavePageActionListener extends UIPageForm.SaveActionListener {
		public void execute(Event<UIPageForm> event) throws Exception {
			UIPageForm uiPageForm = event.getSource();
			UIPortalApplication uiPortalApp = uiPageForm.getAncestorOfType(UIPortalApplication.class);
			PortalRequestContext pcontext = Util.getPortalRequestContext();
			UIPage uiPage = uiPageForm.getUIPage();
			Page page = new Page();
			uiPageForm.invokeSetBindingBean(page);
			UserPortalConfigService configService = uiPageForm.getApplicationComponent(UserPortalConfigService.class);
			//create new page
			if (uiPage == null) {
				DataStorage dataStorage = uiPageForm.getApplicationComponent(DataStorage.class);
				Page existPage = dataStorage.getPage(page.getPageId());
				if (existPage != null) {
					uiPortalApp.addMessage(new ApplicationMessage("UIPageForm.msg.sameName", null));
					pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
					return;
				}
				page.setCreator(pcontext.getRemoteUser());
				page.setModifiable(true);
				if (page.getChildren() == null)
					page.setChildren(new ArrayList<Object>());
				configService.create(page);
				postSave(uiPortalApp, pcontext);
				return;
			}

			page.setOwnerType(uiPage.getOwnerType());
			List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
			findAllPortlet(uiPortlets, uiPage);
			ArrayList<Object> applications = new ArrayList<Object>();
			for (UIPortlet uiPortlet : uiPortlets) {
				applications.add(PortalDataMapper.toPortletModel(uiPortlet));
			}

			if (Page.DESKTOP_PAGE.equals(uiPage.getFactoryId()) && !Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
				page.setShowMaxWindow(false);
				uiPage.getChildren().clear();
				page.setChildren(applications);
        page.setModifier(pcontext.getRemoteUser());
				PortalDataMapper.toUIPage(uiPage, page);
//				if (page.getTemplate() == null) page.setTemplate(uiPage.getTemplate());
				if (page.getChildren() == null)	page.setChildren(new ArrayList<Object>());
				configService.update(page);
				postSave(uiPortalApp, pcontext);
				return;
			}

			List<UIComponent> uiChildren = uiPage.getChildren();
			if (uiChildren == null)	return;
			ArrayList<Object> children = new ArrayList<Object>();
			for (UIComponent child : uiChildren) {
				Object component = PortalDataMapper.buildChild(child);
				if (component != null)
					children.add(component);
			}
			page.setChildren(children);
			uiPage.getChildren().clear();

			page.setModifier(pcontext.getRemoteUser());
			PortalDataMapper.toUIPage(uiPage, page);
//			if (page.getTemplate() == null) page.setTemplate(uiPage.getTemplate());
			if (page.getChildren() == null)	page.setChildren(new ArrayList<Object>());
			if (Page.DESKTOP_PAGE.equals(uiPage.getFactoryId())) {
				configService.update(page);
				postSave(uiPortalApp, pcontext);
			}
		}

		private void postSave(UIPortalApplication uiPortalApp,
				WebuiRequestContext context) throws Exception {
			UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
			uiMaskWS.setUIComponent(null);
			uiMaskWS.setShow(false);
			UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
			UIPageBrowser uiBrowser = (UIPageBrowser) uiToolPanel.getUIComponent();
			uiBrowser.reset();
			context.addUIComponentToUpdateByAjax(uiBrowser);
			context.addUIComponentToUpdateByAjax(uiMaskWS);
		}
	}
}