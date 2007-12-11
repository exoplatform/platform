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
package org.exoplatform.services.xml.serialize;

import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 21, 2007  
 */
public interface BeanMapper {

  public <T> T toBean(Class<T> clazz, XMLDocument document) throws Exception;
  
  public <T> T toBean(Class<T> clazz, XMLNode node) throws Exception ;
  
  public <T> void toBean(Class<T> clazz, T object, XMLNode node) throws Exception ;
}
