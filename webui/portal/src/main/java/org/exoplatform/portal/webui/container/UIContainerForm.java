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
package org.exoplatform.portal.webui.container;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.validator.ExpressionValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.NameValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
import org.exoplatform.webui.organization.UIListPermissionSelector;
import org.exoplatform.webui.organization.UIListPermissionSelector.EmptyIteratorValidator;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 8, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/form/UIFormTabPane.gtmpl",
    events = {
      @EventConfig(listeners = UIContainerForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
//    initParams = {    
//        @ParamConfig(
//            name = "ContainerTemplateOption",
//            value = "app:/WEB-INF/conf/uiconf/portal/webui/component/customization/ContainerTemplateOption.groovy"
//        )
//      },
public class UIContainerForm extends UIFormTabPane { 

  private UIContainer uiContainer_;
  private UIComponent backComponent_;

  public UIContainerForm() throws Exception { //InitParams initParams
    super("UIContainerForm");
    UIFormInputSet infoInputSet = new UIFormInputSet("ContainerSetting") ;
    infoInputSet.addUIFormInput(new UIFormStringInput("id", "id", null).
                                addValidator(MandatoryValidator.class).
                                addValidator(StringLengthValidator.class, 3, 30).
                                addValidator(NameValidator.class)).                     
                 addUIFormInput(new UIFormStringInput("title", "title", null).
                                addValidator(StringLengthValidator.class, 3, 30)).
                 addUIFormInput(new UIFormStringInput("width", "width", null).
                                addValidator(ExpressionValidator.class, "(^([1-9]\\d*)(px|%)$)?", 
                                    "UIContainerForm.msg.InvalidWidthHeight")).
                 addUIFormInput(new UIFormStringInput("height", "height", null).
                                addValidator(ExpressionValidator.class, "(^([1-9]\\d*)(px|%)$)?", 
                                    "UIContainerForm.msg.InvalidWidthHeight"));
    addChild(infoInputSet) ;
    setSelectedTab(infoInputSet.getId()) ;
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIContainerPermission", "accessPermissions");
    uiListPermissionSelector.addValidator(EmptyIteratorValidator.class) ;
    addChild(uiListPermissionSelector);
//    addChild(uiSettingSet);
//    UIFormInputItemSelector uiTemplate = new UIFormInputItemSelector("Template", "template");
//    uiTemplate.setTypeValue(String.class);
//    uiTemplate.setRendered(false);
//    addUIFormInput(uiTemplate); 
    
//    if(initParams == null) return ;    
//    UIFormInputItemSelector itemInput = getChild(UIFormInputItemSelector.class);
//    RequestContext context = RequestContext.getCurrentInstance() ;    
//    List<SelectItemCategory> categories = 
//      initParams.getParam("ContainerTemplateOption").getMapGroovyObject(context) ;
//    itemInput.setItemCategories(categories);
  } 

  public void setValues(final UIContainer uiContainer) throws Exception {
    uiContainer_ = uiContainer;
    Container container = PortalDataMapper.toContainer(uiContainer);
    getUIStringInput("id").setEditable(false);
    invokeGetBindingBean(container);
  }

  public UIContainer getContainer() { return uiContainer_; }

  public UIComponent getBackComponent() { return backComponent_; }
  public void setBackComponent(final UIComponent uiComp) throws Exception { backComponent_ = uiComp; }

  public static class SaveActionListener  extends EventListener<UIContainerForm> {
    public void execute(final Event<UIContainerForm> event) throws Exception {
      UIContainerForm uiForm = event.getSource();
      UIContainer uiContainer = uiForm.getContainer();
      uiForm.invokeSetBindingBean(uiContainer);
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      
      UIMaskWorkspace uiMaskWorkspace = uiForm.getParent();
      uiMaskWorkspace.setUIComponent(null);
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace);
      
      UIPortalApplication uiPortalApp = uiForm.getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      Util.showComponentLayoutMode(UIContainer.class);  
    }
  }
}
