package org.exoplatform.i18n.webui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Nov 01, 2007
 */

@ComponentConfigs ( {
  @ComponentConfig(
      lifecycle = UIApplicationLifecycle.class,
      events = {
        @EventConfig (listeners = UII18nPortlet.DeleteActionListener.class),
        @EventConfig (listeners = UII18nPortlet.EditActionListener.class) 
      }
  ),
  
  @ComponentConfig(
      id = "UISearchI18n",
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

  private UIGrid grid_ ;
  public UII18nPortlet() throws Exception {
    
    grid_ = addChild(UIGrid.class, null, "ResourceList") ;
    grid_.configure("id", RESOURCE_LIST, RESOURCE_ACTION) ;
    grid_.setRendered(true) ;
    
    addChild(UIEditResource.class,null,null).setRendered(false) ;
    
    UIForm uiSearchResource = addChild(UIForm.class,"UISearchI18n", null);
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
    uiSearchResource.setRendered(true) ;
    // update grid
    update(null,null);
  } 
  
  static public class DeleteActionListener extends EventListener<UII18nPortlet> {
    public void execute(Event<UII18nPortlet> event) throws Exception {
      ResourceBundleService serv = event.getSource().getApplicationComponent(ResourceBundleService.class);
      serv.removeResourceBundleData(event.getRequestContext().getRequestParameter(OBJECTID)) ;
      UII18nPortlet uiI18n = event.getSource() ;
      uiI18n.update(null, null) ;
    }
  }

  static public class EditActionListener extends EventListener<UII18nPortlet> {
    public void execute(Event<UII18nPortlet> event) throws Exception {
      UII18nPortlet uiI18n = event.getSource() ;
      
      UIEditResource uiEditResource = uiI18n.getChild(UIEditResource.class) ;
      uiEditResource.setRendered(true) ;
      String paramID = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiEditResource.setResource(paramID) ;
      
      uiI18n.getChild(UIGrid.class).setRendered(false) ;
      UIForm uiSearch = uiI18n.getChildById("UISearchI18n") ;
      uiSearch.setRendered(false) ;
    }
  }

  static public class SearchActionListener  extends EventListener<UIForm> {
    public void execute(Event<UIForm> event) throws Exception {
      UIForm uiSearch = event.getSource() ;
      UII18nPortlet uiI18n = uiSearch.getParent() ;
      String language = uiSearch.getChild(UIFormSelectBox.class).getValue() ;
      if ("".equals(language)) language = null ;
      uiI18n.update(uiSearch.getChild(UIFormStringInput.class).getValue(), language);
    }
  }
  static public class NewResourceActionListener  extends EventListener<UIForm> {
    public void execute(Event<UIForm> event) throws Exception {
      UII18nPortlet uiI18n = event.getSource().getParent() ;
      
      UIEditResource uiEditResource = uiI18n.getChild(UIEditResource.class) ;
      uiEditResource.setRendered(true) ;
      uiEditResource.setResource(null) ;
      
      uiI18n.getChild(UIGrid.class).setRendered(false) ;
      UIForm uiSearch = uiI18n.getChildById("UISearchI18n") ;
      uiSearch.setRendered(false) ;
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiI18n) ;
    }
  }
  
  public void update(String name , String lang) throws Exception {
    ResourceBundleService resBundleServ = getApplicationComponent(ResourceBundleService.class);
    PageList pageList = resBundleServ.findResourceDescriptions(new Query(name,lang)) ;
    pageList.setPageSize(10) ;
    grid_.getUIPageIterator().setPageList(pageList) ;
    UIPageIterator pageIterator = grid_.getUIPageIterator();
    if(pageIterator.getAvailable() == 0 ) {
      UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
      uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null)) ;
      Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
    }
  }
}

