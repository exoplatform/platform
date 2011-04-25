package org.exoplatform.platform.common.space.listeners;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.cms.BasePath;
import org.exoplatform.services.cms.impl.DMSConfiguration;
import org.exoplatform.services.cms.impl.DMSRepositoryConfiguration;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.impl.SpaceServiceImpl;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class CustomizeSpaceDriveListener extends SpaceListenerPlugin {

  private static final String SPACE_DRIVE_VIEW = "space.drive.view";
  private NodeHierarchyCreator nodeHierarchyCreator = null;
  private DMSConfiguration dmsConfiguration = null;
  private RepositoryService repositoryService = null;
  private String viewNodeName = null;
  private static Log logger = ExoLogger.getExoLogger(CustomizeSpaceDriveListener.class);

  public CustomizeSpaceDriveListener(NodeHierarchyCreator nodeHierarchyCreator_, DMSConfiguration dmsConfiguration_,
      RepositoryService repositoryService_, InitParams params) {
    this.nodeHierarchyCreator = nodeHierarchyCreator_;
    this.dmsConfiguration = dmsConfiguration_;
    this.repositoryService = repositoryService_;
    ValueParam viewParamName = params.getValueParam(SPACE_DRIVE_VIEW);
    if (viewParamName != null) {
      viewNodeName = viewParamName.getValue();
    } else {
      logger.warn("No such property found: " + SPACE_DRIVE_VIEW + "\nPlease make sure to have the correct ECMS view name.");
    }
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent event) {
    String groupId = event.getSpace().getGroupId();
    String permission = SpaceServiceImpl.MANAGER + ":" + groupId;
    try {
      if (viewNodeName != null) {
        editSpaceDriveViewPermissions(viewNodeName, permission);
      } else {
        if(logger.isDebugEnabled()){
          logger.debug("Can not edit view's permissions for view node: null");
        }
      }
    } catch (Exception e) {
      logger.error("Can not edit view's permission for space drive: " + groupId, e);
    }

  }

  private void editSpaceDriveViewPermissions(String viewNodeName, String permission) throws RepositoryException {
    if(logger.isDebugEnabled()){
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
      if(logger.isDebugEnabled()){
        logger.debug("Permission " + permission + " added with success to ECMS view " + viewNodeName);
      }
    }else{
      if(logger.isDebugEnabled()){
        logger.debug("Can not find view node: " + viewNodeName);
      }
    }
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent event) {}

  @Override
  public void applicationAdded(SpaceLifeCycleEvent event) {}

  @Override
  public void applicationDeactivated(SpaceLifeCycleEvent event) {}

  @Override
  public void applicationRemoved(SpaceLifeCycleEvent event) {}

  @Override
  public void grantedLead(SpaceLifeCycleEvent event) {}

  @Override
  public void joined(SpaceLifeCycleEvent event) {}

  @Override
  public void left(SpaceLifeCycleEvent event) {}

  @Override
  public void revokedLead(SpaceLifeCycleEvent event) {}

  @Override
  public void spaceRemoved(SpaceLifeCycleEvent event) {}

}
