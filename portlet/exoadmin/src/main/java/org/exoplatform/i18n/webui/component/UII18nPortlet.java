package org.exoplatform.i18n.webui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

@ComponentConfigs ( {
  @ComponentConfig(
      lifecycle = UIApplicationLifecycle.class,
      events = {
        @EventConfig (listeners = UII18nPortlet.DeleteActionListener.class),
        @EventConfig (listeners = UII18nPortlet.EditActionListener.class) 
      }
  ),
  
  @ComponentConfig(
      id = "UISearchadf",
      type = UIForm.class,
      lifecycle = UIFormLifecycle.class,
      template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
      events = {
        @EventConfig (listeners = UII18nPortlet.SearchActionListener.class),
        @EventConfig (listeners = UII18nPortlet.NewResourceActionListener.class)
      }
  )
})
public class UII18nPortlet extends UIPortletApplication {
  private static String[] RESOURCE_LIST = {"name", "language"} ;
  private static String[] RESOURCE_ACTION = {"Edit", "Delete"} ;

  public UII18nPortlet() throws Exception {
    ResourceBundleService resBundleServ = getApplicationComponent(ResourceBundleService.class);
    UIGrid grid = addChild(UIGrid.class, null, "ResourceList") ;
    grid.configure("id", RESOURCE_LIST, RESOURCE_ACTION) ;
    grid.getUIPageIterator().setPageList(resBundleServ.findResourceDescriptions(new Query(null, null))) ;
    UIForm uiSearchResource = addChild(UIForm.class,"UISearchadf", null);
    uiSearchResource.addUIFormInput(new UIFormStringInput("name","name",null));
    
    LocaleConfigService service = getApplicationComponent(LocaleConfigService.class) ;
    Iterator i = service.getLocalConfigs().iterator() ;
    
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>("All", ""));

    while (i.hasNext()) {
      LocaleConfig config = (LocaleConfig) i.next() ;
      options.add(new SelectItemOption<String>(config.getLocaleName(), config.getLanguage()))  ;
    }
    
    uiSearchResource.addUIFormInput(new UIFormSelectBox("language","language",options));
  }
  
  static public class DeleteActionListener extends EventListener<UII18nPortlet> {
    public void execute(Event<UII18nPortlet> event) throws Exception {
      ResourceBundleService serv = event.getSource().getApplicationComponent(ResourceBundleService.class);
      serv.removeResourceBundleData(event.getRequestContext().getRequestParameter(OBJECTID)) ;
    }
  }

  static public class EditActionListener extends EventListener<UII18nPortlet> {
    public void execute(Event<UII18nPortlet> event) throws Exception {
      
    }
  }

  static public class SearchActionListener  extends EventListener<UIForm> {
    public void execute(Event<UIForm> event) throws Exception {
      
      //System.err.print(arg0)
    }
  }
  static public class NewResourceActionListener  extends EventListener<UIForm> {
    public void execute(Event<UIForm> event) throws Exception {     
    }
  }
}

