package org.exoplatform.platform.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;


@ComponentConfig(
	lifecycle = UIApplicationLifecycle.class,
	template = "app:/groovy/construction/portlet/UIConstructionPortlet.gtmpl"
)

public class UIConstructionPortlet extends UIPortletApplication {

	public UIConstructionPortlet() throws Exception {

	}

}
