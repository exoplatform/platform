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
package org.exoplatform.portal.config;

import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Mar 3, 2008  
 */
public class PageTemplateConfig {
  
  String location ;
  List<String> templates ;
  
  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }
  
  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }
  
  /**
   * @return the templates
   */
  public List<String> getTemplates() {
    return templates;
  }
  
  /**
   * @param templates the templates to set
   */
  public void setTemplates(List<String> templates) {
    this.templates = templates;
  }
  
}
