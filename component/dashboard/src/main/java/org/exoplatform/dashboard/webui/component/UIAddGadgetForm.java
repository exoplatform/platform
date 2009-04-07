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
package org.exoplatform.dashboard.webui.component;

import java.net.URI;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.portal.application.UserGadgetStorage;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.GadgetUtil;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.rss.parser.DefaultRSSChannel;
import org.exoplatform.services.rss.parser.DefaultRSSItem;
import org.exoplatform.services.rss.parser.RSSDocument;
import org.exoplatform.services.rss.parser.RSSParser;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.URLValidator;

/**
 * Created by The eXo Platform SAS
 * Oct 15, 2008  
 */

@ComponentConfig (
    lifecycle = UIFormLifecycle.class,
    template = "classpath:groovy/dashboard/webui/component/UIAddGadgetForm.gtmpl",
    events = @EventConfig (listeners = UIAddGadgetForm.AddGadgetByUrlActionListener.class)
)

public class UIAddGadgetForm extends UIForm {
  
  public static String FIELD_URL = "url" ;
  
  public UIAddGadgetForm() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_URL, FIELD_URL, null)) ;
  }
  
  static public class AddGadgetByUrlActionListener extends EventListener<UIAddGadgetForm> {
    public void execute(final Event<UIAddGadgetForm> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext() ;
      UIAddGadgetForm uiForm = event.getSource() ;
      
      UIDashboard uiDashboard = uiForm.getAncestorOfType(UIDashboard.class) ;
      UIDashboardContainer uiContainer = uiDashboard.getChild(UIDashboardContainer.class) ;
      
      GadgetRegistryService service = uiForm.getApplicationComponent(GadgetRegistryService.class) ;
      String url = uiForm.getUIStringInput(FIELD_URL).getValue();
      UIApplication uiApplication = context.getUIApplication() ;
      if(url == null || url.trim().length() == 0) {
        uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.required", null)) ;
        return ;
      }
      url = url.trim() ;
      if(!url.matches(URLValidator.URL_REGEX)) {
        uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.notUrl", null)) ;
        return ;
      }
      Gadget gadget;
      UIGadget uiGadget;

      //TODO check the way we create the unique ID, is it really unique?
      try {
        String name = "gadget" + url.hashCode();
        gadget = GadgetUtil.toGadget(name, url, false) ;
        service.saveGadget(gadget);

        StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE);
        windowId.append("#").append(context.getRemoteUser());
        windowId.append(":/dashboard/").append(gadget.getName()).append('/');
        uiGadget = uiForm.createUIComponent(context, UIGadget.class, null, null);
        //TODO why do we do +1
        uiGadget.setId(Integer.toString(uiGadget.hashCode()+1));
        windowId.append(uiGadget.hashCode());
        uiGadget.setApplicationInstanceId(windowId.toString());
      }  catch (Exception e) {
        try {
          URI uri = URI.create(url) ;
          RSSParser parser = new RSSParser() ;
          RSSDocument<DefaultRSSChannel, DefaultRSSItem> doc = parser.createDocument(uri, "UTF-8") ;
          if(doc == null) throw new Exception("Wrong url") ;
        } catch (Exception e1) {
          uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.notUrl", null)) ;
          return ;
        }
        
        String aggregatorId = uiDashboard.getChild(UIDashboardSelectContainer.class).getAggregatorId() ;
        gadget = service.getGadget(aggregatorId);
        //TODO make sure it's an rss feed
        // TODO make sure that we did not add it already
        StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE);
        windowId.append("#").append(context.getRemoteUser());
        windowId.append(":/dashboard/").append(gadget.getName()).append('/');
        uiGadget = uiForm.createUIComponent(context, UIGadget.class, null, null);
        uiGadget.setId(Integer.toString(url.hashCode()+1));
        windowId.append(url.hashCode());
        uiGadget.setApplicationInstanceId(windowId.toString());

        String params = "{'rssurl':'" + url + "'}";

        UserGadgetStorage userGadgetStorage = uiForm.getApplicationComponent(UserGadgetStorage.class);
        userGadgetStorage.save(Util.getPortalRequestContext().getRemoteUser(), gadget.getName(), "" + url.hashCode(), UIGadget.PREF_KEY, params);
      }
      

      uiContainer.addUIGadget(uiGadget, 0, 0) ;
      uiContainer.save() ;
      uiForm.reset() ;
      context.addUIComponentToUpdateByAjax(uiForm) ;
      context.addUIComponentToUpdateByAjax(uiContainer) ;
    }
    
  }
  
}
