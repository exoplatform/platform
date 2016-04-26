package org.exoplatform.platform.common.rest.services.SuggestPeoplePortlet;

import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserACLMetaData;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONObject;

public class TestPeopleRestServices extends BaseRestServicesTestCase {

    protected Class<?> getComponentClass() {
        return PeopleRestServices.class;
    }

    public void testSuggestions() throws Exception {
        String path = "/homepage/intranet/people/contacts/suggestions";
        EnvironmentContext envctx = new EnvironmentContext();
        HttpServletRequest httpRequest =
           new MockHttpServletRequest(path, null, 0, "GET", null);
        
        envctx.put(HttpServletRequest.class, httpRequest);

        ContainerResponse resp =
           launcher.service("GET", path, "", null, null, envctx);
        assertEquals(500, resp.getStatus());

        // No suggestion, using last users but don't propose myself
        Identity idRoot = new Identity(OrganizationIdentityProvider.NAME, "root");
        idRoot.setId("root");
        Identity idFoo = new Identity(OrganizationIdentityProvider.NAME, "foo");
        idFoo.setId("foo");
        Identity idBar = new Identity(OrganizationIdentityProvider.NAME, "bar");
        idBar.setId("bar");
        envctx.put(SecurityContext.class, new MockSecurityContext(idFoo.getRemoteId()));

        Map<String, Object> imResults = new HashMap<String, Object>();
        imResults.put("getOrCreateIdentity", idFoo);
        imResults.put("getLastIdentities", Arrays.asList(idRoot, idFoo));
        IdentityManager im = createProxy(IdentityManager.class, imResults);
        getContainer().registerComponentInstance("IdentityManager", im);

        Map<String, Object> rmResults = new HashMap<String, Object>();
        rmResults.put("getConnections", new MockListAccess<Identity>(new Identity[]{}));

        RelationshipManager rm = createProxy(RelationshipManager.class, rmResults);
        getContainer().registerComponentInstance("RelationshipManager", rm);

        UserACLMetaData md = new UserACLMetaData();
        md.setSuperUser(idRoot.getRemoteId());
        UserACL uACL = new UserACL(md);
        getContainer().registerComponentInstance("UserACL", uACL);

        resp = launcher.service("GET", path, "", null, null, envctx);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType().toString());
        JSONObject json = new JSONObject(resp.getEntity().toString());
        assertTrue(json.has("items"));
        assertTrue(json.getJSONArray("items").length() == 0);

        // No suggestion, using last users but don't propose user with whom you have a relationship already
        imResults.put("getLastIdentities", Arrays.asList(idBar, idFoo));
        rmResults.put("get", new Relationship("x"));

        resp = launcher.service("GET", path, "", null, null, envctx);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType().toString());
        json = new JSONObject(resp.getEntity().toString());
        assertTrue(json.has("items"));
        assertTrue(json.getJSONArray("items").length() == 0);

        // The only suggestion is demo
        rmResults.put("getConnections", new MockListAccess<Identity>(new Identity[]{idBar}));
        rmResults.put("getSuggestions", Collections.singletonMap(idRoot, 1));
        rmResults.remove("get");

        resp = launcher.service("GET", path, "", null, null, envctx);
        assertEquals(200, resp.getStatus());
        assertEquals("application/json", resp.getContentType().toString());
        json = new JSONObject(resp.getEntity().toString());
        assertTrue(json.has("items"));
        assertTrue(json.getJSONArray("items").length() == 1);
        assertEquals(json.getJSONArray("items").getJSONObject(0).getString("username"), idBar.getRemoteId());

        getContainer().unregisterComponent("UserACL");
        getContainer().unregisterComponent("RelationshipManager");
        getContainer().unregisterComponent("IdentityManager");
    }

}
