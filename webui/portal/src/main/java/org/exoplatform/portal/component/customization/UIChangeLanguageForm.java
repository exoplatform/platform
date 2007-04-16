package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIItemSelector;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/customization/UIChangeLanguageForm.gtmpl",
    initParams = {
      @ParamConfig(
          name = "ChangeLanguageTemplateConfigOption",
          value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/ChangeLanguageTemplateConfigOption.groovy"
      )  
    },
    events = {
      @EventConfig(listeners = UIChangeLanguageForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIChangeLanguageForm extends UIContainer{
  String name_;
  private String[] actions_ = null;
  
  @SuppressWarnings("unchecked")
  public UIChangeLanguageForm(InitParams initParams) throws Exception  { 
    name_ = "UIChangeLanguageForm";
    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ChangeLanguageTemplateConfigOption");
    List<SelectItemCategory> itemCategories = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    
    UIItemSelector selector = new UIItemSelector("Language");
    selector.setItemCategories(itemCategories );
    selector.setRendered(true);
    addChild(selector);
  }
  
  public String getName() {
    return name_;
  }
  
  public void setActions(String [] actions){
    actions_ = actions;
  }
  
  public String[] getActions() {
    if(actions_ != null) return actions_;
    ArrayList<org.exoplatform.webui.config.Event> events = config.getEvents();
    actions_ = new String[events.size()];    
    for(int i = 0; i < actions_.length; i++){
      actions_[i] = events.get(i).getName();
    }
    return actions_;  
  }
  
  public String event(String actionName) throws Exception {
    StringBuilder b = new StringBuilder() ;
    b.append("javascript:eXo.portal.UIPortalControl.changeLanguage('").append(getName()).append("', '");
    b.append(actionName).append("')");
    return b.toString() ;
  }
  
  static public class SaveActionListener  extends EventListener<UIChangeLanguageForm> {
    public void execute(Event<UIChangeLanguageForm> event) throws Exception {
//      String skin  = event.getRequestContext().getRequestParameter(OBJECTID);     
/*      UIChangeLanguageForm uicomp = event.getSource() ;      
      List skinList = uicomp.getChildren();
      Iterator skinIterator = skinList.iterator();
      while (skinIterator.hasNext()) {      
        UIFormInputItemSelector uiFormInputItemSelector = (UIFormInputItemSelector) skinIterator.next();
        System.out.println("\n==========> uiFormInputItemSelector: " + uiFormInputItemSelector.getName() + "\n");
      }*/
    }
  }

}
