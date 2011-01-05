package org.exoplatform.platform.migration.aio.backup.exporter;

import java.util.Arrays;
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
import org.exoplatform.services.jcr.datamodel.ValueData;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.NodeDataOrderComparator;
import org.exoplatform.services.jcr.impl.dataflow.PropertyDataOrderComparator;
import org.exoplatform.services.jcr.impl.dataflow.TransientPropertyData;
import org.exoplatform.services.jcr.impl.dataflow.TransientValueData;
import org.exoplatform.services.jcr.impl.xml.exporting.WorkspaceSystemViewStreamExporter;

public class CollaborationWorkspaceStreamExporter extends WorkspaceSystemViewStreamExporter {

  private boolean deleteWCMServicesLogNodes;

  private static List<String> wcmServicesLogNodesNames = null;
  static {
    wcmServicesLogNodesNames = Arrays.asList(new String[] { "WCMContentInitializerService", "ContentInitializerService", "NewsletterInitializationService" });
  }

  public CollaborationWorkspaceStreamExporter(XMLStreamWriter writer, ItemDataConsumer dataManager, NamespaceRegistry namespaceRegistry, ValueFactoryImpl systemValueFactory, boolean skipBinary, boolean noRecurse, boolean deleteWCMServicesLogNodes) throws NamespaceException, RepositoryException {
    super(writer, dataManager, namespaceRegistry, systemValueFactory, skipBinary, noRecurse);
    this.deleteWCMServicesLogNodes = deleteWCMServicesLogNodes;
  }

  public void visit(NodeData node) throws RepositoryException {
    try {
      String nodeName = getExportName(node, false);
      if (this.deleteWCMServicesLogNodes && (currentLevel == 2) && wcmServicesLogNodesNames.contains(nodeName)) {
        return;
      }
      entering(node, currentLevel);
      if ((maxLevel == -1) || (currentLevel < maxLevel)) {
        currentLevel++;

        List<PropertyData> properties = dataManager.getChildPropertiesData(node);
        Collections.sort(properties, new PropertyDataOrderComparator());
        int index = 0;
        TransientPropertyData propertyToModifyData = null;
        while (index < properties.size() && propertyToModifyData == null) {
          PropertyData propertyData = properties.get(index);
          if (getExportName(propertyData, false).equals("exo:emailAddress")) {
            propertyToModifyData = new TransientPropertyData(propertyData.getQPath(), propertyData.getIdentifier(), propertyData.getPersistedVersion(), propertyData.getType(), propertyData.getParentIdentifier(), true);
            propertyToModifyData.setValues(propertyData.getValues());
            properties.remove(index);
            properties.add(index, propertyToModifyData);
          }
          if (getExportName(propertyData, false).equals("exo:lifecyclePhase")) {
            propertyToModifyData = new TransientPropertyData(propertyData.getQPath(), propertyData.getIdentifier(), propertyData.getPersistedVersion(), propertyData.getType(), propertyData.getParentIdentifier(), true);
            propertyToModifyData.setValues(propertyData.getValues());
            properties.remove(index);
            properties.add(index, propertyToModifyData);
            List<ValueData> list = propertyToModifyData.getValues();
            assert (list.size() == 1);
            ValueData valueData = list.get(0);
            String value = new String(valueData.getAsByteArray());
            TransientValueData newValueData = null;
            if (value.equals("add")) {
              newValueData = new TransientValueData("node_added");
              list.clear();
              list.add(newValueData);
            } else if (value.equals("remove")) {
              newValueData = new TransientValueData("node_remove");
              list.clear();
              list.add(newValueData);
            }
          }
          index++;
        }
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