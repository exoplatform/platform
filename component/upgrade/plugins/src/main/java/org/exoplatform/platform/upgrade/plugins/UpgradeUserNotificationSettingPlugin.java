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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.api.notification.channel.AbstractChannel;
import org.exoplatform.commons.api.notification.channel.ChannelManager;
import org.exoplatform.commons.api.notification.service.setting.PluginContainer;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.notification.NotificationConfiguration;
import org.exoplatform.commons.notification.NotificationUtils;
import org.exoplatform.commons.notification.channel.MailChannel;
import org.exoplatform.commons.notification.impl.AbstractService;
import org.exoplatform.commons.notification.impl.NotificationSessionManager;
import org.exoplatform.commons.notification.impl.setting.UserSettingServiceImpl;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.lifecycle.SocialChromatticLifeCycle;
import org.exoplatform.social.core.chromattic.entity.IdentityEntity;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Dec 23, 2014  
 */
public class UpgradeUserNotificationSettingPlugin extends UpgradeProductPlugin {
  /** */
  private static final Log LOG = ExoLogger.getLogger(UpgradeUserNotificationSettingPlugin.class);
  /** */
  private static final int    LIMIT_LOAD_THRESHOLD = 500;
  /** */
  private static final String SETTING_LIFE_CYCLE_NAME = "setting";
  /** */
  private final String workspace;
  /** */
  private final String settingWorkspace;
  /** */
  private final String socialWorkspace;
  /** */
  private String activeChannelList = null;
  /** */
  private String inactiveChannelList = null;
  /** */
  private Map<String, String> channelProperties;
  
