package org.exoplatform.platform.common.space.listeners;

import org.exoplatform.commons.utils.ExoProperties;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.common.space.SpaceCustomizationService;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar
 *         Chattouna</a>
 * @version $Revision$
 */
public class CustomizeSpaceHomePageListener extends SpaceListenerPlugin {

  private ExoProperties welcomeSCVCustomPreferences = null;
  private SpaceCustomizationService spaceCustomizationService = null;

  public CustomizeSpaceHomePageListener(SpaceCustomizationService spaceCustomizationService_, InitParams params) {
    this.spaceCustomizationService = spaceCustomizationService_;
    this.welcomeSCVCustomPreferences = params.getPropertiesParam("welcomeSCVCustomPreferences").getProperties();
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent spaceLifeCycleEvent) {

    String spacePrettyName = spaceLifeCycleEvent.getSpace().getPrettyName();
    String spaceGroupId = spaceLifeCycleEvent.getSpace().getGroupId();

    spaceCustomizationService.createSpaceHomePage(spacePrettyName, spaceGroupId, welcomeSCVCustomPreferences);
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent event) {}

  @Override
  public void applicationAdded(SpaceLifeCycleEvent spaceLifeCycleEvent) {}

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
