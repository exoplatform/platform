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

import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.config.NewPortalConfig;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelUnmarshaller;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.UnmarshalledObject;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.jibx.runtime.JiBXException;

import java.util.regex.Pattern;

public class PlatformUpgradeUtils {

    private static final Log LOG = ExoLogger.getLogger(PlatformUpgradeUtils.class);

    private static final Pattern OWNER_PATTERN = Pattern.compile("@owner@");

    private static void fixOwnerName(PortalConfig config) {
        config.setName(fixOwnerName(config.getType(), config.getName()));
        fixOwnerName(config.getPortalLayout());
    }
    private static void fixOwnerName(Container container) {
        for (Object o : container.getChildren()) {
            if (o instanceof Container) {
                fixOwnerName((Container) o);
            }
        }
    }
    private static String fixOwnerName(String type, String owner) {
        if ( (type.equals(PortalConfig.USER_TYPE) || type.equals(PortalConfig.GROUP_TYPE) ) && !owner.startsWith("/")) {
            return "/" + owner;
        } else {
            return owner;
        }
    }
    private static <T> UnmarshalledObject<T> fromXML(String ownerType, String owner, String xml, Class<T> clazz) throws Exception {
        UnmarshalledObject<T> obj = ModelUnmarshaller.unmarshall(clazz, xml.getBytes("UTF-8"));
        T o = obj.getObject();
        if (o instanceof org.exoplatform.portal.config.model.PortalConfig) {
            org.exoplatform.portal.config.model.PortalConfig portalConfig = (org.exoplatform.portal.config.model.PortalConfig) o;
            portalConfig.setType(ownerType);
            portalConfig.setName(owner);
            fixOwnerName(portalConfig);
        } else {
            throw new Exception();
        }
        return obj;
    }
    private static ConfigurationManager getConfigurationManagerService() {

        ConfigurationManager configurationManager = (ConfigurationManager) PortalContainer.getInstance().getComponentInstanceOfType(ConfigurationManager.class);

        if (configurationManager != null) {
            return configurationManager;
        }
        return null;
    }
    private static String getDefaultConfig(String location, String path) {
        String s = location + path;
        String content = null;
        try {
            LOG.debug("Attempt to load file " + s);
            content = IOUtil.getStreamContentAsString(getConfigurationManagerService().getInputStream(s));
            LOG.debug("Loaded file from path " + s + " with content " + content);
        } catch (Exception ignore) {
            LOG.debug("Could not get file " + s + " will return null instead");
        }
        return content;
    }

    private static <T> UnmarshalledObject<T> getConfig(NewPortalConfig config, String owner, String fileName, Class<T> type) throws Exception {
        LOG.debug("About to load config=" + config + " owner=" + owner + " fileName=" + fileName);

        //
        String ownerType = config.getOwnerType();

        // Get XML
        String path = "/" + ownerType + "/" + owner + "/" + fileName + ".xml";
        String xml = getDefaultConfig(config.getTemplateLocation(), path);

        //
        if (xml == null) {
            String templateName = config.getTemplateName() != null ? config.getTemplateName() : fileName;
            path = "/" + ownerType + "/template/" + templateName + "/" + fileName + ".xml";
            xml = getDefaultConfig(config.getTemplateLocation(), path);
            if (xml != null) {
                xml = OWNER_PATTERN.matcher(xml).replaceAll(owner);
            }
        }

        //
        if (xml != null) {
            boolean ok = false;
            try {
                final UnmarshalledObject<T> o = PlatformUpgradeUtils.fromXML(config.getOwnerType(), owner, xml, type);
                ok = true;
                return o;
            } catch (JiBXException e) {
                LOG.error(e.getMessage() + " file: " + path, e);
                throw e;
            } finally {
                if (!ok) {
                    LOG.error("Could not load file: " + path);
                }
            }
        }

        //
        return null;
    }
    public static PortalConfig getPortalConfigFromTemplate(String siteType, String templateName, String templatePath) {
        NewPortalConfig config = new NewPortalConfig(templatePath);
        config.setTemplateName(templateName);
        config.setOwnerType(siteType);
        UnmarshalledObject<PortalConfig> result = null;
        try
        {
            result = getConfig(config, templateName, siteType, PortalConfig.class);
            if (result != null)
            {
                return result.getObject();
            }
        }
        catch (Exception e)
        {
            LOG.warn("Cannot find configuration of template: " + templateName);
        }
        return null;
    }

}
