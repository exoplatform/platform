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

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.io.Stream;
import juzu.request.HttpContext;
import juzu.request.RenderContext;
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

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
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
   * @param style
   * @return Response.Content
   * @throws IOException
   */
  @Resource
  public Response.Content uploadFile(HttpContext httpContext, FileItem file, String browser) throws IOException {
    if (browser != null && browser.equals("html5")) {
      if (file != null && file.getContentType().contains("png")) {
        dataStorageService.saveLogoPreview(file);
      }
      Map<String, String> result = new HashMap<String, String>();
      result.put("logoUrl", getLogoUrl(httpContext, false));
      return createJSON(result);
    } else {
      if (file != null && file.getContentType().contains("png")) {
        dataStorageService.saveLogoPreview(file);
        return createText(getLogoUrl(httpContext, false));
      } else {
        return createText("false");
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
  public Response index(HttpContext httpContext, RenderContext renderContext) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("urlUploadFile", BrandingController_.uploadFile(null));
    parameters.put("imageUrl", getLogoUrl(httpContext, true));
    ResourceBundle rs = renderContext.getApplicationContext().resolveBundle(renderContext.getUserContext().getLocale());
    parameters.put("pagetitle", rs.getString("pagetitle.label"));
    parameters.put("selectlogo", rs.getString("selectlogo.label"));
    parameters.put("noteselectlogo", rs.getString("noteselectlogo.label"));
    parameters.put("upload", rs.getString("upload.label"));
    parameters.put("selectstyle", rs.getString("selectstyle.label"));
    parameters.put("darkstyle",rs.getString("style.dark.label"));
    parameters.put("lightstyle",rs.getString("style.light.label"));
    parameters.put("preview", rs.getString("preview.label"));
    parameters.put("save", rs.getString("save.label"));
    parameters.put("cancel", rs.getString("cancel.label"));
    parameters.put("saveok", rs.getString("info.saveok.label"));
    parameters.put("savenotok", rs.getString("info.savenotok.label"));
    parameters.put("cancelok", rs.getString("info.cancelok.label"));
    parameters.put("mustpng", rs.getString("mustpng.label"));
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
    Map<String, String> result = new HashMap<String, String>();
    result.put("error", "1");
    return (createJSON(result));
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
  public Response.Content<Stream.Char> getResource(HttpContext httpContext) {
    Map<String, String> result = new HashMap<String, String>();
    String style = "Dark";
    if (settingService.get(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY) != null) {
      style = (String) settingService.get(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY)
                                     .getValue();
    }
    result.put("error", "0");
    result.put("style", style);
    result.put("logoUrl", getLogoUrl(httpContext, true));
    return createJSON(result);
  }

  /**
   * create a object JSON from the map.
   * 
   * @param Map<String, String> data
   * @return Response.Content
   */
  private Response.Content<Stream.Char> createJSON(final Map<String, String> data) {
    Response.Content<Stream.Char> json = new Response.Content<Stream.Char>(200, Stream.Char.class) {
      @Override
      public String getMimeType() {
        return "application/json";
      }

      @Override
      public void send(Stream.Char stream) throws IOException {
        stream.append("{");
        Iterator<Map.Entry<String, String>> i = data.entrySet().iterator();
        while (i.hasNext()) {
          Map.Entry<String, String> entry = i.next();
          stream.append("\"" + entry.getKey() + "\"");
          stream.append(":");
          stream.append("\"" + entry.getValue() + "\"");
          if (i.hasNext()) {
            stream.append(",");
          }
        }
        stream.append("}");
      }
    };
    return json;
  }

  /**
   * create a object text/html
   * 
   * @param text
   * @return
   */
  private Response.Content<Stream.Char> createText(final String text) {
    Response.Content<Stream.Char> textObject = new Response.Content<Stream.Char>(200,
                                                                                 Stream.Char.class) {
      @Override
      public String getMimeType() {
        return "text/html";
      }

      @Override
      public void send(Stream.Char stream) throws IOException {
        stream.append(text);
      }
    };
    return textObject;
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
