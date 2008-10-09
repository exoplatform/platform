/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.portal.layout;

import org.exoplatform.portal.config.model.Container;

/** 
 * The <code>PortalLayoutService</code> is used to save
 * and load the layout of components.
 * In eXo Portal, components are arranged in containers
 * and a list of containers is a layout.
 * <p>
 * Created by The eXo Platform SAS<br />
 * Jun 25, 2008<br />
 * </p>
 */
public interface PortalLayoutService {
  
  /**
   * Creates new container object in the database.
   * If container is existing, it will throw exception.
   * @param container  the container that is created.
   * @throws Exception
   */
  public void create(Container container) throws Exception ;

  /**
   * Creates new container object in the database.
   * If container is existing, it will throw exception.
   * @param container  the container that is created.
   * @param userId of the user owner of this container.
   * @throws Exception
   */
  public void create(Container container, String userId) throws Exception ;
  
  /**
   * Updates the container object in the database.
   * If the container does not exist, it will throw exception.
   * @param container  the container that is updated.
   * @throws Exception
   */
  public void save(Container container) throws Exception ;


  /**
   * Updates the container object in the database.
   * If the container does not exist, it will throw exception.
   * @param container  the container that is updated.
   * @param userId of the user owner of this container.
   * @throws Exception
   */
  public void save(Container container, String userId) throws Exception ;
  
  /**
   * Removes the container from database.
   * @param container the container object that is removed.
   * @param userId of the user owner of this container.
   * @throws Exception
   */
  public void remove(Container container) throws Exception ;

  /**
   * Removes the container from database.
   * @param container the container object that is removed.
   * @param userId of the user owner of this container.
   * @throws Exception
   */
  public void remove(Container container, String userId) throws Exception ;
  
  /**
   * Gets the container in the database by id.
   * @param id the id of container.
   * @return the container or null if not found.
   * @throws Exception
   */
  public Container getContainer(String id) throws Exception ;


  /**
   * Gets the container in the database by id.
   * @param id the id of container.
   * @param userId of the user owner of this container.
   * @return the container or null if not found.
   * @throws Exception
   */
  public Container getContainer(String id, String userId) throws Exception ;

}
