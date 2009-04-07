/*
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
 */
package org.exoplatform.portal.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.portal.config.jcr.DataMapper;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:trongtt@gmail.com">Tran The Trong</a>
 * @version $Revision$
 */
@Managed
@NameTemplate({
  @Property(key = "view", value = "portal"),
  @Property(key = "service", value = "statistic"),
  @Property(key = "type", value = "portal")
})
@ManagedDescription("The portal statistic service")
public class PortalStatisticService implements Startable {

  private ConcurrentMap<String, PortalStatistic> apps = new ConcurrentHashMap<String, PortalStatistic>();

  private RegistryService              regService_;

  private DataMapper                   mapper_;

  public PortalStatisticService(RegistryService res) {
    regService_ = res;
    mapper_ = new DataMapper();

  }

  /*
   * Returns the list of the known portal names.
   */
  @Managed
  @ManagedDescription("The list of identifier of the known portals")
  public String[] getPortalList() {
    ArrayList<String> list = new ArrayList<String>();
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT);

    try {
      String registryNodePath = regService_.getRegistry(sessionProvider).getNode().getPath();
      Session session = regService_.getRegistry(sessionProvider).getNode().getSession();
      generateLikeScript(builder, "jcr:path", registryNodePath + "/%");
      generateLikeScript(builder, DataMapper.EXO_DATA_TYPE, "PortalConfig");
      QueryManager queryManager = session.getWorkspace().getQueryManager();
      javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql");
      QueryResult result = query.execute();

      NodeIterator itr = result.getNodes();
      while (itr.hasNext()) {
        Node node = itr.nextNode();
        String entryPath = node.getPath().substring(registryNodePath.length() + 1);
        RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath);
        list.add(mapper_.fromDocument(entry.getDocument(), PortalConfig.class).getName());
      }
      Collections.sort(list);
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      sessionProvider.close();
    }
    return list.toArray(new String[list.size()]);
  }

  /*
   * Get PortalStatistic, if it doesn't exits, create a new one.
   */
  public PortalStatistic getPortalStatistic(String appId) {
    PortalStatistic app = apps.get(appId);
    if (app == null) {
      app = new PortalStatistic(appId);
      PortalStatistic existing = apps.putIfAbsent(appId, app);
      if (existing != null) {
        app = existing;
      }
    }
    return app;
  }

  /*
   * Returns the max time of a specified portal
   */
  @Managed
  @ManagedDescription("The maximum execution time of a specified portal in seconds")
  public double getMaxTime(@ManagedDescription("The portal id") @ManagedName("portalId") String id) {
    return toSeconds(getPortalStatistic(id).getMaxTime());
  }

  /*
   * Return the min time of a specified portal
   */
  @Managed
  @ManagedDescription("The mininum execution time of a specified portal in seconds")
  public double getMinTime(@ManagedDescription("The portal id") @ManagedName("portalId") String id) {
    return toSeconds(getPortalStatistic(id).getMinTime());
  }

  /*
   * Return the average time of a specified portal
   */
  @Managed
  @ManagedDescription("The average execution time of a specified portal in seconds")
  public double getAverageTime(@ManagedDescription("The portal id") @ManagedName("portalId") String id) {
    return toSeconds(getPortalStatistic(id).getAverageTime());
  }

  /*
   * Return the throughput of a specified portal
   */
  @Managed
  @ManagedDescription("The number of request per second of a specified portal")
  public double getThroughput(@ManagedDescription("The portal id") @ManagedName("portalId") String id) {
    return getPortalStatistic(id).getThroughput();
  }

  /*
   * Return the count of a specified portal
   */
  @Managed
  @ManagedDescription("The execution count of a specified portal")
  public long getExecutionCount(@ManagedDescription("The portal id") @ManagedName("portalId") String id) {
    return getPortalStatistic(id).viewCount();
  }

  private void generateLikeScript(StringBuilder sql, String name, String value) {
    if (value == null || value.length() < 1)
      return;
    if (sql.indexOf(" where") < 0)
      sql.append(" where ");
    else
      sql.append(" and ");
    value = value.replace('*', '%');
    value = value.replace('?', '_');
    sql.append(name).append(" like '").append(value).append("'");
  }

  private double toSeconds(double value) {
    return value == -1 ? -1 : value / 1000D;
  }

  public void start() {
  }

  public void stop() {
  }
}
