/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.control;

import java.io.Writer;

import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 10, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
      id = "UIControlWorkspace",
      template = "app:/groovy/portal/webui/component/control/UIControlWorkspace.gtmpl"
  ),  
  @ComponentConfig(
      type = UIContainer.class,
      id = "UIMyPortal",
      template = "system:/groovy/portal/webui/component/widget/UIMyPortal.gtmpl"
  )
})
public class UIControlWorkspace extends UIWorkspace {
  
  public  static String WORKING_AREA_ID = "UIControlWSWorkingArea" ;

  public UIControlWorkspace() throws Exception {
    //addChild(UIPortalControlPanel.class, null, "UIPortalControlPanel").setRendered(false) ;
    addChild(UIExoStart.class, null, null) ;

    UIControlWSWorkingArea uiWorking = addChild(UIControlWSWorkingArea.class, null, WORKING_AREA_ID);
    uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
  } 

  @ComponentConfig()
  static public class UIControlWSWorkingArea extends UIComponentDecorator {

    public UIControlWSWorkingArea() throws Exception {
      super();
    }

    public void processRender(WebuiRequestContext context) throws Exception {      
      Writer w =  context.getWriter() ;
      w.write("<div id=\"") ; w.write(getId()); w.write("\">");
      super.renderChildren();
      w.write("</div>");
    }

  }

}

