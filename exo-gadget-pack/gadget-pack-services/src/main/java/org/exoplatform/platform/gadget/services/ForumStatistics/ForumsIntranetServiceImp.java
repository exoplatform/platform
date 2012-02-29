/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
package org.exoplatform.platform.gadget.services.ForumStatistics;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ks.common.jcr.KSDataLocation;
import org.exoplatform.ks.common.jcr.PropertyReader;
import org.exoplatform.ks.common.jcr.SessionManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 * @author <a href="tungdt@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */
public class ForumsIntranetServiceImp implements ForumsIntranetService {

  private static final Log LOG = ExoLogger.getLogger(ForumsIntranetServiceImp.class);
  

  public static final String JCR_ROOT = "/jcr:root";

  private KSDataLocation     dataLocator;

  private SessionManager     sessionManager;

  public ForumsIntranetServiceImp(InitParams params, KSDataLocation locator) throws Exception {
    this.dataLocator = locator;
    this.sessionManager = dataLocator.getSessionManager();
  }

  /**
   * get Top {maxcount} rated of topics by permission of userId
   * 
   * @param userId String is userId of user login
   * @param maxcount maximum of result
   * @return top {maxcount} rated of topics
   * @throws Exception
   */
  public List<TopicBean> getTopRateTopicByUser(List<String> allInfoOfUser, int maxcount) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    List<TopicBean> topicBeans = new ArrayList<TopicBean>();
    try {
      long roleUser = getUserRoleOfForum(sProvider,
                                         (allInfoOfUser.size() > 0) ? allInfoOfUser.get(0) : "");
      NodeIterator iter = getTopVoteTopicByUser(sProvider, (roleUser <= 1) ? true : false);
      //System.out.println(String.format("\n Size: %s \n userRole: %s \nAllofUser: %s", iter.getSize(), roleUser, allInfoOfUser.toString()));
      TopicBean bean;
      PropertyReader reader;
      while (iter.hasNext()) {
        Node node = iter.nextNode();
        if (hasPermissionInForum(sProvider, node, allInfoOfUser, roleUser)) {
          reader = new PropertyReader(node);
          bean = new TopicBean(node.getName());
          bean.setOwner(reader.string("exo:owner"));
          bean.setCreateDate(reader.date("exo:createdDate"));
          bean.setLink(reader.string("exo:link"));
          bean.setTitle(reader.string("exo:name"));
          bean.setVoteRating(reader.d("exo:voteRating"));
          bean.setNumberOfUserVoteRating(reader.list("exo:userVoteRating").size());
          topicBeans.add(bean);
          if (topicBeans.size() == maxcount)
            break;
        }
      }
    } catch (Exception e) {
      LOG.debug("Failed to get top voted rating topics");
    } finally {
      sProvider.close();
    }
    return topicBeans;
  }

  private boolean isListEmpty(List<String> list) {
    if (list == null || list.size() == 0)
      return true;
    for (String string : list) {
      if (string != null && string.trim().length() > 0)
        return false;
    }
    return true;
  }

  private long getUserRoleOfForum(SessionProvider sProvider, String userName) {
    if (userName == null || userName.trim().length() == 0)
      return 3;
    try {
      String userPatch = dataLocator.getUserProfilesLocation() + "/" + userName;
      Node userNode = sessionManager.getSession(sProvider).getRootNode().getNode(userPatch);
      return new PropertyReader(userNode).l("exo:userRole", 3);
    } catch (Exception e) {
      LOG.debug("Failed to get user role of forum.", e);
    }
    return 3;
  }

  private boolean hasPermissionInForum(SessionProvider sProvider, Node topicNode, 
                                       List<String> allInfoOfUser, long userRole) throws Exception {
    try {
      // check for administrators. If is admin --> return true;
      if (userRole == 0)
        return true;
      Node forumNode = topicNode.getParent();
      Node categoryNode = forumNode.getParent();
      PropertyReader reader = new PropertyReader(topicNode);
      // permission in topic
      Set<String> viewers = reader.set("exo:canView", new HashSet<String>());
      // permission in forum
      reader = new PropertyReader(forumNode);
      // forum is closed --> return false;
      if (reader.bool("exo:isClosed"))
        return false;
      // check for moderators
      if (userRole == 1) {
        List<String> moderators = reader.list("exo:moderators", new ArrayList<String>());
        if (!isListEmpty(allInfoOfUser)) {
          for (String string : moderators) {
            // user's moderator of the forum content the topic.
            if (allInfoOfUser.contains(string))
              return true;
          }
        }
      }
      viewers.addAll(reader.set("exo:viewer", new HashSet<String>()));

      // permission in category
      reader = new PropertyReader(categoryNode);
      // check viewer
      viewers.addAll(reader.set("exo:viewer", new HashSet<String>()));
      // check user private.
      viewers.addAll(reader.set("exo:userPrivate", new HashSet<String>()));
      // if viewer is empty then topic public.
      if (isListEmpty(new ArrayList<String>(viewers))) {
        return true;
      }
      // if user login and viewer list not empty.
      if (!isListEmpty(allInfoOfUser)) {
        for (String string : viewers) {
          if (allInfoOfUser.contains(string.trim()))
            return true;
        }
      }
    } catch (Exception e) {
      LOG.debug("Failed to checking has premission viewing topic add in forum.");
    }
    return false;
  }

  /**
   * Get forum home node
   * 
   * @param sProvider
   * @return
   * @throws Exception
   */
  private Node getForumHomeNode(SessionProvider sProvider) throws Exception {
    String path = dataLocator.getForumHomeLocation();
    return sessionManager.getSession(sProvider).getRootNode().getNode(path);
  }

  public NodeIterator getTopVoteTopicByUser(SessionProvider sProvider, boolean isMod) throws Exception {
    try {
      Node categoryHome = getForumHomeNode(sProvider);
      QueryManager qm = categoryHome.getSession().getWorkspace().getQueryManager();
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(JCR_ROOT).append(categoryHome.getPath()).append("//element(*,exo:topic)");
      if (!isMod) {
        stringBuffer.append("[@exo:isClosed='false' and @exo:isWaiting='false' and @exo:isApproved='true' and @exo:isActive='true' and @exo:isActiveByForum='true']");
      }
      stringBuffer.append(" order by @exo:voteRating descending, @exo:isSticky descending ,exo:createdDate descending");
      String pathQuery = stringBuffer.toString();
      Query query = qm.createQuery(pathQuery, Query.XPATH);
      QueryResult result = query.execute();
      return result.getNodes();
    } catch (Exception e) {
      return null;
    }
  }
}
