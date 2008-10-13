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

import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.portal.webui.application.GadgetUtil;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 29, 2008  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UIGadgetEditor.SaveActionListener.class),
      @EventConfig(listeners = UIGadgetEditor.CancelActionListener.class)
    }
)
public class UIGadgetEditor extends UIForm {
  
  final static public String FIELD_NAME = "name" ;
  final static public String FIELD_SOURCE = "source" ;
  
  public UIGadgetEditor() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_NAME, null, null)) ;
    addUIFormInput(new UIFormTextAreaInput(FIELD_SOURCE, null, null)) ;
  }
  
  public void setEditValue(String name, String source) {
    UIFormStringInput uiInputName = getUIStringInput(FIELD_NAME);
    uiInputName.setValue(name);
    uiInputName.setEditable(false);
    UIFormTextAreaInput uiInputSource = getUIFormTextAreaInput(FIELD_SOURCE);
    uiInputSource.setValue(source);
  }
  
  public static class SaveActionListener extends EventListener<UIGadgetEditor> {

    public void execute(Event<UIGadgetEditor> event) throws Exception {
      UIGadgetEditor uiForm = event.getSource() ;
      String name = uiForm.getUIStringInput(UIGadgetEditor.FIELD_NAME).getValue() ;
      String source = uiForm.getUIFormTextAreaInput(UIGadgetEditor.FIELD_SOURCE).getValue() ;
      SourceStorage sourceStorage = uiForm.getApplicationComponent(SourceStorage.class) ;
      GadgetRegistryService service = uiForm.getApplicationComponent(GadgetRegistryService.class) ;
      sourceStorage.saveSource(name, source) ;
      service.saveGadget(GadgetUtil.toGadget(name, sourceStorage.getSourcePath(name), true)) ;
      UIGadgetManagement uiManagement = uiForm.getParent() ;
      uiManagement.reload() ;
      uiManagement.setSelectedGadget(name);
      uiManagement.getChildren().clear();
      UIGadgetInfo uiInfo = uiManagement.addChild(UIGadgetInfo.class, null, null);
      uiInfo.setGadget(uiManagement.getSelectedGadget());      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }
  
  public static class CancelActionListener extends EventListener<UIGadgetEditor> {

    public void execute(Event<UIGadgetEditor> event) throws Exception {
      UIGadgetEditor uiForm = event.getSource() ;
      UIGadgetManagement uiManagement = uiForm.getParent() ;
      uiManagement.getChildren().clear();
      UIGadgetInfo uiInfo = uiManagement.addChild(UIGadgetInfo.class, null, null);
      uiInfo.setGadget(uiManagement.getSelectedGadget());   
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;      
    }
    
  }
}
