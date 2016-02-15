package org.exoplatform.platform.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPopupContainer;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */
@ComponentConfig(
        lifecycle = UIApplicationLifecycle.class,
        template = "app:/groovy/platformNavigation/portlet/UICreatePlatformToolBarPortlet/UICreatePlatformToolBarPortlet.gtmpl"
)
public class UICreatePlatformToolBarPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UICreatePlatformToolBarPortlet.class);
    private String renderedCompId_;
    private SpaceService spaceService = null;
    private String currentPortalName = null;
    private boolean socialPortal = false;

    public UICreatePlatformToolBarPortlet() throws Exception {
        try {
            spaceService = getApplicationComponent(SpaceService.class);
        } catch (Exception exception) {
            LOG.error("paceService could be 'null' when the Social profile isn't activated ", exception);
        }
        if (spaceService == null) { // Social profile disabled
            return;
        }

        addChild(UICreateList.class, null, null);
        addChild(UIPopupContainer.class, null, "CreatePortletPopUPContainer");

    }


    public String getRenderedId(Class T, UICreatePlatformToolBarPortlet uiParent) {
        return uiParent.getChild(T).getId();
    }

    public boolean isSocialPortal() {
        if (currentPortalName != null && getCurrentPortalName().equals(currentPortalName)) {
            return socialPortal;
        }
        if (!isSocialProfileActivated()) {
            socialPortal = false;
        } else {
            currentPortalName = getCurrentPortalName();
            UserPortal userPortal = getUserPortal();
            UserNavigation userNavigation = userPortal.getNavigation(SiteKey.portal(currentPortalName));
            UserNode portalNode = userPortal.getNode(userNavigation, Scope.CHILDREN, null, null);
            socialPortal = portalNode.getChild("spaces") != null;
        }
        return socialPortal;
    }

    public boolean isSocialProfileActivated() {
        return (ExoContainer.getProfiles().contains("social") || ExoContainer.getProfiles().contains("default") || ExoContainer
                .getProfiles().contains("all"));
    }

    public static UserPortal getUserPortal() {
        UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
        return portalConfig.getUserPortal();
    }

    private String getCurrentPortalName() {
        return Util.getPortalRequestContext().getPortalOwner();
    }

}
