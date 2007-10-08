/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Oct 8, 2007  
 */
@ComponentConfig(
    template = "app:/groovy/portal/webui/application/UIWidgetContainerManagement.gtmpl",
    events = {
        @EventConfig(listeners = UIWidgetContainerManagement.SaveActionListener.class),
        @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
    }
)
public class UIWidgetContainerManagement extends UIContainer {
  
  static public class SaveActionListener extends EventListener<UIWidgetContainerManagement> {

    public void execute(Event<UIWidgetContainerManagement> event) throws Exception {
      UIWidgetContainerManagement uiManagement = event.getSource();
      UserPortalConfigService configService = uiManagement.getApplicationComponent(UserPortalConfigService.class) ;
      String ownerType = PortalConfig.USER_TYPE ;
      WebuiRequestContext rcontext = event.getRequestContext() ;
      String ownerId = rcontext.getRemoteUser() ;
      String widgetsId = ownerType + "::" + ownerId ; 
      Widgets widgets = configService.getWidgets(widgetsId) ;
      if(widgets == null) {
        widgets = new Widgets() ;
        widgets.setOwnerType(ownerType) ;
        widgets.setOwnerId(ownerId) ;
        widgets.setChildren(new ArrayList<Container>()) ;
        configService.create(widgets) ;
      }

      deleteContainer(widgets, rcontext) ;
      saveContainer(widgets, rcontext) ;
      
      configService.update(widgets) ;
      UIMaskWorkspace uiMaskWorkspace = uiManagement.getParent() ;
      UIPortalApplication uiPoralApp = uiMaskWorkspace.getAncestorOfType(UIPortalApplication.class) ;
      UIWelcomeComponent uiWelcomeComponent = uiPoralApp.findFirstComponentOfType(UIWelcomeComponent.class) ;
      UIWidgets uiWidgets = uiWelcomeComponent.getChild(UIWidgets.class) ;
      PortalDataMapper.toUIWidgets(uiWidgets, widgets) ;
      rcontext.addUIComponentToUpdateByAjax(uiWelcomeComponent) ;
      uiMaskWorkspace.setUIComponent(null) ;
      rcontext.addUIComponentToUpdateByAjax(uiMaskWorkspace) ;
    }
    
    private void deleteContainer(Widgets widgets, WebuiRequestContext rcontext) {
      List<Container> existingContainers = widgets.getChildren() ;
      String[] deleteContainers = rcontext.getRequestParameterValues("deleted") ;
      if(deleteContainers == null) return ;
      for(String deletedItem :  deleteContainers) {
        Container foundContainer = getContainer(existingContainers, deletedItem) ;
        if(foundContainer != null) existingContainers.remove(foundContainer) ; 
      }    
    }
    
    private void saveContainer(Widgets widgets, WebuiRequestContext rcontext) {
      List<Container> existingContainers = widgets.getChildren() ;
      String[] cIds = rcontext.getRequestParameterValues("id") ;
      String[] cNames = rcontext.getRequestParameterValues("name") ;
      String[] cDesc = rcontext.getRequestParameterValues("desc") ;
      if(cIds == null) return ;
      for(int i = 0; i < cIds.length; i++) {
        Container foundContainer = getContainer(existingContainers, cIds[i]) ;
        if(foundContainer != null) {
          foundContainer.setName(cNames[i]) ;
          foundContainer.setDescription(cDesc[i]) ;
          continue ;
        }
        Container newContainer = createContainer(cIds[i], cNames[i], cDesc[i]) ;
        existingContainers.add(newContainer) ;
      }
    }
    
    private Container getContainer(List<Container> list, String id) {
      for(Container ele : list) {
        if (ele.getId().equals(id)) return ele ;
      }
      
      return null ;
    }
    
    private Container createContainer(String id, String name, String desc) {
      Container container = new Container() ;
      container.setId(id) ;
      container.setName(name) ;
      container.setDescription(desc) ;
      
      return container ;
    }
    
  }
  
}