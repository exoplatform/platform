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
package org.exoplatform.portal.mail;

import org.exoplatform.container.component.ComponentPlugin;


/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@yahoo.com
 * Jul 24, 2008  
 */
public interface MailService {
  /**
   * This method will send message to address but you want send
   * 
   * @param  recipients recipients of email
   * @param  subject sublect of email
   * @param  meassge content of email
   * @param  from sender
   * @throws Exception the exception
   */
  public void sendMessage(String recipients[ ], String subject, String message , String from) throws Exception ;
  
  /**
   * Adds the plugin.
   * 
   * @param plugin the plugin
   * 
   * @throws Exception the exception
   */
  public void addPlugin(ComponentPlugin plugin) throws Exception ;
}
