/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.portlet.juzu.branding;

import juzu.*;
import juzu.request.ApplicationContext;
import juzu.request.HttpContext;
import juzu.request.UserContext;
import juzu.template.Template;
import org.apache.commons.fileupload.FileItem;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.portlet.juzu.branding.models.BrandingDataStorageService;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.json.JSONException;
import org.json.JSONObject;
import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by The eXo Platform SAS Author : Nguyen Viet Bang
 * bangnv@exoplatform.com Jan 28, 2013
 */

public class BrandingController {
	
  private static final Log LOG               = ExoLogger.getExoLogger(BrandingController.class);

  public static String       BAR_NAVIGATION_STYLE_KEY = "bar_navigation_style";

  public static String       fileName;

  public static String       style                    = "";

  @Inject
  @Path("index.gtmpl")
  Template                   index;

  @Inject
  SettingService             settingService;

  @Inject
  BrandingDataStorageService dataStorageService;

  /**
   * method save() records an image in BrandingDataStorageService
   * 
   * @param httpContext
   * @param file
   * @return Response.Content
   * @throws IOException
   */
  @Resource
  public Response.Content uploadFile(HttpContext httpContext, FileItem file, String browser) throws IOException {
    if (browser != null && browser.equals("html5")) {
      if (file != null && file.getContentType().contains("png")) {
        dataStorageService.saveLogoPreview(file);
      }
      JSONObject result = new JSONObject();
      try {
        result.put("logoUrl", getLogoUrl(httpContext, false));
      } catch (JSONException ex) {

      }
      return Response.ok(result.toString()).with(PropertyType.MIME_TYPE, "application/json");
    } else {
      if (file != null && file.getContentType().contains("png")) {
        dataStorageService.saveLogoPreview(file);
        return Response.ok(getLogoUrl(httpContext, false));
      } else {
        return Response.ok("false");
      }
    }
  }

  /**
   * The controller method index() is the name of the default method that Juzu
   * will call. set localization and put into parameters
   * 
   * @param httpContext
   * @return Response
   */
  @View
  public Response index(HttpContext httpContext) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("urlUploadFile", BrandingController_.uploadFile(null));
    parameters.put("imageUrl", getLogoUrl(httpContext, true));
    return index.ok(parameters);
  }

  /**
   * verify if the url of logo is available return true if exist, otherwise
   * return false
   * 
   * @param logoUrl
   * @return boolean
   */
  public boolean isExiste(String logoUrl) {
    int code;
    try {
      URL u = new URL(logoUrl);
      HttpURLConnection huc = (HttpURLConnection) u.openConnection();
      huc.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
      huc.connect();
      code = huc.getResponseCode();
    } catch (Exception e) {
      return false;
    }
    return code == 200;
  }

  /**
   * this method will be invoked when the user click on save
   * 
   * @param style style of navigation bar
   * @param isChangeLogo to know if the logo will be update from logo preview
   * @return
   */
  @Ajax
  @Resource
  public Response.Content save(String style, String isChangeLogo, HttpContext httpContext) {
    if (isAdmin()) {
      if (isChangeLogo != null && Boolean.valueOf(isChangeLogo)) {
        dataStorageService.saveLogo();
      }
      if (style != null && style != "") {
        settingService.set(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY, SettingValue.create(style));
      }
      return getResource(httpContext);
    } else {
      LOG.info("Cannot save branding due to insufficient permission.");
    }
    JSONObject result = new JSONObject();
    try {
      result.put("error", "1");
    } catch (JSONException ex) {

    }
    return Response.ok(result.toString()).with(PropertyType.MIME_TYPE, "application/json");
  }

  /**
   * get logo URL to String
   * 
   * @param httpContext
   * @return String
   */
  public String getLogoUrl(HttpContext httpContext, boolean isRealLogo) {
    String portalName = ExoContainerContext.getCurrentContainer()
                                           .getContext()
                                           .getPortalContainerName();
    String logoFolderUrl = httpContext.getScheme() + "://" + httpContext.getServerName() + ":"
        + httpContext.getServerPort() + "/" + portalName
        + "/rest/jcr/repository/collaboration/Application%20Data/logos/";
    String logoUrl = null;
    if (isRealLogo) {
      logoUrl = logoFolderUrl + BrandingDataStorageService.logo_name + "?"
          + System.currentTimeMillis();
    } else {
      logoUrl = logoFolderUrl + BrandingDataStorageService.logo_preview_name + "?"
          + System.currentTimeMillis();
    }
    if (!isExiste(logoUrl)) {
      logoUrl = "/eXoSkin/skin/images/themes/default/platform/skin/ToolbarContainer/HomeIcon.png";
    }
    return logoUrl;
  }

  /**
   * return the object data contains the url of logo and the bar navigation
   * 
   * @param httpContext
   * @return Resource
   */
  @Ajax
  @Resource
  public Response.Content getResource(HttpContext httpContext) {
    JSONObject json = new JSONObject();
    String style = "Dark";
    if (settingService.get(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY) != null) {
      style = (String) settingService.get(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY)
                                     .getValue();
    }

    try {
      json.put("error", "0");
      json.put("style", style);
      json.put("logoUrl", getLogoUrl(httpContext, true));
    } catch (JSONException ex) {

    }
    return Response.ok(json.toString()).with(PropertyType.MIME_TYPE, "application/json");
  }

  private boolean isAdmin() {
    try {
      UserACL userACL = (UserACL) ExoContainerContext.getCurrentContainer()
                .getComponentInstanceOfType(UserACL.class);
      if (userACL == null) return false;
      ConversationState state = ConversationState.getCurrent();
      if (state == null) return false;
      String userId = state.getIdentity().getUserId();
      if (userId == null) return false;
      if (userId.equalsIgnoreCase(userACL.getSuperUser()) ) {
        return true;
      }
      return state.getIdentity().isMemberOf(userACL.getAdminGroups());
    } catch (Exception e) {
      return false;
    }
  }
}
