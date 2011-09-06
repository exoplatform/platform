package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;

public class LoginHistoryPlugin extends BaseComponentPlugin {
	private InitParams initParams;

	public InitParams getInitParams() {
		return initParams;
	}

    public LoginHistoryPlugin(InitParams params) {
    	this.initParams = params;
    }
}
