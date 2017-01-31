/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.common.rest;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.testing.BaseResourceTestCase;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.wadl.research.HTTPMethods;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.user.UserStateModel;
import org.exoplatform.services.user.UserStateService;
import org.junit.Ignore;

import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Apr 23, 2014  
 */
@Ignore
public class RESTUserServiceTest extends BaseResourceTestCase {
  private final String WORKSPACE_COLLABORATION = "collaboration";
  private UserStateService userStateService;
  private RESTUserService restUserService;

  public void setUp() throws Exception {
    super.setUp();
    ConversationState c = new ConversationState(new Identity(session.getUserID()));
    ConversationState.setCurrent(c);

    userStateService = getService(UserStateService.class);
    restUserService = getService(RESTUserService.class);
    //
    ManageableRepository repo = repositoryService.getRepository(REPO_NAME);
    repo.getConfiguration().setDefaultWorkspaceName(WORKSPACE_COLLABORATION);
    session = repo.getSystemSession(WORKSPACE_COLLABORATION);
    root = session.getRootNode();
    //
    this.resourceBinder.addResource(restUserService, null);
  }

  public void tearDown() throws Exception {
    ManageableRepository repo  = repositoryService.getRepository(REPO_NAME);
    repo.getConfiguration().setDefaultWorkspaceName(WORKSPACE_NAME);
    //
    ExoCache<Serializable, UserStateModel> cache = getService(CacheService.class)
        .getCacheInstance(UserStateService.class.getName() + REPO_NAME);
    //
    cache.clearCache();
  }

  public void testUpdateState() throws Exception {
    String restPath = "/state/ping";

    UserStateModel userModel =
        new UserStateModel(session.getUserID(),
                           new Date().getTime(),
                           UserStateService.DEFAULT_STATUS);
    userStateService.save(userModel);

    ContainerResponse response = service(HTTPMethods.GET.toString(), restPath, StringUtils.EMPTY, null, null);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertTrue(userModel.getLastActivity() != userStateService.getUserState(session.getUserID()).getLastActivity());    
  }

  public void testOnline() throws Exception {
    String restPath = "/state/status";

    UserStateModel userModel =
        new UserStateModel(session.getUserID(),
                           new Date().getTime(),
                           UserStateService.DEFAULT_STATUS);
    userStateService.save(userModel);
    userStateService.ping(userModel.getUserId());

    ContainerResponse response = service(HTTPMethods.GET.toString(), restPath, StringUtils.EMPTY, null, null);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertTrue(response.getEntity().toString().contains("\"activity\":\"online\""));
    assertTrue(response.getEntity().toString().contains("\"status\":\"" + userModel.getStatus() + "\""));
    assertTrue(response.getEntity().toString().contains("\"userId\":\"" + userModel.getUserId() + "\""));

    assertFalse(response.getEntity().toString().contains("\"userId\":\"" + userModel.getUserId() + "temp\""));
  }
  
  public void testGetStatus() throws Exception {
    String restPath = "/state/status/" + session.getUserID();

    UserStateModel userModel =
        new UserStateModel(session.getUserID(),
                           new Date().getTime(),
                           "offline");
    userStateService.save(userModel);
    userStateService.ping(userModel.getUserId());

    ContainerResponse response = service(HTTPMethods.GET.toString(), restPath, StringUtils.EMPTY, null, null);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertTrue(response.getEntity().toString().contains("\"activity\":\"online\""));
    assertTrue(response.getEntity().toString().contains("\"status\":\"" + userModel.getStatus() + "\""));
    assertTrue(response.getEntity().toString().contains("\"userId\":\"" + userModel.getUserId() + "\""));

    assertFalse(response.getEntity().toString().contains("\"userId\":\"" + userModel.getUserId() + "temp\""));
  }
  
  public void testSetStatus() throws Exception {
    String restPath = "/state/status/" + session.getUserID() + "?status=offline";

    UserStateModel userModel =
        new UserStateModel(session.getUserID(),
                           new Date().getTime(),
                           UserStateService.DEFAULT_STATUS);
    userStateService.save(userModel);
    userStateService.ping(userModel.getUserId());
    assertTrue(userStateService.getUserState(session.getUserID()).getStatus().equals(UserStateService.DEFAULT_STATUS));

    ContainerResponse response = service(HTTPMethods.PUT.toString(), restPath, StringUtils.EMPTY, null, null);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    assertTrue(userStateService.getUserState(userModel.getUserId()).getStatus().equals("offline"));
  }
}
