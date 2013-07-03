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


import javax.inject.Inject;

import org.exoplatform.commons.api.notification.model.ProviderData;
import org.exoplatform.commons.api.notification.GroupProvider;
import org.exoplatform.commons.api.notification.service.setting.ProviderSettingService;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.juzu.ajax.Ajax;


import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.impl.common.JSON;
import juzu.request.RenderContext;
import juzu.template.Template;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;


public class NotificationsAdministration {

  @Inject
  @Path("index.gtmpl")
  Template index;
  
  @Inject
  ResourceBundle bundle;  
  
  @Inject
  ProviderSettingService providerSettingService;
  
  @Inject
  SettingService settingService;

  private static String ON_STATUS = "enable";
  private static String OFF_STATUS = "disable";
  
  @View
  public void index(RenderContext renderContext){
    
    ResourceBundle rs = renderContext.getApplicationContext().resolveBundle(renderContext.getUserContext().getLocale());
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("bundle",rs);
    
    List<GroupProvider> groups = providerSettingService.getGroupProviders();
    Map<String,String> providerStatus = new HashMap<String,String>();
    
    parameters.put("groups", groups);     
    for (GroupProvider g : groups){
      List<ProviderData> providers = g.getProviderDatas();
      for (ProviderData provider : providers){
        String providerId = provider.getType();
        providerStatus.put(providerId,getStatus(providerId));
      }    
    }
    
    parameters.put("providers",providerStatus);
    parameters.put("senderName", "intranet notification");
    parameters.put("senderEmail", "notification@intranet.com");
    index.render(parameters);      
  }  
 
  
  @Ajax
  @Resource
  public Response setProvider(String providerId, String enable) {
    try{
    //TODO: SAVE IN SETTING, use ProviderSettingService     
      if (enable.equals("true")) enableProvider(providerId); 
      else if (enable.equals("false")) disableProvider(providerId);
      else throw new Exception("ERROR: Set true/false value to enable or disable the provider");
    }catch(Exception e){
      return new Response.Error(e);
    }
    Boolean isEnable = new Boolean(enable);    
    JSON data = new JSON();
    data.set("provider", providerId);
    data.set("status",isEnable ? ON_STATUS : OFF_STATUS); //current status
        
    return Response.ok(data.toString()).withMimeType("application/json");
  }
  
  @Ajax
  @Resource
  public Response setSender(String name, String email) {
    try{      
        saveSender(name,email);      
    }catch(Exception e){
      return new Response.Error(e);
    }
    JSON data = new JSON();    
    data.set("status","OK");
    data.set("name", name);
    data.set("email",email);
    
    return Response.ok(data.toString()).withMimeType("application/json");
  }

/*============ TODO in commons-component-common/ProviderSettingService ===============*/
  
  private boolean isEnable(String providerId){
    SettingValue<Boolean> isActive = (SettingValue<Boolean>) settingService.get(Context.GLOBAL, Scope.GLOBAL, "exo:"+providerId);
    if (isActive != null)
      return isActive.getValue().booleanValue();
    return false;
  }
  
  private String getStatus(String providerId){
    SettingValue<Boolean> isActive = (SettingValue<Boolean>) settingService.get(Context.GLOBAL, Scope.GLOBAL, "exo:"+providerId);
    if (isActive != null && isActive.getValue())
      return ON_STATUS;
    return OFF_STATUS;
  }
  
  private void enableProvider(String providerId){
      settingService.set(Context.GLOBAL, Scope.GLOBAL, "exo:"+providerId, SettingValue.create(true));
  }
  
  private void disableProvider(String providerId){
      settingService.remove(Context.GLOBAL, Scope.GLOBAL, "exo:"+providerId);     
  }
  
  private void setNotificationAdminSetting(String value){
    String enableProviders = (String) settingService.get(Context.GLOBAL, Scope.GLOBAL, "NotificationAdmin").getValue();
    settingService.set(Context.GLOBAL, Scope.GLOBAL, "ActiveNotificationsType", SettingValue.create(value));
  }
  
  private void saveSender(String name, String email){
    settingService.set(Context.GLOBAL, Scope.PORTAL, "NotificationSender", SettingValue.create("name:"+name+";email:"+email));
  }
  
/* 
  private String createSettingValue(String providerId, boolean disable) throws UnexpectedConditionException{    
    String providersActive = (String) settingService.get(Context.GLOBAL, Scope.PORTAL, "NotificationProviderActive").getValue();    
  
    if (!providersActive.contains(providerId) && !disable){ //verify
      return providersActive.concat(providerId);
    }else if (providersActive.contains(providerId) && disable){//verify
      String[] providers = (providersActive+";").split(";");
      for (String provider : providers){
        
      }
      return providersActive.replace(providerId+";", "");
    }else {
      throw new UnexpectedConditionException("");
    }        
  }

  */
     
}
