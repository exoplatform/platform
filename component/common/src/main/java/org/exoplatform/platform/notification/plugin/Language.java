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
package org.exoplatform.platform.notification.plugin;

public class Language {

  private String name;

  private String subject;

  private String template;

  public Language() {
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
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param subject the subject to set
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * @return the template
   */
  public String getTemplate() {
    return template;
  }

  /**
   * @param template the template to set
   */
  public void setTemplate(String template) {
    this.template = template;
  }

}
