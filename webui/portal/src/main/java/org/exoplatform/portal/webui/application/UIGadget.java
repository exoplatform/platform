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

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.UserGadgetStorage;
import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 06, 2008   
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/application/UIGadget.gtmpl",
    events = {
        @EventConfig(listeners = UIGadget.SaveUserPrefActionListener.class),
        @EventConfig(listeners = UIGadget.SetNoCacheActionListener.class),
        @EventConfig(listeners = UIGadget.SetDebugActionListener.class)
    }
)
/**
 * This class represents user interface gadgets, it using UIGadget.gtmpl for rendering
 * UI in eXo. It mapped to Application model in page or container.
 */
public class UIGadget extends UIComponent {
  
  private String applicationInstanceId_ ;
  private String applicationOwnerType_ ;
  private String applicationOwnerId_ ;
  private String applicationGroup_ ;
  private String applicationName_ ;
  private String applicationInstanceUniqueId_ ;
  private String applicationId_ ;
  private Properties properties_;
  private JSONObject metadata_;
  private String url_;
  private UserGadgetStorage userGadgetStorage = null;
  private GadgetRegistryService gadgetRegistryService  = null;
  public static final String PREF_KEY = "_pref_gadget_";
  public static final String PREF_NO_CACHE = "_pref_no_cache_";
  public static final String PREF_DEBUG = "_pref_debug_";
  public static final String HOME_VIEW = "home";
  public static final String CANVAS_VIEW = "canvas";
  public String view = HOME_VIEW;

  /**
   * Initializes a newly created <code>UIGadget</code> object
   * @throws Exception if can't initialize object
   */
  public UIGadget() throws Exception {
  }
  
  /**
   * Gets instance id of gadget application
   * @return the string represents instance id of gadget application
   */
  public String getApplicationInstanceId() { return applicationInstanceId_ ; }
  
  /**
   * Sets instance id of gadget application
   * @param s an string that is the instance id of gadget application 
   */
  public void setApplicationInstanceId(String s) {  
    applicationInstanceId_ = s ;
    String[]  tmp =  applicationInstanceId_.split("/") ;
    applicationGroup_ = tmp[1] ;
    applicationName_ = tmp[2] ;
    applicationId_ =  applicationGroup_ + "/" + applicationName_ ;
    applicationInstanceUniqueId_ = tmp[3] ;
  }
  
  /**
   * Gets owner type of gadget application
   * @return the string represents owner type of gadget application
   */
  public String getApplicationOwnerType() { return applicationOwnerType_ ;}
  
  /**
   * Sets owner type of gadget application
   * @param ownerType an string that is the owner type of gadget application
   */
  public void setApplicationOwnerType(String ownerType){ applicationOwnerType_ = ownerType;}
  
  /**
   * Gets owner id of gadget application
   * @return the string represents owner id of gadget application
   */
  public String getApplicationOwnerId() { return applicationOwnerId_ ;}
  
  /**
   * Sets owner id of gadget application
   * @param ownerId an string that is the owner id of gadget application
   */
  public void setApplicationOwnerId(String ownerId){ applicationOwnerId_ = ownerId;} 
  
  /**
   * Gets group of gadget application such as eXoGadgets...
   * @return the string represents group of gadget application
   */
  public String getApplicationGroup() { return applicationGroup_ ;}
  
  /**
   * Sets group of gadget application
   * @param group an string that is the group of gadget application
   */
  public void setApplicationGroup(String group){ applicationGroup_ = group;}
  
  /**
   * Gets name of gadget application
   * @return the string represents name of gadget application
   */
  public String getApplicationName() { return applicationName_ ;}
  
  /**
   * Sets name of gadget application
   * @param name an string that is the name of gadget application
   */
  public void setApplicationName(String name) { applicationName_ = name;}
  
  /**
   * Gets Id of gadget application
   * @return gadget application's id
   */
  public String getApplicationId() { return applicationId_ ; }
 
  /**
   * Gets Unique id of instance gadget application
   * @return Id of instance gadget application
   */
  public String getApplicationInstanceUniqueId() { return applicationInstanceUniqueId_ ;}
  
  /**
   * Gets Properties of gadget application such as locationX, locationY in desktop page
   * @return all properties of gadget application
   * @see org.exoplatform.portal.config.model.Application
   * @see org.exoplatform.portal.config.model.Properties
   */
  public Properties getProperties() {
    if(properties_ == null) properties_  = new Properties();
    return properties_; 
  }
  
  /**
   * Sets Properties of gadget application such as locationX, locationY in desktop page
   * @param properties Properties that is the properties of gadget application
   * @see org.exoplatform.portal.config.model.Properties
   * @see org.exoplatform.portal.config.model.Application
   */
  public void setProperties(Properties properties) { this.properties_ = properties; }
  
  public String getMetadata() {
    try {
      if(metadata_ == null) {
        String strMetadata = GadgetUtil.fetchGagdetMetadata(getUrl());
        metadata_ = new JSONObject(strMetadata);
      }
      JSONObject obj = metadata_.getJSONArray("gadgets").getJSONObject(0);
      String token = GadgetUtil.createToken(this.getUrl(), new Long(this.getApplicationInstanceId().hashCode()));
      obj.put("secureToken", token);
      return metadata_.toString();
    } catch (JSONException e) {
      return null;
    }
  }

