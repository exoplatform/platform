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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
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

  @View
  public void index(RenderContext renderContext){
    
    ResourceBundle rs = renderContext.getApplicationContext().resolveBundle(renderContext.getUserContext().getLocale());
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("bundle",rs);   
    
    List<GroupProvider> groups = providerSettingService.getGroupProviders();
    parameters.put("groups", groups);     
    
    parameters.put("senderName", System.getProperty("exo.notifications.portalname", "eXo"));
    parameters.put("senderEmail", System.getProperty("gatein.email.smtp.from", "noreply@exoplatform.com"));
    
    index.render(parameters);      
  }  
 
  
  @Ajax
  @Resource
  public Response setProvider(String providerId, String enable) {
    try{
      if (enable.equals("true") || enable.equals("false"))
        providerSettingService.saveProvider(providerId, Boolean.valueOf(enable));
      else throw new Exception("Bad input exception: need to set true/false value to enable or disable the provider");
    }catch(Exception e){
      return new Response.Error("Exception in switching stat of provider "+providerId+". " + e.toString());
    }
    Boolean isEnable = new Boolean(enable);    
    JSON data = new JSON();
    data.set("provider", providerId);
    data.set("isEnable", (isEnable)); // current status
   
    return Response.ok(data.toString()).withMimeType("application/json");
  }
  
  @Ajax
  @Resource
  public Response setSender(String name, String email) {
    if(name != null && name.length() > 0
         && isValidEmailAddresses(email)) {
      
      System.setProperty("exo.notifications.portalname", name);
      System.setProperty("gatein.email.smtp.from", email);
    } else {
      return new Response.Error("ERROR: Set value of name and email not empty and email is email address");
    }
    JSON data = new JSON();    
    data.set("status","OK");
    data.set("name", name);
    data.set("email",email);
    
    return Response.ok(data.toString()).withMimeType("application/json");
  }
  
  public static boolean isValidEmailAddresses(String addressList){
    if (addressList == null || addressList.length() < 0)
      return false;
    addressList = StringUtils.remove(addressList, " ");
    addressList = StringUtils.replace(addressList, ";", ",");
    try {
      InternetAddress[] iAdds = InternetAddress.parse(addressList, true);
      String emailRegex = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-.]+\\.[A-Za-z]{2,5}";
      for (int i = 0; i < iAdds.length; i++) {
        if (!iAdds[i].getAddress().matches(emailRegex))
          return false;
      }
    } catch (AddressException e) {
      return false;
    }
    return true;
  }
     
}
