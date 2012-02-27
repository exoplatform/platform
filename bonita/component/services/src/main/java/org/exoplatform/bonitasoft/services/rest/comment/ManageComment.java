package org.exoplatform.bonitasoft.services.rest.comment;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path("ManageComment")
public class ManageComment implements ResourceContainer {
  private static Log logger = ExoLogger.getLogger(ManageComment.class);
  private RepositoryService repositoryService;

  public ManageComment(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * return the comment saved on bonita engine
   * 
   * @param link
   * @param comment
   */
  @POST
  @Path("addComment")
  public void addComment(@FormParam("link") String link, @FormParam("commentaires") String comment) throws LoginException,
      NoSuchWorkspaceException, RepositoryException, RepositoryConfigurationException {

    if (logger.isDebugEnabled()) {
      logger.debug("### Starting addComment Action ...");
    }
    String[] pathtab;
    pathtab = link.split("/");

    ManageableRepository repository = repositoryService.getRepository(pathtab[1]);
    // This bloc has to be synchronized, the use of same session with two
    // threads same time could cause a problem
    synchronized (repository) {
      Session session = null;
      try {
        session = SessionProvider.createSystemProvider().getSession(pathtab[2], repository);

        String commentNodePath = "";
        for (int i = 3; i < pathtab.length; i++) {
          commentNodePath += "/" + pathtab[i];
        }
        Node node = (Node) session.getItem(commentNodePath);
        if (node.isNodeType("exo:userComment")) {
          if (node.hasProperty("exo:comment")) {
            node.setProperty("exo:comment", comment);
            node.save();
          }
        } else {
          if (node.canAddMixin("exo:userComment")) {
            node.addMixin("exo:userComment");
            node.save();
            node.setProperty("exo:comment", comment);
          }
        }
        session.save();
      } finally {
        if (session != null) {
          session.logout();
        }
      }
    }
  }

}
