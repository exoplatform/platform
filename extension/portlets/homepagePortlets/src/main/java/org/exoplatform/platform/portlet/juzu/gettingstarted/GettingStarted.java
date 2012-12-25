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
package org.exoplatform.platform.portlet.juzu.gettingstarted;

import juzu.*;
import juzu.template.Template;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.social.core.service.LinkProvider;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.HashMap;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 07/12/12
 */
public class GettingStarted extends Controller {


    NodeHierarchyCreator nodeHierarchyCreator_;

    @Inject
    public GettingStarted(NodeHierarchyCreator nodeHierarchyCreator) {
        nodeHierarchyCreator_ = nodeHierarchyCreator;
    }

    @Inject
    @Path("gettingStarted.gtmpl")
    Template gettingStarted;

    @View
    public void index() throws Exception {
        boolean del=false;
        String remoteUser = renderContext.getSecurityContext().getRemoteUser();
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode("ApplicationData");
        if (userPrivateNode.hasNode("GsGadget")) {
            Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
            if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {
                del = gettingStartedNode.getProperty("exo:gs_deleteGadget").getBoolean();
            }
        }
        if(del)  index("false");
        else index("true");
    }

    @View
    public void index(String show) throws Exception {

        String remoteUser = renderContext.getSecurityContext().getRemoteUser();
        System.out.println(show);
        HashMap parameters =new HashMap();
        parameters.put("show",show);
        parameters.put("profile", LinkProvider.getUserProfileUri(remoteUser)) ;
        parameters.put("profileLabel","Upload a Profile Picture");
        parameters.put("connect",LinkProvider.getUserConnectionsUri(remoteUser) ) ;
        parameters.put("connectLabel","Connect With Collegues");
        parameters.put("space","/portal/intranet/all-spaces");
        parameters.put("spaceLabel","Join a Space");
        parameters.put("activity","#");
        parameters.put("activityLabel","Post an activity");
        parameters.put("upload","portal/intranet/documents");
        parameters.put("uploadLabel","Upload a Document");
        gettingStarted.render(parameters);

    }
}
