/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.migration.bos.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.xml.stream.XMLStreamWriter;

import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.NodeDataOrderComparator;
import org.exoplatform.services.jcr.impl.dataflow.PropertyDataOrderComparator;

/**
 * Created by The eXo Platform MEA Author : Anouar Chattouna anouar.chattouna@exoplatform.com June 07, 2011
 */

public class SystemWorkspaceStreamExporter extends WorkspaceStreamExporter {

  private static final String JCR_SYSTEM = "system";
  private static final String JCR_VERSION_STORAGE = "versionStorage";

  /**
   * Ensures a system backup: Not all contents of system workspace will be backed up, but only the Version History nodes.
   */
  public SystemWorkspaceStreamExporter(XMLStreamWriter writer, ItemDataConsumer dataManager, NamespaceRegistry namespaceRegistry,
      ValueFactoryImpl systemValueFactory, boolean skipBinary, boolean noRecurse) throws NamespaceException, RepositoryException {
    super(writer, dataManager, namespaceRegistry, systemValueFactory, skipBinary, noRecurse);
  }

  /**
   * Redefines the {@Link NodeData} visit behavior. See {@link org.exoplatform.services.jcr.dataflow.ItemDataVisitor#visit(NodeData)}. 
   * This way we will ensure that the workspace system export will preserve only data from node jcr:root/jcr:systemjcr:versionStorage
   */
  public void visit(NodeData node) throws RepositoryException {
    try {

      // ignore if level==1 and name is not "jcr:system"
      if (currentLevel == 1 && !getExportName(node, false).equals(Constants.NS_JCR_PREFIX + ":" + JCR_SYSTEM)) {
        return;
      }
      // ignore if level==2 and name is not "jcr:versionStorage"
      if (currentLevel == 2 && !getExportName(node, false).equals(Constants.NS_JCR_PREFIX + ":" + JCR_VERSION_STORAGE)) {
        return;
      }

      entering(node, currentLevel);

      if ((maxLevel == -1) || (currentLevel < maxLevel)) {
        currentLevel++;
        List<PropertyData> properties = new ArrayList<PropertyData>(dataManager.getChildPropertiesData(node));
        // Sorting properties
        Collections.sort(properties, new PropertyDataOrderComparator());
        for (PropertyData data : properties) {
          InternalQName propName = data.getQPath().getName();
          if (Constants.JCR_LOCKISDEEP.equals(propName) || Constants.JCR_LOCKOWNER.equals(propName)) {
            continue;
          }
          data.accept(this);
        }
        if (!isNoRecurse() && (currentLevel > 0)) {
          List<NodeData> nodes = new ArrayList<NodeData>(dataManager.getChildNodesData(node));
          // Sorting nodes
          Collections.sort(nodes, new NodeDataOrderComparator());
          for (NodeData data : nodes) {
            data.accept(this);
          }
        }
        currentLevel--;
      }
      leaving(node, currentLevel);
    } catch (RepositoryException re) {
      currentLevel = 0;
      throw re;
    }
  }

}
