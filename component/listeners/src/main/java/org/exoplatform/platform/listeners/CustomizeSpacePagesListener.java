package org.exoplatform.platform.listeners;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.utils.ExoProperties;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class CustomizeSpacePagesListener extends SpaceListenerPlugin {

  private static Log LOG = ExoLogger.getExoLogger(CustomizeSpacePagesListener.class);

  private static final String GROUP_ID_PREFERENCE = "{groupId}";
  private static final String MODIFIED_GROUP_ID_PREFERENCE = "{modifiedGroupId}";
  private static final String PAGE_NAME_PREFERENCE = "{pageName}";

  private DataStorage dataStorageService = null;
  private ChromatticManager chromatticManager = null;

  // Portlet Name
  private String portletName = null;

  // Portlet Name
  private String navigationLabel = null;

  // PortletPreferences loaded from InitParams
  private ExoProperties portletPreferencesInitParams = null;

  /**
   * @param dataStorageService
   *          This service is used to load the Page config from the database
   * @param chromatticManager
   *          This service is used to map nodes (from the JCR) to JAVA Objects
   * @param params
   *          Configuration InitParams
   */
  public CustomizeSpacePagesListener(DataStorage dataStorageService, ChromatticManager chromatticManager, InitParams params) {
    this.dataStorageService = dataStorageService;
    this.chromatticManager = chromatticManager;
    this.portletName = params.getValueParam("portlet-name").getValue();
    if (LOG.isDebugEnabled()) {
      LOG.debug("portlet-name init param = " + this.portletName);
    }
    this.navigationLabel = params.getValueParam("navigation-label").getValue();
    if (LOG.isDebugEnabled()) {
      LOG.debug("navigation-name init param = " + this.navigationLabel);
    }
    this.portletPreferencesInitParams = params.getPropertiesParam("portletPreferences").getProperties();
    if (LOG.isDebugEnabled()) {
      LOG.debug("portletPreferences init param = " + this.portletPreferencesInitParams);
    }
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent event) {}

  /**
   * Apply the selected PortletPreferences to the selected Portlet after adding it
   * 
   * @param spaceLifeCycleEvent
   *          Object that encapsulates space informations and pageName
   */
  @Override
  public void applicationAdded(SpaceLifeCycleEvent spaceLifeCycleEvent) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("applicationAdded(spaceLifeCycleEvent = " + spaceLifeCycleEvent + ")");
    }
    String groupId = spaceLifeCycleEvent.getSpace().getGroupId();
    String pageName = spaceLifeCycleEvent.getSource();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Application was added in a page named " + pageName + "for the group" + groupId);
    }
    // If the page added haven't the name of the selected portlet, skip
    if (!pageName.equals(portletName)) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("actual page name is different to the portlet name." + " No modifications will be made on this page");
      }
      return;
    }
    try {
      // Test if there is an open Chromattic request else open new session
      boolean beginRequest = false;
      try {
        if (this.chromatticManager.getSynchronization() == null) {
          this.chromatticManager.beginRequest();
          beginRequest = true;
        }
      } catch (Exception exception) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("chromatticManager.beginRequest() have thrown an exception: " + exception.getMessage());
        }
      }
      Page page = null;
      // The POMSession isn't refreshed, wait until it's refreshed
      int i = 0;
      while (page == null && i < Integer.MAX_VALUE) {
        page = this.dataStorageService.getPage("group::" + groupId + "::" + pageName);
        i++;
      }
      if (page == null) {
        LOG.error(new IllegalStateException("referenced page with '" + pageName + "' name couldn't be found. No modifications will be applied."));
        return;
      }
      PageNavigation pageNavigation = null;
      i = 0;
      while (pageNavigation == null && i < Integer.MAX_VALUE) {
        pageNavigation = this.dataStorageService.getPageNavigation(PortalConfig.GROUP_TYPE, groupId);
        i++;
      }
      if (pageNavigation == null) {
        LOG.error(new IllegalStateException("referenced Page Navigation node for 'group:" + groupId + "' name couldn't be found. No modifications will be applied."));
        return;
      }
      PageNode pageNode = getPageNode(pageName, pageNavigation.getNodes());
      if (pageNode == null) {
        LOG.error(new IllegalStateException("referenced Page Node node with '" + pageName + "' name couldn't be found. No modifications will be applied."));
        return;
      }
      pageNode.setLabel(navigationLabel);
      this.dataStorageService.save(pageNavigation);
      Application<Portlet> selectedPortlet = getPortletApplication(page.getChildren(), this.portletName);
      if (selectedPortlet == null) {
        LOG.warn("referenced portlet with '" + this.portletName + "' name couldn't be found in the page group::" + groupId + "::" + pageName + ". No modifications will be applied.");
        return;
      }
      Portlet prefs = this.dataStorageService.load(selectedPortlet.getState(), ApplicationType.PORTLET);
      if (prefs == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("The portlet '" + this.portletName + "' have no ProtletPreferences.");
        }
        prefs = new Portlet();
      }
      for (String preferenceName : this.portletPreferencesInitParams.keySet()) {
        String preferenceValue = this.portletPreferencesInitParams.get(preferenceName);
        if (preferenceValue.contains(GROUP_ID_PREFERENCE)) {
          preferenceValue = preferenceValue.replace(GROUP_ID_PREFERENCE, groupId);
        }
        if (preferenceValue.contains(MODIFIED_GROUP_ID_PREFERENCE)) {
          preferenceValue = preferenceValue.replace(MODIFIED_GROUP_ID_PREFERENCE, groupId.replace("/", "."));
        }
        if (preferenceValue.contains(PAGE_NAME_PREFERENCE)) {
          preferenceValue = preferenceValue.replace(PAGE_NAME_PREFERENCE, pageName);
        }
        prefs.putPreference(new Preference(preferenceName, preferenceValue, false));
      }
      this.dataStorageService.save(selectedPortlet.getState(), prefs);
      if (LOG.isDebugEnabled()) {
        LOG.debug("PortletPreferences saved successfully.");
      }
      prefs = this.dataStorageService.load(selectedPortlet.getState(), ApplicationType.PORTLET);
      if (beginRequest) {
        try {
          this.chromatticManager.endRequest(true);
        } catch (Exception exception) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("chromatticManager.endRequest(true) have thrown an exception: " + exception.getMessage());
          }
        }
      }
    } catch (Exception exception) {
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      LOG.error(sw.toString());
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("applicationAdded end ");
    }
  }

  public PageNode getPageNode(String pageNodeName, List<PageNode> pageNodes) {
    if (pageNodeName == null || pageNodes == null || pageNodes.size() == 0) {
      return null;
    }
    for (PageNode pageNode : pageNodes) {
      if (pageNode.getName().equals(pageNodeName)) {
        return pageNode;
      } else {
        if (pageNode.getChildren() != null && pageNode.getChildren().size() > 0) {
          PageNode tmpPageNode = getPageNode(pageNodeName, pageNode.getChildren());
          if (tmpPageNode != null) {
            return tmpPageNode;
          }
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Application<Portlet> getPortletApplication(List<ModelObject> children, String pageName) throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("getPortletApplication(children = " + children + ",pageName =  " + pageName + ")");
    }
    if (children == null || children.size() == 0) {
      return null;
    }
    for (ModelObject modelObject : children) {
      Application<Portlet> selectedApplication = null;
      if (modelObject instanceof Container) {
        selectedApplication = getPortletApplication(((Container) modelObject).getChildren(), pageName);
      } else {
        if (modelObject instanceof Application && ((Application<?>) modelObject).getType().equals(ApplicationType.PORTLET)) {
          Application<Portlet> application = (Application<Portlet>) modelObject;
          String portletId = this.dataStorageService.getId(application.getState());
          if (portletId.endsWith("/" + pageName)) {
            selectedApplication = application;
          }
        }
      }
      if (selectedApplication != null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("selectedApplication found");
          LOG.debug("getPortletApplication end ");
        }
        return selectedApplication;
      }
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("return selectedApplication = null");
      LOG.debug("getPortletApplication end ");
    }
    return null;
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
  public void spaceCreated(SpaceLifeCycleEvent event) {}

  @Override
  public void spaceRemoved(SpaceLifeCycleEvent event) {}

}
