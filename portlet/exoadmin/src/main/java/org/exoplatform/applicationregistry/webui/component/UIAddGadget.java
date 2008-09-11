/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui.component;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.applicationregistry.util.ModelDataMapper;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormRadioBoxInput;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.ext.UIFormInputSetWithAction;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 4, 2008  
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UIAddGadget.SaveActionListener.class),
      @EventConfig(listeners = UIAddGadget.BackActionListener.class),
      @EventConfig(listeners = UIAddGadget.CopyGadgetActionListener.class)
    }
)

public class UIAddGadget extends UIForm {
  
  static final String FIELD_URL = "url" ;
  static final String URL = "urlText" ;
  static final String FIELD_DISPLAY = "display" ;
  
  public UIAddGadget() throws Exception {
    UIFormInputSetWithAction uiInput = new UIFormInputSetWithAction(FIELD_URL) ;
    uiInput.addUIFormInput(new UIFormStringInput(URL, null, null)) ;
    uiInput.setActionInfo(URL, new String [] {"CopyGadget"}) ;
    addUIComponentInput(uiInput) ;
    List<SelectItemOption<String>> displayOptions = new ArrayList<SelectItemOption<String>>(2) ;
    displayOptions.add(new SelectItemOption<String>("apllication", "application")) ;
    displayOptions.add(new SelectItemOption<String>("widget", "widget")) ;
    addUIFormInput(new UIFormRadioBoxInput(FIELD_DISPLAY, null, displayOptions)) ;
    setActions(new String [] {"Save", "Back"}) ;
  }
  
  public static class SaveActionListener extends EventListener<UIAddGadget> {

    public void execute(Event<UIAddGadget> event) throws Exception {
      UIAddGadget uiForm = event.getSource() ;
      GadgetRegistryService service = uiForm.getApplicationComponent(GadgetRegistryService.class) ;
      String url = uiForm.getUIStringInput(URL) .getValue();
      String name  = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.')) ;
      GadgetApplication gadgetApp = new GadgetApplication(name, url) ;
      service.addGadget(ModelDataMapper.toGadgetModel(gadgetApp)) ;
      UIGadgetManagement uiParent = uiForm.getParent() ;
      uiParent.reload() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiParent) ;
    }    
  }
  
  public static class BackActionListener extends EventListener<UIAddGadget> {

    public void execute(Event<UIAddGadget> event) throws Exception {
      UIAddGadget uiForm = event.getSource() ;
      UIGadgetManagement uiParent = uiForm.getParent() ;
      uiParent.getChildren().clear() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiParent) ;
    }
    
  }
  
  public static class CopyGadgetActionListener extends EventListener<UIAddGadget> {

    public void execute(Event<UIAddGadget> event) throws Exception {
      UIAddGadget uiForm = event.getSource() ;
      String url = uiForm.getUIStringInput(URL) .getValue();
      URL urlObj = new URL(url) ;
      URLConnection conn = urlObj.openConnection() ;
      InputStream is = conn.getInputStream() ;
      String source = IOUtils.toString(is, "UTF-8") ;
      GadgetRegistryService service = uiForm.getApplicationComponent(GadgetRegistryService.class) ;
      SourceStorage sourceStorage = uiForm.getApplicationComponent(SourceStorage.class) ;
      String name  = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.')) ;
      sourceStorage.saveSource(name, source) ;
      PortalRequestContext pContext = Util.getPortalRequestContext() ;
      StringBuffer requestUrl = pContext.getRequest().getRequestURL() ;
      int index = requestUrl.indexOf(pContext.getRequestContextPath()) ;
      url = requestUrl.substring(0, index) + "/" + sourceStorage.getSourceLink(name) ;
      GadgetApplication app = new GadgetApplication(name, url) ;
      service.addGadget(ModelDataMapper.toGadgetModel(app)) ;
      UIGadgetManagement uiManagement = uiForm.getParent() ;
      uiManagement.reload() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }
  
}