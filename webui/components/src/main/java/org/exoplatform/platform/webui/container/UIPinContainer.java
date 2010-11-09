package org.exoplatform.platform.webui.container;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
    template = "classpath:groovy/platform/webui/containers/UIPinContainer.gtmpl",
    events = { @EventConfig(listeners = UIPinContainer.PinOrUnpinActionListener.class) }
)
public class UIPinContainer extends UIContainer {
  public boolean pinned = false;

  public boolean isPinned() {
    return this.pinned;
  }

  public void setPinned(boolean pinned) {
    this.pinned = pinned;
  }

  public UIPinContainer() {
    super();
  }

  static public class PinOrUnpinActionListener extends EventListener<UIPinContainer> {
    public void execute(Event<UIPinContainer> event) throws Exception {
      String objectId = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPinContainer container = event.getSource();
      if(objectId != null) {
        if(objectId.equals("true")) {
          container.setPinned(true);
        } else {
          container.setPinned(false);
        }
      }
      WebuiRequestContext context = event.getRequestContext();
      context.addUIComponentToUpdateByAjax(container);
    }
  }
}