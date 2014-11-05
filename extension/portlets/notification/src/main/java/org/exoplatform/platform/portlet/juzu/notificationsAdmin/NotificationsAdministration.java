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
package org.exoplatform.platform.portlet.juzu.notificationsAdmin;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.impl.common.JSON;
import juzu.request.ApplicationContext;
import juzu.request.UserContext;
import juzu.template.Template;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.api.notification.model.GroupProvider;
import org.exoplatform.commons.api.notification.plugin.NotificationPluginUtils;
import org.exoplatform.commons.api.notification.plugin.config.PluginConfig;
import org.exoplatform.commons.api.notification.service.setting.PluginSettingService;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.commons.notification.NotificationUtils;
import org.exoplatform.commons.notification.impl.DigestDailyPlugin;
import org.exoplatform.commons.notification.template.TemplateUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.JavascriptManager;
import org.exoplatform.webui.application.WebuiRequestContext;


public class NotificationsAdministration {
  private static final Log LOG = ExoLogger.getLogger(NotificationsAdministration.class);
  
  @Inject
  @Path("index.gtmpl")
  Template index;
  
  @Inject
  ResourceBundle bundle;  
  
  @Inject
  PluginSettingService providerSettingService;
  
  @Inject
  SettingService settingService;

  private Locale locale = Locale.ENGLISH;
  
  @View
  public Response index(ApplicationContext applicationContext, UserContext userContext){
    //Redirect yo the home's page when the feature is off
    if (! CommonsUtils.isFeatureActive(NotificationUtils.FEATURE_NAME)) {
      return redirectToHomePage();
    }
    
    this.locale = userContext.getLocale();
    ResourceBundle rs = applicationContext.resolveBundle(this.locale);
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("_ctx", new Context(rs));   
    
    List<GroupProvider> groups = providerSettingService.getGroupPlugins();
    parameters.put("groups", groups);     
    
    //try to get sender name and email from database. If fail, get default value from properties file
    SettingValue<?> senderName = settingService.get(org.exoplatform.commons.api.settings.data.Context.GLOBAL, Scope.GLOBAL, NotificationPluginUtils.NOTIFICATION_SENDER_NAME);
    SettingValue<?> senderEmail = settingService.get(org.exoplatform.commons.api.settings.data.Context.GLOBAL, Scope.GLOBAL, NotificationPluginUtils.NOTIFICATION_SENDER_EMAIL);
    parameters.put("senderName", senderName != null ? (String)senderName.getValue() : System.getProperty("exo.notifications.portalname", "eXo"));
    parameters.put("senderEmail", senderEmail != null ? (String)senderEmail.getValue() : System.getProperty("gatein.email.smtp.from", "noreply@exoplatform.com"));
    
    return index.ok(parameters);
  }  
 
  private Response redirectToHomePage() {
    PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
    HttpServletRequest currentServletRequest = portalRequestContext.getRequest();
    StringBuilder sb = new StringBuilder();
    sb.append(currentServletRequest.getScheme()).append("://")
      .append(currentServletRequest.getServerName())
      .append(":").append(currentServletRequest.getServerPort())
      .append("/").append(PortalContainer.getCurrentPortalContainerName())
      .append("/").append(Util.getPortalRequestContext().getPortalOwner());
    
    WebuiRequestContext ctx = WebuiRequestContext.getCurrentInstance();
    JavascriptManager jsManager = ctx.getJavascriptManager();
    jsManager.addJavascript("try { window.location.href='" + sb.toString() + "' } catch(e) {" +
            "window.location.href('" + sb.toString() + "') }");

    return Response.redirect(sb.toString());
  }
  
  @Ajax
  @Resource
  public Response saveActivePlugin(String pluginId, String enable) {
    try{
      if (enable.equals("true") || enable.equals("false"))
        providerSettingService.savePlugin(pluginId, Boolean.valueOf(enable));
      else throw new Exception("Bad input exception: need to set true/false value to enable or disable the provider");
    }catch(Exception e){
      return new Response.Error("Exception in switching stat of provider "+pluginId+". " + e.toString());
    }
    Boolean isEnable = new Boolean(enable);    
    JSON data = new JSON();
    data.set("pluginId", pluginId);
    data.set("isEnable", (isEnable)); // current status
   
    return Response.ok(data.toString()).withMimeType("application/json");
  }
  
  @Ajax
  @Resource
  public Response saveSender(String name, String email) {
    JSON data = new JSON();
    data.set("name", name);
    data.set("email",email);
    if(name != null && name.length() > 0
         && NotificationUtils.isValidEmailAddresses(email)) {
      settingService.set(org.exoplatform.commons.api.settings.data.Context.GLOBAL, Scope.GLOBAL, NotificationPluginUtils.NOTIFICATION_SENDER_NAME, SettingValue.create(name));
      settingService.set(org.exoplatform.commons.api.settings.data.Context.GLOBAL, Scope.GLOBAL, NotificationPluginUtils.NOTIFICATION_SENDER_EMAIL, SettingValue.create(email));
      data.set("status","OK");
    } else {
      data.set("status","NOK");
    }
    
    return Response.ok(data.toString()).withMimeType("application/json");
  }
  
  public class Context {
    ResourceBundle rs;

    public Context(ResourceBundle rs) {
      this.rs = rs;
    }

    public String appRes(String key) {
      try {
        return rs.getString(key).replaceAll("'", "&#39;").replaceAll("\"", "&#34;");
      } catch (java.util.MissingResourceException e) {
        LOG.warn("Can't find resource for bundle key " + key);
      } catch (Exception e) {
        LOG.debug("Error when get resource bundle key " + key, e);
      }
      return key;
    }
    
    private String getBundlePath(String id) {
      PluginConfig pluginConfig = providerSettingService.getPluginConfig(id);
      if (pluginConfig != null) {
        return pluginConfig.getTemplateConfig().getBundlePath();
      }
      //
      if (GroupProvider.defaultGroupIds.contains(id)) {
        return providerSettingService.getPluginConfig(DigestDailyPlugin.ID)
            .getTemplateConfig().getBundlePath();
      }
      //
      List<GroupProvider> groups = providerSettingService.getGroupPlugins();
      for (GroupProvider groupProvider : groups) {
        if (groupProvider.getGroupId().equals(id)) {
          return groupProvider.getProviderDatas().get(0).getBundlePath();
        }
      }
      return "";
    }

    public String pluginRes(String key, String id) {
      String path = getBundlePath(id);
      return TemplateUtils.getResourceBundle(key, locale, path);
    }
  }
}
