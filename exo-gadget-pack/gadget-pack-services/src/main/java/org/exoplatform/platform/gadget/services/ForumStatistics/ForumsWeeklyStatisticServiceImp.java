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
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatisticService;
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatistic;
import org.exoplatform.ks.common.jcr.KSDataLocation;
import org.exoplatform.ks.common.jcr.PropertyReader;
import org.exoplatform.ks.common.jcr.SessionManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 * Forum Weekly Statistis service
 * @author <a href="tung.do@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */
public class ForumsWeeklyStatisticServiceImp implements ForumsWeeklyStatisticService, Startable {

  private static final Log   log                 = ExoLogger.getLogger(ForumsWeeklyStatisticServiceImp.class);

  public static final String STATISTIC_WEEK_HOME = "statisticWeekHome"; //node node of node that contain weekly statistic

  private KSDataLocation     dataLocator;

  private SessionManager     sessionManager;

  private String             repository;

  private String             workspace;

  /**
   * Constructor.
   *
   * @param params init parameters that were config in configuration file
   * @param locator Object contain JCR information of Forums 
   * @param nodeHierarchyCreator
   * @throws Exception
   */
  public ForumsWeeklyStatisticServiceImp(InitParams params,
                                         KSDataLocation locator,
                                         NodeHierarchyCreator nodeHierarchyCreator) throws Exception {
    this.dataLocator = locator;
    this.sessionManager = dataLocator.getSessionManager();
    repository = dataLocator.getRepository();
    workspace = dataLocator.getWorkspace();
  }

  public String getRepository() {
    return repository;
  }

  public String getWorkspace() {
    return workspace;
  }

