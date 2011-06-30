package org.exoplatform.platform.samples.website.extention.webui;

import java.net.URLEncoder;

import org.exoplatform.ecm.webui.utils.Utils;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;

@ComponentConfig(template = "system:/groovy/webui/core/UITree.gtmpl", events = @EventConfig(listeners = UITree.ChangeNodeActionListener.class))
/**
 * This class extend <code>org.exoplatform.webui.core.UITree</code> to render
 * node tree for <code>org.exoplatform.portal.config.model.UserNode</code>
 */
public class UINavigationTree extends UITree {

  /*
   * render nodetype icon for node in tree
   * 
   * @see org.exoplatform.webui.core.UITree#renderNode(java.lang.Object)
   */
  public String renderNode(Object obj) throws Exception {

    String nodeTypeIcon = ("DefaultPageIcon");
    String nodeIcon = this.getExpandIcon();
    String iconGroup = this.getIcon();
    String note = "";
    if (isSelected(obj)) {
      nodeIcon = getColapseIcon();
      iconGroup = getSelectedIcon();
      note = " NodeSelected";
    }
    String beanIconField = getBeanIconField();
    if (beanIconField != null && beanIconField.length() > 0) {
      if (getFieldValue(obj, beanIconField) != null)
        iconGroup = (String) getFieldValue(obj, beanIconField);
    }
    String objId = URLEncoder.encode(Utils.formatNodeName(String.valueOf(getId(obj))), "utf-8");
    String actionLink = event("ChangeNode", objId);
    StringBuilder builder = new StringBuilder();
    if (nodeIcon.equals(getExpandIcon())) {
      builder.append(" <a class=\"").append(nodeIcon).append(" ").append(nodeTypeIcon).append("\" href=\"").append(actionLink).append("\">");
    } else {
      builder.append(" <a class=\"")
             .append(nodeIcon)
             .append(" ")
             .append(nodeTypeIcon)
             .append("\" onclick=\"eXo.portal.UIPortalControl.collapseTree(this)")
             .append("\">");
    }
    UIRightClickPopupMenu popupMenu = getUiPopupMenu();
    String beanFieldValue = getDisplayFieldValue(obj);
    String className = "NodeIcon";
    if (popupMenu == null) {
      builder.append(" <div class=\"")
             .append(className)
             .append(" ")
             .append(iconGroup)
             .append(" ")
             .append(nodeTypeIcon)
             .append(note)
             .append("\"")
             .append(" title=\"")
             .append(beanFieldValue)
             .append("\"")
             .append(">")
             .append(beanFieldValue)
             .append("</div>");
    } else {
      builder.append(" <div class=\"")
             .append(className)
             .append(" ")
             .append(iconGroup)
             .append(" ")
             .append(nodeTypeIcon)
             .append(note)
             .append("\" ")
             .append(popupMenu.getJSOnclickShowPopup(objId, null))
             .append(" title=\"")
             .append(beanFieldValue)
             .append("\"")
             .append(">")
             .append(beanFieldValue)
             .append("</div>");
    }
    builder.append(" </a>");
    return builder.toString();
  }

  private String getDisplayFieldValue(Object bean) throws Exception {
    return String.valueOf(getFieldValue(bean, getBeanLabelField()));
  }

}
