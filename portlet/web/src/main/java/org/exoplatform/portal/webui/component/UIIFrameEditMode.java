/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import javax.portlet.PortletPreferences;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * August 15, 2007 9:10:53 AM
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
    events = {
      @EventConfig(listeners = UIIFrameEditMode.SaveActionListener.class)
    }
)
public class UIIFrameEditMode extends UIForm {

  final static public String FIELD_URL = "iframeUrl" ;
  
  public UIIFrameEditMode() throws Exception {
    PortletRequestContext pcontext = (PortletRequestContext)WebuiRequestContext.getCurrentInstance() ;
    PortletPreferences pref = pcontext.getRequest().getPreferences();
    addUIFormInput(new UIFormStringInput(FIELD_URL, FIELD_URL, pref.getValue("url", "http://exoplatform.com"))) ;
  }
  
  static public class SaveActionListener extends EventListener<UIIFrameEditMode> {
    public void execute(Event<UIIFrameEditMode> event) throws Exception {
      PortletRequestContext pcontext = (PortletRequestContext)WebuiRequestContext.getCurrentInstance() ;
      PortletPreferences pref = pcontext.getRequest().getPreferences();
      pref.setValue("url", event.getSource().getUIStringInput(FIELD_URL).getValue()) ;
      pref.store() ;
      pcontext.setApplicationMode(PortletRequestContext.VIEW_MODE) ;
    }
  }
}
