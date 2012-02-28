package org.exoplatform.platform.common.ext;

import org.exoplatform.services.cms.link.LinkManager;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.access.SystemIdentity;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Created by IntelliJ IDEA.
 * User: kmenzli
 * Date: 28/12/11
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class UserDriveExtListener  extends UserEventListener {



    private static final Log log = ExoLogger.getLogger(UserDriveExtListener.class);

    private static final String SYMLINK_NAME        = "Public";

    private static final String SYMLIN_TYPE         = "exo:symlink";

    private static final String PUPLIC_DRIVE_EXT    = "/Public";

    private static final String PRIVATE_DRIVE_EXT   = "/Private";

    /**   RepositoryService used to get current Repository */
    private final RepositoryService repositoryService_;

    /**   LinkManager Service to create jcr symlink **/
    private final LinkManager linkManager_;

    /**   NodeHierarchyCreator Service : used to get complet path to a given user Folder */
    private NodeHierarchyCreator nodeHierarchyCreator_ ;

    public UserDriveExtListener (RepositoryService repositoryService, LinkManager linkManager, NodeHierarchyCreator nodeHierarchyCreator) throws Exception
    {
        this.repositoryService_ = repositoryService;

        this.linkManager_ =  linkManager;

        nodeHierarchyCreator_ = nodeHierarchyCreator ;
    }

    public void postSave(User user, boolean isNew) throws Exception
    {

        if (isNew)
        {
            createLink(user.getUserName());
        }
    }
    private void createLink(String userName) throws Exception
    {

        SessionProvider sessionProvider = SessionProvider.createSystemProvider();


        Session session = getSession(sessionProvider);

        /** path to private user drive*/
        String privatePathUserFolder = "";

        /** path to public user drive*/
        String publicPathUserFolder = "";

        try {

            Node userNode = nodeHierarchyCreator_.getUserNode(sessionProvider,userName);

            privatePathUserFolder = userNode.getPath().concat(PRIVATE_DRIVE_EXT);

            publicPathUserFolder = userNode.getPath().concat(PUPLIC_DRIVE_EXT);

            Node symLinkParent = (Node) session.getItem(privatePathUserFolder);

            Node targetNode = (Node) session.getItem(publicPathUserFolder);

            Node linkNode = null;

            linkNode = linkManager_.createLink(symLinkParent,SYMLIN_TYPE,targetNode,SYMLINK_NAME);

            ((ExtendedNode)linkNode).setPermission(SystemIdentity.ANY, new String[]{PermissionType.READ});

            linkNode.save();


        } catch (Exception E) {

            log.error("An error occurs while processing the creation of Public Symlink " +E);

        }  finally {

            if (session != null) {
                session.logout();
            }

            if (sessionProvider != null) {
                sessionProvider.close();
            }

        }
    }

    private Session getSession(SessionProvider sessionProvider) throws RepositoryException {

        ManageableRepository repository = repositoryService_.getCurrentRepository();

        return sessionProvider.getSession(repository.getConfiguration().getDefaultWorkspaceName(), repository);
    }
}
