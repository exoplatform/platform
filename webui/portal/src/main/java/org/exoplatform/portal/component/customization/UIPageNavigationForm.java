/***************************************************************************

 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.organization.webui.component.UIAccessGroup;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Author : Nguyen Thi Hoa  
 *          hoa.nguyen@exoplatform.com
 * Jun 20, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/component/UIFormTabPane.gtmpl",   
    events = {
      @EventConfig(listeners = UIPageNavigationForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIPageNavigationForm extends UIFormTabPane {

  public PageNavigation pageNav_;
  private String helpUri_ ;

  public UIPageNavigationForm() throws Exception {
    super("UIPageNavigationForm") ;    
    
    List<SelectItemOption<String>> priorties = new ArrayList<SelectItemOption<String>>();
    for(int i = 0; i < 10; i++){
      priorties.add(new SelectItemOption<String>(String.valueOf(i), String.valueOf(i)));
    }    

    UIFormInputSet uiSettingSet = new UIFormInputSet("PageNavigationSetting") ;    
    uiSettingSet.addUIFormInput(new UIFormStringInput("ownerType", "ownerType",null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("ownerId", "ownerId",null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("creator", "creator",null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("modifier", "modifier",null).setEditable(false)).
                 addUIFormInput(new UIFormTextAreaInput("description","description", null)).
                 addUIFormInput(new UIFormSelectBox("priority", null, priorties));
    addUIFormInput(uiSettingSet) ;
    
    UIAccessGroup uiAccessGroup = createUIComponent(UIAccessGroup.class, null, "UIAccessGroup");
    uiAccessGroup.setRendered(false);
    uiAccessGroup.configure("AccessGroup", "accessGroup");
    addUIComponentInput(uiAccessGroup);
    
  }

  public PageNavigation getPageNavigation(){ return pageNav_; }
  
  public void setUriForQuickHelp(String helpUri) { helpUri_ = helpUri ; }
  public String getUriForQuickHelp() { return helpUri_ ; }

  public void setValues(PageNavigation pageNavigation) throws Exception {
    pageNav_ = pageNavigation;
    invokeGetBindingBean(pageNavigation) ;    

    UIAccessGroup uiAccessGroup = getChild(UIAccessGroup.class);
    uiAccessGroup.setGroups(pageNavigation.getAccessGroup());
    
    UIFormSelectBox uiSelectBox = findFirstComponentOfType(UIFormSelectBox.class);
    uiSelectBox.setValue(String.valueOf(pageNavigation.getPriority()));
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);   
  }

  static public class SaveActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {   
      UIPageNavigationForm uiForm = event.getSource();
      PageNavigation pageNav = uiForm.getPageNavigation();
      uiForm.invokeSetBindingBean(pageNav) ;
      UIFormSelectBox uiSelectBox = uiForm.findFirstComponentOfType(UIFormSelectBox.class);
      int priority = Integer.parseInt(uiSelectBox.getValue());
      pageNav.setPriority(priority);
      pageNav.setModifier(event.getRequestContext().getRemoteUser());
      
      UIAccessGroup uiAccessGroup = uiForm.getChild(UIAccessGroup.class);
      pageNav.setAccessGroup(uiAccessGroup.getAccessGroup());
      
      UIComponentDecorator uiFormParent = uiForm.getParent(); 
      uiFormParent.setUIComponent(null);
      UserPortalConfigService dataService = uiForm.getApplicationComponent(UserPortalConfigService.class);
      dataService.update(pageNav);  
      event.getRequestContext().addUIComponentToUpdateByAjax(uiFormParent);    
    }
  }

}
