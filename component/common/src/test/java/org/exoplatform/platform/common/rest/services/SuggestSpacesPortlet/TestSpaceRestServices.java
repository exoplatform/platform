package org.exoplatform.platform.common.rest.services.SuggestSpacesPortlet;

import org.apache.commons.lang.ArrayUtils;
import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

public class TestSpaceRestServices extends BaseRestServicesTestCase {

    protected Class<?> getComponentClass() {
       return SpaceRestServices.class;
    }

    public void testSuggestions() throws Exception {
        String path = "/homepage/intranet/spaces/suggestions";
        EnvironmentContext envctx = new EnvironmentContext();
        HttpServletRequest httpRequest =
           new MockHttpServletRequest(path, null, 0, "GET", null);
       
        envctx.put(HttpServletRequest.class, httpRequest);

        ContainerResponse resp =
           launcher.service("GET", path, "", null, null, envctx);
        assertEquals(500, resp.getStatus());

        Identity idRoot = new Identity(OrganizationIdentityProvider.NAME, "root");
        idRoot.setId("root");
        Identity idFoo = new Identity(OrganizationIdentityProvider.NAME, "foo");
        idFoo.setId("foo");
        Identity idBar = new Identity(OrganizationIdentityProvider.NAME, "bar");
        idBar.setId("bar");
        envctx.put(SecurityContext.class, new MockSecurityContext(idFoo.getRemoteId()));

        // There are nothing to suggest
        Map<String, Object> imResults = new HashMap<String, Object>();
        IdentityManager im = createProxy(IdentityManager.class, imResults);
        getContainer().registerComponentInstance(im);

        Map<String, Object> ssResults = new HashMap<String, Object>();
        ssResults.put("getPublicSpacesWithListAccess", new MockListAccess<Space>(new Space[]{}));
        SpaceService ss = createProxy(SpaceService.class, ssResults);
        getContainer().registerComponentInstance(ss);

        resp = launcher.service("GET", path, "", null, null, envctx);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType().toString());
        assertTrue(resp.getEntity().toString().contains("items"));


        // The current user has no connection
        // Don't propose hidden and closed spaces but also spaces where the current user is already
        // a member or a pending member
        imResults.put("getOrCreateIdentity", idFoo);
        imResults.put("getConnectionsWithListAccess", new MockListAccess<Identity>(new Identity[]{}));

        Space space1 = new Space();
        space1.setPrettyName("space1");
        space1.setId("space1");
        space1.setVisibility(Space.HIDDEN);
        Space space2 = new Space();
        space2.setPrettyName("space2");
        space2.setId("space2");
        space2.setVisibility(Space.PUBLIC);
        space2.setRegistration(Space.CLOSE);
        Space space3 = new Space();
        space3.setPrettyName("space3");
        space3.setId("space3");
        space3.setVisibility(Space.PUBLIC);
        space3.setRegistration(Space.OPEN);
        space3.setMembers(new String[]{idFoo.getRemoteId()});
        Space space4 = new Space();
        space4.setPrettyName("space4");
        space4.setId("space4");
        space4.setVisibility(Space.PUBLIC);
        space4.setRegistration(Space.OPEN);
        space4.setPendingUsers(new String[]{idFoo.getRemoteId()});
        Space space5 = new Space();
        space5.setPrettyName("space5");
        space5.setId("space5");
        space5.setVisibility(Space.PUBLIC);
        space5.setRegistration(Space.OPEN);
        ssResults.put("getPublicSpacesWithListAccess", new MockListAccess<Space>(new Space[]{space1, space2, space3,
           space4, space5}));
        ssResults.put("getLastSpaces", Arrays.asList(space1, space2, space3, space4, space5));
        ssResults.put("isMember", new Invoker() {
            public Object invoke(Object[] args) {
                return ArrayUtils.contains(((Space)args[0]).getMembers(), args[1]);
            }
        });
        ssResults.put("isPendingUser", new Invoker() {
            public Object invoke(Object[] args) {
                return ArrayUtils.contains(((Space)args[0]).getPendingUsers(), args[1]);
            }
       });

        resp = launcher.service("GET", path, "", null, null, envctx);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType().toString());
        assertTrue(resp.getEntity().toString().contains("items"));
        assertFalse(resp.getEntity().toString().contains("space1"));
        assertFalse(resp.getEntity().toString().contains("space2"));
        assertFalse(resp.getEntity().toString().contains("space3"));
        assertFalse(resp.getEntity().toString().contains("space4"));
        assertTrue(resp.getEntity().toString().contains("space5"));

        getContainer().unregisterComponentByInstance(ss);
        getContainer().unregisterComponentByInstance(im);
    }
}
