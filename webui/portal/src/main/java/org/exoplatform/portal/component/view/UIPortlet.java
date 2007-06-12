/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.exoplatform.portal.component.view.lifecycle.UIPortletLifecycle;
import org.exoplatform.portal.component.view.listener.UIPortletActionListener.ChangePortletModeActionListener;
import org.exoplatform.portal.component.view.listener.UIPortletActionListener.ChangeWindowStateActionListener;
import org.exoplatform.portal.component.view.listener.UIPortletActionListener.EditPortletActionListener;
import org.exoplatform.portal.component.view.listener.UIPortletActionListener.ProcessActionActionListener;
import org.exoplatform.portal.component.view.listener.UIPortletActionListener.RenderActionListener;
import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.portal.webui.portal.UIPortalComponent;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.services.portletcontainer.pci.model.Supports;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 19, 2006
 */
@ComponentConfig(
    lifecycle = UIPortletLifecycle.class,    
    template = "system:/groovy/portal/webui/component/view/UIPortlet.gtmpl",
    events = {
      @EventConfig(listeners = RenderActionListener.class),
      @EventConfig(listeners = ChangePortletModeActionListener.class),
      @EventConfig(listeners = ChangeWindowStateActionListener.class),
      @EventConfig(listeners = DeleteComponentActionListener.class, confirm = "UIPortlet.deletePortlet"),
      @EventConfig(listeners = EditPortletActionListener.class),
      @EventConfig(phase = Phase.PROCESS, listeners = ProcessActionActionListener.class)
    }    
)
public class UIPortlet extends UIPortalComponent { 
  
  private String windowId ;
  private String portletStyle ;

  private boolean  showInfoBar = true ;
  private boolean  showWindowState = true ;
  private boolean  showPortletMode = true ;
  private String   description;
  private String   icon;
  
  private Map renderParametersMap_ ;
  private ExoWindowID exoWindowId_ ;
  private PortletMode currentPortletMode_ = PortletMode.VIEW;
  private WindowState currentWindowState_ = WindowState.NORMAL;  
  
  private List<String> supportModes_ ;
  
  private Properties properties;
  
  public String getId()  { return exoWindowId_.getUniqueID() ; }
  
  public String getWindowId() { return windowId ; }
  public void   setWindowId(String s) {
    windowId = s ;
    exoWindowId_ = new ExoWindowID(windowId) ;
  }
  
  public String getPortletStyle() {  return  portletStyle ; }
  public void   setPortletStyle(String s) { portletStyle = s ;}
  
  public String getDescription() {  return  description ; }
  public void   setDescription(String s) { description = s ;}
  
  public boolean getShowInfoBar() { return showInfoBar ; }
  public void    setShowInfoBar(Boolean b) {showInfoBar = b ;}
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; } 
  
  public boolean getShowWindowState() { return showWindowState ; }
  public void    setShowWindowState(Boolean b) { showWindowState = b ; }
  
  public boolean getShowPortletMode() { return showPortletMode ; }
  public void    setShowPortletMode(Boolean b) { showPortletMode = b ; }
  
  public ExoWindowID  getExoWindowID() { return exoWindowId_ ; }
  
  public  Map  getRenderParametersMap() { return renderParametersMap_ ;}
  public  void setRenderParametersMap(Map map) { renderParametersMap_ =  map ; } 
  
  public PortletMode getCurrentPortletMode() { return currentPortletMode_ ;}
  public void  setCurrentPortletMode(PortletMode mode) { currentPortletMode_ = mode;}
  
  public WindowState getCurrentWindowState() { return currentWindowState_ ;}
  public void  setCurrentWindowState(WindowState state) { currentWindowState_ = state;}

  public  List<String> getSupportModes() { 
    if (supportModes_ != null) return supportModes_;
    PortletContainerService portletContainer =  getApplicationComponent(PortletContainerService.class);
    String  portletId = exoWindowId_.getPortletApplicationName() + "/" + exoWindowId_.getPortletName();   
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
  
  public Properties getProperties() {
    if(properties == null) properties  = new Properties();
    return properties; 
  }
  public void setProperties(Properties properties) { this.properties = properties; }
  
}