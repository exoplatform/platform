/*
 * Copyright (C) 2003-2016 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.test;

import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.commons.chain.Context;

import org.exoplatform.services.command.action.Action;

/**
 * Created by The eXo Platform SAS Author : Boubaker Khanfir
 * bkhanfir@exoplatform.com April 16, 2016
 */
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
