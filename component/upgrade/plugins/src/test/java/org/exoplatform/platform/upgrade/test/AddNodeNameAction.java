package org.exoplatform.platform.upgrade.test;

import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.commons.chain.Context;

import org.exoplatform.services.command.action.Action;

public class AddNodeNameAction implements Action {

  @Override
  public boolean execute(Context context) throws Exception {
    Object item = context.get("currentItem");
    Node node = (item instanceof Property) ? ((Property) item).getParent() : (Node) item;
    if (node.isNodeType("nt:resource"))
      node = node.getParent();

    if (node.canAddMixin("exo:sortable")) {
      node.addMixin("exo:sortable");
    }

    if (!node.hasProperty("exo:name")) {
      node.setProperty("exo:name", node.getName());
    }
    return false;
  }

}
