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
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 26/11/12
 */
public class NavigationServiceHandler implements Startable {

    RepositoryService   repositoryService;

    private static Log logger = ExoLogger.getLogger(NavigationServiceHandler.class);


    public NavigationServiceHandler() {
        repositoryService = (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
    }

    @Override
    public void start() {
        if (logger.isDebugEnabled()) {
            logger.debug("NavigationServiceHandler starts");
        }
        Session session = null;
        SessionProvider sessionProvider = null;

        try {
            sessionProvider = SessionProvider.createSystemProvider();
            session = sessionProvider.getSession("collaboration",repositoryService.getCurrentRepository());
            Node rootNode = session.getRootNode();
            if (!rootNode.hasNode("Application Data")) {
                rootNode.addNode("Application Data", "nt:folder");
                session.save();
            }
            Node applicationDataNode = rootNode.getNode("Application Data");
            if (!applicationDataNode.hasNode("logos")) {
                applicationDataNode.addNode("logos", "nt:folder");
                session.save();
            }
        } catch (Exception e) {
            logger.error("NavigationServiceHandler - Error while creating the logo folder : ", e.getMessage());
        } finally {

            if (session != null) {
                session.logout();
            }

            if (sessionProvider != null) {
                sessionProvider.close();
            }

        }
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public static String getHomePageLogoURI() {
        Boolean isavailable = false;
        Node imageNode = null;
        String pathImageNode = null;
        Session session = null;
        SessionProvider sProvider = null;
        try {
            NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
            sProvider = SessionProvider.createSystemProvider();
            Node publicApplicationNode = nodeCreator.getPublicApplicationNode(sProvider);
            session = publicApplicationNode.getSession();
            Node rootNode = session.getRootNode();
            String path = "Application Data/logos/";
            Node logoNode = rootNode.getNode(path);
            if (logoNode.hasNodes()) {
                for (NodeIterator iterator = logoNode.getNodes(); iterator.hasNext(); ) {
                    Node node = iterator.nextNode();
                    imageNode = node;
                    if (imageNode.hasNode("jcr:content")) {
                        if (imageNode.getNode("jcr:content").hasProperty("jcr:mimeType")) {
                            String JcrMimeType = imageNode.getNode("jcr:content").getProperty("jcr:mimeType").getString();
                            if (JcrMimeType.equals("image/gif") || JcrMimeType.equals("image/ief") ||
                                    JcrMimeType.equals("image/jpeg") || JcrMimeType.equals("image/pjpeg") ||
                                    JcrMimeType.equals("image/bmp") || JcrMimeType.equals("image/x-portable-bitmap") ||
                                    JcrMimeType.equals("image/x-portable-graymap") || JcrMimeType.equals("image/png") ||
                                    JcrMimeType.equals("image/x-png") || JcrMimeType.equals("image/x-portable-anymap") ||
                                    JcrMimeType.equals("image/x-portable-pixmap") || JcrMimeType.equals("image/x-cmu-raster") ||
                                    JcrMimeType.equals("image/x-rgb") || JcrMimeType.equals("image/tiff") ||
                                    JcrMimeType.equals("image/x-xbitmap") || JcrMimeType.equals("image/x-xpixmap") ||
                                    JcrMimeType.equals("image/x-xwindowdump")) {

                                pathImageNode = imageNode.getPath()+"?"+System.currentTimeMillis();
                                break;

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Company LOGO not specified : default LOGO will be used");
            return null;

        } finally {

            if (session != null) {
                session.logout();
            }

            if (sProvider != null) {
                sProvider.close();
            }

        }
        return pathImageNode;


    }
}