  public UpgradeUserNotificationSettingPlugin(InitParams initParams, ChromatticManager manager) {
    super(initParams);
    NotificationConfiguration configuration = CommonsUtils.getService(NotificationConfiguration.class);
    this.workspace = configuration != null ? configuration.getWorkspace() : null;
    this.socialWorkspace = manager.getLifeCycle(SocialChromatticLifeCycle.SOCIAL_LIFECYCLE_NAME).getWorkspaceName();
    this.settingWorkspace = manager.getLifeCycle(SETTING_LIFE_CYCLE_NAME).getWorkspaceName();
    this.channelProperties = new HashMap<String, String>();
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (workspace == null) {
      return;
    }
    boolean created = NotificationSessionManager.createSystemProvider();
    SessionProvider sProvider = NotificationSessionManager.getSessionProvider();
    loadChannels();
    int offset = 0;
    long total = System.currentTimeMillis();
    
    try {
      Session socialSession = getJCRSession(sProvider, socialWorkspace);
      NodeIterator ni = getIdentityNodes(socialSession, offset, LIMIT_LOAD_THRESHOLD);
      Node node = null;
      String remoteId = null;
      long t = 0;
      
      while (ni.hasNext()) {
        node = ni.nextNode();
        remoteId = node.getProperty(IdentityEntity.remoteId.getName()).getString();
        t = System.currentTimeMillis();
        LOG.info(String.format("| \\ START::user number: %s (%s user)", offset, remoteId));
        processUpgrade(sProvider, remoteId);
        offset++;
        LOG.info(String.format("| // END::Migration setting (%s user) consumed time: %s", remoteId, System.currentTimeMillis() - t));
        
        if (offset % LIMIT_LOAD_THRESHOLD == 0) {
          socialSession.logout();
          socialSession = null;
          socialSession = getJCRSession(sProvider, socialWorkspace);
          ni = getIdentityNodes(socialSession, offset, LIMIT_LOAD_THRESHOLD);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally { 
      LOG.info(String.format("| DONE::total: %s user(s) consumed time: %s", offset, System.currentTimeMillis() - total));
      NotificationSessionManager.closeSessionProvider(created);
    }
  }
  
  private void processUpgrade(SessionProvider sProvider, String remoteId) throws PathNotFoundException, RepositoryException {
    Node userNode = getUserSettingNode(sProvider, remoteId);
    //
    if (userNode == null) {
      addMixin(sProvider, remoteId);
      return;
    }
    boolean mustMigration = mustUpgrade(userNode);
    //
    String globalPath = Scope.GLOBAL.toString().toLowerCase();
    //
    Node userSettingNode = userNode.hasNode(globalPath) ? userNode.getNode(globalPath) : null;
    //do normalization the user setting
    UpgradeBuilder upgradeBuilder = builder(userSettingNode).doNormalization(userNode, remoteId);
    //
    if (userSettingNode != null && mustMigration) {
      upgradeBuilder.upgradeDaily()
                    .upgradeWeekly()
                    .upgradeInstantly()
                    .upgradeIsActive()
                    .upgradeChannels();
    }
    //
    upgradeBuilder.done();
    
  }
  
  /**
   * Creates the new upgrade builder with userSetting node
   * 
   * @param userSettingNode
   * @return the new instance of builder
   */
  private UpgradeBuilder builder(Node userSettingNode) {
    return new UpgradeBuilder(userSettingNode);
  }
  /**
   * Upgrade setting builder
   * 
   * @author thanhvc
   *
   */
  private class UpgradeBuilder {
    private final Node userSettingNode;
    
    private UpgradeBuilder(Node userSettingNode) {
      this.userSettingNode = userSettingNode;
    }
    
    private UpgradeBuilder upgradeDaily() throws RepositoryException {
      upgradeProperty(userSettingNode, AbstractService.EXO_DAILY, null);
      return this;
    }
    
    private UpgradeBuilder upgradeWeekly() throws RepositoryException {
      upgradeProperty(userSettingNode, AbstractService.EXO_WEEKLY, null);
      return this;
    }
    
    private UpgradeBuilder upgradeInstantly() throws RepositoryException {
      upgradeProperty(userSettingNode, AbstractService.EXO_INSTANTLY, null);
      return this;
    }
    
    private UpgradeBuilder upgradeIsActive() throws RepositoryException {
      upgradeProperty(userSettingNode, AbstractService.EXO_IS_ACTIVE, null);
      return this;
    }
    
    private UpgradeBuilder upgradeChannels() throws RepositoryException {
      LOG.info(String.format("  %s channel(s) will be add the plugins in the user setting", channelProperties.size()));
      for (Map.Entry<String , String> entry : channelProperties.entrySet()) {
        String key = getChannelProperty(entry.getKey());
        upgradeProperty(userSettingNode, key, entry.getValue());
      }
      return this;
    }
    
    private UpgradeBuilder doNormalization(Node userNode, String remoteId) throws RepositoryException {
      normalize(userNode, remoteId);
      if (userSettingNode == null) {
        userNode.getSession().save();
      }
      return this;
    }
    
    private void done() throws RepositoryException {
      if (userSettingNode != null) {
        userSettingNode.getSession().save();
      }
    }
  }
  
  /**
   * Gets the channel key
   * @param channelId the channel Id
   * @return the channel key
   */
  private String getChannelProperty(String channelId) {
    return UserSettingServiceImpl.NAME_PATTERN.replace("{CHANNELID}", channelId);
  }

  
  /**
   * Gets the channels what is using for Active setting.
   * 
   * @return
   */
  private String loadActiveValue() {
    if (this.activeChannelList == null) {
      ChannelManager channelManager = CommonsUtils.getService(ChannelManager.class);
      List<String> channelIds = new ArrayList<String> ();
      for (AbstractChannel channel : channelManager.getChannels()) {
        channelIds.add(channel.getId());
      }
      
      activeChannelList = StringUtils.join(channelIds, ',');
      
    }
    return activeChannelList;
  }
  
  /**
   * Loads the channel list with default active plugins except Mail channel
   */
  private void loadChannels() {
    if (this.channelProperties == null || channelProperties.size() == 0) {
      PluginContainer container = CommonsUtils.getService(PluginContainer.class);
      ChannelManager channelManager = CommonsUtils.getService(ChannelManager.class);
      for (AbstractChannel channel : channelManager.getChannels()) {
        if (!MailChannel.ID.equals(channel.getId())) {
          channelProperties.put(channel.getId(), NotificationUtils.listToString(container.getDefaultActivePlugins(), AbstractService.VALUE_PATTERN));
        }
      }
    }
  }
  
  /**
   * If user setting is inactive (Never Notify Me). Mail Channel is disable, others is enable
   * 
   * @return
   */
  private String loadInactiveValue() {
    if (this.inactiveChannelList == null) {
      ChannelManager channelManager = CommonsUtils.getService(ChannelManager.class);
      List<String> channelIds = new ArrayList<String> ();
      for (AbstractChannel channel : channelManager.getChannels()) {
        if (!MailChannel.ID.equals(channel.getId())) {
          channelIds.add(channel.getId());
        }
      }
      inactiveChannelList = StringUtils.join(channelIds, ',');
    }
    return inactiveChannelList;
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion,previousVersion);
  }

  /**
   * The target of method upgrades the old setting from 4.1 to new notification setting in 4.2
   * 
   * Case 1: EXO_DAILY and EXO_WEEKLY changes to {<PluginA>}, {<PluginB>}...
   * Case 2: EXO_INSTANTLY changes to EXO_EMAILCHANNEL = {<PluginA>}, {<PluginB>}
   * Case 3: EXO_IS_ACTIVE = TRUE/FALSE: 
   *             TRUE > Enable ALL CHANNELS, FALSE: Only MAIL Disable
   * 
   * @param userSettingNode
   * @param property
   * @param propertyValue
   * @throws RepositoryException 
   * @throws PathNotFoundException 
   * @throws ValueFormatException 
   * @throws Exception
   */
  private void upgradeProperty(Node userSettingNode, String property, String propertyValue) throws ValueFormatException, PathNotFoundException, RepositoryException {
    if (AbstractService.EXO_DAILY.equals(property) && userSettingNode.hasProperty(AbstractService.EXO_DAILY)) {
        Value value = userSettingNode.getProperty(property).getValue();
        /** Case 1: EXO_DAILY changes to {<PluginA>}, {<PluginB>}...*/
        if (value != null) {
          String oldValue = value.getString();
          //exo:daily:'LikePlugin,SpaceInvitationPlugin' in PLF 4.1
          //exo:daily:'{LikePlugin},{SpaceInvitationPlugin}' in PLF 4.2
          //NotificationUtils.listToString uses to transform that
          String newValue = NotificationUtils.listToString(NotificationUtils.stringToList(oldValue), AbstractService.VALUE_PATTERN);
          userSettingNode.setProperty(property, newValue);
        }
    } else if (AbstractService.EXO_WEEKLY.equals(property) && userSettingNode.hasProperty(AbstractService.EXO_WEEKLY)) {
      Value value = userSettingNode.getProperty(property).getValue();
      /** Case 1: EXO_WEEKLY changes to {<PluginA>}, {<PluginB>}...*/
      if (value != null) {
        String oldValue = value.getString();
        //exo:weekly:'LikePlugin,SpaceInvitationPlugin' in PLF 4.1
        //exo:weekly:'{LikePlugin},{SpaceInvitationPlugin}' in PLF 4.2
        //NotificationUtils.listToString uses to transform that
        String newValue = NotificationUtils.listToString(NotificationUtils.stringToList(oldValue), AbstractService.VALUE_PATTERN);
        userSettingNode.setProperty(property, newValue);
      }
    } else if (AbstractService.EXO_INSTANTLY.equals(property)) {
      if (userSettingNode.hasProperty(AbstractService.EXO_INSTANTLY)) {
        Value value = userSettingNode.getProperty(property).getValue();
        /**Case 2: EXO_INSTANTLY changes to exo:MAIL_CHANNELChannel = {<PluginA>}, {<PluginB>}*/
        if (value != null) {
          String oldValue = value.getString();
          String newValue = NotificationUtils.listToString(NotificationUtils.stringToList(oldValue), AbstractService.VALUE_PATTERN);
          userSettingNode.setProperty(UserSettingServiceImpl.NAME_PATTERN.replace("{CHANNELID}", MailChannel.ID), newValue);
          
          //remove exo:instantly property
          //Passing a null as the second parameter removes the property. 
          //It is equivalent to calling remove on the Property object itself. 
          //For example, N.setProperty("P", (Value)null) would remove property called "P" of the node in N.
          userSettingNode.setProperty(AbstractService.EXO_INSTANTLY, (Value)null);
        }
      }
      
    } else if (AbstractService.EXO_IS_ACTIVE.equals(property)) {
      if (userSettingNode.hasProperty(AbstractService.EXO_IS_ACTIVE)) {
        Value value = userSettingNode.getProperty(property).getValue();
        if (value != null) {
          String oldValue = value.getString();
          String newValue = "";
          if ("true".equals(oldValue)) {//Setting not upgraded and mail is enabled
            newValue = loadActiveValue();
          } else if ("false".equals(oldValue) || oldValue.isEmpty()) {//Setting not upgraded and mail is disabled
            newValue = loadInactiveValue();
          } else {//Setting has already upgraded
            newValue = oldValue;
          }
          userSettingNode.setProperty(AbstractService.EXO_IS_ACTIVE, newValue);
        }
      }
    } else {
      userSettingNode.setProperty(property, propertyValue);
    }
  }
 
  /**
   * Loads the identities with offset and limit
   * 
   * @param session
   * @param offset
   * @param limit
   * @return the identity list
   */
  private NodeIterator getIdentityNodes(Session session, int offset, int limit) {
    //
    try {
      StringBuilder sqlQuery = new StringBuilder("SELECT * FROM ").append("soc:identitydefinition")
                                                                  .append(" WHERE ")
                                                                  .append(" (")
                                                                  .append("jcr:path LIKE '")
                                                                  .append("/production/soc:providers/soc:organization/%'")
                                                                  .append(" AND NOT jcr:path LIKE '")
                                                                  .append("/production/soc:providers/soc:organization/%/%'")
                                                                  .append(")");

      String queryStatement = sqlQuery.toString();
      QueryManager queryMgr = session.getWorkspace().getQueryManager();
      Query query = queryMgr.createQuery(queryStatement, Query.SQL);
      QueryImpl impl = (QueryImpl) query;

      //
      impl.setOffset(offset);
      impl.setLimit(limit);

      //
      return query.execute().getNodes();
    } catch (Exception ex) {
      LOG.error("Query is failed!.", ex);
      return null;
    }
  }
  
  private Session getJCRSession(SessionProvider sProvider, String wpName) {
    Session session = null;
    try {
      session = sProvider.getSession(wpName, CommonsUtils.getRepository());
    } catch (RepositoryException e) {
      LOG.error(e);
    }

    return session;
  }
  /**
   * Adds the default setting(mixintype) to the User setting
   * 
   * @param sProvider
   * @param userName the give userName
   */
  private void addMixin(SessionProvider sProvider, String userName) {
    try {
      Session session = getJCRSession(sProvider, settingWorkspace);
      Node userHomeNode = getUserSettingHome(session);
      Node userNode = userHomeNode.addNode(userName, AbstractService.STG_SIMPLE_CONTEXT);
      if (userNode.canAddMixin(AbstractService.MIX_DEFAULT_SETTING)) {
        userNode.addMixin(AbstractService.MIX_DEFAULT_SETTING);
        LOG.debug("|| Done to addMixin default setting for user: " + userName);
      }
      session.save();
    } catch (Exception e) {
      LOG.error("Failed to addMixin for user notification setting", e);
    }
  }
  
  private Node getUserSettingHome(Session session) throws Exception {
    Node settingNode = session.getRootNode().getNode(AbstractService.SETTING_NODE);
    Node userHomeNode = null;
    if (settingNode.hasNode(AbstractService.SETTING_USER_NODE) == false) {
      userHomeNode = settingNode.addNode(AbstractService.SETTING_USER_NODE, AbstractService.STG_SUBCONTEXT);
      session.save();
    } else {
      userHomeNode = settingNode.getNode(AbstractService.SETTING_USER_NODE);
    }
    return userHomeNode;
  }
  
  /**
   * Gets the user setting node by the give userName.
   * 
   * @param sProvider
   * @param userName
   * @return Setting node or NULL if not found
   */
  private Node getUserSettingNode(SessionProvider sProvider, String userName) {
    Session session = getJCRSession(sProvider, settingWorkspace);
    try {
      return (Node) session.getItem("/" + AbstractService.SETTING_USER_PATH + "/" + userName);
    } catch (Exception e) {
      return null;
    }
  }
  /**
   * Must upgrade only happens when the user setting has already the available notification setting.
   * 
   * Notice: 
   * In the case, user setting has both mixin and notif setting, it will be normalize setting by normalize() method
   * 
   * @param userNode
   * @return
   */
  private boolean mustUpgrade(Node userNode) {
    try {
      //user has both defaul setting and user setting
      if (userNode.isNodeType(AbstractService.MIX_DEFAULT_SETTING)) {
        if (userNode.hasNode(Scope.GLOBAL.toString().toLowerCase())) {
          Node global = userNode.getNode(Scope.GLOBAL.toString().toLowerCase());
          return global.hasProperty(AbstractService.EXO_INSTANTLY) || global.hasProperty(AbstractService.EXO_DAILY)
              || global.hasProperty(AbstractService.EXO_WEEKLY);
        } else {
          return false;
        }
      }
      
      //case without default setting and have user setting
      if (!userNode.isNodeType(AbstractService.MIX_DEFAULT_SETTING)) {
        if (userNode.hasNode(Scope.GLOBAL.toString().toLowerCase())) {
          Node global = userNode.getNode(Scope.GLOBAL.toString().toLowerCase());
          return global.hasProperty(AbstractService.EXO_INSTANTLY) || global.hasProperty(AbstractService.EXO_DAILY)
              || global.hasProperty(AbstractService.EXO_WEEKLY);
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Normalize the user setting following the cases:
   * 
   * CASE 1. 
   * Mixin type + Global node
   * - exo:daily, exo:weekly, or exo:instantly is existing >> remove Mixin type.
   * 
   * CASE 2
   * No Mixin type + Global node
   * - WITHOUT exo:daily, exo:weekly, or exo:instantly is NOT existing>> Add mixin type
   * 
   * CASE 3 
   * No Mixin type + WITHOUT Global node
   * -  Add mixin type
   * 
   * @param userNode
   * @param remoteId
   */
  private void normalize(Node userNode, String remoteId) {
    try {
      //user with default setting
      if (userNode.isNodeType(AbstractService.MIX_DEFAULT_SETTING)) {
        if (userNode.hasNode(Scope.GLOBAL.toString().toLowerCase())) {
          Node global = userNode.getNode(Scope.GLOBAL.toString().toLowerCase());
          if (global.hasProperty(AbstractService.EXO_INSTANTLY) || global.hasProperty(AbstractService.EXO_DAILY)
              || global.hasProperty(AbstractService.EXO_WEEKLY)) {
            LOG.info(String.format("   CASE 1:: %s user has both mixin and notif setting >> Action: remove mixin", remoteId));
            userNode.removeMixin(AbstractService.MIX_DEFAULT_SETTING);
            return;
          }
        }
      }
      
      //user doesn't have default setting and without Global node
      if (!userNode.isNodeType(AbstractService.MIX_DEFAULT_SETTING)) {
        if (userNode.hasNode(Scope.GLOBAL.toString().toLowerCase())) {
          Node global = userNode.getNode(Scope.GLOBAL.toString().toLowerCase());
          if (!global.hasProperty(AbstractService.EXO_INSTANTLY) && !global.hasProperty(AbstractService.EXO_DAILY)
              && !global.hasProperty(AbstractService.EXO_WEEKLY)) {
            LOG.info(String.format("   CASE 2:: %s user has NOT both mixin and notif setting >> Action: add mixin", remoteId));
            userNode.addMixin(AbstractService.MIX_DEFAULT_SETTING);
            return;
          }
        } else {
          LOG.info(String.format("   CASE 3:: %s user has NOT both mixin and global node >> Action: add mixin", remoteId));
          userNode.addMixin(AbstractService.MIX_DEFAULT_SETTING);
          return;
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
  
  
}
