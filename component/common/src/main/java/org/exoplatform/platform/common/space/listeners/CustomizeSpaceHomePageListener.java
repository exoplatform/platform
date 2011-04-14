package org.exoplatform.platform.common.space.listeners;

import java.util.List;

import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.utils.ExoProperties;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.SpaceUtils;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar Chattouna</a>
 * @version $Revision$
 */
public class CustomizeSpaceHomePageListener extends SpaceListenerPlugin {

  private static final String SPACE_GROUP_ID_PREFERENCE = "{spaceGroupId}";
  private static final String SPACE_HOME_PAGE_PORTLET_NAME = "SpaceActivityStreamPortlet";
  private static final String SPACE_NEW_HOME_PAGE_TEMPLATE = "custom space";
  private static final String SCV_PORTLEt_NAME = "SingleContentViewer";
  private static Log logger = ExoLogger.getExoLogger(CustomizeSpaceHomePageListener.class);

  private DataStorage dataStorageService = null;
  private ChromatticManager chromatticManager = null;
  private UserPortalConfigService userPortalConfigService = null;
  private ExoProperties welcomeSCVCustomPreferences = null;

  public CustomizeSpaceHomePageListener(DataStorage dataStorageService, ChromatticManager chromatticManager,
      UserPortalConfigService userPortalConfigService, InitParams params) {
    this.dataStorageService = dataStorageService;
    this.chromatticManager = chromatticManager;
    this.userPortalConfigService = userPortalConfigService;
    this.welcomeSCVCustomPreferences = params.getPropertiesParam("welcomeSCVCustomPreferences").getProperties();
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent spaceLifeCycleEvent) {

    String spacePrettyName = spaceLifeCycleEvent.getSpace().getPrettyName();
    String spaceGroupId = spaceLifeCycleEvent.getSpace().getGroupId();

    // Test if there is an open Chromattic request else open new session
    boolean beginRequest = false;
    try {
      if (chromatticManager.getSynchronization() == null) {
        chromatticManager.beginRequest();
        beginRequest = true;
      }
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("An exception has occurred while trying to begin the chromatticManager request: " + e.getMessage());
      }
    }
    try {
      // creates the new home page
      Page oldSpaceHomePage = null;
      // needs to wait till the dataStorageService can get the target page
      while (oldSpaceHomePage == null) {
        oldSpaceHomePage = dataStorageService.getPage(PortalConfig.GROUP_TYPE + "::" + spaceGroupId + "::"
            + SPACE_HOME_PAGE_PORTLET_NAME);
      }
      // creates the customized home page for the space and set few fields with values from the old home page
      Page customSpaceHomePage = userPortalConfigService.createPageTemplate(SPACE_NEW_HOME_PAGE_TEMPLATE,
          PortalConfig.GROUP_TYPE, spacePrettyName);
      customSpaceHomePage.setTitle(oldSpaceHomePage.getTitle());
      customSpaceHomePage.setName(oldSpaceHomePage.getName());
      customSpaceHomePage.setAccessPermissions(oldSpaceHomePage.getAccessPermissions());
      customSpaceHomePage.setEditPermission(oldSpaceHomePage.getEditPermission());
      customSpaceHomePage.setOwnerType(PortalConfig.GROUP_TYPE);
      customSpaceHomePage.setOwnerId(spaceGroupId);
      // needs to populate the accessPermissions list to all children: containers and applications
      editChildrenAccesPermisions(customSpaceHomePage.getChildren(), customSpaceHomePage.getAccessPermissions());
//      dataStorageService.save(customSpaceHomePage);
      // mandatory preference "Space_URL" should be added to the home page applications
      editSpaceURLPreference(customSpaceHomePage.getChildren(), spacePrettyName);
      // gets the welcome SingleContentViewer Portlet
      Application<Portlet> welcomeSCVPortlet = getPortletApplication(customSpaceHomePage.getChildren(), SCV_PORTLEt_NAME);
      // configures the welcome SingleContentViewer Portlet
      editSCVPreference(welcomeSCVPortlet, spaceGroupId);
      dataStorageService.save(customSpaceHomePage);

    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.error("Error while customizing the Space home page for space: " + spaceGroupId, e);
      }
    }
    // Test if Chromattic session is opened and try to end it
    if (beginRequest) {
      try {
        chromatticManager.endRequest(true);
      } catch (Exception e) {
        if (logger.isDebugEnabled()) {
          logger.debug("An exception has occurred while trying to end the chromatticManager request: " + e.getMessage());
        }
      }
    }
  }

  private void editSCVPreference(Application<Portlet> selectedPortlet, String prefValue) throws Exception {
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
