package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/component/UIFormWithTitle.gtmpl",    
    events = {
      @EventConfig(listeners = UIChangeSkinForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIChangeSkinForm extends UIForm{
  
  @SuppressWarnings("unchecked")
  public UIChangeSkinForm() throws Exception  {   
//    SelectItemOption vistaSkin = new SelectItemOption("vista", null);
//    SelectItemOption macSkin = new SelectItemOption("mac", null);;
//    SelectItemOption defaultSkin = new SelectItemOption("default", null);
//    
//    List<SelectItemOption> itemList = new ArrayList<SelectItemOption>();
//    itemList.add(vistaSkin);
//    itemList.add(macSkin);
//    itemList.add(defaultSkin);

    SelectItemCategory skinVista = new SelectItemCategory("Vista", false);
    SelectItemCategory skinMac = new SelectItemCategory("Mac", false);
    SelectItemCategory skinDefault = new SelectItemCategory("Default", false);
    
//    skinCategory.setSelectItemOptions(itemList);
    
    List<SelectItemCategory> itemCategories = new ArrayList<SelectItemCategory>();
    itemCategories.add(skinDefault);
    itemCategories.add(skinMac);
    itemCategories.add(skinVista);
    
    UIFormInputItemSelector uiTemplate = new UIFormInputItemSelector("Skin", "  ");
    uiTemplate.setItemCategories(itemCategories );
    uiTemplate.setRendered(true);
    
    addChild(uiTemplate);
  }
  

  static public class SaveActionListener  extends EventListener<UIChangeSkinForm> {
    public void execute(Event<UIChangeSkinForm> event) throws Exception {
        System.out.println("\n=======> save ne`\n");
//      UIChangeSkinForm uiPageForm = event.getSource();     
//      UIPage uiPage = uiPageForm.getUIPage();      
//      Page page = new Page() ;
//      uiPageForm.invokeSetBindingBean(page);     
//      if(uiPage != null){
//        if(page.getTemplate() == null) page.setTemplate(uiPage.getTemplate()) ;
//        PortalDataModelUtil.toUIPage(uiPage, page, true);       
//      }else{
//        page.setOwner(Util.getUIPortal().getOwner());
//      }
//      if(page.getChildren() == null){
//        page.setChildren(new ArrayList<org.exoplatform.portal.config.model.Component>());        
//      }      
//           
//      PortalDAO configService = uiPageForm.getApplicationComponent(PortalDAO.class);
//      configService.savePage(page);      
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageForm) ;
    }
  }

}
