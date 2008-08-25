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
package org.exoplatform.web.command.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.json.BeanToJSONPlugin;
import org.exoplatform.json.JSONService;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 31, 2007  
 */
public class GetApplicationHandler extends Command {
  
  private String [] applicationType;
  
  public void setApplicationTypes(String [] type) { applicationType = type; }
  
  @SuppressWarnings("unused")
  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    res.setHeader("Cache-Control", "no-cache") ;
    Writer writer = res.getWriter();
    try{
      writer.append(getApplications(req.getRemoteUser()));
    }catch (Exception e) {
      e.printStackTrace() ;
      throw new IOException(e.getMessage());
    }
  }
  
  @SuppressWarnings("unchecked")
  private StringBuilder getApplications(String remoteUser) throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    ApplicationRegistryService prService = 
      (ApplicationRegistryService)container.getComponentInstanceOfType(ApplicationRegistryService.class) ;    

    if(applicationType == null) applicationType = new String[]{};
    List<ApplicationCategory> appCategories = prService.getApplicationCategories(remoteUser, applicationType);
    ApplicationCategoryToJSONPlugin toJSON = new ApplicationCategoryToJSONPlugin();

    StringBuilder value = new StringBuilder();
    JSONService jsonService = new JSONService();
    jsonService.register(ApplicationCategory.class, toJSON);
    
    if(appCategories.size() < 1) return value;
    
    value.append("{\n").append("  applicationRegistry : {\n");
    for(int i = 0; i < appCategories.size(); i++) {
      ApplicationCategory category = appCategories.get(i);
      jsonService.toJSONScript(category, value, 1);
      if(i < appCategories.size() - 1) value.append("   ,\n");
    }
    value.append("  }\n").append("}\n");
    
    return value; 
  }
  
  private class ApplicationCategoryToJSONPlugin extends BeanToJSONPlugin<ApplicationCategory> {

    @SuppressWarnings("unchecked")
    public void toJSONScript(ApplicationCategory category, StringBuilder builder, int indentLevel) throws Exception {
      StringBuilder builderPortlet =  toJSONScript(category, indentLevel + 2);
      if(builderPortlet.length() < 1) return;
      
      appendIndentation(builder, indentLevel);
      builder.append('\'').append(category.getName()).append("' : {\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'name' : '").append(category.getDisplayName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'applications' : {\n");
      builder.append(builderPortlet);      
      appendIndentation(builder, indentLevel+1);
      builder.append("}\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n"); 
    }    
    
    @SuppressWarnings("unchecked")
    private StringBuilder toJSONScript(ApplicationCategory category, int indentLevel) throws Exception {
      StringBuilder builder = new StringBuilder();
      List<Application> applications = category.getApplications();
      
      for(int j = 0; j<applications.size(); j++){
        toJSONScript(applications.get(j), builder, indentLevel);
        if(j < applications.size() - 1){
          appendIndentation(builder, indentLevel);
          builder.append(",\n");
        }
      }
      
      return builder;
    }
    
    private void toJSONScript(Application application, StringBuilder builder, int indentLevel) {
      appendIndentation(builder, indentLevel);
      builder.append('\'').append(application.getId()).append("' : {\n");
      appendIndentation(builder, indentLevel+1);
      //TODO: Tung.Pham modified
      //----------------------------
      //builder.append("'title' : ").append("'").append(application.getApplicationName()).append("',\n");
      builder.append("'name' : ").append("'").append(application.getApplicationName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'title' : ").append("'").append(application.getDisplayName()).append("',\n");
      //------------------------------
      appendIndentation(builder, indentLevel+1);
      builder.append("'des' : ").append("'").append(application.getDescription()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'id' : ").append("'").append(application.getId()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'type' : ").append("'").append(application.getApplicationType()).append("'\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n");
    }
  }

}
