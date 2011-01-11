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

public class KnowledgeWorkspaceStreamExporter extends WorkspaceSystemViewStreamExporter {

  private static final String FAQ_VIEWER_TEMPLATE_NODE_NAME = "UIFAQViewer";
  private static final String FAQ_SERVICE_NODE_NAME = "faqApp";
  private static final String FORUM_SERVICE_NODE_NAME = "ForumService";
  private static final String EXO_APPLICATIONS_NODE_NAME = "exo:applications";

  public KnowledgeWorkspaceStreamExporter(XMLStreamWriter writer, ItemDataConsumer dataManager, NamespaceRegistry namespaceRegistry, ValueFactoryImpl systemValueFactory, boolean skipBinary, boolean noRecurse) throws NamespaceException, RepositoryException {
    super(writer, dataManager, namespaceRegistry, systemValueFactory, skipBinary, noRecurse);
  }

  public void visit(NodeData node) throws RepositoryException {
    try {
      String nodeName = getExportName(node, false);
      if ((currentLevel == 1) && !nodeName.equals(EXO_APPLICATIONS_NODE_NAME)) {
        return;
      }
      if ((currentLevel == 2) && !(nodeName.equals(FORUM_SERVICE_NODE_NAME) || nodeName.equals(FAQ_SERVICE_NODE_NAME))) {
        return;
      }
      if ((currentLevel == 4) && nodeName.equals(FAQ_VIEWER_TEMPLATE_NODE_NAME)) {
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
    } catch (Exception re) {
      currentLevel = 0;
      throw new RuntimeException(re);
    }
  }
}