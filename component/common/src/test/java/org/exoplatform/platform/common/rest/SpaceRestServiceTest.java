package org.exoplatform.platform.common.rest;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.platform.common.space.rest.SpaceRestServiceImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class SpaceRestServiceTest extends BaseRestServicesTestCase {

  protected Class<?> getComponentClass() {
    return SpaceRestServiceImpl.class;
  }

  public void testSearchSpace() throws Exception {
    String path = "/space/user/searchSpace/?fields=id,url,displayName,avatarUrl&keyword=";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(path, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);
    envctx.put(SecurityContext.class, new MockSecurityContext("foo"));

    Map<String, Object> ssResults = new HashMap<String, Object>();
    Space space1 = new Space();
    space1.setPrettyName("space1");
    space1.setId("space1");
    space1.setGroupId("/spaces/space1");
    space1.setVisibility(Space.HIDDEN);
    space1.setMembers(new String[] { "foo" });
    Space space2 = new Space();
    space2.setPrettyName("space2");
    space2.setId("space2");
    space2.setGroupId("/spaces/space2");
    space2.setVisibility(Space.PUBLIC);
    space2.setRegistration(Space.CLOSE);
    space2.setMembers(new String[] { "foo" });
    Space space3 = new Space();
    space3.setPrettyName("space3");
    space3.setId("space3");
    space3.setGroupId("/spaces/space3");
    space3.setVisibility(Space.PUBLIC);
    space3.setRegistration(Space.OPEN);
    space3.setMembers(new String[] { "foo" });
    Space space4 = new Space();
    space4.setPrettyName("space4");
    space4.setId("space4");
    space4.setGroupId("/spaces/space4");
    space4.setVisibility(Space.PUBLIC);
    space4.setRegistration(Space.OPEN);
    space4.setMembers(new String[] { "foo" });
    Space space5 = new Space();
    space5.setPrettyName("space5");
    space5.setId("space5");
    space5.setGroupId("/spaces/space5");
    space5.setVisibility(Space.PUBLIC);
    space5.setRegistration(Space.OPEN);
    space5.setMembers(new String[] { "foo" });
    Space space6 = new Space();
    space6.setPrettyName("space6");
    space6.setId("space6");
    space6.setGroupId("/spaces/space6");
    space6.setVisibility(Space.PUBLIC);
    space6.setRegistration(Space.OPEN);
    space6.setMembers(new String[] { "foo" });
    List<Space> allSpaces = Arrays.asList(new Space[] { space1, space2, space3, space4, space5, space6 });
    ssResults.put("getMemberSpacesByFilter", new Invoker() {
      public Object invoke(Object[] args) {
        SpaceFilter spaceFilter = (SpaceFilter) args[1];
        String searchKeyword = spaceFilter == null ? null : spaceFilter.getSpaceNameSearchCondition();
        if (StringUtils.isBlank(searchKeyword)) {
          return new MockListAccess<Space>(allSpaces.toArray(new Space[0]));
        } else {
          return new MockListAccess<Space>(allSpaces.stream()
                                                    .filter(space -> space.getPrettyName().contains(searchKeyword))
                                                    .collect(Collectors.toList())
                                                    .toArray(new Space[0]));
        }
      }
    });
    ssResults.put("getLastAccessedSpace", new MockListAccess<Space>(new Space[] { space1 }));

    SpaceService ss = createProxy(SpaceService.class, ssResults);
    getContainer().registerComponentInstance("SpaceService", ss);

    ContainerResponse resp = launcher.service("GET", path, "", null, null, envctx);
    assertEquals(200, resp.getStatus());
    assertEquals("application/json", resp.getContentType().toString());
    assertTrue(resp.getEntity().toString().contains("space1"));
    assertTrue(resp.getEntity().toString().contains("space2"));
    assertTrue(resp.getEntity().toString().contains("space3"));
    assertTrue(resp.getEntity().toString().contains("space4"));
    assertTrue(resp.getEntity().toString().contains("space5"));
    assertTrue(resp.getEntity().toString().contains("space6"));

    resp = launcher.service("GET", path + "space", "", null, null, envctx);
    assertEquals(200, resp.getStatus());
    assertEquals("application/json", resp.getContentType().toString());
    assertTrue(resp.getEntity().toString().contains("space1"));
    assertTrue(resp.getEntity().toString().contains("space2"));
    assertTrue(resp.getEntity().toString().contains("space3"));
    assertTrue(resp.getEntity().toString().contains("space4"));
    assertTrue(resp.getEntity().toString().contains("space5"));
    assertTrue(resp.getEntity().toString().contains("space6"));

    resp = launcher.service("GET", path + "1", "", null, null, envctx);
    assertEquals(200, resp.getStatus());
    assertEquals("application/json", resp.getContentType().toString());
    assertTrue(resp.getEntity().toString().contains("space1"));
    assertFalse(resp.getEntity().toString().contains("space2"));
    assertFalse(resp.getEntity().toString().contains("space3"));
    assertFalse(resp.getEntity().toString().contains("space4"));
    assertFalse(resp.getEntity().toString().contains("space5"));
    assertFalse(resp.getEntity().toString().contains("space6"));

    resp = launcher.service("GET", path + "2", "", null, null, envctx);
    assertEquals(200, resp.getStatus());
    assertEquals("application/json", resp.getContentType().toString());
    assertFalse(resp.getEntity().toString().contains("space1"));
    assertTrue(resp.getEntity().toString().contains("space2"));
    assertFalse(resp.getEntity().toString().contains("space3"));
    assertFalse(resp.getEntity().toString().contains("space4"));
    assertFalse(resp.getEntity().toString().contains("space5"));
    assertFalse(resp.getEntity().toString().contains("space6"));

    resp = launcher.service("GET", path + "6", "", null, null, envctx);
    assertEquals(200, resp.getStatus());
    assertEquals("application/json", resp.getContentType().toString());
    assertFalse(resp.getEntity().toString().contains("space1"));
    assertFalse(resp.getEntity().toString().contains("space2"));
    assertFalse(resp.getEntity().toString().contains("space3"));
    assertFalse(resp.getEntity().toString().contains("space4"));
    assertFalse(resp.getEntity().toString().contains("space5"));
    assertTrue(resp.getEntity().toString().contains("space6"));

    getContainer().unregisterComponent("SpaceService");
  }
}
