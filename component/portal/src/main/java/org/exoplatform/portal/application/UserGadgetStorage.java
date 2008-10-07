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

import java.util.Set;
import java.util.Map;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Aug 8, 2007  
 */
public interface UserGadgetStorage {

  public void save(String userName, String gadgetType, String instanceId, String key, String value) throws Exception ;

  public void save(String userName, String gadgetType, String instanceId, Map<String, String> values) throws Exception ;
  
  public String get(String userName, String gadgetType, String instanceId, String key) throws Exception ;

  public Map<String,String> get(String userName, String gadgetType, String instanceId, Set<String> key) throws Exception ;
  
  public void delete(String userName, String gadgetType, String instanceId) throws Exception ;

  public void delete(String userName, String gadgetType, String instanceId, Set<String> keys) throws Exception ;
  
}
