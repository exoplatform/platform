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
package org.exoplatform.portal.webui.page;

import java.io.Writer;

import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Sep 5, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(),  
  @ComponentConfig(
      id = "WizardPageEditBar",
      type = UIPageEditBar.class,
      template = "system:/groovy/webui/core/UIToolbar.gtmpl" ,
      events = {
        @EventConfig(listeners = UIPageEditBar.PagePreviewActionListener.class),
        @EventConfig(listeners = UIWizardPageCreationBar.EditContainerActionListener.class),
        @EventConfig(listeners = UIWizardPageCreationBar.EditPortletActionListener.class)
      }
  )
})
public class UIWizardPageCreationBar extends UIContainer {

  public UIWizardPageCreationBar() throws Exception{ 
    addChild(UIPageEditBar.class, "WizardPageEditBar", null);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
  }  

  public <T extends UIComponent> T setRendered(boolean b) { 
    getChild(UIPageEditBar.class).setRendered(false);
    return super.<T>setRendered(b);
  }
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w = context.getWriter();    
    w.append("<div class=\"UICreatePage\" id=\"UIWizardPageCreationBar\">") ;
    w.append("  <div class=\"CreatePageContainer\">") ;
    w.append("    <div class=\"CreatePageBarLeft\">") ;
    w.append("      <div class=\"CreatePageBarRight\">") ;
    w.append("        <div class=\"CreatePageLabel\">" + 
        context.getApplicationResourceBundle().getString("UIAdvancedCustomization.label.UIPageManagement") + 
        "</div>") ;
    w.append("      </div>") ;
    w.append("    </div>") ;
    renderChildren() ;
    w.append("  </div>") ;
    w.append("</div>") ;
  }  

  static public class EditContainerActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
      UIPageEditBar uiEditBar = event.getSource();                
      UIWizardPageCreationBar uiParent = uiEditBar.getParent();
      Class<?> [] childrenToRender ={UIPageEditBar.class, UIContainerConfigOptions.class }; 
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
      Util.updateUIApplication(event);
    }    
  }

  static public class EditPortletActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
      UIPageEditBar uiEditBar = event.getSource();         
      UIWizardPageCreationBar uiParent = uiEditBar.getParent();
      Class<?> [] childrenToRender = { UIPageEditBar.class, UIPortletOptions.class}; 
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
      Util.updateUIApplication(event);
    }
  }

}
