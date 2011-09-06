/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.calendar.client.JSO;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.calendar.client.CalendarGadget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gadgets.client.io.AuthorizationType;
import com.google.gwt.gadgets.client.io.GadgetsIo;
import com.google.gwt.gadgets.client.io.IoProvider;
import com.google.gwt.gadgets.client.io.MethodType;
import com.google.gwt.gadgets.client.io.RequestOptions;
import com.google.gwt.gadgets.client.io.Response;
import com.google.gwt.gadgets.client.io.ResponseReceivedHandler;
import com.google.gwt.json.client.JSONObject;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class RestRequestController {
  public static String REST_BASE_URL = "http://localhost:8080/rest";
  
  private RestRequestController() {}
  
  private static RestRequestController instance;
  
  static {
    instance = new RestRequestController();
  }
  
  public static RestRequestController instance() {
    return instance;
  }
  
  public void makeGetJsonRequest(String relPath, ResponseReceivedHandler<? extends JavaScriptObject> responseReceivedHandler) {
    GadgetsIo io = IoProvider.get();
    RequestOptions options = RequestOptions.newInstance();
    options.setAuthorizationType(AuthorizationType.SIGNED);
    options.setMethodType(MethodType.GET);
    io.makeRequestAsJso(REST_BASE_URL + relPath, responseReceivedHandler, options);
  }
  
  public void makePostJsonRequest(String relPath, JSONObject postdata, ResponseReceivedHandler<? extends JavaScriptObject> responseReceivedEvent) {
    GadgetsIo io = IoProvider.get();
    RequestOptions options = RequestOptions.newInstance();
    options.setAuthorizationType(AuthorizationType.SIGNED);
    options.setMethodType(MethodType.POST);
    if (postdata != null) options.setPostData(io.encodeValues(postdata.getJavaScriptObject()));
    io.makeRequestAsJso(REST_BASE_URL + relPath, responseReceivedEvent, options);
  }
  
  @Deprecated
  public List<Calendar> getPersonalCalendars() {
    String relPath = "/calgad/calendars/personal";
    final List<Calendar> calendars = new ArrayList<Calendar>();
    final StringBuffer sb = new StringBuffer();
    ResponseReceivedHandler<JavaScriptObject> receivedHandler = new ResponseReceivedHandler<JavaScriptObject>() {
     
      public void onResponseReceived(com.google.gwt.gadgets.client.io.ResponseReceivedHandler.ResponseReceivedEvent<JavaScriptObject> event) {
        Response<JavaScriptObject> response = event.getResponse();
        if (response.getStatusCode() == 200) {
          ResponseData responseData = (ResponseData) event.getResponse().getData();
          JavaScriptObject[] list = responseData.getList();
          CalendarGadget.debug("start get calendars +++++++++++++++");
          if (list.length > 0) {
            for (JavaScriptObject e : list) {
              CalendarGadget.debug("calendar: " + ((Calendar) e).getId());
              calendars.add((Calendar) e);
            }
          }
        }
        sb.append(true);
      }
      
    };
    while (sb.length() == 0);
    makeGetJsonRequest(relPath, receivedHandler);
    
    
    return calendars;
  }
  
}
