package org.exoplatform.i18n.webui.component;

import java.util.*;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ViewChildActionListener;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;


@ComponentConfigs ( {
  @ComponentConfig(
      lifecycle = UIApplicationLifecycle.class
      //template = "app:/groovy/resources/webui/component/UII18nPortlet.gtmpl"
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
  public UII18nPortlet() throws Exception {
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

