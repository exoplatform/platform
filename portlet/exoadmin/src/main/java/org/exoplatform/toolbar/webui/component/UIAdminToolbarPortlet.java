package org.exoplatform.toolbar.webui.component;

import java.util.Locale;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class)
public class UIAdminToolbarPortlet extends UIPortletApplication {
	
	private String lastPortalURI = null;

	private Locale lastLocale = null;

	public UIAdminToolbarPortlet() throws Exception {
		PortalRequestContext portalRequestContext = Util
				.getPortalRequestContext();
		String userId = portalRequestContext.getRemoteUser();
		if (userId != null) {
			lastPortalURI = portalRequestContext.getPortalURI();
			lastLocale = portalRequestContext.getLocale();
			addChild(UIAdminToolbar.class, null, UIPortletApplication.VIEW_MODE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.exoplatform.webui.core.UIPortletApplication#processRender(org.exoplatform.webui.application.WebuiApplication,
	 *      org.exoplatform.webui.application.WebuiRequestContext)
	 */
	public void processRender(WebuiApplication app, WebuiRequestContext context)
			throws Exception {
		UIAdminToolbar adminToolbar = getChild(UIAdminToolbar.class);
		PortalRequestContext portalRequestContext = Util
				.getPortalRequestContext();
		if (adminToolbar != null) {
			if (portalRequestContext.getFullRender()) {
				adminToolbar.refresh();
			}
			String currentPortalURI = portalRequestContext.getPortalURI();
			if (!currentPortalURI.equalsIgnoreCase(lastPortalURI)) {
				adminToolbar.refresh();
				lastPortalURI = currentPortalURI;
			}
			Locale currentLocale = portalRequestContext.getLocale();
			if (!currentLocale.getLanguage().equalsIgnoreCase(
					lastLocale.getLanguage())) {
				adminToolbar.changeNavigationsLanguage(currentLocale
						.getLanguage());
				lastLocale = currentLocale;
			}
		}
		super.processRender(app, context);
	}
}
