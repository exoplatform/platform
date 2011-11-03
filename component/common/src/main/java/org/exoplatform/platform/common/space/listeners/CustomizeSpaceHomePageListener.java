package org.exoplatform.platform.common.space.listeners;

import java.util.HashMap;
import java.util.Map;

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
  private Map<String, Boolean> spaceIds = new HashMap<String, Boolean>();

  public CustomizeSpaceHomePageListener(SpaceCustomizationService spaceCustomizationService_, InitParams params) {
    this.spaceCustomizationService = spaceCustomizationService_;
    this.welcomeSCVCustomPreferences = params.getPropertiesParam("welcomeSCVCustomPreferences").getProperties();
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent spaceLifeCycleEvent) {
    spaceIds.put(spaceLifeCycleEvent.getSpace().getGroupId(), true);
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent event) {}

  @Override
  public void applicationAdded(SpaceLifeCycleEvent spaceLifeCycleEvent) {
    // Workaround : Unfortunately the 'spaceCreated' listener is called
    // before the creation of space's pages&navigations, so the Home page
    // isn't accessible and couldn't be modified there.
    // To be sure that pages are created when calling
    // 'spaceCustomizationService.createSpaceHomePage', we have added this
    // instruction in 'applicationAdded'.
    // To be sure that this will be called once, when space is created, we
    // used 'spaceIds' map

    String spaceGroupId = spaceLifeCycleEvent.getSpace().getGroupId();
    Boolean spaceCreated = spaceIds.get(spaceGroupId);
    if (spaceCreated == null || !spaceCreated) {
      return;
    }

    spaceIds.put(spaceGroupId, false);
    String spacePrettyName = spaceLifeCycleEvent.getSpace().getPrettyName();
    spaceCustomizationService.createSpaceHomePage(spacePrettyName, spaceGroupId, welcomeSCVCustomPreferences);
  }

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
