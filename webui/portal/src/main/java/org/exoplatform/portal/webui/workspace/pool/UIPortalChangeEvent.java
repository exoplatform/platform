package org.exoplatform.portal.webui.workspace.pool;

import org.exoplatform.portal.webui.portal.UIPortal;

/**
 * 
 * Created by eXoPlatform SAS
 *
 * Author: Minh Hoang TO - hoang281283@gmail.com
 *
 *      Aug 4, 2009
 */
public class UIPortalChangeEvent{
  
	private UIPortal source;
  
  public UIPortalChangeEvent(UIPortal _source){
  	source=_source;
  }
  
  public UIPortal getSource(){
  	return source;
  }
}
