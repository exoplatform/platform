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
package org.exoplatform.i18n.webui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIRepeater;
import org.exoplatform.webui.core.UIVirtualList;
//import org.exoplatform.webui.core.UIGrid;
//import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.ResourceValidator;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Nov 01, 2007
 */

@ComponentConfig(
      lifecycle = UIFormLifecycle.class,
      template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
      events = {
        @EventConfig (listeners = UIEditResource.SaveActionListener.class),
        @EventConfig (listeners = UIEditResource.EditActionListener.class),
        @EventConfig (listeners = UIEditResource.CancelActionListener.class, phase = Phase.DECODE)
      }
  )
public class UIEditResource extends UIForm {

  private boolean editAble = true;
  
  public UIEditResource() throws Exception {
    addUIFormInput(new UIFormStringInput("name",null,null).addValidator(MandatoryValidator.class).addValidator(ResourceValidator.class)) ;
    
    LocaleConfigService service = getApplicationComponent(LocaleConfigService.class) ;
    Iterator i = service.getLocalConfigs().iterator() ;
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>("All", ""));
    while (i.hasNext()) {
      LocaleConfig config = (LocaleConfig) i.next() ;
      options.add(new SelectItemOption<String>(config.getLocaleName(), config.getLanguage()))  ;
    }
    
    addUIFormInput(new UIFormSelectBox("language","language",options).addValidator(MandatoryValidator.class)) ;
    addUIFormInput(new UIFormTextAreaInput("resource", null,null).addValidator(MandatoryValidator.class)) ;
  } 

  static public class SaveActionListener  extends EventListener<UIEditResource> {
    public void execute(Event<UIEditResource> event) throws Exception {
      UIEditResource uiEditResource = event.getSource() ;
      UII18nPortlet uiI18n = uiEditResource.getParent() ;
      
      String data = uiEditResource.getChild(UIFormTextAreaInput.class).getValue() ;
      ResourceBundleService serv = uiEditResource.getApplicationComponent(ResourceBundleService.class) ;
      ResourceBundleData resData = serv.createResourceBundleDataInstance() ; 
      
      String name = uiEditResource.getUIStringInput("name").getValue() ;
      String language = uiEditResource.getChild(UIFormSelectBox.class).getValue() ;
      
      // get current page
      //UIPageIterator pageIterator = uiI18n.getChild(UIGrid.class).getUIPageIterator();
      //int currentPage = pageIterator.getCurrentPage();
      
      UIVirtualList virtualList = uiI18n.getChild(UIVirtualList.class);
      UIRepeater uiRepeater = (UIRepeater)virtualList.getDataFeed();
      int currentPage = uiRepeater.getDataSource().getCurrentPage();
      
      PageList pageList = serv.findResourceDescriptions(new Query(name,language)) ;
      if((pageList.getAvailable() > 0)&& uiEditResource.isSave()) {
        UIApplication uiApp = event.getRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIEditResource.add.exist", null)) ;
        return ;
      }
      
      resData.setData(data) ;
      resData.setName(name) ;
      resData.setLanguage(language) ;
      
      serv.saveResourceBundle(resData) ;
      
      // update when create new resource
      if(uiEditResource.isSave()) uiI18n.update(null, null) ;
      else uiI18n.update(uiI18n.getLastQuery().getName(), uiI18n.getLastQuery().getLanguage()) ;
      //pageIterator.setCurrentPage(currentPage);
      uiRepeater.getDataSource().getPage(currentPage);
      
      //uiI18n.getChild(UIGrid.class).setRendered(true) ;
      uiI18n.getChild(UIVirtualList.class).setRendered(true) ;
      UIForm uiSearch = uiI18n.getChildById("UISearchI18n") ;
      uiSearch.setRendered(true) ;
      uiI18n.getChild(UIEditResource.class).setRendered(false) ;
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiI18n) ;

    }
  }
  
  static public class CancelActionListener  extends EventListener<UIEditResource> {
    public void execute(Event<UIEditResource> event) throws Exception {
      UIEditResource uiEditResource = event.getSource() ;
      UII18nPortlet uiI18n = uiEditResource.getParent() ;
      uiI18n.getChild(UIEditResource.class).setRendered(false) ;
      //uiI18n.getChild(UIGrid.class).setRendered(true) ;
      uiI18n.getChild(UIVirtualList.class).setRendered(true) ;
      UIForm uiSearch = uiI18n.getChildById("UISearchI18n") ;
      uiSearch.setRendered(true) ;
    }
  }
  
  static public class EditActionListener extends EventListener<UIEditResource> {
    public void execute(Event<UIEditResource> event) throws Exception {
      UIEditResource uiEditResource = event.getSource() ;
      uiEditResource.getChild(UIFormTextAreaInput.class).setEditable(true) ;
      uiEditResource.getUIStringInput("name").setEditable(false) ;
      uiEditResource.getChild(UIFormSelectBox.class).setEnable(false) ;
      uiEditResource.setSave(false) ;
      uiEditResource.setActions(new String[]{"Save", "Cancel"});
    }
  }
  
  public void setResource(String resource) throws Exception {
    if(resource != null) {      
      ResourceBundleService serv = getApplicationComponent(ResourceBundleService.class) ;
      ResourceBundleData redata = serv.getResourceBundleData(resource) ;
      getChild(UIFormTextAreaInput.class).setValue(redata.getData()) ;
      getUIStringInput("name").setValue(redata.getName()) ;
      getUIStringInput("name").setEditable(false) ;
      getChild(UIFormSelectBox.class).setValue(redata.getLanguage()) ;
      getChild(UIFormSelectBox.class).setEnable(false) ;
    }
    else {
      getChild(UIFormTextAreaInput.class).setValue("") ;
      getChild(UIFormTextAreaInput.class).setEditable(true) ;
      getUIStringInput("name").setValue("") ;
      getUIStringInput("name").setEditable(true) ;
      getChild(UIFormSelectBox.class).setValue("") ;
      getChild(UIFormSelectBox.class).setEnable(true) ;
      setSave(true) ;
    }
  }
  
  private void setSave(boolean editAble) {
    this.editAble = editAble ;
  }
  private boolean isSave() {
    return editAble ;
  }
}

