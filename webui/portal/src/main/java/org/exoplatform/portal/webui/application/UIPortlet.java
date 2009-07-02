/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.exoplatform.services.log.Log;
import org.exoplatform.Constants;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ChangePortletModeActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ChangeWindowStateActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.EditPortletActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ProcessActionActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ProcessEventsActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.RenderActionListener;
import org.exoplatform.portal.webui.application.UIPortletActionListener.ServeResourceActionListener;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
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
  private String theme_ ;
  static final public String DEFAULT_THEME = "Default:DefaultTheme::Vista:VistaTheme::Mac:MacTheme" ;
  private String windowId ;
  private String portletStyle ;

  private boolean  showPortletMode = true ;
  
  private Map<String, String[]> renderParametersMap_ ;
  private ExoWindowID exoWindowId_ ;
  private PortletMode currentPortletMode_ = PortletMode.VIEW;
  private WindowState currentWindowState_ = WindowState.NORMAL;  
  
  private List<String> supportModes_ ;

  private List<QName> supportedProcessingEvents_;
  private List<String> supportedPublicParams_;
  private boolean portletInPortal_ = true;  
  
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
  
  public void setPortletInPortal(boolean b) {
    portletInPortal_  = b;
  }
  public boolean isPortletInPortal() { return portletInPortal_; }  
  
  public String getTheme() {
    if(theme_ == null || theme_.trim().length() < 1) return DEFAULT_THEME ;
    return theme_ ;
  }

  public void setTheme(String theme) {
    theme_ = theme ;
  }

  public String getSuitedTheme(String skin) {
    if(skin == null) {
      skin = getAncestorOfType(UIPortalApplication.class).getSkin() ;
    }   
    Map<String, String> themeMap = stringToThemeMap(getTheme()) ;
    if(themeMap.containsKey(skin)) {
      return themeMap.get(skin) ;
    }
    return  DEFAULT_THEME.split(":")[1] ;
  }

  public void putSuitedTheme(String skin, String theme) {
    if(skin == null) {
      skin = getAncestorOfType(UIPortalApplication.class).getSkin() ;
    }
    Map<String, String> themeMap = stringToThemeMap(getTheme()) ;
    themeMap.put(skin, theme) ;
    setTheme(themeMapToString(themeMap)) ;
  }
  
  private String themeMapToString(Map<String, String> themeMap) {
    StringBuffer builder = new StringBuffer() ;
    Iterator<Entry<String, String>> itr = themeMap.entrySet().iterator() ;
    while(itr.hasNext()) {
      Entry<String, String> entry = itr.next() ; 
      builder.append(entry.getKey()).append(":").append(entry.getValue()) ;
      if(itr.hasNext()) builder.append("::") ;
    }
    return builder.toString() ;
  }
  
  private Map<String, String> stringToThemeMap(String themesString) {
    Map<String, String> themeMap = new HashMap<String, String>() ;
    String[] themeIds = themesString.split("::") ;
    for(String ele : themeIds) {
      String[] strs = ele.split(":");
      themeMap.put(strs[0], strs[1]) ;
    }
    return themeMap ;
  }
  
  public ExoWindowID  getExoWindowID() { return exoWindowId_ ; }
  
  public  Map<String, String[]>  getRenderParametersMap() { return renderParametersMap_ ;}
  public  void setRenderParametersMap(Map<String, String[]> map) { renderParametersMap_ =  map ; } 
  
  public PortletMode getCurrentPortletMode() { return currentPortletMode_ ;}
  public void  setCurrentPortletMode(PortletMode mode) { currentPortletMode_ = mode;}
  
  public WindowState getCurrentWindowState() { return currentWindowState_ ;}
  public void  setCurrentWindowState(WindowState state) { currentWindowState_ = state;}
  
  public List<QName> getSupportedProcessingEvents() { return supportedProcessingEvents_; }
  public void setSupportedProcessingEvents(List<QName> supportedProcessingEvents) {
    supportedProcessingEvents_ = supportedProcessingEvents;
  }
  
  public List<String> getSupportedPublicRenderParameters() { return supportedPublicParams_; }
  public void setSupportedPublicRenderParameters(List<String> supportedPublicRenderParameters) {
    supportedPublicParams_ = supportedPublicRenderParameters;
  }
  
  public  List<String> getSupportModes() { 
    if (supportModes_ != null) return supportModes_;
    PortletContainerService portletContainer =  getApplicationComponent(PortletContainerService.class);
    String  portletId = exoWindowId_.getPortletApplicationName() + Constants.PORTLET_META_DATA_ENCODER + exoWindowId_.getPortletName();   
    PortletData portletData = portletContainer.getAllPortletMetaData().get(portletId);
    List<String> supportModes = new ArrayList<String>() ;
    if(portletData == null) return supportModes ;
    List<Supports> sukepportsList = portletData.getSupports() ;
    for (int i = 0; i < sukepportsList.size(); i++) {
      Supports supports = sukepportsList.get(i) ;
      String mimeType = supports.getMimeType() ;
      if ("text/html".equals(mimeType)) {
        List<String> modes = supports.getPortletMode() ;
        for (int j =0 ; j < modes.size() ; j++) {
          supportModes.add(modes.get(j).toLowerCase()) ;
        }
        break ;
      }
    }
    if(supportModes.size() > 0) supportModes.remove("view");
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
      PortletData portletData = portletContainer.getAllPortletMetaData().get(portletId);
      supportedProcessingEvents_ = portletData.getSupportedProcessingEvent();
	  }
	  if(supportedProcessingEvents_ == null) return false;
	  for (Iterator<QName> iter = supportedProcessingEvents_.iterator(); iter.hasNext();) {
	    QName eventName = iter.next();
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
      PortletData portletData = portletContainer.getAllPortletMetaData().get(portletId);
      supportedPublicParams_ = portletData.getSupportedPublicRenderParameter();
	  }	
	  if(supportedPublicParams_ == null) return false;
	  for (Iterator<String> iter = supportedPublicParams_.iterator(); iter.hasNext();) {
	    String publicParam = iter.next();
	    if(publicParam.equals(supportedPublicParam)) {
        if(log.isDebugEnabled())
		      log.debug("The Portlet " + windowId + " supports the public render parameter : " + supportedPublicParam);
		    return true;
	    }
	  }	
	  return false;
  }
  
  /**
   * This methods return the public render parameters names supported
   * by the targeted portlet; in other words, it sorts the full public
   * render params list and only return the ones that the current portlet
   * can handle
   */
  public List<String> getPublicRenderParamNames() {
    UIPortal uiPortal = Util.getUIPortal();
    Map<String, String[]> publicParams = uiPortal.getPublicParameters();

    List<String> publicParamsSupportedByPortlet = new ArrayList<String>();
    if (publicParams != null) {
      Set<String> keys = publicParams.keySet();
      for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
        String key = iter.next();
        if (supportsPublicParam(key)) {
          publicParamsSupportedByPortlet.add(key);
        }
      }
      return publicParamsSupportedByPortlet;
    }
    return new ArrayList<String>();
  }  
  
  public Map<String, String[]> getPublicParameters() {
    Map<String, String[]> publicParamsMap = new HashMap<String, String[]>();
    UIPortal uiPortal = Util.getUIPortal();
    Map<String, String[]> publicParams = uiPortal.getPublicParameters();
    Set<String> allPublicParamsNames = publicParams.keySet();       
    List<String> supportedPublicParamNames = getPublicRenderParamNames();
    for (Iterator<String> iter = allPublicParamsNames.iterator(); iter.hasNext();) {
      String oneOfAllParams = iter.next();
      if(supportedPublicParamNames.contains(oneOfAllParams))
        publicParamsMap.put(oneOfAllParams, publicParams.get(oneOfAllParams));
    }    
    return publicParamsMap;   
  }
  
  
}
