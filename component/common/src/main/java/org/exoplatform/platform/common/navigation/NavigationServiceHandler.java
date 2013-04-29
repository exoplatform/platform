/**
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
package org.exoplatform.platform.common.navigation;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 26/11/12
 */
public class NavigationServiceHandler implements Startable {

    private static Log logger = ExoLogger.getLogger(NavigationServiceHandler.class);
    NodeHierarchyCreator nodeCreator;
    Session session;
    SessionProvider sProvider;
    Node rootNode;
    Node publicApplicationNode;
    String path = "Application Data/logos/";
    String logo_name  = "logo.png";

    @Override
    public void start()
    {
        nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
        sProvider = SessionProvider.createSystemProvider();

        try {
            publicApplicationNode = nodeCreator.getPublicApplicationNode(sProvider);
            session = publicApplicationNode.getSession();
            rootNode = session.getRootNode();
        } catch (Exception e) {
            logger.warn("Can not create rootNode when starting NavigationServiceHandler");
        }
    }

    public String getHomePageLogoURI() {
        String pathImageNode = null;
        Node fileNode = null;
        try {
            Node logosNode = rootNode.getNode(path);
            if (logosNode.hasNode(logo_name)) {
                fileNode = logosNode.getNode(logo_name);
                pathImageNode = fileNode.getPath()+"?"+System.currentTimeMillis();

            }
        } catch (Exception e) {
            logger.warn("Can not get path of Logo : default LOGO will be used");
            return null;

        }
        return pathImageNode;
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
