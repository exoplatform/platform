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
package org.exoplatform.application.gadget;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Aug 6, 2008  
 */
public interface SourceStorage {
  /**
   * This method will get source from a specify source's path in database
   * @param sourcePath
   * @return
   * @throws Exception
   */
  public Source getSource(String sourcePath) throws Exception ;
  /**
   * This method will save source to database to a specify path
   * @param dirPath
   * @param source
   * @throws Exception
   */
  public void saveSource(String dirPath, Source source) throws Exception ;
  /**
   * This method will remove source from database base on source path
   * @param sourcePath
   * @throws Exception
   */
  public void removeSource(String sourcePath) throws Exception ;
  /**
   * This method will get source URI from database. 
   * For example: jcr/repository/collaboration/source/Todo.xml
   * @param sourcePath
   * @return
   */
  public String getSourceURI(String sourcePath) ;

}