/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.json.BeanToJSONPlugin;
import org.exoplatform.json.JSONService;
import org.exoplatform.portal.application.PortalApplication;
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
  
  public void setApplicationType(String [] type) { applicationType = type; }
  
  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    PortalApplication app =  controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID) ;
    Writer writer = res.getWriter();
    try{
      StringBuilder value = getApplications(app, req.getRemoteUser());
      writer.append(value);
    }catch (Exception e) {
      e.printStackTrace() ;
      throw new IOException(e.getMessage());
    }
  }
  
  @SuppressWarnings("unchecked")
  private StringBuilder getApplications(PortalApplication app, String remoteUser) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer() ;
    ApplicationRegistryService prService = 
      (ApplicationRegistryService)container.getComponentInstanceOfType(ApplicationRegistryService.class) ;    

    List<ApplicationCategory> appCategories = prService.getApplicationCategories(remoteUser);
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
      builder.append("'name' : '").append(category.getName()).append("',\n");
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
      builder.append("'title' : ").append("'").append(application.getApplicationName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'des' : ").append("'").append(application.getDescription()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'type' : ").append("'").append(application.getApplicationType()).append("'\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n");
    }
  }

}
