package org.exoplatform.platform.component;

import org.exoplatform.forum.create.*;
import org.exoplatform.forum.create.UICreateForm;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.wcm.webui.Utils;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.ext.UIExtension;
import org.exoplatform.webui.ext.UIExtensionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */
@ComponentConfig(
        template = "app:/groovy/platformNavigation/portlet/UICreatePlatformToolBarPortlet/UICreateList.gtmpl",
        events = {
                @EventConfig(listeners = UICreateList.CancelActionListener.class),
                @EventConfig(listeners = UICreateList.QuickAddActionListener.class)
        }
)

public class UICreateList extends UIContainer {
    private static Log LOG = ExoLogger.getLogger(UICreateList.class);
    static String parStatus;

  private final UIExtensionManager uiExtensionManager;
  private final List<UIExtension> extensions;

  public UICreateList() {
    uiExtensionManager = getApplicationComponent(UIExtensionManager.class);
    List<UIExtension> extensions = uiExtensionManager.getUIExtensions(UICreateList.class.getName());
    if (extensions != null) {
      this.extensions = Collections.unmodifiableList(extensions);
    } else {
      this.extensions = Collections.emptyList();
    }
  }

  public List<UIExtension> getExtensions() {
    List<UIExtension> list = new ArrayList<>();
    for (UIExtension ui : this.extensions) {
      if (uiExtensionManager.accept(ui.getType(), ui.getName(), null)) {
        list.add(ui);
      }
    }
    return list;
  }


    public static void remove(UICreateList uiform) {
        List<UIComponent> uilist = uiform.getChildren();
        List<String> lisID = new ArrayList<String>();
        if (uilist.size() != 0) {
            for (UIComponent uIComponent : uilist) {
                lisID.add(uIComponent.getId());
            }
            for (String id : lisID) {
                uiform.removeChildById(id);

            }
        }
    }

  public static class QuickAddActionListener extends EventListener<UICreateList> {
    @Override
    public void execute(Event<UICreateList> event) throws Exception {
      UICreateList uiList = event.getSource();
      UICreatePlatformToolBarPortlet uiParent = uiList.getAncestorOfType(UICreatePlatformToolBarPortlet.class);
      remove(uiList);
      String extensionName = event.getRequestContext().getRequestParameter("objectId");
      UIExtension extension = uiList.uiExtensionManager.getUIExtension(UICreateList.class.getName(), extensionName);
      if (extension != null) {
        if (extension.getComponent().equals(UIUploadComponent.class)) {
          String title = WebuiRequestContext.getCurrentInstance().getApplicationResourceBundle().getString("UIUploadFile.Select");
          if ((title == null) || (title.equals(""))) {
            title = "Select File";
          }
          UIUploadComponent selector = uiList.createUIComponent(UIUploadComponent.class, null, title);
          selector.setTitle(title);
          Utils.createPopupWindow(uiList, selector, "UploadFileSelectorPopUpWindow", 335);
        } else {
          UIComponent component = uiList.addChild(extension.getComponent(), null, null);
          if (component != null) {
              //
              if (component instanceof org.exoplatform.forum.create.UICreateForm) {
                UICreateForm form = (UICreateForm)component;
                String par;
                try {
                  par = event.getRequestContext().getApplicationResourceBundle().getString("UICreateList.label." + extensionName);
                } catch (MissingResourceException ex) {
                  par = parStatus;
                }
                form.setParStatus(par);
              }
              event.getRequestContext().addUIComponentToUpdateByAjax(uiParent);
              event.getRequestContext().getJavascriptManager().require("SHARED/navigation-toolbar", "toolbarnav").addScripts("toolbarnav.UIPortalNavigation.ClickActionButton('" + uiParent.getId() + "') ;");
          }
        }
      }
    }
  }

  static public class CancelActionListener extends EventListener<UICreateList> {
    public void execute(Event<UICreateList> event)
            throws Exception {
      UICreatePlatformToolBarPortlet uiParent = event.getSource().getAncestorOfType(UICreatePlatformToolBarPortlet.class);
      UICreateList uiSource = event.getSource();
      WebuiRequestContext context = event.getRequestContext();
      remove(uiSource);
      context.addUIComponentToUpdateByAjax(uiSource);
      context.addUIComponentToUpdateByAjax(uiParent);
      event.getRequestContext().getJavascriptManager().require("SHARED/navigation-toolbar", "toolbarnav").addScripts("toolbarnav.UIPortalNavigation.ClickActionButton('"+uiParent.getId()+"') ;");
    }
  }

    public static String getParStatus() {
        return parStatus;
    }

    public String[] getActions() {
        return new String[]{"Topic", "Poll", "AddEvent", "Wiki", "Upload"};
    }
}
