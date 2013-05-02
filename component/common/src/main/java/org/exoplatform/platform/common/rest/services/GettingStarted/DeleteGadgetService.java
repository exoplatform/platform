/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.common.rest.services.GettingStarted;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import javax.jcr.Node;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 10/12/12
 */
@Path("homepage/intranet/getting-started/deletePortlet/")
@Produces(MediaType.APPLICATION_JSON)

public class DeleteGadgetService implements ResourceContainer {

    @GET
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)

    public Response delete() throws Exception {

        SessionProvider sProvider = null;
        try {

            String userId = ConversationState.getCurrent().getIdentity().getUserId();
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
            sProvider = SessionProvider.createSystemProvider();
            Node userPrivateNode = nodeCreator.getUserNode(sProvider, userId).getNode("ApplicationData");
            if (userPrivateNode.hasNode("GsGadget")) {
                Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
                ApplicationRegistryService appReg = (ApplicationRegistryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationRegistryService.class);

                if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {
                    if (gettingStartedNode.getProperty("exo:gs_deleteGadget").getBoolean()) {
                        Application app = null;
                        ApplicationCategory appCAt= appReg.getApplicationCategory("Home_Page_Portlets");
                        Application a=  appReg.getApplication("Home_Page_Portlets","GettingStartedPortlet");
                        Application a1=  appReg.getApplication("Home_Page_Portlets/GettingStartedPortlet");
                        if ((app = appReg.getApplication("Home_Page_Portlets/local._homepage-portlets.GettingStartedPortlet")) != null) {
                            appReg.remove(app);
                            return Response.ok("deleted").build();
                        } else return Response.ok("no Application with Id GettingStartedPortlet").build();
                    } else return Response.ok("exo:gs_deleteGadget is set to false").build();
                } else return Response.ok("no Property exo:gs_deleteGadget ").build();
            } else return Response.ok("no Node GsGadget ").build();
        } catch (Exception e) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).build();
        } finally {
            if (sProvider != null ) {
                sProvider.close();

            }

        }
    }


    @GET
    @Path("setDelete")
    @Produces(MediaType.APPLICATION_JSON)

    public Response setDelete() throws Exception {
        SessionProvider sProvider = null;
        try {
            String userId = ConversationState.getCurrent().getIdentity().getUserId();

            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(NodeHierarchyCreator.class);

            sProvider = SessionProvider.createSystemProvider();

            Node userPrivateNode = nodeCreator.getUserNode(sProvider, userId).getNode("ApplicationData");

            if (userPrivateNode.hasNode("GsGadget")) {

                Node gettingStartedNode = userPrivateNode.getNode("GsGadget");

                if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {

                    gettingStartedNode.setProperty("exo:gs_deleteGadget", true);

                    gettingStartedNode.save();

                    return Response.ok("Property exo:gs_deleteGadget set to true").build();

                } else return Response.ok("no Property exo:gs_deleteGadget ").build();

            } else return Response.ok("no Node GsGadget ").build();

        } catch (Exception e) {

            return Response.status(HTTPStatus.INTERNAL_ERROR).build();

        } finally {
            if (sProvider != null) {
                sProvider.close();

            }

        }
    }

    @GET
    @Path("IsDelete")
    @Produces(MediaType.APPLICATION_JSON)

    public Response IsDelete() throws Exception {
        SessionProvider sProvider = null;
        try {
            String userId = ConversationState.getCurrent().getIdentity().getUserId();

            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(NodeHierarchyCreator.class);

            sProvider = SessionProvider.createSystemProvider();

            Node userPrivateNode = nodeCreator.getUserNode(sProvider, userId).getNode("ApplicationData");

            if (userPrivateNode.hasNode("GsGadget")) {

                Node gettingStartedNode = userPrivateNode.getNode("GsGadget");

                if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {

                    Boolean del=gettingStartedNode.getProperty("exo:gs_deleteGadget").getBoolean();

                    return Response.ok(del.toString()).build();

                } else return Response.ok("no Property exo:gs_deleteGadget ").build();

            } else return Response.ok("no Node GsGadget ").build();

        } catch (Exception e) {

            return Response.status(HTTPStatus.INTERNAL_ERROR).build();

        } finally {
            if (sProvider != null) {
                sProvider.close();

            }

        }
    }
}
