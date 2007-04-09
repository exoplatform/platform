package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/portal/webui/component/customization/UIChangeLanguageForm.gtmpl",   
    events = {
      @EventConfig(listeners = UIChangeLanguageForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIChangeLanguageForm extends UIFormTabPane{
  
  @SuppressWarnings("unchecked")
  public UIChangeLanguageForm() throws Exception  { 
    super("UIChangeLanguageForm");

    SelectItemCategory skinVista = new SelectItemCategory("English", false);
    List<SelectItemOption> english = new  ArrayList<SelectItemOption>();
    english.add(new SelectItemOption("English", "English", "English"));
    skinVista.setSelectItemOptions(english);
    
    
    SelectItemCategory skinMac = new SelectItemCategory("VietNamese", false);
    List<SelectItemOption> vietnamese = new  ArrayList<SelectItemOption>();
    vietnamese.add(new SelectItemOption("VietNamese", "VietNamese", "Vietnamese"));
    skinMac.setSelectItemOptions(vietnamese);
    
    SelectItemCategory french = new SelectItemCategory("French", false);
    List<SelectItemOption> defaultList = new  ArrayList<SelectItemOption>();
    defaultList.add(new SelectItemOption("French", "French", "French"));
    french.setSelectItemOptions(defaultList);
    french.setSelected(true);
    
    List<SelectItemCategory> itemCategories = new ArrayList<SelectItemCategory>();
    itemCategories.add(french);
    itemCategories.add(skinMac);
    itemCategories.add(skinVista);
    
    UIFormInputItemSelector uiTemplate = new UIFormInputItemSelector("Language", "  ");
    uiTemplate.setItemCategories(itemCategories );
    uiTemplate.setRendered(true);
//    uiTemplate.setValue(null);
    addChild(uiTemplate);
  }
  
  static public class SaveActionListener  extends EventListener<UIChangeLanguageForm> {
    public void execute(Event<UIChangeLanguageForm> event) throws Exception {
      System.out.println("\n=======> save ne`\n");
//      String skin  = event.getRequestContext().getRequestParameter(OBJECTID);     
      UIChangeLanguageForm uicomp = event.getSource() ;      
      List skinList = uicomp.getChildren();
      Iterator skinIterator = skinList.iterator();
      while (skinIterator.hasNext()) {      
        UIFormInputItemSelector uiFormInputItemSelector = (UIFormInputItemSelector) skinIterator.next();
        System.out.println("\n==========> uiFormInputItemSelector: " + uiFormInputItemSelector.getName() + "\n");
      }
    }
  }

}
