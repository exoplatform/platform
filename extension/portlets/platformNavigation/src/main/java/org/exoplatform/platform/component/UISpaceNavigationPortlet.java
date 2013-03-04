package org.exoplatform.platform.component;


import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 22/11/12
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UISpaceNavigationPortlet/UISpaceNavigationPortlet.gtmpl",
        events = {
                @EventConfig(listeners = UISpaceNavigationPortlet.IncrementActionListener.class),
                @EventConfig(listeners = UISpaceNavigationPortlet.LoadNavigationActionListener.class),
                @EventConfig(listeners = UISpaceNavigationPortlet.SelectSpaceActionListener.class)
        }
)
public class UISpaceNavigationPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UISpaceNavigationPortlet.class);
    private static final String SPACE_SETTINGS = "settings";
    private static final String MY_SPACE_REST_URL = "/space/user/searchSpace/";
    public static final String LOAD_NAVIGATION_ACTION = "LoadNavigation";
    private static final int MAX_LOADED_SPACES_BY_REQUEST = 30;
    private static final String OBJECT_ID = "objectId";
    private static final String SPACE_URL_PATTERN = ":spaces";

    private SpaceService spaceService = null;

    private String userId = null;

    static int MY_SPACES_MAX_NUMBER = 10;

    static int loadingCapacity = 10;

    static private String portalContainerName = "";

    public UISpaceNavigationPortlet() throws Exception {
        try {
            spaceService = ((SpaceService)getApplicationComponent(SpaceService.class));
            portalContainerName = PortalContainer.getCurrentPortalContainerName();
        } catch (Exception exception) {
            LOG.error("SpaceService could be 'null' when the Social profile isn't activated ", exception);
        }
        if (spaceService == null)
            return;
    }

    public List<Space> getSpaceLastedAccessed() throws Exception {
        List spaces = null;
        if (spaceService != null) {
            String remoteUser = getUserId();
            spaces = spaceService.getLastAccessedSpace(remoteUser, null,0, MAX_LOADED_SPACES_BY_REQUEST);
        }

        return spaces;
    }

    private String getUserId()
    {
        if (userId == null) {
            userId = Util.getPortalRequestContext().getRemoteUser();
        }
        return userId;
    }

    public int getSpaceMaxNumber()
    {
        HttpServletRequest request = Util.getPortalRequestContext().getRequest();
        Integer NUMBER = (Integer)request.getAttribute("MY_SPACES_MAX_NUMBER");
        if (NUMBER != null) {
            return NUMBER.intValue();
        }
        MY_SPACES_MAX_NUMBER = 10;
        return 0;
    }

    public String getImageSource(String SpaceLaBel)
            throws Exception
    {
        SpaceService spaceService = Utils.getSpaceService();
        Space space = spaceService.getSpaceByDisplayName(SpaceLaBel);
        if (space == null) {
            return LinkProvider.SPACE_DEFAULT_AVATAR_URL;
        }
        return space.getAvatarUrl();
    }

    protected String getRestUrl() {
        return getCurrentRestURL() + "/space/user/searchSpace/";
    }

    public static String getCurrentRestURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(PortalContainer.getCurrentPortalContainerName()).append("/");
        sb.append(PortalContainer.getCurrentRestContextName());
        return sb.toString();
    }

    public String buildSpaceURL(String spaceLabel) throws Exception
    {
        StringBuffer baseSpaceURL = null;

        if (this.spaceService != null)
        {
            baseSpaceURL = new StringBuffer();

            baseSpaceURL.append("g/:spaces:");


            Space space = spaceService.getSpaceByDisplayName(spaceLabel);

            String groupId = space.getGroupId();

            String permanentSpaceName = groupId.split("/")[2];

            if (permanentSpaceName.equals(space.getPrettyName()))
            {
                baseSpaceURL.append(permanentSpaceName);
                baseSpaceURL.append("/");
                baseSpaceURL.append(permanentSpaceName);
            }
            else {
                baseSpaceURL.append(permanentSpaceName);
                baseSpaceURL.append("/");
                baseSpaceURL.append(space.getPrettyName());
            }
        }

        return baseSpaceURL.toString();
    }
    public Boolean isSelectedSpace(String spaceNAme) throws Exception {
        String spaceUrl="";
        Space space = spaceService.getSpaceByDisplayName(spaceNAme);
        if (space == null) {
            return false;
        }
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        UserPortal userPortal = Util.getPortalRequestContext().getUserPortalConfig().getUserPortal();
        UserNavigation nav = userPortal.getNavigation(node.getNavigation().getKey());
        String ownerId = nav.getKey().getName();
        if (ownerId.contains("/spaces/")) {
        Space spaceSelected = spaceService.getSpaceByGroupId(ownerId);
            if (spaceSelected == null) {
                return false;
            }
        if(spaceSelected.getPrettyName().equals(space.getPrettyName()))   {
            return true  ;

        }else{
            return false ;
        }
        }else{
            return false;
        }
    }


    public static class SelectSpaceActionListener extends EventListener<UISpaceNavigationPortlet>
    {
        public void execute(Event<UISpaceNavigationPortlet> event) throws Exception {

            UISpaceNavigationPortlet uisource = (UISpaceNavigationPortlet)event.getSource();
            PortalRequestContext pContext = Util.getPortalRequestContext();
            String spaceName = event.getRequestContext().getRequestParameter(OBJECT_ID);
            String fullUrl = ((HttpServletRequest) pContext.getRequest()).getRequestURL().toString();
            String applicationDisplayed = "";
            String subUrl = "";
            if (fullUrl.contains(SPACE_URL_PATTERN)) {
                applicationDisplayed = fullUrl.substring(fullUrl.lastIndexOf("/"));
                subUrl = fullUrl.substring(0, fullUrl.indexOf(portalContainerName) + portalContainerName.length());
                subUrl +="/"+ spaceName+applicationDisplayed;
            } else {
                subUrl = fullUrl.substring(0, fullUrl.indexOf(portalContainerName) + portalContainerName.length());
                subUrl +="/"+ spaceName;
            }

            event.getRequestContext().getJavascriptManager().getRequireJS().require("SHARED/navigation-spaces-search", "spaceSearchNavigationPortlet").addScripts("spaceSearchNavigationPortlet.ajaxRedirect('"+subUrl+"');");
        }
    }

    public static class IncrementActionListener extends EventListener<UISpaceNavigationPortlet>
    {
        public void execute(Event<UISpaceNavigationPortlet> event)
                throws Exception
        {
            HttpServletRequest request = Util.getPortalRequestContext().getRequest();

            UISpaceNavigationPortlet.MY_SPACES_MAX_NUMBER += UISpaceNavigationPortlet.loadingCapacity;

            request.setAttribute("MY_SPACES_MAX_NUMBER", Integer.valueOf(UISpaceNavigationPortlet.MY_SPACES_MAX_NUMBER));

            UISpaceNavigationPortlet uisource = (UISpaceNavigationPortlet)event.getSource();

            event.getRequestContext().addUIComponentToUpdateByAjax(uisource);
        }
    }

    public static class LoadNavigationActionListener extends EventListener<UISpaceNavigationPortlet>
    {
        public void execute(Event<UISpaceNavigationPortlet> event)
                throws Exception
        {
            UISpaceNavigationPortlet uisource = (UISpaceNavigationPortlet)event.getSource();

            event.getRequestContext().addUIComponentToUpdateByAjax(uisource);
        }
    }
}
