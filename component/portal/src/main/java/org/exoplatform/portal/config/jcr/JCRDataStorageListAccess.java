/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.config.jcr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.portal.config.Query;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="trong.tran@exoplatform">Trong Tran</a>
 */
public class JCRDataStorageListAccess implements ListAccess<Object> {

  /**
   * The query.
   */
  private Query<?>      q;

  private Comparator<Object> sortComparator;

  private DataMapper mapper_ = new DataMapper();

  /**
   * JCRUserListAccess constructor.
   * 
   * @param service The JCROrganizationService
   */
  /**
   * The RegistryService.
   */
  protected RegistryService service_;
  
  public JCRDataStorageListAccess(RegistryService service, Query<?> query, Comparator<Object> sortComparator) {
  	service_ = service;
    this.q = query;
    this.sortComparator = sortComparator;
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT);
    String registryNodePath = service_.getRegistry(sessionProvider).getNode().getPath();
    generateLikeScript(builder, "jcr:path", registryNodePath + "/%");
    generateLikeScript(builder, DataMapper.EXO_DATA_TYPE, q.getClassType().getSimpleName());
    generateContainScript(builder, DataMapper.EXO_OWNER_TYPE, q.getOwnerType());
    generateContainScript(builder, DataMapper.EXO_OWNER_ID, q.getOwnerId());
    generateContainScript(builder, DataMapper.EXO_NAME, q.getName());
    generateContainScript(builder, DataMapper.EXO_TITLE, q.getTitle());
    Session session = service_.getRegistry(sessionProvider).getNode().getSession();
    try {
      QueryManager queryManager = session.getWorkspace().getQueryManager();
      javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql");
      QueryResult result = query.execute();
      return (int) result.getNodes().getSize();
    } finally {
      sessionProvider.close();
    }
  }

  /**
   * {@inheritDoc}
   */
  public Object[] load(int index, int length) throws Exception {
    if (index < 0)
      throw new IllegalArgumentException("Illegal index: index must be a positive number");

    if (length < 0)
      throw new IllegalArgumentException("Illegal length: length must be a positive number");

    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT);
    String registryNodePath = service_.getRegistry(sessionProvider).getNode().getPath();
    generateLikeScript(builder, "jcr:path", registryNodePath + "/%");
    generateLikeScript(builder, DataMapper.EXO_DATA_TYPE, q.getClassType().getSimpleName());
    generateContainScript(builder, DataMapper.EXO_OWNER_TYPE, q.getOwnerType());
    generateContainScript(builder, DataMapper.EXO_OWNER_ID, q.getOwnerId());
    generateContainScript(builder, DataMapper.EXO_NAME, q.getName());
    generateContainScript(builder, DataMapper.EXO_TITLE, q.getTitle());
    Session session = service_.getRegistry(sessionProvider).getNode().getSession();
    try {
      QueryManager queryManager = session.getWorkspace().getQueryManager();
      javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql");
      ((QueryImpl) query).setLimit(length);
      ((QueryImpl) query).setOffset(index); 
      QueryResult result = query.execute();
      ArrayList<Object> list = new ArrayList<Object>();
      NodeIterator itr = result.getNodes();

      while(itr.hasNext()) {
      	Node uNode = itr.nextNode();
      	String entryPath = uNode.getPath().substring(registryNodePath.length() + 1);
      	RegistryEntry entry = service_.getEntry(sessionProvider, entryPath);
      	list.add(mapper_.fromDocument(entry.getDocument(), q.getClassType()));
      }
      if (sortComparator != null) Collections.sort(list, sortComparator);
      return list.toArray();
    } finally {
    	sessionProvider.close();
    }
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

  private void generateContainScript(StringBuilder sql, String name, String value) {

    if (value == null || value.length() < 1)
      return;

    if (value.indexOf("*") < 0) {
      if (value.charAt(0) != '*')
        value = "*" + value;
      if (value.charAt(value.length() - 1) != '*')
        value += "*";
    }
    value = value.replace('?', '_');

    if (sql.indexOf(" where") < 0)
      sql.append(" where ");
    else
      sql.append(" and ");
    sql.append("contains(").append(name).append(", '").append(value).append("')");
  }
}
