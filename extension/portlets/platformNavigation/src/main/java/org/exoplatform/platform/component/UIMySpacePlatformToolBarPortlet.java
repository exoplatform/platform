package org.exoplatform.platform.component;

/**
 * Created by IntelliJ IDEA.
 * User: khemais.menzli
 * Date: 31 août 2010
 * Time: 13:16:17
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(
        lifecycle = UIApplicationLifecycle.class,
        template = "app:/groovy/platformNavigation/portlet/UIMySpacePlatformToolBarPortlet/UIMySpacePlatformToolBarPortlet.gtmpl"
)
public class UIMySpacePlatformToolBarPortlet extends UIPortletApplication {
    private static final String SPACE_SETTING_PORTLET = "SpaceSettingPortlet";

    private SpaceService spaceService = null;
    private String userId = null;
    
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public UIMySpacePlatformToolBarPortlet() throws Exception {
      try {
        spaceService = getApplicationComponent(SpaceService.class);
      } catch (Exception exception) {
        // spaceService should be "null" because the Social profile isn't activated
      }
    }


    public List<PageNavigation> getGroupNavigations() throws Exception {
      String remoteUser = getUserId();
      UserPortalConfig userPortalConfig = Util.getUIPortalApplication().getUserPortalConfig();
      List<PageNavigation> allNavigations = userPortalConfig.getNavigations();
      List<PageNavigation> computedNavigations = null;
      if (spaceService != null) {
        computedNavigations = new ArrayList<PageNavigation>(allNavigations);
        List<Space> spaces = spaceService.getAccessibleSpaces(remoteUser);
        Iterator<PageNavigation> navigationItr = computedNavigations.iterator();
        String ownerId;
        String[] navigationParts;
        Space space;
        while (navigationItr.hasNext()) {
          ownerId = navigationItr.next().getOwnerId();
          if (ownerId.startsWith("/spaces")) {
            navigationParts = ownerId.split("/");
            space = spaceService.getSpaceByUrl(navigationParts[2]);
            if (space == null)
              navigationItr.remove();
            if (!navigationParts[1].equals("spaces") && !spaces.contains(space))
              navigationItr.remove();
          } else { // not spaces navigation
            navigationItr.remove();
          }
        }
      } else { // Social Services aren't loaded in the current PortalContainer
        computedNavigations = new ArrayList<PageNavigation>();
      }
      for (PageNavigation navigation : allNavigations) {
        if ((navigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)) && (navigation.getOwnerId().indexOf("spaces") < 0)) {
          computedNavigations.add(PageNavigationUtils.filter(navigation, remoteUser));
        }
      }
      return computedNavigations;
    }

    public boolean isRender(PageNode spaceNode, PageNode applicationNode) throws SpaceException {
        if(spaceService == null) {
          return false;
        }
        String remoteUser = getUserId();
        String spaceUrl = spaceNode.getUri();
        if (spaceUrl.contains("/")) {
            spaceUrl = spaceUrl.split("/")[0];
        }
        Space space = spaceService.getSpaceByUrl(spaceUrl);
        // space is deleted
        if (space == null) return false;
        if (spaceService.hasEditPermission(space, remoteUser)) return true;
        String appName = applicationNode.getName();
        if (!appName.contains(SPACE_SETTING_PORTLET)) {
            return true;
        }
        return false;
    }

    public PageNode getSelectedPageNode() throws Exception {
        return Util.getUIPortal().getSelectedNode();
    }

    /**
     * gets remote user Id
     *
     * @return userId
     */
    private String getUserId() {
        if (userId == null) {
            userId = Util.getPortalRequestContext().getRemoteUser();
        }
        return userId;
    }

    public List<PageNavigation> getNavigations() throws Exception {
        List<PageNavigation> result = new ArrayList<PageNavigation>();
        UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
        List<PageNavigation> navigations = uiPortalApp.getNavigations();

        for (PageNavigation pageNavigation : navigations) {
            if (pageNavigation.getOwnerType().equals("portal"))
                result.add(pageNavigation);
        }
        return result;
    }

    public boolean hasPermission(List<PageNavigation> groupNavigationsList) throws Exception {
      UIPortalApplication portalApp = Util.getUIPortalApplication();
      UserACL userACL = portalApp.getApplicationComponent(UserACL.class);
      for (PageNavigation pageNavigation : groupNavigationsList) {
        if(userACL.hasEditPermission(pageNavigation)){
          return true;
        }
      }
      return true;
    }

}