package org.exoplatform.portal.webui.workspace.pool;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.core.UIComponent;

/**
 * 
 * Created by eXoPlatform SAS
 *
 * Author: Minh Hoang TO - hoang281283@gmail.com
 *
 *      Aug 3, 2009
 */
public class UIPortalPool{

	private static Log logger=ExoLogger.getExoLogger(UIPortalPool.class);
	private static Map<String, UIPortal> _pooledUIPortals=new HashMap<String, UIPortal>();
	private UIPortalChangeEventListener _listener;

	private static UIPortalPool instance;
	private UIPortal currentUIPortal;
  private UIPortal defaultUIPortal;

	private UIPortalPool(){
	}

	public static UIPortalPool getInstance(){
		if(instance==null){
			logger.info("Instantiate UIPortalPool");
			instance=new UIPortalPool();
		}
		return instance;
	}

	//TODO: Test deadlock when there are multiple administrators
	synchronized public void setSelectedUIPortal(UIPortal _uiPortal){
		if(currentUIPortal==_uiPortal){
			logger.info("The UIPortal parsed in parameter is currently selected, no need to reset it");
			return;
		}else if(_uiPortal!=null){
			currentUIPortal=_uiPortal;
			logger.info("Fire the UIPortalChangeEvent event");
			fireEvent(new UIPortalChangeEvent(_uiPortal));
		}
	}

	public UIPortal fetchUIPortal(String portalName,UserPortalConfig _userPortalConfig,UIComponent creatorComponent){
		UIPortal uiPortal=_pooledUIPortals.get(portalName);
		if(uiPortal!=null){
			logger.debug("UIPortal named "+portalName+" has been built and set in the map");
			return uiPortal;
		}
		logger.info("Build a new UIPortal named "+portalName+" from model stored in databas ");
		try{
			//TODO: Find a static method to create UIPortal, that removes the need of creatorComponent	
			uiPortal=creatorComponent.createUIComponent(UIPortal.class, null, null, null);	
			PortalDataMapper.toUIPortal(uiPortal, _userPortalConfig);
		}
		catch(Exception ex){
			logger.error("Could not build UIPortal named "+portalName+" from database",ex);
			return null;
		}
		logger.info("UIPortal named "+portalName+" has just been built from database, it is needed to be added into the map");
		_pooledUIPortals.put(portalName, uiPortal);
		return uiPortal;
	}

	public void setDefaultUIPortal(UIPortal _defaultUIPortal){
		defaultUIPortal=_defaultUIPortal;
	}
	
	public UIPortal getUIPortalChild(){
		return currentUIPortal;
	}

	//TODO: Should make a check on invoking this method
	public void backupUIPortal(String portalName,UIPortal uiPortal){
		_pooledUIPortals.put(portalName, uiPortal);
	}

	//TODO: Re-implement this method when UIPortalPool is no longer singleton
	public void setListener(UIPortalChangeEventListener listener){
		if(_listener==null){
			_listener=listener;
		}
	}

	public void fireEvent(UIPortalChangeEvent event){
		_listener.execute(event);
	}
	
	public void resetDefaultUIPortal(){
		 logger.info("Reset default UIPortal on the UIPortalPool");
	   this.setSelectedUIPortal(this.defaultUIPortal);	
	}
}
