/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.gadget.core.SecurityTokenGenerator;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by The eXo Platform SAS Author : Pham Thanh Tung
 * thanhtungty@gmail.com Oct 2, 2008
 */
public class GadgetUtil {
  static final public GadgetApplication toGadgetApplication(Gadget model) {
    return new GadgetApplication(model.getName(), model.getUrl(), model.isLocal());
  }

  static final public Gadget toGadget(String name, String path, boolean isLocal) throws Exception {
    Gadget gadget = new Gadget();
    gadget.setName(name);
    gadget.setUrl(path);
    gadget.setLocal(isLocal);
    Map<String, String> metaData = getMapMetadata(reproduceUrl(path, isLocal));
    if (metaData.containsKey("errors"))
      throw new Exception("error on the server: " + metaData.get("errors"));
    String title = metaData.get("directoryTitle");
    if (title == null || title.trim().length() < 1)
      title = metaData.get("title");
    if (title == null || title.trim().length() < 1)
      title = gadget.getName();
    gadget.setTitle(title);
    gadget.setDescription(metaData.get("description"));
    gadget.setReferenceUrl(metaData.get("titleUrl"));
    gadget.setThumbnail(metaData.get("thumbnail"));
    return gadget;
  }

  /**
   * Fetchs Metatada of gadget application, create the connection to shindig
   * server to get the metadata TODO cache the informations for better
   * performance
   * 
   * @return the string represents metadata of gadget application
   */
  static final public String fetchGagdetMetadata(String urlStr) {
    String result = null;

    ExoContainer container = ExoContainerContext.getCurrentContainer();
    GadgetRegistryService gadgetService = (GadgetRegistryService) container.getComponentInstanceOfType(GadgetRegistryService.class);
    try {
      String data = "{\"context\":{\"country\":\"" + gadgetService.getCountry()
          + "\",\"language\":\"" + gadgetService.getLanguage() + "\"},\"gadgets\":["
          + "{\"moduleId\":" + gadgetService.getModuleId() + ",\"url\":\"" + urlStr
          + "\",\"prefs\":[]}]}";
      // Send data
      URL url = new URL(getHostBase() + "/eXoGadgetServer/gadgets/metadata");
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(data);
      wr.flush();
      // Get the response
      result = IOUtils.toString(conn.getInputStream(), "UTF-8");
      wr.close();
    } catch (IOException ioexc) {
      return "{}";
    }
    return result;
  }

  public static String createToken(String gadgetURL, Long moduleId) {
  	SecurityTokenGenerator tokenGenerator = (SecurityTokenGenerator) ExoContainerContext.getCurrentContainer().
  	getComponentInstanceOfType(SecurityTokenGenerator.class);
    return tokenGenerator.createToken(gadgetURL, moduleId);
  }

  /**
   * Gets map metadata of gadget application
   * 
   * @return map metadata of gadget application so can get value of metadata by
   *         it's key such as title, url
   * @throws JSONException if can't create jsonObject from metadata
   */
  @SuppressWarnings("unchecked")
  static final public Map<String, String> getMapMetadata(String url) throws JSONException {
    Map<String, String> mapMetaData = new HashMap<String, String>();
    String metadata = fetchGagdetMetadata(url);
    metadata = metadata.substring(metadata.indexOf("[") + 1, metadata.lastIndexOf("]"));
    JSONObject jsonObj = new JSONObject(metadata);
    Iterator<String> iter = jsonObj.keys();
    while (iter.hasNext()) {
      String element = iter.next();
      mapMetaData.put(element, jsonObj.get(element).toString());
    }
    return mapMetaData;
  }

  static final public String reproduceUrl(String path, boolean isLocal) {
    if (isLocal) {
      return getViewPath(path);
    }
    return path;
  }

  static final public String getViewPath(String uri) {
    return getHostBase() + "/rest/" + uri;
  }

  static final public String getEditPath(String uri) {
    return getHostBase() + "/rest/private/" + uri;
  }
  
  static private String getHostBase() {
    String hostName = getHostName();
    URL url = null;
    try {
       url = new URL(hostName);
    } catch (Exception e) {}
    if(url == null) return hostName ;
    int index = hostName.indexOf(url.getPath()) ;
    if(index < 1) return hostName ;
    return hostName.substring(0, index) ;
  }

  static final private String getHostName() {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    GadgetRegistryService gadgetService = (GadgetRegistryService) container.getComponentInstanceOfType(GadgetRegistryService.class);
    return gadgetService.getHostName();
  }
}
