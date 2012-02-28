package org.exoplatform.platform.common.space.listeners;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.common.space.SpaceCustomizationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.impl.SpaceServiceImpl;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class CustomizeSpaceDriveListener extends SpaceListenerPlugin {

  private static final String SPACE_DRIVE_VIEW = "space.drive.view";
  private SpaceCustomizationService spaceCustomizationService = null;
  private String viewNodeName = null;
  private static Log LOG = ExoLogger.getExoLogger(CustomizeSpaceDriveListener.class);

  public CustomizeSpaceDriveListener(SpaceCustomizationService spaceCustomizationService_, InitParams params) {
    this.spaceCustomizationService = spaceCustomizationService_;
    ValueParam viewParamName = params.getValueParam(SPACE_DRIVE_VIEW);
    if (viewParamName != null) {
      viewNodeName = viewParamName.getValue();
    } else {
      LOG.warn("No such property found: " + SPACE_DRIVE_VIEW + "\nPlease make sure to have the correct ECMS view name.");
    }
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent event) {
    String groupId = event.getSpace().getGroupId();
    String permission = SpaceServiceImpl.MANAGER + ":" + groupId;
    try {
      if (viewNodeName != null) {
        spaceCustomizationService.editSpaceDriveViewPermissions(viewNodeName, permission);
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Can not edit view's permissions for view node: null");
        }
      }
    } catch (Exception e) {
      LOG.error("Can not edit view's permission for space drive: " + groupId, e);
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
