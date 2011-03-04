package org.exoplatform.platform.migration.aio.backup.exporter;

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
import org.exoplatform.services.jcr.impl.xml.exporting.WorkspaceSystemViewStreamExporter;

public class SystemWorkspaceStreamExporter extends WorkspaceSystemViewStreamExporter {

  public SystemWorkspaceStreamExporter(XMLStreamWriter writer, ItemDataConsumer dataManager, NamespaceRegistry namespaceRegistry, ValueFactoryImpl systemValueFactory, boolean skipBinary, boolean noRecurse) throws NamespaceException, RepositoryException {
    super(writer, dataManager, namespaceRegistry, systemValueFactory, skipBinary, noRecurse);
  }

  public void visit(NodeData node) throws RepositoryException {
    try {
      if (currentLevel == 1 && !getExportName(node, false).equals("jcr:system")) {
        return;
      }
      if (currentLevel == 2 && !getExportName(node, false).equals("jcr:versionStorage") && !getExportName(node, false).equals("exo:ecm")) {
        return;
      }
      entering(node, currentLevel);
      if ((maxLevel == -1) || (currentLevel < maxLevel)) {
        currentLevel++;

        List<PropertyData> properties = dataManager.getChildPropertiesData(node);

        Collections.sort(properties, new PropertyDataOrderComparator());
        for (PropertyData data : properties) {
          InternalQName propName = data.getQPath().getName();

          if (Constants.JCR_LOCKISDEEP.equals(propName) || Constants.JCR_LOCKOWNER.equals(propName)) {
            continue;
          }
          data.accept(this);
        }
        if (!isNoRecurse() && (currentLevel > 0)) {
          List<NodeData> nodes = dataManager.getChildNodesData(node);
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