  /**
   * Gets GadgetApplication by GadgedRegistryService
   * @return Gadget Application 
   * @throws Exception 
   */
  private GadgetApplication getApplication() {
    WebAppController webController = getApplicationComponent(WebAppController.class);
    GadgetApplication application = webController.getApplication(applicationId_);
    if(application == null) {
      GadgetRegistryService gadgetService = getApplicationComponent(GadgetRegistryService.class);
      Gadget model;
      try{
        model = gadgetService.getGadget(applicationName_); 
      }
      catch(Exception ex) {
        return null; 
      }
      application = GadgetUtil.toGadgetApplication(model);
      webController.addApplication(application);
    }
    return application;
  }

  /**
   * Gets Url of gadget application, it saved before by GadgetRegistryService
   * @return url of gadget application, such as "http://www.google.com/ig/modules/horoscope.xml"
   */
  public String getUrl() {
    if(url_ == null) {
      GadgetApplication application = getApplication();
      url_ =  GadgetUtil.reproduceUrl(application.getUrl(), application.isLocal());
    }
    return url_;
  }

  private UserGadgetStorage getGadgetStorage() {
    if(userGadgetStorage == null)
      userGadgetStorage = (UserGadgetStorage) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(UserGadgetStorage.class);
    return userGadgetStorage;
  }

  private GadgetRegistryService getGadgetRegistryService() {
    if(gadgetRegistryService == null)
      gadgetRegistryService = (GadgetRegistryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(GadgetRegistryService.class);
    return gadgetRegistryService;
  }

  public boolean isNoCache() {
    try {
      UserGadgetStorage userGadgetStorage = getGadgetStorage();
      String username = Util.getPortalRequestContext().getRemoteUser();
      if(username != null) {
        String prefs = userGadgetStorage.get(username, getApplicationName(), getApplicationInstanceUniqueId(), PREF_NO_CACHE);
        return prefs.equals("1");
      }
    } catch (Exception e) {}
    return false;
  }

  public void setNoCache(boolean value) {
   try {
      UserGadgetStorage userGadgetStorage = getGadgetStorage();
      String username = Util.getPortalRequestContext().getRemoteUser();
      if(username != null && getGadgetRegistryService().isGadgetDeveloper(username)) {
        userGadgetStorage.save(username, getApplicationName(), getApplicationInstanceUniqueId(), PREF_NO_CACHE, value ? "1" : "0");
      }
    } catch (Exception e) {}
  }

  public boolean isDebug() {
    try {
      UserGadgetStorage userGadgetStorage = getGadgetStorage();
      String username = Util.getPortalRequestContext().getRemoteUser();
      if(username != null) {
        String prefs = userGadgetStorage.get(username, getApplicationName(), getApplicationInstanceUniqueId(), PREF_DEBUG);
        return prefs.equals("1");
      }
    } catch (Exception e) {}
    return false;
  }

  public void setDebug(boolean value) {
   try {
      UserGadgetStorage userGadgetStorage = getGadgetStorage();
      String username = Util.getPortalRequestContext().getRemoteUser();
      if(username != null && getGadgetRegistryService().isGadgetDeveloper(username)) {
        userGadgetStorage.save(username, getApplicationName(), getApplicationInstanceUniqueId(), PREF_DEBUG, value ? "1" : "0");
      }
    } catch (Exception e) {}
  }

  public boolean isGadgetDeveloper() {
    return getGadgetRegistryService().isGadgetDeveloper(Util.getPortalRequestContext().getRemoteUser());
  }

  public String getView() {
    if (view != null)
      return view;
    return HOME_VIEW;
  }

  public void setView(String view) {
    this.view = view;
  }

  /**
   * Gets user preference of gadget application
   * @return the string represents user preference of gadget application
   * @throws Exception 
   * @throws Exception when can't convert object to string
   */
  public String getUserPref() throws Exception {
    String prefs = null;
    UserGadgetStorage userGadgetStorage = getGadgetStorage();
    if(Util.getPortalRequestContext().getRemoteUser() != null) {
      prefs = userGadgetStorage.get(Util.getPortalRequestContext().getRemoteUser(), getApplicationName(), getApplicationInstanceUniqueId(), PREF_KEY);
    }
    return prefs;
  }

  /**
   * Initializes a newly created <code>SaveUserPrefActionListener</code> object
   * @throws Exception if can't initialize object
   */
  static public class SaveUserPrefActionListener extends EventListener<UIGadget> {
    public void execute(Event<UIGadget> event) throws Exception {
      String userPref = event.getRequestContext().getRequestParameter("userPref") ;
      UIGadget uiGadget = event.getSource() ;
      String userName = event.getRequestContext().getRemoteUser();
      UserGadgetStorage userGadgetStorage = uiGadget.getApplicationComponent(UserGadgetStorage.class);
      if(userName != null && userName.trim().length()>0) {
        userGadgetStorage.save(userName, uiGadget.getApplicationName(), uiGadget.getApplicationInstanceUniqueId(), PREF_KEY, userPref);
      }
    }
  }

  static public class SetNoCacheActionListener extends EventListener<UIGadget> {
    public void execute(Event<UIGadget> event) throws Exception {
      String noCache = event.getRequestContext().getRequestParameter("nocache") ;
      UIGadget uiGadget = event.getSource() ;
      uiGadget.setNoCache(noCache.equals("1"));
    }
  }

  static public class SetDebugActionListener extends EventListener<UIGadget> {
    public void execute(Event<UIGadget> event) throws Exception {
      String debug = event.getRequestContext().getRequestParameter("debug") ;
      UIGadget uiGadget = event.getSource() ;
      uiGadget.setDebug(debug.equals("1"));
    }
  }
}