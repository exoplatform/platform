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

import java.util.Locale;
import java.util.ResourceBundle;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.exoplatform.web.application.Application;

import org.apache.commons.io.IOUtils;


/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 * tungcnw@gmail.com
 * May 06, 2008
 */
public class GadgetApplication extends Application {
  private String appId_;
  private String url_;
  private String metadata = null;

  public GadgetApplication(String appId, String url) {
    appId_ = appId;
    url_ = url;
  }

  public String getApplicationType() {
    return "eXoGadget";
  }

  public String getApplicationGroup() {
    return "eXoGadgets";
  }

  public String getUrl() {
    return url_;
  }

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

  public String getApplicationId() {
    return appId_;
  }

  public String getApplicationName() {
    return appId_;
  }

  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    return null;
  }

  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    return null;
  }


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
}
