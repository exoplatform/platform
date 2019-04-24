/**
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.*;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.NavigationContext;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.SpaceApplication;
import org.exoplatform.social.core.space.SpaceTemplate;
import org.exoplatform.social.core.space.SpaceUtils;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.space.spi.SpaceTemplateService;
import org.exoplatform.social.core.storage.api.SpaceStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class UpgradeSpaceHomePagePlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeSpaceHomePagePlugin.class);


    private static final String SPACE_NEW_HOME_PAGE_TEMPLATE = "custom space";

    private final OrganizationService service;

    private final SpaceStorage spaceStorage;

    private DataStorage dataStorageService = null;

    private SpaceService spaceService = null;

    private SpaceTemplateService spaceTemplateService = null;

    private PageService pageService = null;

    private UserPortalConfigService userPortalConfigService = null;

    public   UpgradeSpaceHomePagePlugin (DataStorage dataStorageService, OrganizationService organizationService, SpaceStorage spaceStorage, UserPortalConfigService userPortalConfigService, PageService pageService, InitParams initParams) {
        super(initParams);
        this.service = organizationService;
        this.dataStorageService = dataStorageService;
        this.spaceStorage = spaceStorage;
        this.pageService = pageService;
        this.userPortalConfigService = userPortalConfigService;
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {
        RequestLifeCycle.begin(PortalContainer.getInstance());

        try {
            GroupHandler groupHandler = service.getGroupHandler();
            Group spacesParentGroup = service.getGroupHandler().findGroupById("/spaces");

            @SuppressWarnings("unchecked")
            Collection<Group> spacesGroupsList = groupHandler.findGroups(spacesParentGroup);

            SessionProvider sessionProvider = SessionProvider.createSystemProvider();
            if (spacesGroupsList == null || spacesGroupsList.isEmpty()) {
                LOG.info("No space was found, no upgrade operation will be done.");
                return;
            }
            for (Group group : spacesGroupsList) {
                LOG.info("Proceed Upgrade '" + group.getId() + "' Space.");

                Space space = spaceStorage.getSpaceByGroupId(group.getId());
                if (space == null) {
                    LOG.warn("Cannot find space for group: " + group.getId());
                    continue;
                }
                LOG.info("Proceed space migration: " + group.getId());

                editSpaceHomePage(space.getPrettyName(), group.getId());
            }
        } catch (Exception e) {
            LOG.error("Error during spaces migration : " + e.getMessage(), e);
        } finally {
            RequestLifeCycle.end();
        }

    }

    @Override
    public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
        // --- return true anly for the first version of platform
        return VersionComparator.isAfter(newVersion, previousVersion);
    }
    private void editSpaceHomePage(String spacePrettyName, String spaceGroupId) {

        RequestLifeCycle.begin(PortalContainer.getInstance());
        try {
            LOG.info("Updating '" + spaceGroupId + "' Space Home Page");

            // creates the new home page
            Space space = getSpaceService().getSpaceByGroupId(spaceGroupId);
            if (space == null) {
                throw new IllegalStateException("Can't find space with group id " + spaceGroupId);
            }
            String spaceType = space.getType();
            SpaceTemplateService spaceTemplateService = getSpaceTemplateService();
            SpaceTemplate spaceTemplate = spaceTemplateService.getSpaceTemplateByName(spaceType);
            if (spaceTemplate == null) {
                LOG.warn("Could not find space template:{}. Space home page will not be updated for space:{}.", spaceType, spacePrettyName);
                return;
            }
            SpaceApplication homeApplication = spaceTemplate.getSpaceHomeApplication();
            if (homeApplication == null) {
                LOG.warn("Could not find home application for template:{}. Space home page will not be updated for space:{}.", spaceType, spacePrettyName);
                return;
            }
            String portletName = homeApplication.getPortletName();
            String pageName = PortalConfig.GROUP_TYPE + "::" + spaceGroupId + "::" + portletName;
            Page oldSpaceHomePage = dataStorageService.getPage(pageName);
            PageContext pageContext = pageService.loadPage(PageKey.parse(oldSpaceHomePage.getPageId()));
            pageContext.update(oldSpaceHomePage);

            // creates the customized home page for the space and set few fields
            // with values from the old home page
            Page customSpaceHomePage = userPortalConfigService.createPageTemplate(SPACE_NEW_HOME_PAGE_TEMPLATE,PortalConfig.GROUP_TYPE, spaceGroupId);

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

            NavigationContext navContext = SpaceUtils.createGroupNavigation(spaceGroupId);


            SiteKey siteKey = navContext.getKey();
            PageKey pageKey = new PageKey(siteKey, customSpaceHomePage.getName());
            PageState pageState = new PageState(
                    customSpaceHomePage.getTitle(),
                    customSpaceHomePage.getDescription(),
                    customSpaceHomePage.isShowMaxWindow(),
                    customSpaceHomePage.getFactoryId(),
                    customSpaceHomePage.getAccessPermissions() != null ?
                    Arrays.asList(customSpaceHomePage.getAccessPermissions()) : null,
                    customSpaceHomePage.getEditPermission(),
                    customSpaceHomePage.getMoveAppsPermissions() != null ? Arrays.asList(customSpaceHomePage.getMoveAppsPermissions()) : null,
                    customSpaceHomePage.getMoveContainersPermissions() != null ? Arrays.asList(customSpaceHomePage.getMoveContainersPermissions()) : null);

            pageService.savePage(new PageContext(pageKey, pageState));
            dataStorageService.save(customSpaceHomePage);

        } catch (Exception e) {
            LOG.error("Error while customizing the Space home page for space: " + spaceGroupId, e);
        } finally {
            try {
                RequestLifeCycle.end();
            } catch (Exception e) {
                LOG.warn("An exception has occurred while proceed RequestLifeCycle.end() : " + e.getMessage());
            }
        }
    }

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
    private void editSpaceURLPreference(List<ModelObject> children, String prefValue) throws Exception {
        if (children == null || children.size() == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can not get a portlet application from children.\nChildren == null or have no items");
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

    private SpaceService getSpaceService() {
        if (this.spaceService == null) {
            this.spaceService = (SpaceService) PortalContainer.getInstance().getComponentInstanceOfType(SpaceService.class);
        }
        return this.spaceService;
    }

    private SpaceTemplateService getSpaceTemplateService() {
        if (this.spaceTemplateService == null) {
            this.spaceTemplateService = CommonsUtils.getService(SpaceTemplateService.class);
        }
        return this.spaceTemplateService;
    }
}
