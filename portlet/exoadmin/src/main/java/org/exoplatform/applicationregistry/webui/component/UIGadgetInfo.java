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
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.Source;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.portal.webui.application.GadgetUtil;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Aug 22, 2008  
 */
@ComponentConfig(
    template = "app:/groovy/applicationregistry/webui/component/UIGadgetInfo.gtmpl",
    events = {
        @EventConfig(listeners = UIGadgetInfo.RefreshActionListener.class),
        @EventConfig(listeners = UIGadgetInfo.CopyActionListener.class),
        @EventConfig(listeners = UIGadgetInfo.EditActionListener.class)
    }
)

public class UIGadgetInfo extends UIComponent {
  
  private Gadget gadget_ ;
  
  public UIGadgetInfo() {}
  
  public Gadget getGadget() { return gadget_ ; }
  
  public void setGadget(Gadget gadget) { gadget_ = gadget ; }
  
  public String getGadgetUrl() {
    return (GadgetUtil.reproduceUrl(gadget_.getUrl(), gadget_.isLocal()));
  }
  
  static public class RefreshActionListener extends EventListener<UIGadgetInfo> {
    
    public void execute(Event<UIGadgetInfo> event) throws Exception {
      UIGadgetInfo uiInfo = event.getSource() ;
      Gadget gadget = uiInfo.getGadget();
      GadgetRegistryService service = uiInfo.getApplicationComponent(GadgetRegistryService.class) ;
      service.saveGadget(GadgetUtil.toGadget(gadget.getName(), gadget.getUrl(), gadget.isLocal())) ;
      UIGadgetManagement uiManagement = uiInfo.getParent() ;
      uiManagement.initData() ;
      uiManagement.setSelectedGadget(gadget.getName());
      uiInfo.setGadget(uiManagement.getSelectedGadget());
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;      
    }
    
  }

  static public class CopyActionListener extends EventListener<UIGadgetInfo> {
    
    public void execute(Event<UIGadgetInfo> event) throws Exception {
      UIGadgetInfo uiInfo = event.getSource() ;
      String url = uiInfo.getGadget().getUrl();
      String name = uiInfo.getGadget().getName();
      URL urlObj = new URL(url) ;
      URLConnection conn = urlObj.openConnection() ;
      InputStream is = conn.getInputStream() ;
      SourceStorage sourceStorage = uiInfo.getApplicationComponent(SourceStorage.class) ;
      String fileName = name + ".xml";
      Source source = new Source(fileName, "application/xml", "UTF-8");
      source.setTextContent(IOUtils.toString(is, "UTF-8"));
      source.setLastModified(Calendar.getInstance());
      sourceStorage.saveSource(null, source) ;
      GadgetRegistryService service = uiInfo.getApplicationComponent(GadgetRegistryService.class) ;
      service.saveGadget(GadgetUtil.toGadget(name, sourceStorage.getSourceURI(fileName), true)) ;
      UIGadgetManagement uiManagement = uiInfo.getParent() ;
      uiManagement.initData() ;
      uiManagement.setSelectedGadget(name);
      uiInfo.setGadget(uiManagement.getSelectedGadget());      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;      
    }
    
  }
  
  static public class EditActionListener extends EventListener<UIGadgetInfo> {
    
    public void execute(Event<UIGadgetInfo> event) throws Exception {
      UIGadgetInfo uiInfo = event.getSource();
      UIGadgetManagement uiManagement = uiInfo.getParent();
      SourceStorage sourceStorage = uiManagement.getApplicationComponent(SourceStorage.class);
      Gadget gadget = uiInfo.getGadget();
      UIGadgetEditor uiEditor = uiManagement.createUIComponent(UIGadgetEditor.class, null, null);
      String fileName = gadget.getName() + ".xml";
      String text = sourceStorage.getSource(fileName).getTextContent();
      uiEditor.setEditValue(gadget.getName(), text);
      uiManagement.getChildren().clear();
      uiManagement.addChild(uiEditor);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
    }
    
  }

}