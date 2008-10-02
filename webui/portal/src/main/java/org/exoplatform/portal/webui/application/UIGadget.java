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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortalRequestContext;
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
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 06, 2008   
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/application/UIGadget.gtmpl",
    events = {@EventConfig(listeners = UIGadget.SaveUserPrefActionListener.class)}
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
  private String metadata_;
  
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
    if(metadata_ == null) {
      metadata_ = getMetadata(getUrl());
    }
    return metadata_;
  }

  /**
   * Gets metadata of gadget application
   * @return string represents metadata of gaget application
   * @exception throws IOException when can't create the http connection or streaming 
   */
  static public String getMetadata(String url) {
    String data = null;
    try {
      data = fetchGagdetMetadata(url);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    if(data == null){
      data = "{}";
    }
    return data;
  }

  /**
   * Fetchs Metatada of gadget application, create the connection to shindig server to get the metadata
   * @return the string represents metadata of gadget application
   * @throws IOException if can't crate the connection to shindig server or from streaming
   */
  static private String fetchGagdetMetadata(String urlStr) throws IOException {
/*    JSONArray gadgets = new JSONArray()
        .put(createGadget(url_, 0, null));

    JSONObject input = new JSONObject()
        .put("context", createContext("en", "US"))
        .put("gadgets", gadgets);*/
    String data = "{\"context\":{\"country\":\"US\",\"language\":\"en\"},\"gadgets\":[" +
        "{\"moduleId\":0,\"url\":\"" + urlStr + "\",\"prefs\":[]}]}";
    // Send data
    URL url = new URL("http://localhost:8080/eXoGadgetServer/gadgets/metadata");
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(data);
    wr.flush();
    // Get the response
    String result = IOUtils.toString(conn.getInputStream(), "UTF-8");
    wr.close();
    return result;
  }
  /**
   * Gets map metadata of gadget application
   * @return  map metadata of gadget application so can get value of metadata by it's key
   *          such as title, url
   * @throws JSONException if can't create jsonObject from metadata
   */
  @SuppressWarnings("unchecked")
  static public Map<String, String> getMapMetadata(String url) throws JSONException {
    Map<String, String> mapMetaData = new HashMap<String, String>();
    String metadata = getMetadata(url);
    metadata = metadata.substring(metadata.indexOf("[")+1,metadata.lastIndexOf("]"));
    JSONObject jsonObj = new JSONObject(metadata);
    Iterator<String> iter = jsonObj.keys();
    while (iter.hasNext()) {
      String element = iter.next();
      mapMetaData.put(element, jsonObj.get(element).toString());
    }
    return mapMetaData;
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
      GadgetRegistryService gadgetServcie = getApplicationComponent(GadgetRegistryService.class);
      Gadget model;
      try{ model = gadgetServcie.getGadget(applicationName_); }
      catch(Exception ex) { return null; }
      application = Util.toGadgetApplication(model);
      webController.addApplication(application);
    }
    return application;
  }

  /**
   * Gets Url of gadget application, it saved before by GadgetRegistryService
   * @return url of gadget application, such as "http://www.google.com/ig/modules/horoscope.xml"
   */
  public String getUrl() {
    GadgetApplication application = getApplication();
    return getUrl(application.getUrl(), application.isLocal());
  }
  
  static public String getUrl(String path, boolean isLocal) {
    if(isLocal) {
      PortalRequestContext pContext = Util.getPortalRequestContext() ;
      StringBuffer requestUrl = pContext.getRequest().getRequestURL() ;
      int index = requestUrl.indexOf(pContext.getRequestContextPath()) ;
      return requestUrl.substring(0, index) + "/" + path;

    }
    return path ;    
  }

  /**
   * Gets user preference of gadget application
   * @return the string represents user preference of gadget application
   * @throws Exception 
   * @throws Exception when can't convert object to string
   */
  public String getUserPref() throws Exception {
    byte[] bytes = null;
    UserGadgetStorage userGadgetStorage = (UserGadgetStorage) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(UserGadgetStorage.class);
    bytes = (byte[])userGadgetStorage.get(Util.getPortalRequestContext().getRemoteUser(), getApplicationName(), getApplicationInstanceUniqueId());
    if(bytes == null) return null;
    else return new String(bytes);
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
        userGadgetStorage.save(userName, uiGadget.getApplicationName(), uiGadget.getApplicationInstanceUniqueId(), userPref);
      }
    }
  }
}