/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.json.BeanToJSONPlugin;
import org.exoplatform.json.JSONService;
import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;
/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 9, 2006  
 */
public class ServiceRequestHandler extends WebRequestHandler {
  
  static String[]  PATHS = {"/service"} ;

  public String[] getPath() { return PATHS ; }

  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    PortalApplication app =  controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID) ;
    String serviceName = req.getParameter("serviceName");
    
    if(serviceName.equals("portletRegistry")){
      Writer writer = res.getWriter();
      try{
        String remoteUser = req.getRemoteUser();
        StringBuilder value = getPortlets(app, remoteUser, req.isUserInRole("admin"));
        writer.append(value);
      }catch (Exception e) {
        e.printStackTrace() ;
        throw new IOException(e.getMessage());
      }
    }
    
  }

  @SuppressWarnings("unchecked")
  private StringBuilder getPortlets(PortalApplication app, String remoteUser, boolean isRoleAdmin) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer() ;
    
    ApplicationRegisteryService prService = (ApplicationRegisteryService)container.getComponentInstanceOfType(ApplicationRegisteryService.class) ;    
    List<ApplicationCategory> portletCategories = prService.getApplicationCategories();
//    UserACL userACL = (UserACL)container.getComponentInstanceOfType(UserACL.class) ;
    
    PortletCategoryToJSONPlugin toJSON = new PortletCategoryToJSONPlugin(prService, remoteUser, isRoleAdmin);

    StringBuilder value = new StringBuilder();
    JSONService jsonService = new JSONService();
    jsonService.register(ApplicationCategory.class, toJSON);
    
    if(portletCategories.size() < 1) return value;
    
    value.append("{\n").append("  portletRegistry : {\n");
    for(int i = 0; i < portletCategories.size(); i++) {
      ApplicationCategory category = portletCategories.get(i);
      jsonService.toJSONScript(category, value, 1);
      if(i < portletCategories.size() - 1) value.append("   ,\n");
    }    
    value.append("  }\n").append("}\n");
    
    return value; 
  }
  
  class PortletCategoryToJSONPlugin extends BeanToJSONPlugin<ApplicationCategory> {

    private ApplicationRegisteryService registeryService_;
    private String remoteUser_;
    private boolean isRoleAdmin_ = false;

    PortletCategoryToJSONPlugin(ApplicationRegisteryService registeryService, 
                                String remoteUser, boolean isRoleAdmin) {
      registeryService_ = registeryService;
      remoteUser_  = remoteUser;
      isRoleAdmin_ = isRoleAdmin;
    }

    @SuppressWarnings("unchecked")
    public void toJSONScript(ApplicationCategory category, StringBuilder builder, int indentLevel) throws Exception {
      StringBuilder builderPortlet =  toJSONScript(category, indentLevel + 2);
      if(builderPortlet.length() < 1) return;
      
      appendIndentation(builder, indentLevel);
      builder.append('\'').append(category.getName()).append("' : {\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'name' : '").append(category.getName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'portlets' : {\n");
      builder.append(builderPortlet);      
      appendIndentation(builder, indentLevel+1);
      builder.append("}\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n"); 
    }    
    
    @SuppressWarnings("unchecked")
    private StringBuilder toJSONScript(ApplicationCategory category, int indentLevel) throws Exception {
      StringBuilder builder = new StringBuilder();
      List<Application> portlets = registeryService_.getApplications(category);
      
      for(int j = 0; j<portlets.size(); j++){
        String perm = null;//portlets.get(j).getViewPermission();
        if(perm == null) perm = "member:/user";
//        if(!isRoleAdmin_ && !userACL_.hasPermission(null, remoteUser_, perm)) continue;
        toJSONScript(portlets.get(j), builder, indentLevel);
        if(j < portlets.size() - 1){
          appendIndentation(builder, indentLevel);
          builder.append(",\n");
        }
      }
      
      return builder;
    }
    
    private void toJSONScript(Application portlet, StringBuilder builder, int indentLevel) {
      appendIndentation(builder, indentLevel);
      builder.append('\'').append(portlet.getId()).append("' : {\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'title' : ").append("'").append(portlet.getApplicationName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'des' : ").append("'").append(portlet.getDescription()).append("'\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n");
    }
  }

}