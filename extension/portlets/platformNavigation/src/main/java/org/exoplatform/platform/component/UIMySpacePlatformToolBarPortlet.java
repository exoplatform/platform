package org.exoplatform.platform.component;

/**
 * Created by IntelliJ IDEA.
 * User: khemais.menzli
 * Date: 31 août 2010
 * Time: 13:16:17
 * To change this template use File | Settings | File Templates.
 */

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ComponentConfig(
        lifecycle = UIApplicationLifecycle.class,

        template = "app:/groovy/platformNavigation/portlet/UIMySpacePlatformToolBarPortlet/UIMySpacePlatformToolBarPortlet.gtmpl"
)
public class UIMySpacePlatformToolBarPortlet extends UIPortletApplication {
    private static final String SPACE_SETTING_PORTLET = "SpaceSettingPortlet";

    /**
     * constructor
     *
     * @throws Exception
     */
    public UIMySpacePlatformToolBarPortlet() throws Exception {
    }

    private SpaceService spaceService = null;
    private String userId = null;

    public List<PageNavigation> getGroupNavigations() throws Exception {
        String remoteUser = getUserId();
        List<Space> spaces = getSpaceService().getAccessibleSpaces(remoteUser);
        UserPortalConfig userPortalConfig = Util.getUIPortalApplication().getUserPortalConfig();
        List<PageNavigation> allNavigations = userPortalConfig.getNavigations();
        List<PageNavigation> navigations = new ArrayList<PageNavigation>();
        // Copy to another list to fix Concurency error
        for (PageNavigation navi : allNavigations) {
            navigations.add(navi);
        }
        Iterator<PageNavigation> navigationItr = navigations.iterator();
        String ownerId;
        String[] navigationParts;
        Space space;
        while (navigationItr.hasNext()) {
            ownerId = navigationItr.next().getOwnerId();
            if (ownerId.startsWith("/spaces")) {
                navigationParts = ownerId.split("/");
                space = spaceService.getSpaceByUrl(navigationParts[2]);
                if (space == null) navigationItr.remove();
                if (!navigationParts[1].equals("spaces") && !spaces.contains(space)) navigationItr.remove();
            } else { // not spaces navigation
                navigationItr.remove();
            }
            
        }
        for (PageNavigation navigation : allNavigations) {
            if ((navigation.getOwnerType().equals(PortalConfig.GROUP_TYPE) )&& (navigation.getOwnerId().indexOf("spaces")<0)){
                navigations.add(PageNavigationUtils.filter(navigation, remoteUser));
            }
        }
        

        return navigations;
    }

    public boolean isRender(PageNode spaceNode, PageNode applicationNode) throws SpaceException {
        SpaceService spaceSrv = getSpaceService();
        String remoteUser = getUserId();
        String spaceUrl = spaceNode.getUri();
        if (spaceUrl.contains("/")) {
            spaceUrl = spaceUrl.split("/")[0];
        }

        Space space = spaceSrv.getSpaceByUrl(spaceUrl);

        // space is deleted
        if (space == null) return false;

        if (spaceSrv.hasEditPermission(space, remoteUser)) return true;

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
     * gets spaceService
     *
     * @return spaceService
     * @see SpaceService
     */
    private SpaceService getSpaceService() {
        if (spaceService == null) {
            spaceService = getApplicationComponent(SpaceService.class);
        }
        return spaceService;
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
    
  
    	 private boolean hasPermission() throws Exception
    	   {
    	      UIPortalApplication portalApp = Util.getUIPortalApplication();
    	      UserACL userACL = portalApp.getApplicationComponent(UserACL.class);
    	      return userACL.hasCreatePortalPermission();
    	   }  
    

    // --- Merging Group navigation PLF-488

   /* public List<PageNavigation> getGroupNavigations() throws Exception {
        String remoteUser = Util.getPortalRequestContext().getRemoteUser();
        //List<PageNavigation> allNavigations = Util.getUIPortal().getNavigations();
        List<PageNavigation> allNavigations = Util.getUIPortalApplication().getNavigations();
        List<PageNavigation> navigations = new ArrayList<PageNavigation>();
        for (PageNavigation navigation : allNavigations) {
            if (navigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)) {
                navigations.add(PageNavigationUtils.filter(navigation, remoteUser));
            }
        }
        return navigations;
    }*/

}