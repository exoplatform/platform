package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.webui.component.UIForm;
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
    template = "system:/groovy/portal/webui/component/customization/UIChangeSkinForm.gtmpl",   
    events = {
      @EventConfig(listeners = UIChangeSkinForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIChangeSkinForm extends UIFormTabPane{
  
  @SuppressWarnings("unchecked")
  public UIChangeSkinForm() throws Exception  { 
    super("UIChangeSkinForm");

    SelectItemCategory skinVista = new SelectItemCategory("Vista", false);
    List<SelectItemOption> vistaList = new  ArrayList<SelectItemOption>();
    vistaList.add(new SelectItemOption("Vista", "vista"));
    skinVista.setSelectItemOptions(vistaList);
    
    
    SelectItemCategory skinMac = new SelectItemCategory("Mac", false);
    List<SelectItemOption> macList = new  ArrayList<SelectItemOption>();
    macList.add(new SelectItemOption("Mac", "mac"));
    skinMac.setSelectItemOptions(macList);
    
    SelectItemCategory skinDefault = new SelectItemCategory("Default", false);
    List<SelectItemOption> defaultList = new  ArrayList<SelectItemOption>();
    defaultList.add(new SelectItemOption("Default", "default"));
    skinDefault.setSelectItemOptions(defaultList);
    
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
//      String skin  = event.getRequestContext().getRequestParameter(OBJECTID);     
      UIChangeSkinForm uicomp = event.getSource() ;      
      List skinList = uicomp.getChildren();
      Iterator skinIterator = skinList.iterator();
      while (skinIterator.hasNext()) {      
        UIFormInputItemSelector uiFormInputItemSelector = (UIFormInputItemSelector) skinIterator.next();
        System.out.println("\n==========> uiFormInputItemSelector: " + uiFormInputItemSelector.getName() + "\n");
      }
      
      UIPortalApplication uiApp = uicomp.getAncestorOfType(UIPortalApplication.class);      
//      uiApp.setSkin(skin);
  //    System.out.println("\n==========> skin ne`: " + skin + "\n");
/*      UIChangeSkinForm uiPageForm = event.getSource();     
      UIPage uiPage = new UIPage();  
      Page page = new Page() ;
      uiPageForm.invokeSetBindingBean(page);     
      if(uiPage != null){
        if(page.getTemplate() == null) page.setTemplate(uiPage.getTemplate()) ;
        PortalDataModelUtil.toUIPage(uiPage, page, true);       
      }else{
        page.setOwner(Util.getUIPortal().getOwner());
      }
      if(page.getChildren() == null){
        page.setChildren(new ArrayList<org.exoplatform.portal.config.model.Component>());        
      }      
           
      PortalDAO configService = uiPageForm.getApplicationComponent(PortalDAO.class);
      configService.savePage(page);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageForm) ;*/
    }
  }

}
