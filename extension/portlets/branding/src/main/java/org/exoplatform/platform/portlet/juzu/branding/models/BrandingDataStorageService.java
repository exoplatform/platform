/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package org.exoplatform.platform.portlet.juzu.branding.models;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Calendar;
import org.exoplatform.services.cms.impl.ImageUtils;
import javax.imageio.ImageIO;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.commons.fileupload.FileItem;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

/**
 * Created by The eXo Platform SAS Author : Nguyen Viet Bang
 * bangnv@exoplatform.com Jan 22, 2013
 */

public class BrandingDataStorageService {
  private static final Log LOG               = ExoLogger.getExoLogger(BrandingDataStorageService.class);

  public static int        logoHeight        = 34;

  RepositoryService        repositoryService;

  public static String     logo_preview_name = "logo_preview.png";

  public static String     logo_name         = "logo.png";

  public BrandingDataStorageService() {
    repositoryService = (RepositoryService) ExoContainerContext.getCurrentContainer()
                                                               .getComponentInstanceOfType(RepositoryService.class);
  }

  /**
   * Save logo file in the jcr
   * 
   * @param item, item file
   */

  public void saveLogoPreview(FileItem item) {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Session session = sessionProvider.getSession("collaboration",
                                                   repositoryService.getCurrentRepository());
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
      Node logosNode = applicationDataNode.getNode("logos");

      Node fileNode = null;
      if (logosNode.hasNode(logo_preview_name)) {
        fileNode = logosNode.getNode(logo_preview_name);
        fileNode.remove();
        session.save();
      }
      fileNode = logosNode.addNode(logo_preview_name, "nt:file");
      Node jcrContent = fileNode.addNode("jcr:content", "nt:resource");
      jcrContent.setProperty("jcr:data", resizeImage(item.getInputStream()));
      jcrContent.setProperty("jcr:lastModified", Calendar.getInstance());
      jcrContent.setProperty("jcr:encoding", "UTF-8");
      jcrContent.setProperty("jcr:mimeType", item.getContentType());
      logosNode.save();
      session.save();
    } catch (Exception e) {
      LOG.error("Branding - Error while saving the logo: ", e.getMessage());
    } finally {
      sessionProvider.close();
      ConversationState state = ConversationState.getCurrent();
      String userId = (state != null) ? state.getIdentity().getUserId() : null;
      if (userId != null) {
        LOG.info("Branding - A new logo on the navigation bar has been saved by user :" + userId);
      }
    }
  }

  /**
   * Move logo preview file to logo file
   */

  public void saveLogo() {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Session session = sessionProvider.getSession("collaboration",
                                                   repositoryService.getCurrentRepository());
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
      Node logosNode = applicationDataNode.getNode("logos");

      Node fileNode = null;

      if (logosNode.hasNode(logo_name)) {
        fileNode = logosNode.getNode(logo_name);
        fileNode.remove();
        session.save();
      }
      if (logosNode.hasNode(logo_preview_name)) {
        fileNode = logosNode.getNode(logo_preview_name);
        String newPath = fileNode.getPath().replace(logo_preview_name, logo_name);
        session.move(fileNode.getPath(), newPath);
        session.save();
      }
    } catch (Exception e) {
      LOG.error("Branding - Error while saving the logo: ", e.getMessage());
    } finally {
      sessionProvider.close();
      ConversationState state = ConversationState.getCurrent();
      String userId = (state != null) ? state.getIdentity().getUserId() : null;
      if (userId != null) {
        LOG.info("Branding - A new logo on the navigation bar has been saved by user :" + userId);
      }
    }
  }
  
  /**
   * resize image to scale, just used in custom size case. When 0 as specified as width then it will be calculated automatically to fit with expected height.
   * @param input
   * @return
   * @throws Exception
   */
  private static InputStream resizeImage(InputStream input) throws Exception{
    BufferedImage buffer = ImageIO.read(input);
    return ImageUtils.scaleImage(buffer, 0, logoHeight, false);
  }

}
