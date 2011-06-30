package org.exoplatform.platform.component;

import java.util.Collection;
import java.util.Collections;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIBreadcrumbPlatformToolBarPortlet/UIBreadcrumbPlatformToolBarPortlet.gtmpl"

)
public class UIBreadcrumbPlatformToolBarPortlet extends UIPortletApplication {

	private OrganizationService organizationService = null;
	private SpaceService spaceService = null;
	private UserPortalConfigService userPortalConfigService =null;
	private Scope navigationScope;
	private final UserNodeFilterConfig NAVIGATION_FILTER_CONFIG;
	private Log log = ExoLogger.getLogger(this.getClass());

	public UIBreadcrumbPlatformToolBarPortlet() throws Exception {
	  UserNodeFilterConfig.Builder filterConfigBuilder = UserNodeFilterConfig.builder();
    filterConfigBuilder.withAuthorizationCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL);
    filterConfigBuilder.withTemporalCheck();
    NAVIGATION_FILTER_CONFIG = filterConfigBuilder.build();
    navigationScope = Scope.ALL;
	  try {
			organizationService = getApplicationComponent(OrganizationService.class);
			spaceService = getApplicationComponent(SpaceService.class);
			userPortalConfigService = getApplicationComponent(UserPortalConfigService.class);
		} catch (Exception e) {
			log.error("Error while initializing ... " + e.getMessage());
		}

	}

	public UserNode getSelectedPageNode() throws Exception {
		return Util.getUIPortal().getSelectedUserNode();
	}

	public UserNode getSelectedNavigationNodes() throws Exception {
    UserPortal userPortal = Util.getUIPortalApplication().getUserPortalConfig().getUserPortal();
    UserNavigation userNavigation = Util.getUIPortal().getUserNavigation();
    try 
    {
       UserNode rootNode = userPortal.getNode(userNavigation, navigationScope, NAVIGATION_FILTER_CONFIG, null);      
       return rootNode;
    } 
    catch (Exception ex)
    {
       log.error("Error occured while getting the navigation", ex);
    }
    return null;
	}

	public String getOwnerLabel() throws Exception {
		String ownerType = Util.getUIPortal().getOwnerType();
		String ownerLabel = Util.getUIPortal().getSelectedUserNode().getNavigation().getKey().getName();
		if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
			// space navigation
			if (isSpaceNavigation()) {
				ownerLabel = ownerLabel.split("/")[2];
				ownerLabel = spaceService.getSpaceByUrl(ownerLabel).getDisplayName();
			} else {
				//gets the group label from the organization service
				Group group = organizationService.getGroupHandler()
						.findGroupById(ownerLabel);
				if (group.getLabel() != null) {
					ownerLabel = group.getLabel();
				} else {
					ownerLabel = group.getGroupName();
				}
			}
		}
		return ownerLabel;
	}

	public String getOwnerURI() throws Exception {
		if (getSelectedNavigationNodes().getChildrenCount() > 0) {
			return getDefaultPageURI(getSelectedNavigationNodes());
		} else
			return "";
	}

	private String getDefaultPageURI(UserNode pageNodeList) {
		if (pageNodeList.getChild(0).getPageRef() != null) {
			return pageNodeList.getChild(0).getURI();
		}
		if (pageNodeList.getChild(0).getChildren().size() > 0) {
			return getDefaultPageURI(pageNodeList.getChild(0));
		} else
			return "";
	}

	//Home : links to the default page of the default site in the portal container
	public String getHomeURI() throws Exception {
		return userPortalConfigService.getDefaultPortal();
	}

	// portalContainerName
	public String getBaseURI() {
		PortletRequestContext portletRequestContext = PortletRequestContext
				.getCurrentInstance();
		return portletRequestContext.getPortalContextPath();
	}

	public String getAccessMode() {
		if (Util.getPortalRequestContext().getAccessPath() == 1) {
			return "private";
		} else
			return "public";
	}

  public boolean isSpaceNavigation() throws Exception {
    if (PortalConfig.GROUP_TYPE.equals(Util.getUIPortal().getOwnerType())) {
      if (Util.getUIPortal().getSelectedUserNode().getNavigation().getKey().getName().startsWith("/spaces")) {
        return true;
      }
    }
    return false;
  }
  
  public UserNavigation getCurrentUserNavigation() throws Exception
  {
     WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance();
     return getNavigation(SiteKey.user(rcontext.getRemoteUser()));
  }

  private UserNavigation getNavigation(SiteKey userKey)
  {
     UserPortal userPortal = getUserPortal();
     return userPortal.getNavigation(userKey);
  }

  private UserPortal getUserPortal()
  {
     UIPortalApplication uiPortalApplication = Util.getUIPortalApplication();
     return uiPortalApplication.getUserPortalConfig().getUserPortal();
  }
  
  public Collection<UserNode> getUserNodes(UserNavigation nav)
  {
     UserPortal userPortall = getUserPortal();
     if (nav != null)
     {
        try
        {
           UserNode rootNode = userPortall.getNode(nav, Scope.ALL, NAVIGATION_FILTER_CONFIG, null);
           return rootNode.getChildren();
        }
        catch (Exception exp)
        {
           log.warn(nav.getKey().getName() + " has been deleted");
        }
     }
     return Collections.emptyList();
  }

}