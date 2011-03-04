package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIBreadcrumbPlatformToolBarPortlet/UIBreadcrumbPlatformToolBarPortlet.gtmpl"

)
public class UIBreadcrumbPlatformToolBarPortlet extends UIPortletApplication {

	private ExoContainer container = null;
	private OrganizationService organizationService = null;
	private Log log = ExoLogger.getLogger(this.getClass());

	public UIBreadcrumbPlatformToolBarPortlet() throws Exception {
		try {
			container = ExoContainerContext.getCurrentContainer();
			organizationService = getApplicationComponent(OrganizationService.class);
		} catch (Exception e) {
			log.error("Error while initializing ... " + e.getMessage());
		}

	}

	public PageNode getSelectedPageNode() throws Exception {
		return Util.getUIPortal().getSelectedNode();
	}

	public ArrayList<PageNode> getSelectedNavigationNodes() throws Exception {
		return Util.getUIPortal().getSelectedNavigation().getNodes();
	}

	public String getOwnerLabel() throws Exception {
		String ownerType = Util.getUIPortal().getOwnerType();
		String ownerLabel = Util.getUIPortal().getSelectedNavigation()
				.getOwnerId();
		if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
			// space navigation
			if (ownerLabel.startsWith("/spaces")) {
				ownerLabel = ownerLabel.split("/")[2];
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
		if (getSelectedNavigationNodes().size() > 0) {
			return getDefaultPageURI(getSelectedNavigationNodes());
		} else
			return "";
	}

	private String getDefaultPageURI(List<PageNode> pageNodeList) {
		if (pageNodeList.get(0).getPageReference() != null) {
			return pageNodeList.get(0).getUri();
		}
		if (pageNodeList.get(0).getChildren().size() > 0) {
			return getDefaultPageURI(pageNodeList.get(0).getChildren());
		} else
			return "";
	}

	//Home : links to the default page of the default site in the portal container
	public String getHomeURI() throws Exception {
		UserPortalConfigService userPortalConfigService = (UserPortalConfigService) container
				.getComponentInstanceOfType(UserPortalConfigService.class);
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

}