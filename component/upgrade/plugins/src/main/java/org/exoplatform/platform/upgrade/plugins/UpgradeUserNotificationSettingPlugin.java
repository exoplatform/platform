/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.plugins;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;

import org.exoplatform.commons.api.notification.channel.AbstractChannel;
import org.exoplatform.commons.api.notification.channel.ChannelManager;
import org.exoplatform.commons.api.notification.service.setting.PluginContainer;
import org.exoplatform.commons.notification.NotificationConfiguration;
import org.exoplatform.commons.notification.NotificationUtils;
import org.exoplatform.commons.notification.channel.MailChannel;
import org.exoplatform.commons.notification.impl.AbstractService;
import org.exoplatform.commons.notification.impl.NotificationSessionManager;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Dec 23, 2014  
 */
public class UpgradeUserNotificationSettingPlugin extends UpgradeProductPlugin {
  
  private static final Log LOG = ExoLogger.getLogger(UpgradeUserNotificationSettingPlugin.class);
  
  private final String workspace;
  public static final String NAME_PATTERN = "exo:{CHANNELID}Channel";
  public UpgradeUserNotificationSettingPlugin(InitParams initParams) {
    super(initParams);
    NotificationConfiguration configuration = CommonsUtils.getService(NotificationConfiguration.class);
    workspace = configuration != null ? configuration.getWorkspace() : null;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (workspace == null) {
      return;
    }
    //
    boolean created = NotificationSessionManager.createSystemProvider();
    SessionProvider sProvider = NotificationSessionManager.getSessionProvider();
    try {
      Session session = AbstractService.getSession(sProvider, workspace);
      Node userNodes = session.getRootNode().getNode(AbstractService.SETTING_NODE).getNode(AbstractService.SETTING_USER_NODE);
      NodeIterator iterator = userNodes.getNodes();
      while (iterator.hasNext()) {
        Node userNode = iterator.nextNode();
        //don't migrate default setting
        if (! userNode.hasNode("global")) {
          continue;
        }
        LOG.info("Updating notification setting of user : " + userNode.getName());
        //
        Node userSettingNode = userNode.getNode("global");
        updateProperty(AbstractService.EXO_DAILY, "", userSettingNode);
        updateProperty(AbstractService.EXO_WEEKLY, "", userSettingNode);
        //add new property exo:MAIL_CHANNELChannel with the same value of exo:instantly
        String s = userSettingNode.getProperty(AbstractService.EXO_INSTANTLY).getString();
        userSettingNode.setProperty(NAME_PATTERN.replace("{CHANNELID}", MailChannel.ID),
                                    NotificationUtils.listToString(NotificationUtils.stringToList(s), AbstractService.VALUE_PATTERN));
        //remove exo:instantly
        userSettingNode.setProperty(AbstractService.EXO_INSTANTLY, (Value)null);
        //add new property for each channel with the default active plugin
        PluginContainer container = CommonsUtils.getService(PluginContainer.class);
        ChannelManager channelManager = CommonsUtils.getService(ChannelManager.class);
        StringBuilder builder = new StringBuilder();
        boolean hasMore = false;
        for (AbstractChannel channel : channelManager.getChannels()) {
          if (MailChannel.ID.equals(channel.getId())) {
            continue;
          }
          userSettingNode.setProperty(NAME_PATTERN.replace("{CHANNELID}", channel.getId()),
                                      NotificationUtils.listToString(container.getDefaultActivePlugins(), AbstractService.VALUE_PATTERN));
          if (hasMore) {
            builder.append(",");
          }
          builder.append(channel.getId());
          hasMore = true;
        }
        updateProperty(AbstractService.EXO_IS_ACTIVE, builder.toString(), userSettingNode);
        
        //
        userSettingNode.getSession().save();
      }
    } catch (Exception e) {
      LOG.debug("Failed to migrate user setting", e);
    } finally { 
      NotificationSessionManager.closeSessionProvider(created);
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion,previousVersion);
  }

  private void updateProperty(String property, String defaultValue, Node userSettingNode) throws Exception {
    Value value = userSettingNode.getProperty(property).getValue();
    if (value != null) {
      String strs = value.getString();
      if ("true".equals(strs)) {
        strs = MailChannel.ID + "," + defaultValue;
      }
      if ("false".equals(strs)) {
        strs = defaultValue;
      }
      defaultValue = NotificationUtils.listToString(NotificationUtils.stringToList(strs), AbstractService.VALUE_PATTERN);
    }
    userSettingNode.setProperty(property, defaultValue);
  }
 
}