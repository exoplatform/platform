package org.exoplatform.ecms.upgrade;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.cms.BasePath;
import org.exoplatform.services.cms.impl.DMSConfiguration;
import org.exoplatform.services.cms.impl.DMSRepositoryConfiguration;
import org.exoplatform.services.cms.templates.TemplateService;
import org.exoplatform.services.cms.templates.impl.TemplateConfig;
import org.exoplatform.services.cms.templates.impl.TemplatePlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class UpgradeNodeTypesTemplatesService extends UpgradeProductPlugin {
  private static final Log log = ExoLogger.getLogger(UpgradeNodeTypesTemplatesService.class);

  private TemplateService templateService = null;
  private ConfigurationManager configurationManager = null;
  private RepositoryService repositoryService = null;
  private DMSConfiguration dmsConfiguration = null;
  private String cmsTemplatesBasePath = null;
  private List<String> pluginNamesList = new ArrayList<String>();

  private String oldVersion;

  public UpgradeNodeTypesTemplatesService(RepositoryService repositoryService, NodeHierarchyCreator nodeHierarchyCreator,
      DMSConfiguration dmsConfiguration, TemplateService templateService, ConfigurationManager configurationManager,
      InitParams initParams) {
    super(initParams);
    this.templateService = templateService;
    this.configurationManager = configurationManager;
    this.repositoryService = repositoryService;
    this.dmsConfiguration = dmsConfiguration;

    // List of eXo predefined Templates component-plugin names,
    // that contains the DocumentType Templates
    // that we want to be upgraded each Platform upgrade
    List<?> pluginNamesList = initParams.getValuesParam("plugin-names").getValues();
    for (Object object : pluginNamesList) {
      this.pluginNamesList.add((String) object);
    }
    cmsTemplatesBasePath = nodeHierarchyCreator.getJcrPath(BasePath.CMS_TEMPLATES_PATH);
    if (cmsTemplatesBasePath == null) {
      throw new RuntimeException("UpgradeNodeTypesTemplatesService: " + BasePath.CMS_TEMPLATES_PATH + " path couldn't be found");
    }
  }

  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      this.oldVersion = oldVersion;

      // Begin: Get all TemplateService declared componentPlugins

      // Get ExternalComponentPlugins
      ExternalComponentPlugins externalComponentPlugins = configurationManager.getConfiguration().getExternalComponentPlugins(
          TemplateService.class.getName());
      List<ComponentPlugin> componentPlugins = new ArrayList<ComponentPlugin>();
      if (externalComponentPlugins != null && externalComponentPlugins.getComponentPlugins() != null) {
        log.info("add Template Services externalComponentPlugins");
        componentPlugins.addAll(externalComponentPlugins.getComponentPlugins());
      }

      // Get inner ComponentPlugins
      Component component = configurationManager.getComponent(TemplateService.class);
      if (component.getComponentPlugins() != null) {
        log.info("add Template Services componentPlugins");
        componentPlugins.addAll(component.getComponentPlugins());
      }

      // End: Get all TemplateService declared componentPlugins

      for (ComponentPlugin componentPlugin : componentPlugins) {
        log.info("Begin processing Component Plugin '" + componentPlugin.getName() + "'...");
        // Test if The ComponentPlugin is declared as "upgradable"
        if (!componentPlugin.getType().equals(TemplatePlugin.class.getName())
            || !pluginNamesList.contains(componentPlugin.getName())) {
          log.info("not upgradable component plugin");
          continue;
        }

        // Begin: Read ComponentPlugin's initParams
        InitParams initParams = componentPlugin.getInitParams();
        ValueParam locationParam = initParams.getValueParam("storedLocation");
        String storedLocation = locationParam.getValue();
        ValueParam param = initParams.getValueParam("autoCreateInNewRepository");
        boolean autoCreateInNewRepository = false;
        if (param != null) {
          autoCreateInNewRepository = Boolean.parseBoolean(param.getValue());
        }
        List<TemplateConfig> templatesConfig = new ArrayList<TemplateConfig>();
        Iterator<?> iter = initParams.getObjectParamIterator();
        while (iter.hasNext()) {
          Object object = ((ObjectParameter) iter.next()).getObject();
          if (object instanceof TemplateConfig) {
            templatesConfig.add((TemplateConfig) object);
          }
        }
        // End: Read ComponentPlugin's initParams

        if (autoCreateInNewRepository) { // if templates are defined in multiple repositories
          log.info("templates are defined in multiple repositories, upgrade in all repos");
          List<RepositoryEntry> repositories = repositoryService.getConfig().getRepositoryConfigurations();
          for (RepositoryEntry repo : repositories) {
            log.info("upgrade templates in '" + repo.getName() + "' repository");
            upgradePredefinedTemplates(repo.getName(), templatesConfig, storedLocation);
          }
        } else { // if templates are defined in a single repository
          ValueParam valueParam = initParams.getValueParam("repository");
          String repository = valueParam != null ? valueParam.getValue() : repositoryService.getCurrentRepository()
              .getConfiguration().getName();
          log.info("upgrade templates in '" + repository + "' repository");
          upgradePredefinedTemplates(repository, templatesConfig, storedLocation);
        }
      }
    } catch (Exception exception) {
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      log.error(sw.toString());
    }
  }

  private void upgradePredefinedTemplates(String repositoryName, List<TemplateConfig> templatesConfig, String storedLocation)
      throws Exception {
    // Begin: Get system session on dms-system workspace
    ManageableRepository repository = repositoryService.getRepository(repositoryName);
    DMSRepositoryConfiguration dmsRepoConfig = dmsConfiguration.getConfig();
    String workspace = dmsRepoConfig.getSystemWorkspace();
    Session session = repository.getSystemSession(workspace);
    // End: Get system session on dms-system workspace

    Node templatesHome = (Node) session.getItem(cmsTemplatesBasePath);
    for (TemplateConfig templateConfig : templatesConfig) {
      List<TemplateConfig.NodeType> nodetypes = templateConfig.getNodeTypes();
      for (Object object : nodetypes) {
        TemplateConfig.NodeType nodeType = (TemplateConfig.NodeType) object;

        // Add new version of all declared dialogs
        if (log.isDebugEnabled()) {
          log.debug("upgrade dialogs for nodeType '" + nodeType.getNodetypeName() + "'");
        }
        List dialogs = nodeType.getReferencedDialog();
        updateTemplateContent(storedLocation, nodeType, dialogs, TemplatePlugin.DIALOGS, templatesHome);

        // Add new version of all declared views
        if (log.isDebugEnabled()) {
          log.debug("upgrade views for nodeType '" + nodeType.getNodetypeName() + "'");
        }
        List views = nodeType.getReferencedView();
        updateTemplateContent(storedLocation, nodeType, views, TemplatePlugin.VIEWS, templatesHome);

        // Add new version of all declared skins
        if (log.isDebugEnabled()) {
          log.debug("upgrade skins for nodeType '" + nodeType.getNodetypeName() + "'");
        }
        List skins = nodeType.getReferencedSkin();
        if (skins != null) {
          updateTemplateContent(storedLocation, nodeType, skins, TemplatePlugin.SKINS, templatesHome);
        }
      }
    }
    session.logout();
  }

  private void updateTemplateContent(String basePath, TemplateConfig.NodeType nodeType, List templates, String templateType,
      Node templatesHome) throws Exception {
    for (Iterator iterator = templates.iterator(); iterator.hasNext();) {
      TemplateConfig.Template template = (TemplateConfig.Template) iterator.next();
      String templateFileName = template.getTemplateFile();
      InputStream in = configurationManager.getInputStream(basePath + templateFileName);
      String templateNodeName = templateFileName.substring(templateFileName.lastIndexOf("/") + 1,
          templateFileName.lastIndexOf("."));
      if (!templatesHome.hasNode(nodeType.getNodetypeName())) {
        continue;
      }
      Node nodeTypeHome = templatesHome.getNode(nodeType.getNodetypeName());
      Node specifiedTemplatesHome = nodeTypeHome.getNode(templateType);
      if (specifiedTemplatesHome.hasNode(templateNodeName)) {
        Node templateNode = specifiedTemplatesHome.getNode(templateNodeName);

        // Store the old Template content into a version
        super.addNodeVersion(templateNode, oldVersion);

        // Update the template content by the new one
        templateService.addTemplate(templateType, nodeType.getNodetypeName(), nodeType.getLabel(),
            nodeType.getDocumentTemplate(), templateNodeName, template.getParsedRoles(), in, templatesHome);
      } else {
        throw new IllegalStateException("Template for :" + templateNodeName + "not found");
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
    return true;
  }

}
