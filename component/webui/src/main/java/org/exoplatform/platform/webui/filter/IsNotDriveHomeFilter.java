package org.exoplatform.platform.webui.filter;

import java.util.Map;

import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.webui.ext.filter.UIExtensionAbstractFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

/**
 * This filter compares current node path with the drive home path. 
 * If not equals, it's ok, filter returns TRUE.
 * 
 * @author Clement
 *
 */
public class IsNotDriveHomeFilter extends UIExtensionAbstractFilter {


  public IsNotDriveHomeFilter() {
    this("UIActionBar.msg.cannot-action-in-rootnode");
  }

  public IsNotDriveHomeFilter(String messageKey) {
    super(messageKey, UIExtensionFilterType.MANDATORY);
  }

  public boolean accept(Map<String, Object> context) throws Exception {
    boolean accepted = false;
    UIJCRExplorer uiJcrExplorer = ((UIJCRExplorer) context.get(UIJCRExplorer.class.getName()));
    if(uiJcrExplorer != null) {
      String currentPath = uiJcrExplorer.getCurrentPath();
      String rootPath = uiJcrExplorer.getRootPath();
      accepted = ! currentPath.equals(rootPath);
    }
    return accepted;
  }

  public void onDeny(Map<String, Object> context) throws Exception {
    createUIPopupMessages(context, messageKey);
  }
}

