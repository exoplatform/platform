package org.exoplatform.platform.common.space;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.commons.utils.ExoProperties;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;
import org.exoplatform.services.cms.BasePath;
import org.exoplatform.services.cms.impl.DMSConfiguration;
import org.exoplatform.services.cms.impl.DMSRepositoryConfiguration;
import org.exoplatform.services.cms.impl.Utils;
import org.exoplatform.services.deployment.DeploymentDescriptor;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.services.wcm.core.NodetypeConstant;
import org.exoplatform.social.core.space.SpaceUtils;

public class SpaceCustomizationService {
  final static private Log logger = ExoLogger.getExoLogger(SpaceCustomizationService.class);
  final static private String GROUPS_PATH = "groupsPath";
  private static final String SPACE_GROUP_ID_PREFERENCE = "{spaceGroupId}";
  private static final String SPACE_HOME_PAGE_PORTLET_NAME = "SpaceActivityStreamPortlet";
  private static final String SPACE_NEW_HOME_PAGE_TEMPLATE = "custom space";
  private static final String SCV_PORTLEt_NAME = "SingleContentViewer";

  private NodeHierarchyCreator nodeHierarchyCreator = null;
  private DMSConfiguration dmsConfiguration = null;
  private RepositoryService repositoryService = null;
  private ConfigurationManager configurationManager = null;
  private DataStorage dataStorageService = null;
  private UserPortalConfigService userPortalConfigService = null;
  private UserACL userACL = null;
  private String groupsPath;

  public SpaceCustomizationService(DataStorage dataStorageService_, UserPortalConfigService userPortalConfigService_,
      NodeHierarchyCreator nodeHierarchyCreator_, DMSConfiguration dmsConfiguration_, RepositoryService repositoryService_,
      ConfigurationManager configurationManager_, UserACL userACL_) {
    this.nodeHierarchyCreator = nodeHierarchyCreator_;
    this.dmsConfiguration = dmsConfiguration_;
    this.repositoryService = repositoryService_;
    this.userACL = userACL_;
    this.configurationManager = configurationManager_;
    this.dataStorageService = dataStorageService_;
    this.userPortalConfigService = userPortalConfigService_;
    groupsPath = nodeHierarchyCreator.getJcrPath(GROUPS_PATH);
    if (groupsPath.lastIndexOf("/") == groupsPath.length() - 1) {
      groupsPath = groupsPath.substring(0, groupsPath.lastIndexOf("/"));
    }
  }

