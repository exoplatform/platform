package org.exoplatform.platform.component;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.common.space.statistic.SpaceAccessService;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 22/11/12
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UISpaceNavigationPortlet/UISpaceNavigationPortlet.gtmpl",
        events = {
                @EventConfig(listeners = UISpaceNavigationPortlet.IncrementActionListener.class),
                @EventConfig(listeners = UISpaceNavigationPortlet.LoadNavigationActionListener.class)
        }
)
public class UISpaceNavigationPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UISpaceNavigationPortlet.class);

    private static final String SPACE_SETTINGS = "settings";

    private static final String MY_SPACE_REST_URL = "/space/user/searchSpace/";

    public static final String LOAD_NAVIGATION_ACTION = "LoadNavigation";

    private SpaceService spaceService = null;

    private OrganizationService organizationService = null;

    private String userId = null;

    private boolean groupNavigationPermitted = false;

    private UserNodeFilterConfig mySpaceFilterConfig;

    private List<String> spacesSortedByAccesscount = null;

    static int MY_SPACES_MAX_NUMBER = 10;

    static int loadingCapacity = 10;

    private Comparator<UserNavigation> spaceAccessComparator = new Comparator<UserNavigation>() {
        public int compare(UserNavigation o1, UserNavigation o2) {
            String ownerId1 = o1.getKey().getName().substring(1);
            String ownerId2 = o2.getKey().getName().substring(1);
            return spacesSortedByAccesscount.indexOf(ownerId2) - spacesSortedByAccesscount.indexOf(ownerId1);
        }
    };

    public UISpaceNavigationPortlet() throws Exception {
        try {
            spaceService = getApplicationComponent(SpaceService.class);
        } catch (Exception exception) {
            LOG.error("SpaceService could be 'null' when the Social profile isn't activated ", exception);
        }
        if (spaceService == null) { // Social profile disabled
            return;
        }
        SpaceAccessService spaceAccessService = getApplicationComponent(SpaceAccessService.class);
        spacesSortedByAccesscount = spaceAccessService.getSpaceAccessList(getUserId());
        organizationService = getApplicationComponent(OrganizationService.class);
        UserACL userACL = getApplicationComponent(UserACL.class);
        if (getUserId().equals(userACL.getSuperUser())) {
            groupNavigationPermitted = true;
        } else {
            Collection<?> memberships = organizationService.getMembershipHandler().findMembershipsByUser(getUserId());
            for (Object object : memberships) {
                Membership membership = (Membership) object;
                if (membership.getMembershipType().equals(userACL.getAdminMSType())) {
                    groupNavigationPermitted = true;
                    break;
                }
            }
        }
        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        mySpaceFilterConfig = builder.build();
    }


    public List<UserNavigation> getGroupNavigations() throws Exception {
        List<UserNavigation> computedNavigations = null;
        if (spaceService != null) {
            String remoteUser = getUserId();
            UserPortal userPortal = getUserPortal();
            List<UserNavigation> allNavigations = userPortal.getNavigations();
            computedNavigations = new ArrayList<UserNavigation>(allNavigations);
            ListAccess<Space> spacesListAccess = spaceService.getAccessibleSpacesWithListAccess(remoteUser);
            List<Space> spaces = Arrays.asList(spacesListAccess.load(0, spacesListAccess.getSize()));
            Iterator<UserNavigation> navigationItr = computedNavigations.iterator();
            String ownerId;
            String[] navigationParts;
            Space space;
            while (navigationItr.hasNext()) {
                ownerId = navigationItr.next().getKey().getName();
                if (ownerId.startsWith("/spaces/")) {
                    navigationParts = ownerId.split("/");
                    if (navigationParts.length < 3) {
                        continue;
                    }
                    space = spaceService.getSpaceByUrl(navigationParts[2]);
                    if (space == null)
                        navigationItr.remove();
                    if (!navigationParts[1].equals("spaces") && !spaces.contains(space))
                        navigationItr.remove();
                } else { // not spaces navigation
                    navigationItr.remove();
                }
            }
            if (spacesSortedByAccesscount != null && !spacesSortedByAccesscount.isEmpty()) {
                Collections.sort(computedNavigations, spaceAccessComparator);
            }
        }
        return computedNavigations;
    }

    public boolean isRender(UserNode spaceNode, UserNode applicationNode) throws SpaceException {
        if (spaceService != null) {
            String remoteUser = getUserId();
            String spaceUrl = spaceNode.getURI();
            if (spaceUrl.contains("/")) {
                spaceUrl = spaceUrl.split("/")[0];
            }
            Space space = spaceService.getSpaceByUrl(spaceUrl);
            if (space != null) {
                if (spaceService.hasSettingPermission(space, remoteUser)) {
                    return true;
                }
                if (SPACE_SETTINGS.equals(applicationNode.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    public UserNode getSelectedPageNode() throws Exception {
        return Util.getUIPortal().getSelectedUserNode();
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

    boolean renderSpacesLink() throws Exception {
        if (spaceService != null) {
            UserNavigation nav = getCurrentPortalNavigation();
            Collection<UserNode> userNodes = getUserNodes(nav);
            for (UserNode node : userNodes) {
                if (node.getURI().equals("spaces")) {
                    return true;
                }
            }
        }
        return false;
    }

    private UserNavigation getCurrentPortalNavigation() {
        List<UserNavigation> userNavigation = getUserPortal().getNavigations();
        for (UserNavigation nav : userNavigation) {
            if (nav.getKey().getType().equals(SiteType.PORTAL)) {
                return nav;
            }
        }
        return null;
    }

    public List<UserNavigation> getAllGroupUserNavigation() {
        List<UserNavigation> groupNavigation = new LinkedList<UserNavigation>();
        List<UserNavigation> userNavigation = getUserPortal().getNavigations();
        for (UserNavigation nav : userNavigation) {
            if (nav.getKey().getType().equals(SiteType.GROUP)) {
                groupNavigation.add(nav);
            }
        }
        return groupNavigation;
    }

    public UserNavigation getCurrentUserNavigation() throws Exception {
        WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance();
        return getNavigation(SiteKey.user(rcontext.getRemoteUser()));
    }

    private UserNavigation getNavigation(SiteKey userKey) {
        UserPortal userPortal = getUserPortal();
        return userPortal.getNavigation(userKey);
    }

    public static UserPortal getUserPortal() {
        UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
        return portalConfig.getUserPortal();
    }

    public Collection<UserNode> getUserNodes(UserNavigation nav) {
        UserPortal userPortall = getUserPortal();
        if (nav != null) {
            try {
                UserNode rootNode = userPortall.getNode(nav, Scope.ALL, mySpaceFilterConfig, null);
                return rootNode.getChildren();
            } catch (Exception exp) {
                log.warn(nav.getKey().getName() + " has been deleted");
            }
        }
        return Collections.emptyList();
    }

    public UserNode getSelectedNode() throws Exception {
        return Util.getUIPortal().getSelectedUserNode();
    }

    public boolean hasPermission() throws Exception {
        return groupNavigationPermitted;
    }

    public static class LoadNavigationActionListener extends EventListener<UISpaceNavigationPortlet> {

        @Override
        public void execute(Event<UISpaceNavigationPortlet> event) throws Exception {

            UISpaceNavigationPortlet uisource = event.getSource();

            event.getRequestContext().addUIComponentToUpdateByAjax(uisource);
        }
    }

    public static class IncrementActionListener extends EventListener<UISpaceNavigationPortlet> {

        @Override
        public void execute(Event<UISpaceNavigationPortlet> event) throws Exception {
            HttpServletRequest request = Util.getPortalRequestContext().getRequest();

            MY_SPACES_MAX_NUMBER += loadingCapacity;

            request.setAttribute("MY_SPACES_MAX_NUMBER", MY_SPACES_MAX_NUMBER);

            UISpaceNavigationPortlet uisource = event.getSource();

            event.getRequestContext().addUIComponentToUpdateByAjax(uisource);
        }

    }

    public int getSpaceMaxNumber() {
        HttpServletRequest request = Util.getPortalRequestContext().getRequest();
        Integer NUMBER = (Integer) request.getAttribute("MY_SPACES_MAX_NUMBER");
        if (NUMBER != null) {
            return NUMBER.intValue();
        } else {
            MY_SPACES_MAX_NUMBER = 10;
            return 0;
        }
    }

    static public class SelectSpaceActionListener extends EventListener<UISpaceNavigationPortlet> {
        @Override
        public void execute(Event<UISpaceNavigationPortlet> event) throws Exception {

        }
    }

    public String getImageSource(String SpaceLaBel) throws Exception {
        SpaceService spaceService = Utils.getSpaceService();
        Space space = spaceService.getSpaceByDisplayName(SpaceLaBel);
        return space.getAvatarUrl();
    }

    protected String getRestUrl() {
        return getCurrentRestURL() + MY_SPACE_REST_URL;
    }

    public static String getCurrentRestURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(PortalContainer.getCurrentPortalContainerName()).append("/");
        sb.append(PortalContainer.getCurrentRestContextName());
        return sb.toString();
    }
}
