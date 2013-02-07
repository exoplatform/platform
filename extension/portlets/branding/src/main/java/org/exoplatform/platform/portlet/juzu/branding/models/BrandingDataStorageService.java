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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
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
  private static final Log LOG = ExoLogger.getExoLogger(BrandingDataStorageService.class);
  static String     fileName = "logo.png";
  public static int logoHeight = 34;

  RepositoryService repositoryService;

  public BrandingDataStorageService() {
    repositoryService = (RepositoryService) ExoContainerContext.getCurrentContainer()
                                                               .getComponentInstanceOfType(RepositoryService.class);
  }
  
  /**
   *  Save logo file in the jcr
   * @param item, item file
   */

  public void saveFile(FileItem item) {
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
      if (logosNode.hasNode(fileName)) {
        fileNode = logosNode.getNode(fileName);
        fileNode.remove();
        session.save();
      }
      fileNode = logosNode.addNode(fileName, "nt:file");
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
      if(userId!=null) {
        LOG.info("Branding - A new logo on the navigation bar has been saved by user :" +userId);
      }
    }
  }

  /**
   * 
   * @param input inputStream
   * @return  InputStream after resize the logo
   * @throws IOException
   */
  private static InputStream resizeImage(InputStream input) throws IOException {
    BufferedImage buffer = ImageIO.read(input);
    if(buffer.getHeight()==logoHeight){
      return   input;
    }
    BufferedImage newBuffer = resizeImage(buffer);
    File tmp = File.createTempFile("RESIZED", null);
    ImageIO.write(newBuffer, "png", tmp);
    return (new FileInputStream(tmp));
  }
  /**
   * 
   * @param bufferedImage the source bufferedImage which will be resized
   * @return  the new image after doing the resize
   */

  private static BufferedImage resizeImage(BufferedImage bufferedImage) {
    int newWidth = (bufferedImage.getWidth() * logoHeight) / (bufferedImage.getHeight());
    return resizeImage(bufferedImage, newWidth, logoHeight);
  }

  
  /**
   * 
   * @param bufferedImage the source bufferedImage which will be resized
   * @param width, the width of image
   * @param height, the height of image
   * @return the new image after doing the resize
   */
  private static BufferedImage resizeImage(BufferedImage bufferedImage, int width, int height) {
    int type = (bufferedImage.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                                                                : BufferedImage.TYPE_INT_ARGB;
    BufferedImage resizedImage = new BufferedImage(width, height,type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(bufferedImage, 0, 0, width, height, null);
    g.dispose();
    g.setComposite(AlphaComposite.Src);
    if (bufferedImage.getHeight() < height) {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                         RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    } else {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                         RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return resizedImage;
  }

}
