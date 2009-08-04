package org.exoplatform.portal.webui.workspace.pool;

import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;

/**
 * 
 * Created by eXoPlatform SAS
 *
 * Author: Minh Hoang TO - hoang281283@gmail.com
 *
 *      Aug 4, 2009
 */
public class UIPCListenerImpl implements UIPortalChangeEventListener{

	private UIWorkingWorkspace workingWorkspace;
	private UIPortalPool portalPool;

	public UIPCListenerImpl(UIWorkingWorkspace _wkWorkspace,UIPortalPool _portalPool){
		workingWorkspace=_wkWorkspace;
		portalPool=_portalPool;
	}

	public void execute(UIPortalChangeEvent event) {
		// TODO Auto-generated method stub
		updateWorkingWorkspace(event.getSource());
	}

	private void updateWorkingWorkspace(UIPortal uiPortal){
		UIPortal currentPortal=workingWorkspace.removeChild(UIPortal.class);
		if(currentPortal!=null){
			portalPool.backupUIPortal(currentPortal.getName(), currentPortal);
		}
		workingWorkspace.addChild(uiPortal);
		//TODO: Update all portal-related config object currently set on the workspace
	}

}
