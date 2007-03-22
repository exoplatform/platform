/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.NameValidator;
import org.exoplatform.webui.component.validator.NumberFormatValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 8, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/component/UIFormWithTitle.gtmpl",

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
public class UIContainerForm extends UIForm { 

  private UIContainer uiContainer_ ;
  private UIComponent backComponent_ ;

  @SuppressWarnings("unchecked")
  public UIContainerForm() throws Exception {//InitParams initParams
//    super("UIContainerForm");
    this.addUIFormInput(new UIFormStringInput("id", "id", null).
                                addValidator(EmptyFieldValidator.class).
                                addValidator(NameValidator.class)).                     
                 addUIFormInput(new UIFormStringInput("title", "title", null)).
                 addUIFormInput(new UIFormStringInput("width", "width", null).
                                addValidator(NumberFormatValidator.class)).
                 addUIFormInput(new UIFormStringInput("height", "height", null).
                                addValidator(NumberFormatValidator.class));  
    
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

  public void setValues(UIContainer uiContainer) throws Exception {
    this.uiContainer_ = uiContainer;
    Container container = PortalDataModelUtil.toContainerModel(uiContainer, false) ;
    getUIStringInput("id").setEditable(false);
    invokeGetBindingBean(container) ;    
  }

  public UIContainer getContainer() { return uiContainer_ ; }

  public UIComponent getBackComponent() { return backComponent_ ; }
  public void setBackComponent(UIComponent uiComp) throws Exception {
    backComponent_ = uiComp;
  }

  static public class SaveActionListener  extends EventListener<UIContainerForm> {
    public void execute(Event<UIContainerForm> event) throws Exception {
      System.out.println("\n\n\n\nSaveActionListener in UIContainerForm\n\n\n\n");
      UIContainerForm uiForm = event.getSource() ;
      UIContainer uiContainer = uiForm.getContainer() ;
      Container container = new Container();
      uiForm.invokeSetBindingBean(container) ;
      PortalDataModelUtil.toUIContainer(uiContainer, container, true);
      
      UIComponentDecorator uiParent = uiForm.getParent();
      uiParent.setUIComponent(null);
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
      Util.updateUIApplication(event);
    }
  }

  static public class BackActionListener extends EventListener<UIContainerForm> {
    public void execute(Event<UIContainerForm> event) throws Exception {
      
      UIContainerForm containerForm = event.getSource();
      UIComponentDecorator uiParent = containerForm.getParent();
      uiParent.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiParent);
      
      Util.updateUIApplication(event);
      
      /*UIContainerForm uiForm = event.getSource() ;
      UIComponent uiComp = uiForm.getBackComponent() ;
      UIPortalApplication uiApp = uiForm.getAncestorOfType(UIPortalApplication.class) ;
      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      pcontext.setForceFullUpdate(true);   
      UIContainerConfigOptions uiContainerConfigOptions = uiApp.findFirstComponentOfType(UIContainerConfigOptions.class);
      pcontext.addUIComponentToUpdateByAjax(uiContainerConfigOptions.getParent());
      
      if(uiComp instanceof UIPortal) {
        uiWorkingWS.setRenderedChild(UIPortal.class) ;
        Util.showPortalComponentLayoutMode(uiApp);
        return;
      } 
      UIPortalToolPanel uiToolpanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolpanel.setUIComponent(uiComp) ;
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      Util.showPageComponentLayoutMode(uiApp);*/

    }
  }

}
