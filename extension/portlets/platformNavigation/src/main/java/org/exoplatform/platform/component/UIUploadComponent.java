package org.exoplatform.platform.component;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.commons.UIDocumentSelector;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */

@ComponentConfig(
        lifecycle = Lifecycle.class,
        template = "app:/groovy/platformNavigation/portlet/UICreatePlatformToolBarPortlet/UIUploadComponent.gtmpl",
        events = {
                @EventConfig(
                        listeners = UIUploadComponent.CancelActionListener.class)
        }
)
public class UIUploadComponent extends UIContainer {
    public UIUploadComponent() throws Exception {
        super();
        UIDocumentSelector selector = addChild(UIDocumentSelector.class, null, null);
    }

    public static class CancelActionListener extends EventListener<UIUploadComponent> {

        public void execute(Event<UIUploadComponent> event)
                throws Exception {

            UIPopupContainer uiparent = event.getSource().getAncestorOfType(UIPopupContainer.class);
            WebuiRequestContext context = event.getRequestContext();
            if (uiparent.getChild(UIPopupWindow.class) != null) {
                uiparent.removeChild(UIPopupWindow.class);
            }

            context.addUIComponentToUpdateByAjax(uiparent);

        }
    }
}