  public void editSpaceDriveViewPermissions(String viewNodeName, String permission) throws RepositoryException {
    if (logger.isDebugEnabled()) {
      logger.debug("Trying to add permission " + permission + " for ECMS view " + viewNodeName);
    }
    String viewsPath = nodeHierarchyCreator.getJcrPath(BasePath.CMS_VIEWS_PATH);
    ManageableRepository manageableRepository = repositoryService.getCurrentRepository();
    DMSRepositoryConfiguration dmsRepoConfig = dmsConfiguration.getConfig();
    Session session = manageableRepository.getSystemSession(dmsRepoConfig.getSystemWorkspace());
    Node viewHomeNode = (Node) session.getItem(viewsPath);
    if (viewHomeNode.hasNode(viewNodeName)) {
      Node contentNode = viewHomeNode.getNode(viewNodeName);
      String contentNodePermissions = contentNode.getProperty("exo:accessPermissions").getString();
      contentNode.setProperty("exo:accessPermissions", contentNodePermissions.concat(",").concat(permission));
      viewHomeNode.save();
      if (logger.isDebugEnabled()) {
        logger.debug("Permission " + permission + " added with success to ECMS view " + viewNodeName);
      }
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("Can not find view node: " + viewNodeName);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.deployment.DeploymentPlugin#deploy(org.
   * exoplatform.services.jcr.ext.common.SessionProvider)
   */
  public void deployContentToSpaceDrive(SessionProvider sessionProvider, String spaceId, DeploymentDescriptor deploymentDescriptor)
      throws Exception {

    String sourcePath = deploymentDescriptor.getSourcePath();
    logger.info("Deploying '" + sourcePath + "'content to '" + spaceId + "' Space JCR location");

    // sourcePath should start with: war:/, jar:/, classpath:/, file:/
    Boolean cleanupPublication = deploymentDescriptor.getCleanupPublication();

    InputStream inputStream = configurationManager.getInputStream(sourcePath);
    ManageableRepository repository = repositoryService.getCurrentRepository();
    Session session = sessionProvider.getSession(deploymentDescriptor.getTarget().getWorkspace(), repository);
    String targetNodePath = deploymentDescriptor.getTarget().getNodePath();
    if (targetNodePath.indexOf("/") == 0) {
      targetNodePath = targetNodePath.replaceFirst("/", "");
    }
    if (targetNodePath.lastIndexOf("/") == targetNodePath.length() - 1) {
      targetNodePath = targetNodePath.substring(0, targetNodePath.lastIndexOf("/"));
    }
    // if target path contains folders, then create them
    if (!targetNodePath.equals("")) {
      Node spaceRootNode = (Node) session.getItem(groupsPath + spaceId);
      Utils.makePath(spaceRootNode, targetNodePath, NodetypeConstant.NT_UNSTRUCTURED);
    }
    String fullTargetNodePath = groupsPath + spaceId + "/" + targetNodePath;
    Node parentTargetNode = (Node) session.getItem(fullTargetNodePath);
    NodeIterator nodeIterator = parentTargetNode.getNodes();
    List<String> initialChildNodesUUID = new ArrayList<String>();
    List<String> initialChildNodesNames = new ArrayList<String>();
    while (nodeIterator.hasNext()) {
      Node node = nodeIterator.nextNode();
      String uuid = null;
      try {
        uuid = node.getUUID();
      } catch (Exception exception) {
        // node is not referenceable
        continue;
      }
      initialChildNodesUUID.add(uuid);
      initialChildNodesNames.add(node.getName());
    }

    session.importXML(fullTargetNodePath, inputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);

    parentTargetNode = (Node) session.getItem(fullTargetNodePath);
    nodeIterator = parentTargetNode.getNodes();
    List<ExtendedNode> newChildNodesUUID = new ArrayList<ExtendedNode>();
    while (nodeIterator.hasNext()) {
      ExtendedNode childNode = (ExtendedNode) nodeIterator.nextNode();
      String uuid = null;
      try {
        uuid = childNode.getUUID();
      } catch (Exception exception) {
        // node is not referenceable
        continue;
      }
      // determines wether this is a new node or not
      if (!initialChildNodesUUID.contains(uuid)) {
        if (initialChildNodesNames.contains(childNode.getName())) {
          logger.info(childNode.getName() + " already exists under " + fullTargetNodePath + ". This node will not be imported!");
          childNode.remove();
        } else {
          newChildNodesUUID.add(childNode);
        }
      }
    }
    String spaceMembershipManager = userACL.getAdminMSType() + spaceId;
    for (ExtendedNode extendedNode : newChildNodesUUID) {
      if (extendedNode.isNodeType(NodetypeConstant.EXO_PRIVILEGEABLE)) {
        extendedNode.clearACL();
      } else if (extendedNode.canAddMixin(NodetypeConstant.EXO_PRIVILEGEABLE)) {
        extendedNode.addMixin("exo:privilegeable");
        extendedNode.clearACL();
      } else {
        throw new IllegalStateException("Can't change permissions on node imported to the added Space.");
      }
      extendedNode.setPermission(IdentityConstants.ANY, new String[] { PermissionType.READ });
      extendedNode.setPermission(spaceMembershipManager, PermissionType.ALL);
      if (cleanupPublication) {

        /**
         * This code allows to cleanup the publication lifecycle in the
         * target folder after importing the data. By using this, the
         * publication live revision property will be re-initialized and
         * the content will be set as published directly. Thus, the content
         * will be visible in front side.
         */
        if (extendedNode.hasProperty("publication:liveRevision") && extendedNode.hasProperty("publication:currentState")) {
          logger.info("\"" + extendedNode.getName() + "\" publication lifecycle has been cleaned up");
          extendedNode.setProperty("publication:liveRevision", "");
          extendedNode.setProperty("publication:currentState", "published");
        }
      }
    }

    session.save();
    session.logout();
    logger.info(deploymentDescriptor.getSourcePath() + " is deployed succesfully into " + fullTargetNodePath);
  }

  public void createSpaceHomePage(String spacePrettyName, String spaceGroupId, ExoProperties welcomeSCVCustomPreferences) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      logger.info("Updating '" + spaceGroupId + "' Space Home Page");
      // creates the new home page
      Page oldSpaceHomePage = dataStorageService.getPage(PortalConfig.GROUP_TYPE + "::" + spaceGroupId + "::"
          + SPACE_HOME_PAGE_PORTLET_NAME);
      if (oldSpaceHomePage == null) {
        throw new IllegalStateException(spaceGroupId + " Home page couldn't be found");
      }
      // creates the customized home page for the space and set few fields
      // with values from the old home page
      Page customSpaceHomePage = userPortalConfigService.createPageTemplate(SPACE_NEW_HOME_PAGE_TEMPLATE,
          PortalConfig.GROUP_TYPE, spacePrettyName);
      customSpaceHomePage.setTitle(oldSpaceHomePage.getTitle());
      customSpaceHomePage.setName(oldSpaceHomePage.getName());
      customSpaceHomePage.setAccessPermissions(oldSpaceHomePage.getAccessPermissions());
      customSpaceHomePage.setEditPermission(oldSpaceHomePage.getEditPermission());
      customSpaceHomePage.setOwnerType(PortalConfig.GROUP_TYPE);
      customSpaceHomePage.setOwnerId(spaceGroupId);
      // needs to populate the accessPermissions list to all children:
      // containers and applications
      editChildrenAccesPermisions(customSpaceHomePage.getChildren(), customSpaceHomePage.getAccessPermissions());
      // dataStorageService.save(customSpaceHomePage);
      // mandatory preference "Space_URL" should be added to the home page
      // applications
      editSpaceURLPreference(customSpaceHomePage.getChildren(), spacePrettyName);
      // gets the welcome SingleContentViewer Portlet
      Application<Portlet> welcomeSCVPortlet = getPortletApplication(customSpaceHomePage.getChildren(), SCV_PORTLEt_NAME);
      // configures the welcome SingleContentViewer Portlet
      editSCVPreference(welcomeSCVPortlet, spaceGroupId, welcomeSCVCustomPreferences);
      dataStorageService.save(customSpaceHomePage);

    } catch (Exception e) {
      logger.error("Error while customizing the Space home page for space: " + spaceGroupId, e);
    } finally {
      try {
        RequestLifeCycle.end();
      } catch (Exception e) {
        logger.warn("An exception has occurred while proceed RequestLifeCycle.end() : " + e.getMessage());
      }
    }
  }

  private void editSCVPreference(Application<Portlet> selectedPortlet, String prefValue, ExoProperties welcomeSCVCustomPreferences)
      throws Exception {
    // loads the scv preferences
    Portlet prefs = dataStorageService.load(selectedPortlet.getState(), ApplicationType.PORTLET);
    if (prefs == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("The portlet prefs == null : portlet application " + selectedPortlet.getId());
      }
      prefs = new Portlet();
    }
    // edits the nodeIdentifier preference
    for (String preferenceName : welcomeSCVCustomPreferences.keySet()) {
      String preferenceValue = welcomeSCVCustomPreferences.get(preferenceName);
      if (preferenceValue.contains(SPACE_GROUP_ID_PREFERENCE)) {
        preferenceValue = preferenceValue.replace(SPACE_GROUP_ID_PREFERENCE, prefValue);
      }
      prefs.putPreference(new Preference(preferenceName, preferenceValue, false));
    }
  }

  @SuppressWarnings("unchecked")
  private void editSpaceURLPreference(List<ModelObject> children, String prefValue) throws Exception {
    if (children == null || children.size() == 0) {
      if (logger.isDebugEnabled()) {
        logger.debug("Can not get a portlet application from children.\nChildren == null or have no items");
      }
    }
    // parses the children list
    for (ModelObject modelObject : children) {
      // if a container, check for its children
      if (modelObject instanceof Container) {
        editSpaceURLPreference(((Container) modelObject).getChildren(), prefValue);
      } else {
        // if a portlet application, set the preference value
        if (modelObject instanceof Application && ((Application<?>) modelObject).getType().equals(ApplicationType.PORTLET)) {
          Application<Portlet> application = (Application<Portlet>) modelObject;
          Portlet portletPreference = dataStorageService.load(application.getState(), ApplicationType.PORTLET);
          if (portletPreference == null) {
            portletPreference = new Portlet();
          }
          portletPreference.putPreference(new Preference(SpaceUtils.SPACE_URL, prefValue, false));

        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Application<Portlet> getPortletApplication(List<ModelObject> children, String portletName) throws Exception {
    if (children == null || children.size() == 0) {
      if (logger.isDebugEnabled()) {
        logger.debug("Can not get a portlet application from children.\nChildren == null or have no items");
      }
    }
    for (ModelObject modelObject : children) {
      Application<Portlet> selectedApplication = null;
      if (modelObject instanceof Container) {
        selectedApplication = getPortletApplication(((Container) modelObject).getChildren(), portletName);
      } else {
        if (modelObject instanceof Application && ((Application<?>) modelObject).getType().equals(ApplicationType.PORTLET)) {
          Application<Portlet> application = (Application<Portlet>) modelObject;
          String portletId = this.dataStorageService.getId(application.getState());
          if (portletId.endsWith("/" + portletName)) {
            selectedApplication = application;
          }
        }
      }
      if (selectedApplication != null) {
        return selectedApplication;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private void editChildrenAccesPermisions(List<ModelObject> children, String[] accessPermissions) {
    if (children != null && children.size() > 0) {
      for (ModelObject modelObject : children) {
        if (modelObject instanceof Container) {
          ((Container) modelObject).setAccessPermissions(accessPermissions);
          editChildrenAccesPermisions(((Container) modelObject).getChildren(), accessPermissions);
        } else {
          if (modelObject instanceof Application && ((Application<?>) modelObject).getType().equals(ApplicationType.PORTLET)) {
            Application<Portlet> application = (Application<Portlet>) modelObject;
            application.setAccessPermissions(accessPermissions);
          }
        }
      }
    }
  }

}
