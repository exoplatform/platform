/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIFormCheckBoxInput;
import org.exoplatform.webui.component.UIFormInputInfo;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIFormTableInputSet;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 29, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/component/UIFormTabPane.gtmpl",
    events = {
      @EventConfig(listeners = UIAddPortletForm.SaveActionListener.class),    
      @EventConfig(listeners = UIAddPortletForm.RefreshActionListener.class, phase=Phase.DECODE)
    }
)
public class UIAddPortletForm extends UIFormTabPane {   

  final static String [] TABLE_COLUMNS = {"label", "description", "input"};

  public UIAddPortletForm() throws Exception {
    super("UIAddPortletForm", false);
    super.setInfoBar(false);
    super.setRenderResourceTabName(false) ;

    loadPortlet();
  } 

  @SuppressWarnings("unchecked")
  private void loadPortlet() throws Exception {
    getChildren().clear();    
    ApplicationRegisteryService registeryService = getApplicationComponent(ApplicationRegisteryService.class) ;
    List<ApplicationCategory> portletCategories = registeryService.getApplicationCategories();

    String tableName = getClass().getSimpleName();
    boolean selected = true;
    for(ApplicationCategory category : portletCategories) {      
      UIFormInputSet uiInputSet = new UIFormInputSet(category.getName()) ;
      uiInputSet.setRendered(selected);
      if(selected) selected = false;
      addUIFormInput(uiInputSet) ;           

      UIFormTableInputSet uiTableInputSet = createUIComponent(UIFormTableInputSet.class, null, null) ;
      uiTableInputSet.setName(tableName);
      uiTableInputSet.setColumns(TABLE_COLUMNS);
      uiInputSet.addChild(uiTableInputSet);

      List<Application> portlets = registeryService.getApplications(category) ; 
      for(Application portlet : portlets){
        String id = portlet.getId();      

        uiInputSet = new UIFormInputSet(id) ;
        UIFormInputInfo uiInfo = new UIFormInputInfo("label", null, portlet.getDisplayName());
        uiInputSet.addChild(uiInfo);
        uiInfo = new UIFormInputInfo("description", null, portlet.getDescription());
        uiInputSet.addChild(uiInfo);

        UIFormCheckBoxInput<String> uiCheckbox = new UIFormCheckBoxInput<String>(id, null, id);       
        uiCheckbox.setValue(id);
        uiCheckbox.setChecked(false);
        uiInputSet.addChild(uiCheckbox);
        uiTableInputSet.addChild(uiInputSet);       
      }
    }   

  } 

  public void processDecode(WebuiRequestContext context) throws Exception {
    super.processDecode(context);
    for(UIComponent child : getChildren())  {
      child.processDecode(context) ;
    }
  }

  @SuppressWarnings("unchecked")
  static public class SaveActionListener extends EventListener<UIAddPortletForm> {
    public void execute(Event<UIAddPortletForm> event) throws Exception {
      List<UIFormCheckBoxInput> listCheckbox =  new ArrayList<UIFormCheckBoxInput>();
      event.getSource().findComponentOfType(listCheckbox, UIFormCheckBoxInput.class);

      UIPortal uiPortal = Util.getUIPortal();        
      UIPage uiPage = uiPortal.findFirstComponentOfType(UIPage.class);

      for(UIFormCheckBoxInput<String> ele : listCheckbox){
        if(!ele.isChecked())continue;    
        UIPortlet uiPortlet =  uiPage.createUIComponent(UIPortlet.class, null, null);
        StringBuilder windowId = new StringBuilder();
        windowId.append(Util.getUIPortal().getOwner()).append(":/");
        windowId.append(ele.getValue()).append('/').append(uiPortlet.hashCode());
        uiPortlet.setWindowId(windowId.toString());
        uiPage.addChild(uiPortlet);
      }      

      Page page = PortalDataModelUtil.toPageModel(uiPage); 
      DataStorage configService = uiPage.getApplicationComponent(DataStorage.class);
      configService.save(page);

      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext().getParentAppRequestContext();
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
    }  
    
  }

  @SuppressWarnings("unchecked")
  static public class RefreshActionListener  extends EventListener<UIAddPortletForm> {
    public void execute(Event<UIAddPortletForm> event) throws Exception {  
      event.getSource().loadPortlet();
    }  
  }

}