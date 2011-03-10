package org.exoplatform.platform.webui.extension;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.ecm.webui.component.admin.manager.UIAbstractManager;
import org.exoplatform.ecm.webui.component.admin.manager.UIAbstractManagerComponent;
import org.exoplatform.ecm.webui.component.explorer.UIConfirmMessage;
import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.ecm.webui.component.explorer.UIWorkingArea;
import org.exoplatform.ecm.webui.component.explorer.control.filter.IsNotRootNodeFilter;
import org.exoplatform.ecm.webui.component.explorer.control.listener.UIActionBarActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilters;

@ComponentConfig(events = { @EventConfig(listeners = DeleteNodeComponent.DeleteNodeActionListener.class) })
public class DeleteNodeComponent extends UIAbstractManagerComponent {

  private static final List<UIExtensionFilter> FILTERS = Arrays.asList(new UIExtensionFilter[] { new IsNotRootNodeFilter() });

  @UIExtensionFilters
  public static List<UIExtensionFilter> getFilters() {
    return FILTERS;
  }

  @Override
  public Class<? extends UIAbstractManager> getUIAbstractManagerClass() {
    return null;
  }

  public static class DeleteNodeActionListener extends UIActionBarActionListener<DeleteNodeComponent> {
    public void processEvent(Event<DeleteNodeComponent> event) throws Exception {
      UIJCRExplorer uiExplorer = event.getSource().getAncestorOfType(UIJCRExplorer.class);
      String nodePath = uiExplorer.getCurrentWorkspace() + ":" + uiExplorer.getCurrentPath();

      UIPopupContainer UIPopupContainer = uiExplorer.getChild(UIPopupContainer.class);
      UIConfirmMessage uiConfirmMessage = uiExplorer.createUIComponent(UIConfirmMessage.class, null, null);
      uiConfirmMessage.setMessageKey("UIWorkingArea.msg.confirm-delete");
      uiConfirmMessage.setArguments(new String[] { nodePath });
      uiConfirmMessage.setNodePath(nodePath);
      UIPopupContainer.activate(uiConfirmMessage, 500, 180);

      event.getRequestContext().addUIComponentToUpdateByAjax(UIPopupContainer);
    }
  }
}
