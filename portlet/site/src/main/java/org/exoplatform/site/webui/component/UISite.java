/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.site.webui.component;

import java.io.File;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.exoplatform.resolver.FileResourceResolver;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Sep 27, 2006  
 */
@ComponentConfig()
public class UISite extends UIComponent {
  private String realSiteLocation_ ;
  private Node navigation_ ;
  private ResourceResolver resourceResolver_ = new FileResourceResolver() ;
  
  public UISite() {
    PortletRequestContext pcontext = (PortletRequestContext)WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = pcontext.getRequest() ;
    PortletPreferences prefs = prequest.getPreferences() ;
    String location = prefs.getValue("site.location", null) ;
    if(location.startsWith("file:")) {
      realSiteLocation_ =  location.substring("file:".length()) ;
    } else if(location.startsWith("par:")) {
      location =  location.substring("par:".length()) ;
      realSiteLocation_ = prequest.getPortletSession().getPortletContext().getRealPath(location) ;
    }
    
    File content = new File(realSiteLocation_ + "/content") ;
    navigation_ = new Node("/", "/") ;
    buildTree(navigation_, content) ;
  }
  
  public String getTemplate() {  return realSiteLocation_ + "/template/SiteTemplate.gtmpl"; }
  
  @SuppressWarnings("unused")
  public ResourceResolver getTemplateResourceResolver(WebuiRequestContext context, String template) {
    return  resourceResolver_ ;
  }
  
  private  void  buildTree(Node node, File folder) {
    if(folder == null) return;
    File  [] files = folder.listFiles();
    if(files == null) return;
    for(File childFile : files) {
      String nodeName = childFile.getName() ;
      Node childNode = new Node(node.getPath() + "/" + nodeName, nodeName) ;
      node.addChild(childNode) ;
      if(childFile.isDirectory()) {
        buildTree(childNode, childFile) ;
      }
    }
  }
  
  
}
