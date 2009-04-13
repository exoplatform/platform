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
package org.exoplatform.portal.session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SAS
 * Author : Tan Pham Dinh
 *          tan.pham@exoplatform.com
 * Apr 9, 2009  
 */
public class TestSessionManagementService extends BasicTestCase {
  String sessionId = "session1" ;
  String sessionId2 = "session2" ;
  String userName = "username1" ;
  String userName2 = "username2" ;

  public void testCreate() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    SessionManagementService sessionService = (SessionManagementService) 
      container.getComponentInstanceOfType(SessionManagementService.class) ;
    sessionService.saveSessionUsername(sessionId, userName) ;
    String user = sessionService.getSessionUsername(sessionId) ;
    assertEquals(userName, user) ;
  }
  
  public void testSave() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    SessionManagementService sessionService = (SessionManagementService) 
      container.getComponentInstanceOfType(SessionManagementService.class) ;
    sessionService.saveSessionUsername(sessionId, userName) ;
    String user = sessionService.getSessionUsername(sessionId) ;
    assertEquals(userName, user) ;
    sessionService.saveSessionUsername(sessionId, userName2) ;
    user = sessionService.getSessionUsername(sessionId) ;
    assertNotSame(userName, user) ;
    assertEquals(userName2, user) ;
  }
  
  public void testClear() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    SessionManagementService sessionService = (SessionManagementService) 
      container.getComponentInstanceOfType(SessionManagementService.class) ;
    sessionService.saveSessionUsername(sessionId, userName) ;
    sessionService.saveSessionUsername(sessionId2, userName2) ;
    String user1 = sessionService.getSessionUsername(sessionId) ;
    String user2 = sessionService.getSessionUsername(sessionId2) ;
    assertEquals(userName, user1) ;
    assertEquals(userName2, user2) ;
    sessionService.clearAll() ;
    user1 = sessionService.getSessionUsername(sessionId) ;
    user2 = sessionService.getSessionUsername(sessionId2) ;
    assertNull(user1) ;
    assertNull(user2) ;
  }
  
  public void testDelete() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    SessionManagementService sessionService = (SessionManagementService) 
      container.getComponentInstanceOfType(SessionManagementService.class) ;
    sessionService.saveSessionUsername(sessionId, userName) ;
    String user1 = sessionService.getSessionUsername(sessionId) ;
    assertEquals(userName, user1) ;
    sessionService.deleteSessionUsername(sessionId) ;
    user1 = sessionService.getSessionUsername(sessionId) ;
    assertNull(user1) ;
  }
  
}
