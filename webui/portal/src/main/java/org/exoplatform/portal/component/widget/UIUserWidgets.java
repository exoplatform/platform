/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.widget;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIDropDownItemSelector;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy, 
 *          lebienthuy@gmail.com
 * Jul 11, 2006  
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/component/widget/UIUserWidgets.gtmpl" ,
  events = @EventConfig(phase=Phase.DECODE, listeners = UIUserWidgets.ChangeOptionActionListener.class)
)
public class UIUserWidgets extends UIContainer {
  
  public UIUserWidgets() throws Exception{
    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
    ls.add(new SelectItemOption<String>("Information", "info")) ;
    ls.add(new SelectItemOption<String>("Calendar", "calendar")) ;
    ls.add(new SelectItemOption<String>("Calculator", "calculator")) ;
    
    UIDropDownItemSelector dropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
    dropDownItemSelector.setOptions(ls);
    dropDownItemSelector.setTitle("Test");
    dropDownItemSelector.setOnChange("ChangeOption");
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIUserWidgets> {
    public void execute(Event<UIUserWidgets> event) throws Exception {
      String instanceId  = event.getRequestContext().getRequestParameter("lable");
      System.out.println("\n\n>>>>>>>>>>>>>>>>>>> "+ instanceId + " <<<<<<<<<<<<<<<<<<\n\n");
    }
  }
  
}
