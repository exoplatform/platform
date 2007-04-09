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
    jsonService.register(MapToJSONPlugin.class, new MapToJSONPlugin());
    
    if(portletCategories.size() < 1) return value;
    
/*    value.append("{\n").
          append("  portletRegistry : {\n");*/

    for(int i = 0; i < portletCategories.size(); i++) {
      PortletCategory category = portletCategories.get(i); 
      jsonService.toJSONScript(category, value, 0);
      List<Portlet> portlets = registeryService.getPortlets(category.getId()) ;
      for(int j = 0; j<portlets.size(); j++){
        Portlet portlet = portlets.get(j);
        jsonService.toJSONScript(portlet, value, 0);
      }
    }
 /*   value.append("  }\n").
    append("}\n");*/
    return value; 
   /* 
    ExoContainer container = app.getApplicationServiceContainer() ;
    PortletRegisteryService registeryService = (PortletRegisteryService)container.getComponentInstanceOfType(PortletRegisteryService.class) ;    
    List<PortletCategory> portletCategories = registeryService.getPortletCategories();

    StringBuilder value = new StringBuilder();    
    if(portletCategories.size() < 1) return value;
    
    value.append("{\n").
          append("  portletRegistry : {\n");

    for(int i = 0; i < portletCategories.size(); i++) {
      PortletCategory category = portletCategories.get(i);
      value.  append("    '").append(category.getId()).append("' : {\n").
              append("      'name' : '").append(category.getPortletCategoryName()).append("',\n").
              append("      'portlets' : {\n");
      List<Portlet> portlets = registeryService.getPortlets(category.getId()) ;
      for(int j = 0; j<portlets.size(); j++){
        Portlet portlet = portlets.get(j);
        value.    append("        '").append(portlet.getId()).append("' : {\n").
                  append("          'title' : ").append("'").append(portlet.getPortletName()).append("',\n").
                  append("          'des' : ").append("'").append(portlet.getDescription()).append("'\n");              
        if(j < portlets.size() - 1){
          value.  append("        },\n");//end a portlet
        }else{
          value.  append("        }\n");//end a portlet
        }
      }
      value.  append("      }\n");//and all portlets
      if(i < portletCategories.size() - 1){
        value.append("    },\n");// end category info
      }else{
        value.append("    }\n"); //end category info
      }
    }  
    value.append("  }\n").
          append("}\n");
    return value;*/
  }

}