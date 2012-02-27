package org.exoplatform.bonitasoft.services.rest.publication;

import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.exoplatform.services.ecm.publication.IncorrectStateUpdateLifecycleException;
import org.exoplatform.services.ecm.publication.PublicationPlugin;
import org.exoplatform.services.ecm.publication.PublicationService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.wcm.publication.lifecycle.stageversion.StageAndVersionPublicationConstant;

@Path("publicationService")
public class PublicationRestService implements ResourceContainer {

  private static Log log = ExoLogger.getLogger(PublicationRestService.class);
  private PublicationService publicationService;
  private RepositoryService repositoryService;

  public PublicationRestService(RepositoryService repositoryService, PublicationService publicationService) {
    this.publicationService = publicationService;
    this.repositoryService = repositoryService;
  }

  /**
   * change the state of a given node --- When you use POST, you are
   * allowed to use void --- If you use GET void is not allowed
   * 
   * @param path
   * @param userName
   * @param status
   * @return
   */
  @POST
  @Path("publisheddocument")
  public String changeDocumentsStatus(@FormParam("path") String path, @FormParam("userName") String userName,
      @FormParam("status") String status) throws Exception {
    log.info("Change Publication status for node" + path + "with status " + status);

    String[] pathtab = path.split("/");
    String filePath = "";
    for (int i = 3; i < pathtab.length; i++) {
      filePath += "/" + pathtab[i];
    }
    // get the list of nodes related to current path
    ManageableRepository repository = repositoryService.getRepository(pathtab[1]);
    // This bloc has to be synchronized, the use of same session with two
    // threads same time could cause a problem
    synchronized (repository) {
      Session session = null;
      try {
        session = SessionProvider.createSystemProvider().getSession(pathtab[2], repository);

        Node node = (Node) session.getItem(filePath);
        changeState(node, status);
      } finally {
        if (session != null) {
          session.logout();
        }
      }
    }
    return path;
  }

  /**
   * allow to change the publication state of node to draft or published
   * state
   * 
   * @param node
   * @param publicationService
   */
  public void changeState(Node node, String status) throws ValueFormatException,
      PathNotFoundException, UnsupportedRepositoryOperationException, LockException, RepositoryException,
      IncorrectStateUpdateLifecycleException, Exception {

    if (node.hasProperty(StageAndVersionPublicationConstant.PUBLICATION_LIFECYCLE_NAME)) {
      String lifeCycleName = node.getProperty(StageAndVersionPublicationConstant.PUBLICATION_LIFECYCLE_NAME).getString();
      PublicationPlugin publicationPlugin = publicationService.getPublicationPlugins().get(lifeCycleName);
      if (log.isDebugEnabled()) {
        log.debug("processing " + node.getPath());
      }
      if (!node.isCheckedOut()) {
        node.checkout();
      }
      HashMap<String, String> map = new HashMap<String, String>();
      map.put(StageAndVersionPublicationConstant.CURRENT_REVISION_NAME, node.getName());

      if (log.isDebugEnabled()) {
        log.debug("try to pass " + node.getName() + " to published");
      }
      publicationPlugin.changeState(node, status, map);

      if (log.isDebugEnabled()) {
        log.debug("node drafted");
      }

    }
  }
}
