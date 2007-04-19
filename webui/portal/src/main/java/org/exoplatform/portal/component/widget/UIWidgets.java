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
  template = "system:/groovy/portal/webui/component/widget/UIWidgets.gtmpl" ,
  events = @EventConfig(phase=Phase.DECODE, listeners = UIWidgets.ChangeOptionActionListener.class)
)
public class UIWidgets extends UIContainer {
  
  public UIWidgets() throws Exception{
    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
    ls.add(new SelectItemOption<String>("SQL0", "sql0")) ;
    ls.add(new SelectItemOption<String>("SQL1", "sql1")) ;
    ls.add(new SelectItemOption<String>("SQL2", "sql2")) ;
    ls.add(new SelectItemOption<String>("SQL3", "sql3")) ;
    ls.add(new SelectItemOption<String>("SQL4", "sql4")) ;
    ls.add(new SelectItemOption<String>("SQL5", "sql5")) ;
    
    ls.add(new SelectItemOption<String>("xPath0", "xpath0")) ;
    ls.add(new SelectItemOption<String>("xPath1", "xpath1")) ;
    ls.add(new SelectItemOption<String>("xPath2", "xpath2")) ;
    ls.add(new SelectItemOption<String>("xPath3", "xpath3")) ;
    ls.add(new SelectItemOption<String>("xPath4", "xpath4")) ;
    ls.add(new SelectItemOption<String>("xPath5", "xpath5")) ;
    
    
    UIDropDownItemSelector dropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
    dropDownItemSelector.setOptions(ls);
    dropDownItemSelector.setTitle("Test");
    dropDownItemSelector.setOnChange("ChangeOption");
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIWidgets> {
    public void execute(Event<UIWidgets> event) throws Exception {
      String instanceId  = event.getRequestContext().getRequestParameter("lable");
      System.out.println("\n\n>>>>>>>>>>>>>>>>>>> "+ instanceId + " <<<<<<<<<<<<<<<<<<\n\n");
    }
  }
  
}
