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
import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.portal.config.model.PortalConfig;

public class UpgradeGroupSiteLayoutPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeGroupSiteLayoutPlugin.class);

    private static final String GROUP_SITE_TEMPLATE_NAME = "group.site.template.name";

    private static final String GROUP_SITE_TEMPLATE_LOACTION = "group.site.template.location";

    private DataStorage dataStorage_;

    protected String groupSiteTemplateName;

    protected String groupSiteTemplateLocation;

    public UpgradeGroupSiteLayoutPlugin (DataStorage dataStorage, InitParams initParams) {
        super(initParams);
        dataStorage_ = dataStorage;
        groupSiteTemplateName = initParams.getValueParam(GROUP_SITE_TEMPLATE_NAME).getValue();
        groupSiteTemplateLocation = initParams.getValueParam(GROUP_SITE_TEMPLATE_LOACTION).getValue();
    }
    @Override
    public void processUpgrade(String oldVersion, String newVersion) {

        RequestLifeCycle.begin(PortalContainer.getInstance());
        try {

            Query<PortalConfig> query = new Query<PortalConfig>(groupSiteTemplateName, null, PortalConfig.class);

            LazyPageList<PortalConfig> usersPortalConfig = dataStorage_.find(query);

            if (usersPortalConfig == null) {
                LOG.info("No Group Site was found, no upgrade operation will be done.");
                return;

            }
            // Get new portal config from template
            PortalConfig tempPortalConfig = PlatformUpgradeUtils.getPortalConfigFromTemplate(PortalConfig.GROUP_TYPE,groupSiteTemplateName,groupSiteTemplateLocation);

            // Get old portalConfig stored in the JCR
            for (PortalConfig userPortalConfig : usersPortalConfig.getAll()) {
                LOG.info("Proceed group site layout migration: " + userPortalConfig.getName());

                PortalConfig newPortalConfig = new PortalConfig(PortalConfig.GROUP_TYPE,userPortalConfig.getName(),userPortalConfig.getStorageId());
                // Merge data from Old PortalConfig to new PortalConfig (Set storage name and storage id from old to new portal config)
                newPortalConfig.setStorageName(userPortalConfig.getStorageName());
                newPortalConfig.setName(userPortalConfig.getName());
                newPortalConfig.setAccessPermissions(userPortalConfig.getAccessPermissions());
                newPortalConfig.setDescription(userPortalConfig.getDescription());
                newPortalConfig.setEditPermission(userPortalConfig.getEditPermission());
                newPortalConfig.setLabel(userPortalConfig.getLabel());
                newPortalConfig.setModifiable(userPortalConfig.isModifiable());
                newPortalConfig.setPortalLayout(tempPortalConfig.getPortalLayout());
                newPortalConfig.setPortalRedirects(userPortalConfig.getPortalRedirects());
                newPortalConfig.setType(userPortalConfig.getType());

                // datastorage.save(new built portal config)
                dataStorage_.save(newPortalConfig);
            }
        } catch (Exception e) {
            LOG.error("Error during Group Layout migration : " + e.getMessage(), e);
        } finally {
            RequestLifeCycle.end();
        }
    }

    @Override
    public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
        // --- return true anly for the first version of platform
        return VersionComparator.isAfter(newVersion, previousVersion);
    }
}
