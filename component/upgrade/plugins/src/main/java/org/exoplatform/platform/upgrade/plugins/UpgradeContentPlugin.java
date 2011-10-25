package org.exoplatform.platform.upgrade.plugins;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.deployment.DeploymentDescriptor;
import org.exoplatform.services.deployment.DeploymentPlugin;
import org.exoplatform.services.deployment.WCMContentInitializerService;
import org.exoplatform.services.deployment.plugins.XMLDeploymentPlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.util.ISO9075;
import org.exoplatform.services.jcr.impl.util.NodeTypeRecognizer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class UpgradeContentPlugin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(UpgradeContentPlugin.class);

  private ConfigurationManager configurationManager;
  private RepositoryService repositoryService;
  private List<DeploymentDescriptor> deploymentDescriptors = new ArrayList<DeploymentDescriptor>();
  private Map<String, XMLDeploymentPlugin> deploymentPlugins = new HashMap<String, XMLDeploymentPlugin>();
  private List<String> webappsNames = new ArrayList<String>();

  @SuppressWarnings("unchecked")
  public UpgradeContentPlugin(InitParams initParams, ConfigurationManager configurationManager,
      RepositoryService repositoryService) throws Exception {
    super(initParams);
    this.configurationManager = configurationManager;
    this.repositoryService = repositoryService;
    if (initParams.containsKey("webapps-mames")) {
      webappsNames = initParams.getValuesParam("webapps-mames").getValues();
    }

    Component wcmContentInitializerServiceComponent = configurationManager.getComponent(WCMContentInitializerService.class);
    List<ComponentPlugin> plugins = wcmContentInitializerServiceComponent.getComponentPlugins();
    if (plugins == null) {
      plugins = new ArrayList<ComponentPlugin>();
    }

    ExternalComponentPlugins externalComponentPlugins = configurationManager.getConfiguration().getExternalComponentPlugins(
        WCMContentInitializerService.class.getName());
    plugins.addAll(externalComponentPlugins.getComponentPlugins());
    // read XMLDeploymentPlugin plugins
    for (ComponentPlugin plugin : plugins) {
      if (!plugin.getType().equals(XMLDeploymentPlugin.class.getName()) || plugin.getInitParams() == null) {
        continue;
      }
      InitParams pluginInitParams = plugin.getInitParams();

      // divide each XMLDeploymentPlugin to multiple XMLDeploymentPlugin
      // that each new one will have only one single DeploymentDescriptor

      Iterator<ObjectParameter> objectParamIterator = pluginInitParams.getObjectParamIterator();
      if (objectParamIterator == null) {
        continue;
      }
      while (objectParamIterator.hasNext()) {
        ObjectParameter objectParameter = objectParamIterator.next();
        DeploymentDescriptor deploymentDescriptor = (DeploymentDescriptor) objectParameter.getObject();

        deploymentDescriptors.add(deploymentDescriptor);

        InitParams params = new InitParams();
        params.addParameter(objectParameter);

        String key = getMapKey(deploymentDescriptor);
        XMLDeploymentPlugin deploymentPlugin = new XMLDeploymentPlugin(params, configurationManager, repositoryService);
        deploymentPlugin.setName(plugin.getName());
        deploymentPlugins.put(key, deploymentPlugin);
      }
    }
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    ManageableRepository repository = null;
    try {
      repository = repositoryService.getCurrentRepository();
    } catch (RepositoryException exception1) {
      throw new IllegalStateException("Could not retrieve current repository. Contents upgrade is canceled.");
    }

    for (DeploymentDescriptor deploymentDescriptor : deploymentDescriptors) {

      Session session = null;
      try {
        // Delete invalid plugins
        if (!shouldDeploy(deploymentDescriptor)) {
          deploymentPlugins.remove(getMapKey(deploymentDescriptor));
          continue;
        }

        InputStream inputStream = configurationManager.getInputStream(deploymentDescriptor.getSourcePath());
        String nodeName = getNodeName(inputStream);

        session = sessionProvider.getSession(deploymentDescriptor.getTarget().getWorkspace(), repository);
        if (!session.itemExists(deploymentDescriptor.getTarget().getNodePath())) {
          LOG.warn(deploymentDescriptor.getTarget().getNodePath() + " doesn't exist, could not import "
              + deploymentDescriptor.getSourcePath());
        }
        Node node = (Node) session.getItem(deploymentDescriptor.getTarget().getNodePath());
        if (!node.hasNode(nodeName)) {
          continue;
        }
        LOG.info("Deleting nodes " + deploymentDescriptor.getTarget().getNodePath() + "/" + nodeName + " to be replaced by "
            + deploymentDescriptor.getSourcePath());

        NodeIterator nodeIterator = node.getNodes(nodeName);
        while (nodeIterator.hasNext()) {
          Node targetNode = nodeIterator.nextNode();
          LOG.info("  - Remove " + targetNode.getPath());

          targetNode.remove();
        }

        // get the corresponding XMLDeploymentPlugin
        String key = getMapKey(deploymentDescriptor);
        XMLDeploymentPlugin deploymentPlugin = deploymentPlugins.remove(key);
        deploymentPlugin.deploy(sessionProvider);

        session.save();
      } catch (Exception exception) {
        throw new IllegalStateException("Can't read stream from " + deploymentDescriptor.getSourcePath(), exception);
      } finally {
        if (session != null && session.isLive()) {
          session.logout();
        }
      }
    }
    Collection<XMLDeploymentPlugin> remainingDeploymentPlugins = deploymentPlugins.values();
    for (DeploymentPlugin deploymentPlugin : remainingDeploymentPlugins) {
      try {
        deploymentPlugin.deploy(sessionProvider);
      } catch (Exception exception) {
        LOG.error("Can't proceed to content deployment of deploymentPlugin : " + deploymentPlugin.getName(), exception);
      }
    }
  }

  private boolean shouldDeploy(DeploymentDescriptor deploymentDescriptor) {
    if (webappsNames.isEmpty()) {
      return true;
    }
    try {
      URL url = configurationManager.getResource(deploymentDescriptor.getSourcePath());
      String path = url.getPath();
      for (String webapp : webappsNames) {
        if (path.contains("/" + webapp + "/WEB-INF/")) {
          return true;
        }
      }
    } catch (Exception exception) {
      LOG.error("An error occured while upgrading :" + deploymentDescriptor.getSourcePath(), exception);
    }
    return false;
  }

  private String getMapKey(DeploymentDescriptor deploymentDescriptor) {
    return deploymentDescriptor.getTarget().getWorkspace() + deploymentDescriptor.getTarget().getNodePath()
        + deploymentDescriptor.getSourcePath();
  }

  private String getNodeName(InputStream stream) throws Exception {
    String nodeToImportName = null;
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader reader = null;
    try {
      reader = factory.createXMLEventReader(stream);

      XMLEvent event = null;
      do {
        event = reader.nextEvent();
      } while (reader.hasNext() && (event.getEventType() != XMLStreamConstants.START_ELEMENT));
      if (event.getEventType() != XMLStreamConstants.START_ELEMENT) {
        throw new IllegalStateException("Content isn't lisible");
      }
      StartElement element = event.asStartElement();
      QName name = element.getName();
      switch (NodeTypeRecognizer.recognize(name.getNamespaceURI(), name.getPrefix() + ":" + name.getLocalPart())) {
        case DOCVIEW:
          if (name.getPrefix() == null || name.getPrefix().isEmpty()) {
            nodeToImportName = ISO9075.decode(name.getLocalPart());
          } else {
            nodeToImportName = ISO9075.decode(name.getPrefix() + ":" + name.getLocalPart());
          }
          break;
        case SYSVIEW:
          @SuppressWarnings("rawtypes")
          Iterator attributes = element.getAttributes();
          while (attributes.hasNext() && nodeToImportName == null) {
            Attribute attribute = (Attribute) attributes.next();
            if ((attribute.getName().getNamespaceURI() + ":" + attribute.getName().getLocalPart()).equals(Constants.SV_NAME_NAME
                .getNamespace() + ":" + Constants.SV_NAME_NAME.getName())) {
              nodeToImportName = attribute.getValue();
              break;
            }
          }
          break;
        default:
          throw new IllegalStateException("There was an error during ascertaining the " + "type of document. First element ");
      }
    } finally {
      if (reader != null) {
        reader.close();
        stream.close();
      }
    }
    return nodeToImportName;
  }

  @Override
  public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
    return true;
  }

}
