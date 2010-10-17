package org.exoplatform.platform.service.impl;

import java.util.ArrayList;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;
import org.exoplatform.portal.pom.spi.portlet.Portlet;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform
 * zaoui.ahmed@exoplatform.com Oct 15, 2010
 */

public class NewSpaceAppListener extends SpaceListenerPlugin {

	private static final Log log = ExoLogger
			.getLogger(NewSpaceAppListener.class);
	private final InitParams params;
	private DataStorage dataStorage = null;

	public NewSpaceAppListener(DataStorage dataStorage_, InitParams params) {
		this.dataStorage = dataStorage_;
		this.params = params;
	}

	public void applicationActivated(SpaceLifeCycleEvent arg0) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void applicationAdded(SpaceLifeCycleEvent event) {
		String portletName = "";
		String driveName = "";
		String usecase="";
		try {
			portletName = params.getValueParam("portletName").getValue();
		} catch (Exception e) {
			// do nothing here. It means that initparam is not configured.
		}

		if (!portletName.equals(event.getSource())) {

			/*
			 * this function is called only if FileExplorerPortlet Portlet is
			 * added to Social Space. Hence, if the application added to space
			 * do not have the name as configured, we will do nothing.
			 */
			return;
		}

		String pageName = event.getSource();
		String groupId = event.getSpace().getGroupId();
		/* --- begin Request --- */
		try {
			PortalContainer.getInstance();
			((ChromatticManager) PortalContainer
					.getComponent(ChromatticManager.class)).beginRequest();
		} catch (Exception e) {
			log.error("request already started" + e.getMessage());
		}

		Page tempPage = null;
		Page currentPage = null;

		Application<Portlet> app = null;
		try {

			// waiting page to be stored
			while (tempPage == null) {

				currentPage = dataStorage.getPage(PortalConfig.GROUP_TYPE
						+ "::" + groupId + "::" + pageName);
				tempPage = currentPage;
			}

			app = (Application<Portlet>) findApplicationByContainer(currentPage
					.getChildren());

			if (null != app) {

				/*
				 * while (Thread.currentThread().getState() !=
				 * Thread.State.WAITING) { // Wait until thread is blocked }
				 */

				Portlet pagePrefs = dataStorage.load(app.getState(), app
						.getType());
				
               //get portlet preferences from configuration file
				driveName = params.getValueParam("driveName").getValue();
				pagePrefs.setValue("driveName", driveName);
				usecase = params.getValueParam("usecase").getValue();
				pagePrefs.setValue("usecase", usecase);
				dataStorage.save(app.getState(), pagePrefs);

			}
		}

		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* --- end Request --- */
		try {
			PortalContainer.getInstance();
			((ChromatticManager) PortalContainer
					.getComponent(ChromatticManager.class)).endRequest(true);
		} catch (Exception e) {
			log.error("can not end Request" + e.getMessage());
		}

	}

	private ModelObject findApplicationByContainer(
			ArrayList<ModelObject> children) {
		ModelObject cont = null;
		for (ModelObject modelObject : children) {
			cont = modelObject;
			if (modelObject instanceof Application<?>) {

				return modelObject;
			}

		}

		return findApplicationByContainer(((Container) cont).getChildren());
	}

	public void applicationDeactivated(SpaceLifeCycleEvent arg0) {

	}

	public void applicationRemoved(SpaceLifeCycleEvent arg0) {

	}

	public void grantedLead(SpaceLifeCycleEvent arg0) {

	}

	public void joined(SpaceLifeCycleEvent arg0) {

	}

	public void left(SpaceLifeCycleEvent arg0) {

	}

	public void revokedLead(SpaceLifeCycleEvent arg0) {

	}

	public void spaceCreated(SpaceLifeCycleEvent event) {

	}

	public void spaceRemoved(SpaceLifeCycleEvent arg0) {

	}
}
