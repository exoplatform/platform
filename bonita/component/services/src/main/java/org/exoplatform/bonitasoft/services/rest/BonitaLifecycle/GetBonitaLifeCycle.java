package org.exoplatform.bonitasoft.services.rest.BonitaLifecycle;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
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

@Path("ManageBonita")
public class GetBonitaLifeCycle implements ResourceContainer {

  private static Log logger = ExoLogger.getLogger(GetBonitaLifeCycle.class);
  private RepositoryService repositoryService;

  public GetBonitaLifeCycle(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * allow to add a property to given node have the information that this
   * node is enrolled on bonita process
   * 
   * @param link
   * @param inlife
   */
  @POST
  @Path("lifecycle")
  public void getCycle(@FormParam("link") String link, @FormParam("inlife") String inlife) throws ValueFormatException,
      VersionException, LockException, ConstraintViolationException, RepositoryException, RepositoryConfigurationException {
    if (logger.isDebugEnabled()) {
      logger.debug("### Starting getCycle Action ...");
    }
    String[] pathtab = link.split("/");
    ManageableRepository repository = repositoryService.getRepository(pathtab[1]);
    // This bloc has to be synchronized, the use of same session with two
    // threads same time could cause a problem
    synchronized (repository) {
      Session session = null;
      try {
        session = SessionProvider.createSystemProvider().getSession(pathtab[2], repository);
        String lifecycleNodePath = "";
        for (int i = 3; i < pathtab.length; i++) {
          lifecycleNodePath += "/" + pathtab[i];
        }
        Node node = (Node) session.getItem(lifecycleNodePath);
        if (node.isNodeType("exo:bonitaLifecycle")) {
          if (node.hasProperty("exo:bonitaEnrolledIn")) {
            node.setProperty("exo:bonitaEnrolledIn", inlife);
            node.save();
          }
        } else {
          if (node.canAddMixin("exo:bonitaLifecycle") && inlife.equals("true")) {
            node.addMixin("exo:bonitaLifecycle");
            node.save();
            node.setProperty("exo:bonitaEnrolledIn", inlife);
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
