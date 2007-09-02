/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.exoplatform.Constants;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ChangePortletModeActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ChangeWindowStateActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.EditPortletActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ProcessActionActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ProcessEventsActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.RenderActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ServeResourceActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.services.portletcontainer.pci.model.Supports;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event.Phase;

/**
 * May 19, 2006
 */
@ComponentConfig(
    lifecycle = UIPortletLifecycle.class,    
    template = "system:/groovy/portal/webui/application/UIPortlet.gtmpl",
    events = {
      @EventConfig(listeners = RenderActionListener.class),
      @EventConfig(listeners = ChangePortletModeActionListener.class),
      @EventConfig(listeners = ChangeWindowStateActionListener.class),
      @EventConfig(listeners = DeleteComponentActionListener.class, confirm = "UIPortlet.deletePortlet"),
      @EventConfig(listeners = EditPortletActionListener.class),
      @EventConfig(phase = Phase.PROCESS, listeners = ProcessActionActionListener.class),
      @EventConfig(phase = Phase.PROCESS, listeners = ServeResourceActionListener.class),
      @EventConfig(phase = Phase.PROCESS, listeners = ProcessEventsActionListener.class)      
    }    
)
public class UIPortlet extends UIApplication { 
  
  protected static Log log = ExoLogger.getLogger("portal:UIPortlet"); 
  
  private String windowId ;
  private String portletStyle ;

  private boolean  showPortletMode = true ;
  
  private Map renderParametersMap_ ;
  private ExoWindowID exoWindowId_ ;
  private PortletMode currentPortletMode_ = PortletMode.VIEW;
  private WindowState currentWindowState_ = WindowState.NORMAL;  
  
  private List<String> supportModes_ ;

  private List supportedProcessingEvents_;
  private List supportedPublicParams_;
  
  public String getId()  { return exoWindowId_.getUniqueID() ; }
  
  public String getWindowId() { return windowId ; }
  public void   setWindowId(String s) {
    windowId = s ;
    exoWindowId_ = new ExoWindowID(windowId) ;
  }
  
  public String getPortletStyle() {  return  portletStyle ; }
  public void   setPortletStyle(String s) { portletStyle = s ;}
  
  public boolean getShowPortletMode() { return showPortletMode ; }
  public void    setShowPortletMode(Boolean b) { showPortletMode = b ; }
  
  public ExoWindowID  getExoWindowID() { return exoWindowId_ ; }
  
  public  Map  getRenderParametersMap() { return renderParametersMap_ ;}
  public  void setRenderParametersMap(Map map) { renderParametersMap_ =  map ; } 
  
  public PortletMode getCurrentPortletMode() { return currentPortletMode_ ;}
  public void  setCurrentPortletMode(PortletMode mode) { currentPortletMode_ = mode;}
  
  public WindowState getCurrentWindowState() { return currentWindowState_ ;}
  public void  setCurrentWindowState(WindowState state) { currentWindowState_ = state;}
  
  public List getSupportedProcessingEvents() { return supportedProcessingEvents_; }
  public void setSupportedProcessingEvents(List supportedProcessingEvents) {
	supportedProcessingEvents_ = supportedProcessingEvents;
  }
  
  public List getSupportedPublicRenderParameters() { return supportedPublicParams_; }
  public void setSupportedPublicRenderParameters(List supportedPublicRenderParameters) {
	supportedPublicParams_ = supportedPublicRenderParameters;
  }
  
  public  List<String> getSupportModes() { 
    if (supportModes_ != null) return supportModes_;
    PortletContainerService portletContainer =  getApplicationComponent(PortletContainerService.class);
    String  portletId = exoWindowId_.getPortletApplicationName() + Constants.PORTLET_META_DATA_ENCODER + exoWindowId_.getPortletName();   
    PortletData portletData = (PortletData) portletContainer.getAllPortletMetaData().get(portletId);
    if(portletData == null) return null;
    List supportsList = portletData.getSupports() ;
    List<String> supportModes = new ArrayList<String>() ;
    for (int i = 0; i < supportsList.size(); i++) {
      Supports supports = (Supports) supportsList.get(i) ;
      String mimeType = supports.getMimeType() ;
      if ("text/html".equals(mimeType)) {
        List modes = supports.getPortletMode() ;
        for (int j =0 ; j < modes.size() ; j++) {
          String mode =(String)modes.get(j) ;
          mode = mode.toLowerCase() ;
          //check role admin
          if("config".equals(mode)) { 
            //if(adminRole) 
            supportModes.add(mode) ;
          } else {
            supportModes.add(mode) ;
          }
        }
        break ;
      }
    }
    if(supportModes.size() > 1) supportModes.remove("view");
    setSupportModes(supportModes);
    return supportModes;
  }
  public void setSupportModes(List<String> supportModes) { supportModes_ = supportModes; }

  
  /**
   * Tells, according to the info located in portlet.xml, wether this portlet can handle
   * a portlet event with the QName given as the method argument
   */
  public boolean supportsProcessingEvent(QName name) {
	  if(supportedProcessingEvents_ == null) {
      PortletContainerService portletContainer =  getApplicationComponent(PortletContainerService.class);
      String  portletId = exoWindowId_.getPortletApplicationName() + Constants.PORTLET_META_DATA_ENCODER + exoWindowId_.getPortletName();   
      PortletData portletData = (PortletData) portletContainer.getAllPortletMetaData().get(portletId);
      supportedProcessingEvents_ = portletData.getSupportedProcessingEvent();
	  }
	  if(supportedProcessingEvents_ == null) return false;
	  for (Iterator iter = supportedProcessingEvents_.iterator(); iter.hasNext();) {
	    QName eventName = (QName) iter.next();
	    if(eventName.equals(name)) {
		    log.info("The Portlet " + windowId + " supports the event : " + name);
		    return true;
	    }
	  }
	  return false;
  }
  
  /**
   * Tells, according to the info located in portlet.xml, wether this portlet supports the public
   * render parameter given as a method argument
   */
  public boolean supportsPublicParam(String supportedPublicParam) {
	  if(supportedPublicParams_ == null) {
      PortletContainerService portletContainer =  getApplicationComponent(PortletContainerService.class);
      String  portletId = exoWindowId_.getPortletApplicationName() + Constants.PORTLET_META_DATA_ENCODER + exoWindowId_.getPortletName();   
      PortletData portletData = (PortletData) portletContainer.getAllPortletMetaData().get(portletId);
      supportedPublicParams_ = portletData.getSupportedPublicRenderParameter();
	  }	
	  if(supportedPublicParams_ == null) return false;
	  for (Iterator iter = supportedPublicParams_.iterator(); iter.hasNext();) {
	    String publicParam = (String) iter.next();
	    if(publicParam.equals(supportedPublicParam)) {
        if(log.isDebugEnabled())
		      log.debug("The Portlet " + windowId + " supports the public render parameter : " + supportedPublicParam);
		    return true;
	    }
	  }	
	  return false;
  }
  
}
