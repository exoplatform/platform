
package org.exoplatform.portal.config.jcr;

import javax.jcr.Session;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryService;

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

/*
 * Created by The eXo Platform SAS
 * Author : tam.nguyen
 *          tamndrok@gmail.com
 * May 28, 2009  
 */
public abstract class DataStorageListAccess implements ListAccess<Object> {

  /**
   * The RegistryService.
   */
  protected RegistryService service;

  /**
   * RegistryService constructor.
   * 
   * @param service
   *          The RegistryService
   */
  public DataStorageListAccess(RegistryService service) {
    this.service = service;
  }

  /**
   * {@inheritDoc}
   */
  public abstract Object[] load(int index, int length) throws Exception ;

  /**
   * Determine the count of available users.
   * 
   * @param session
   *          The current session
   * @return list size
   * @throws Exception
   *           if any error occurs
   */
  public abstract int getSize() throws Exception;

}
