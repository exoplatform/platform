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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.json.BeanToJSONPlugin;
import org.exoplatform.json.JSONService;
import org.exoplatform.json.MapToJSONPlugin;
import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
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

  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    String serviceName  = req.getParameter("serviceName");
    if(serviceName.equals("portletRegistry")){
      Writer  writer = res.getWriter();
      try{
        PortalApplication app =  controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID) ;
        StringBuilder value = getPortlets(app);
        writer.append(value);
      }catch (Exception e) {
        e.printStackTrace() ;
        throw new IOException(e.getMessage());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private StringBuilder getPortlets(PortalApplication app) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer() ;
    PortletRegisteryService registeryService = (PortletRegisteryService)container.getComponentInstanceOfType(PortletRegisteryService.class) ;    
    List<PortletCategory> portletCategories = registeryService.getPortletCategories();

    StringBuilder value = new StringBuilder();
    JSONService jsonService = new JSONService();
    jsonService.register(PortletCategory.class, new PortletCategoryToJSONPlugin(registeryService));
    jsonService.register(Portlet.class, new PortletToJSONPlugin());
    
    if(portletCategories.size() < 1) return value;
    
    value.append("{\n").append("  portletRegistry : {\n");

    for(int i = 0; i < portletCategories.size(); i++) {
      PortletCategory category = portletCategories.get(i);
      jsonService.toJSONScript(category, value, 1);
      if(i < portletCategories.size() - 1) value.append("   ,\n");
    }    
    value.append("  }\n").append("}\n");
    
    return value; 
  }
  
  class PortletCategoryToJSONPlugin extends BeanToJSONPlugin<PortletCategory> {

    private PortletRegisteryService registeryService;

    PortletCategoryToJSONPlugin(PortletRegisteryService registeryService) {
      this.registeryService = registeryService;
    }

    @SuppressWarnings("unchecked")
    public void toJSONScript(PortletCategory category, StringBuilder builder, int indentLevel) throws Exception {
      appendIndentation(builder, indentLevel);
      builder.append('\'').append(category.getId()).append("' : {\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'name' : '").append(category.getPortletCategoryName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'portlets' : {\n");
      
      List<Portlet> portlets = registeryService.getPortlets(category.getId()) ;
      BeanToJSONPlugin plugin = service_.getConverterPlugin(Portlet.class);
      for(int j = 0; j<portlets.size(); j++){
        plugin.toJSONScript(portlets.get(j), builder, indentLevel+2);
        if(j < portlets.size() - 1){
          appendIndentation(builder, indentLevel+2);
          builder. append(",\n");
        }
      }
      appendIndentation(builder, indentLevel+1);
      builder.append("}\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n"); 
    }    
  }
  
  class PortletToJSONPlugin extends BeanToJSONPlugin<Portlet> {
    
    public void toJSONScript(Portlet portlet, StringBuilder builder, int indentLevel) throws Exception {
      appendIndentation(builder, indentLevel);
      builder.append('\'').append(portlet.getId()).append("' : {\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'title' : ").append("'").append(portlet.getPortletName()).append("',\n");
      appendIndentation(builder, indentLevel+1);
      builder.append("'des' : ").append("'").append(portlet.getDescription()).append("'\n");
      appendIndentation(builder, indentLevel);
      builder.append("}\n");
    }
  }

}