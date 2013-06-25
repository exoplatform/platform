/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.notification;

import java.util.HashMap;
import java.util.Map;

public class Provider {
  private String              type;

  private String              name;

  private String              isActive  = "false";

  private Map<String, String> templates = new HashMap<String, String>();

  private Map<String, String> subjects = new HashMap<String, String>();

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the id to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the isActive
   */
  public String getIsActive() {
    return isActive;
  }

  /**
   * @return the boolean of isActive
   */
  public boolean isActive() {
    return Boolean.valueOf(getIsActive());
  }

  /**
   * @param isActive the isActive to set
   */
  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = String.valueOf(isActive);
  }

  /**
   * @return the templates
   */
  public Map<String, String> getTemplates() {
    return templates;
  }

  /**
   * @param templates the templates to set
   */
  public void setTemplates(Map<String, String> templates) {
    this.templates = templates;
  }
  
  public void addTemplate(String language, String template) {
    this.templates.put(language, template);
  }

  /**
   * @return the subjects
   */
  public Map<String, String> getSubjects() {
    return subjects;
  }
  
  /**
   * @param subjects the subjects to set
   */
  public void setSubjects(Map<String, String> subjects) {
    this.subjects = subjects;
  }
  
  public void addSubject(String language, String subject) {
    this.subjects.put(language, subject);
  }
}
