package org.exoplatform.platform.navigation.component.utils;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.web.controller.QualifiedName;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 2/13/13
 */
public class NavigationUtils {
  private static final Log LOG = ExoLogger.getLogger(NavigationUtils.class);

  public static String getCurrentUser() {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    IdentityManager idm = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
    PortalRequestContext request = Util.getPortalRequestContext() ;
    String currentPath = request.getControllerContext().getParameter(QualifiedName.parse("gtn:path"));
    String []splitCurrentUser = currentPath.split("/");
    String currentUserName = currentPath.split("/")[splitCurrentUser.length - 1];
    try {
      if ((currentUserName != null)&& (idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false) != null)) return currentUserName;
      else if (((currentUserName = currentPath.split("/")[splitCurrentUser.length-2]) != null)&&
        (idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false) != null)) {
        return currentUserName;
      }
    } catch (Exception e) {
      if(LOG.isDebugEnabled()) {
        LOG.debug("Could not found Identity of user " + currentUserName);
      }
      return null;
    }
    return null;
  } 
}