  /**
   * Start the service and create a new node that contains all Forums Weekly Statistic if not found.
   * 
   * @see org.picocontainer.Startable#start()
   */
  public void start() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = sessionManager.getSession(sProvider);
      Node forumHome = session.getRootNode().getNode(dataLocator.getForumHomeLocation());
      if (!forumHome.hasNode(STATISTIC_WEEK_HOME)) {
        Node statisticWeekHome = forumHome.addNode(STATISTIC_WEEK_HOME, "nt:unstructured");
        session.save();
      }
    } catch (Exception e) {
      log.error("can not start ForumsWeeklyStatisticService", e);
    } finally {
      sProvider.close();
    }
  }

  public void stop() {
  }

  /**
   * Get node that contains all Forums Weekly Statistic node
   * 
   * @param sProvider
   * @return node that contains Forums weekly statistic
   * @throws Exception
   */
  private Node getStatisticWeekHomeNode(SessionProvider sProvider) throws Exception {
    String path = dataLocator.getForumHomeLocation() + "/" + STATISTIC_WEEK_HOME;
    return sessionManager.getSession(sProvider).getRootNode().getNode(path);
  }


  private ForumsWeeklyStatistic getForumsWeeklyStatistic(Node node) throws Exception {

    ForumsWeeklyStatistic statistic = new ForumsWeeklyStatistic();
    statistic.setId(node.getName());
    statistic.setStartDate(node.getProperty("exo:startDate").getDate().getTime());
    statistic.setStartPostsCountOfWeek(node.getProperty("exo:startPostsCountOfWeek").getLong());
    statistic.setPostsCountOfWeek(node.getProperty("exo:postsCountOfWeek").getLong());
    statistic.setLastStatEntry(node.getProperty("exo:lastStatEntry").getDate().getTime());

    return statistic;
  }

  /**
   * Get lastest Forums Weekly Statistic 
   * 
   * @return lastest Forums Weekly Statistic 
   */
  public ForumsWeeklyStatistic getLastForumsWeeklyStatistic() throws Exception {

    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Node statisticHome = getStatisticWeekHomeNode(sProvider);
      QueryManager qm = statisticHome.getSession().getWorkspace().getQueryManager();
      String pathQuery = "/jcr:root" + statisticHome.getPath()
          + "/element(*,exo:periodicForumStats) order by @exo:startDate descending";
      Query query = qm.createQuery(pathQuery, Query.XPATH);
      QueryResult result = query.execute();
      NodeIterator iter = result.getNodes();
      if (iter != null && iter.getSize() > 0)
        return getForumsWeeklyStatistic(iter.nextNode());
    } catch (Exception e) {
      log.error("can not get getLastForumsWeeklyStatistic", e);
    } finally {
      sProvider.close();
    }
    return null;

  }

  /**
   * Get all Forums Weekly Statistic
   * 
   * @return all Forums Weekly Statistic
   */
  public List<ForumsWeeklyStatistic> getAllForumsWeeklyStatistic() throws Exception {

    SessionProvider sProvider = SessionProvider.createSystemProvider();
    List<ForumsWeeklyStatistic> listForumsWeeklyStatistic = new ArrayList<ForumsWeeklyStatistic>();
    try {
      Node statisticHome = getStatisticWeekHomeNode(sProvider);
      QueryManager qm = statisticHome.getSession().getWorkspace().getQueryManager();
      String pathQuery = "/jcr:root" + statisticHome.getPath()
          + "/element(*,exo:periodicForumStats) order by @exo:startDate descending";
      Query query = qm.createQuery(pathQuery, Query.XPATH);
      QueryResult result = query.execute();
      NodeIterator iter = result.getNodes();
      while (iter.hasNext()) {
        listForumsWeeklyStatistic.add(getForumsWeeklyStatistic(iter.nextNode()));
      }
    } catch (Exception e) {
      log.error("can not get getAllForumsWeeklyStatistic", e);
    } finally {
      sProvider.close();
    }
    return listForumsWeeklyStatistic;

  }

  /**
   * Store Forums Weekly statistic in JCR data
   */
  public void saveForumsWeeklyStatistic(ForumsWeeklyStatistic forumsWeeklyStatistic) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {

      Node forumStatisticHome = getStatisticWeekHomeNode(sProvider);

      Node statisticWeekNode = null;
      try {
        //find latest node to update
        statisticWeekNode = forumStatisticHome.getNode(forumsWeeklyStatistic.getId());
      } catch (PathNotFoundException e) {
        //create new node for new week
        statisticWeekNode = forumStatisticHome.addNode(forumsWeeklyStatistic.getId(),
                                                       "exo:periodicForumStats");
      }

      Calendar cal = Calendar.getInstance();
      cal.setTime(forumsWeeklyStatistic.getStartDate());
      statisticWeekNode.setProperty(EXO_START_DATE, cal);

      statisticWeekNode.setProperty(EXO_START_POSTS_COUNT_OF_WEEK,
                                    forumsWeeklyStatistic.getStartPostsCountOfWeek());

      statisticWeekNode.setProperty(EXO_POSTS_COUNT_OF_WEEK,
                                    forumsWeeklyStatistic.getPostsCountOfWeek());

      cal.setTime(forumsWeeklyStatistic.getLastStatEntry());
      statisticWeekNode.setProperty(EXO_LAST_STATISTIC_ENTRY, cal);

      if (forumStatisticHome.isNew()) {
        forumStatisticHome.getSession().save();
      } else
        forumStatisticHome.save();

    } catch (Exception e) {
      log.error("save ForumsWeeklyStatistic failure", e);
    } finally {
      sProvider.close();
    }
  }

  /**
   * Get total Post Count of Forums
   * 
   * @return total Post Count of Forums
   */
  public long getPostCountForumStatistic() throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Node statisticNode = sessionManager.getSession(sProvider)
                                         .getRootNode()
                                         .getNode(dataLocator.getForumStatisticsLocation());
      return new PropertyReader(statisticNode).l("exo:postCount", 0);
    } catch (Exception e) {
      log.error("getPostCountForumStatistic failure", e);
    } finally {
      sProvider.close();
    }
    return 0;
  }

}
