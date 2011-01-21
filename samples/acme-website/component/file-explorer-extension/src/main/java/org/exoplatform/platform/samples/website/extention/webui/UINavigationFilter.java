package org.exoplatform.platform.samples.website.extention.webui;

import java.util.Map;

import javax.jcr.Node;

import org.exoplatform.webui.ext.filter.UIExtensionAbstractFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

public class UINavigationFilter extends UIExtensionAbstractFilter {
  /**
   * This method checks if the current node is of the right type
   */
  public boolean accept(Map<String, Object> context) throws Exception {
    // Retrieve the current node from the context
    Node currentNode = (Node) context.get(Node.class.getName());
    return currentNode.isNodeType("exo:taxonomy");
  }

  /**
   * This is the type of the filter
   */
  public UIExtensionFilterType getType() {
    return UIExtensionFilterType.MANDATORY;
  }

  /**
   * This is called when the filter has failed
   */
  public void onDeny(Map<String, Object> context) throws Exception {
    System.out.println("This document has been rejected");
  }

}
