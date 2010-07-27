package org.exoplatform.platform.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;


@ComponentConfig(
	lifecycle = UIApplicationLifecycle.class,
	template = "app:/groovy/platformNavigation/portlet/UIUserToolBarSpacesPortlet/UIUserToolBarSpacesPortlet.gtmpl"
)

public class UIUserToolBarSpacesPortlet extends UIPortletApplication {

	public UIUserToolBarSpacesPortlet() throws Exception {

	}

}
