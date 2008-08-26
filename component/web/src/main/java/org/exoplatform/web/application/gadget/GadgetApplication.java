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
package org.exoplatform.web.application.gadget;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.exoplatform.web.application.Application;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class extends from Application, it represents an gadget application in eXo and used for registry
 * Gadgets.
 */
public class GadgetApplication extends Application {
  private String appId_;
  private String url_;
  private String metadata = null;

  /**
   * Initializes a newly created <code>GadgetApplication</code> object
   * @param appId an String that is the application id of gadget application
   * @param url an String that is the the url of gadget application such as 
   *        "http://www.google.com/ig/modules/horoscope.xml"
   */
  
  public GadgetApplication(String url) throws Exception {
    url_ = url;
    appId_ = getMapMetadata().get("directoryTitle").replace(' ', '_') ;
  }

  public GadgetApplication(String appId, String url) {
    url_ = url;
    appId_ = appId;
  }
  
  /**
   * Gets gadget application type
   * @return org.exoplatform.web.application.Application.EXO_GAGGET_TYPE
   * @see org.exoplatform.web.application.Application
   */
  public String getApplicationType() {
    return "eXoGadget";
  }
  
  /**
   * Gets group of gadget application
   * @return alway returns "eXoGadgets"
   */
  public String getApplicationGroup() {
    return "eXoGadgets";
  }

  /**
   * Gets url of gadget application
   * @return string represents url of gadget application
   */
  public String getUrl() {
    return url_;
  }

  /**
   * Gets metadata of gadget application
   * @return string represents metadata of gaget application
   * @exception throws IOException when can't create the http connection or streaming 
   */
  public String getMetadata() {
    if(metadata == null)  {
      try {
        metadata = fetchGagdetMetadata();
      } catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
    if(metadata == null){
      return "{}";
    }
    return metadata;
  }

  /**
   * Gets id of gadget application
   * @return the string represents id of gadget application
   */
  public String getApplicationId() {
    return appId_;
  }

  /**
   * Gets name of gadget application
   * @return the string represents name of gadget application
   */
  public String getApplicationName() {
    return appId_;
  }

  /**
   * Gets owner resource bundle of gadget application
   * @param username remote username logged in portal
   * @param locale the location of logged user
   * @return always return null
   * @throws Exception if can't get the resource bundle by the ResouceBundleService
   */
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    return null;
  }

  /**
   * Gets resource bundle of gadget application
   * @param locale the location of logged user
   * @return always return null
   * @throws Exception if can't get the resource bundle by the ResourceBundleService
   */
  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    return null;
  }

  /**
   * Fetchs Metatada of gadget application, create the connection to shindig server to get the metadata
   * @return the string represents metadata of gadget application
   * @throws IOException if can't crate the connection to shindig server or from streaming
   */
  private String fetchGagdetMetadata() throws IOException {
/*    JSONArray gadgets = new JSONArray()
        .put(createGadget(url_, 0, null));

    JSONObject input = new JSONObject()
        .put("context", createContext("en", "US"))
        .put("gadgets", gadgets);*/
    String data = "{\"context\":{\"country\":\"US\",\"language\":\"en\"},\"gadgets\":[" +
        "{\"moduleId\":0,\"url\":\"" + url_ + "\",\"prefs\":[]}]}";
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
  public Map<String, String> getMapMetadata() throws JSONException {
    Map<String, String> mapMetaData = new HashMap<String, String>();
    String metadata = getMetadata();
    metadata = metadata.substring(metadata.indexOf("[")+1,metadata.lastIndexOf("]"));
    JSONObject jsonObj = new JSONObject(metadata);
    Iterator<String> iter = jsonObj.keys();
    while (iter.hasNext()) {
      String element = iter.next();
      mapMetaData.put(element, jsonObj.get(element).toString());
    }
    return mapMetaData;
  }
}
  
