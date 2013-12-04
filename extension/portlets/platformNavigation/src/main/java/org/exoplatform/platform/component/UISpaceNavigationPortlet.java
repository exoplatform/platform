package org.exoplatform.platform.component;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.RequestNavigationData;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 22/11/12
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UISpaceNavigationPortlet/UISpaceNavigationPortlet.gtmpl",
        events = {
                @EventConfig(listeners = UISpaceNavigationPortlet.IncrementActionListener.class),
                @EventConfig(listeners = UISpaceNavigationPortlet.SelectSpaceActionListener.class)
        }
)
public class UISpaceNavigationPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UISpaceNavigationPortlet.class);

    private static final String MY_SPACE_REST_URL = "/space/user/searchSpace/";

    private static final String SPACE_URL_PATTERN = ":spaces";

    private SpaceService spaceService = null;

    private ListAccess<Space> spaceListAccess;

    private LinkedList<Space> spaceList = new LinkedList<Space>();

    private static final int MY_SPACES_MAX_NUMBER = 10;

    private static String portalContainerName = "";

    private int offset = 0;

    private boolean reload = false;

    private String oldNavigation = null;

    private String currentNavigation = null;

    private String selectedSpaceId = null;

    private String userId = null;

    public UISpaceNavigationPortlet() throws Exception {

        try {
            spaceService = ((SpaceService)getApplicationComponent(SpaceService.class));
            portalContainerName = PortalContainer.getCurrentPortalContainerName();
            if (spaceService != null) {
                this.spaceListAccess = spaceService.getLastAccessedSpace(getUserId(), null);
                setReload(true);
            }
            setOldNavigation(Util.getPortalRequestContext().getRequest().getRequestURI());

        } catch (Exception exception) {
            LOG.error("SpaceService could be 'null' when the Social profile isn't activated ", exception);
        }
    }

    /**
     *
     * @return
     */
    public List<Space> getSpaces() {
        Space space = Utils.getSpaceByContext();
        if (space != null) {
            putTop(space);
        }
        // Workaround to load only the 10 first space in the next iteration
         if (selectedSpaceId != null) {
             setSelectedSpaceId(null);
         }

        int from = 0;
        int to = this.offset + MY_SPACES_MAX_NUMBER;
        if (to >= this.spaceList.size()) {
            return this.spaceList;
        }
        return this.spaceList.subList(from, to);
    }

    /**
     * Refresh loaded spaces in left component UI
     * @throws Exception
     */
    public void refresh() throws Exception {
       setCurrentNavigation(Util.getPortalRequestContext().getRequest().getRequestURI());
       boolean navChanged = getCurrentNavigation().equals(getOldNavigation());
        if ((this.reload && selectedSpaceId == null) || !navChanged) {
            this.spaceList.clear();
            this.offset = 0;
            loadSpaces();
        }
        setOldNavigation(getCurrentNavigation());
    }

    /**
     * Complete the rendered Spaces list with the 10 next spaces
     * @throws Exception
     */
    private void loadSpaces() throws Exception {
        if (spaceListAccess != null) {
            this.spaceList.addAll(Arrays.asList(spaceListAccess.load(this.offset, MY_SPACES_MAX_NUMBER)));
        }
    }

    public Space putTop(String spaceId) {
        selectedSpaceId = spaceId;
        Space space = new Space();
        space.setId(spaceId);
        return putTop(space);
    }

    private Space putTop(Space space) {
        int idx = this.spaceList.indexOf(space);
        Space got = null;
        if (idx >= 0) {
            got = this.spaceList.remove(idx);
        } else {
            got = Utils.getSpaceService().getSpaceById(space.getId());
        }
        this.spaceList.addFirst(got);
        return got;
    }

    public int numberOfRemainSpaces() throws Exception {
        if (spaceListAccess != null) {
            int to = this.offset + MY_SPACES_MAX_NUMBER;
            int newLimit = Math.min(to, this.spaceList.size());
            return spaceListAccess.getSize() - newLimit;
        }
        return 0;
    }

    private String getUserId()
    {
        if (userId == null) {
            userId = Util.getPortalRequestContext().getRemoteUser();
        }
        return userId;
    }

    /**
     * Builds the Space URI
     * @param space
     * @return
     * @throws Exception
     */
    public String buildSpaceURL(Space space) throws Exception
    {
        return Utils.getSpaceHomeURL(space);
    }
    public Boolean isSelectedSpace(Space space) throws Exception {
        String groupId = Util.getPortalRequestContext().getControllerContext().getParameter(RequestNavigationData.REQUEST_SITE_NAME);
        return space.getGroupId().equalsIgnoreCase(groupId);
    }


    public static class SelectSpaceActionListener extends EventListener<UISpaceNavigationPortlet>
    {
        public void execute(Event<UISpaceNavigationPortlet> event) throws Exception {
            PortalRequestContext pContext = Util.getPortalRequestContext();
            UISpaceNavigationPortlet uisource = (UISpaceNavigationPortlet) event.getSource();
            String spaceId = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
            Space space = uisource.putTop(spaceId);
            //--- Get the space URL using reouter API
            String spaceURL = Utils.getSpaceHomeURL(space);
            //---- Get only the GROUP navigation
            if (spaceURL.contains(portalContainerName)) {
                spaceURL.substring(portalContainerName.length()+2);
            }
            String fullUrl = ((HttpServletRequest) pContext.getRequest()).getRequestURL().toString();
            String subUrl = fullUrl.substring(0, fullUrl.indexOf(portalContainerName) + portalContainerName.length());
            String applicationDisplayed = "";
            String constructURL =  fullUrl.substring(subUrl.length()+1);
            if (fullUrl.contains(SPACE_URL_PATTERN)) {
                int count = StringUtils.countMatches(constructURL, "/");
                if(count == 2){
                    subUrl +="/"+ spaceURL;
                } else {
                    applicationDisplayed = constructURL.substring(constructURL.lastIndexOf("/"));
                    subUrl +="/"+ spaceURL+applicationDisplayed;
                }
            }
            else {
                subUrl +="/"+ spaceURL;
            }

            uisource.setReload(true);
            event.getRequestContext().getJavascriptManager().getRequireJS().require("SHARED/navigation-spaces-search", "spaceSearchNavigationPortlet").addScripts("spaceSearchNavigationPortlet.ajaxRedirect('"+subUrl+"');");
        }
    }

    public static class IncrementActionListener extends EventListener<UISpaceNavigationPortlet>
    {
        public void execute(Event<UISpaceNavigationPortlet> event)
                throws Exception
        {
            HttpServletRequest request = Util.getPortalRequestContext().getRequest();

            UISpaceNavigationPortlet uisource = (UISpaceNavigationPortlet) event.getSource();

            uisource.loadMore(MY_SPACES_MAX_NUMBER);

            uisource.setReload(false);

            event.getRequestContext().addUIComponentToUpdateByAjax(uisource);
        }
    }

    protected String getRestUrl() {
        return getCurrentRestURL().concat(MY_SPACE_REST_URL);
    }

    public static String getCurrentRestURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(PortalContainer.getCurrentPortalContainerName()).append("/");
        sb.append(PortalContainer.getCurrentRestContextName());
        return sb.toString();
    }

    public void loadMore(int capacity) throws Exception {
        this.offset += capacity;
        loadSpaces();
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }
    public void setSelectedSpaceId(String selectedSpaceId) {
        this.selectedSpaceId = selectedSpaceId;
    }
    public String getCurrentNavigation() {
        return currentNavigation;
    }

    public void setCurrentNavigation(String currentNavigation) {
        this.currentNavigation = currentNavigation;
    }

    public String getOldNavigation() {
        return oldNavigation;
    }

    public void setOldNavigation(String oldNavigation) {
        this.oldNavigation = oldNavigation;
    }
}
