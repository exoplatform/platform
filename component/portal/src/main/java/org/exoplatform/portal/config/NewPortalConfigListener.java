/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.portal.portlet.PortletPreferences.PortletPreferencesSet;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 22, 2006
 */
public class NewPortalConfigListener extends BaseComponentPlugin {
  
  private ConfigurationManager cmanager_ ;
  private DataStorage pdcService_;  
  private List<?> configs;
  
  private String defaultPortal ;
  
  public NewPortalConfigListener(DataStorage pdcService,
                                 ConfigurationManager cmanager,                                        
                                 InitParams params) throws Exception {
    cmanager_ = cmanager ;
    pdcService_ = pdcService;
    
    String checkPortal = "site";
    ValueParam valueParam = params.getValueParam("default.portal");
    if(valueParam != null) checkPortal = valueParam.getValue();
    if(checkPortal == null  || checkPortal.trim().length() == 0) checkPortal = "site";    
    
    configs = params.getObjectParamValues(NewPortalConfig.class);
   
    if(isInitedDB(checkPortal)) return;
    
    for (Object ele : configs) {
      NewPortalConfig portalConfig  = (NewPortalConfig)ele;
      if(portalConfig.getOwnerType().equals("user")) {
        initUserTypeDB(portalConfig);  
      } else if (portalConfig.getOwnerType().equals("group")){
        initGroupTypeDB(portalConfig);
      } else {
        initPortalTypeDB(portalConfig);
      }
      portalConfig.getPredefinedOwner().clear();
    }
  }
  
  NewPortalConfig getPortalConfig(String ownerType) {
    for(Object object : configs) {
      NewPortalConfig portalConfig  = (NewPortalConfig) object;
      if(portalConfig.getOwnerType().equals(ownerType)) return portalConfig;
    }
    return null;
  }
  
  private boolean isInitedDB(String user) throws Exception {
    PortalConfig pconfig = pdcService_.getPortalConfig(user);
    return pconfig != null;
  }
  
  public void initUserTypeDB(NewPortalConfig config) throws Exception {
    HashSet<String> owners = config.getPredefinedOwner();
    Iterator<String> iter  = owners.iterator();
    while(iter.hasNext()){
      String owner = iter.next();
      createPage(config, owner);
      createPageNavigation(config, owner);
      createWidgets(config, owner);
    }
  }
  
  public void initGroupTypeDB(NewPortalConfig config) throws Exception {
    HashSet<String> owners = config.getPredefinedOwner();
    Iterator<String> iter  = owners.iterator();
    while(iter.hasNext()){
      String owner = iter.next();
      createPage(config, owner);
      createPageNavigation(config, owner);
      createPortletPreferences(config, owner);
    }
  }
  
  public void initPortalTypeDB(NewPortalConfig config) throws Exception {
    HashSet<String> owners = config.getPredefinedOwner();
    Iterator<String> iter  = owners.iterator();
    while(iter.hasNext()){
      String owner = iter.next();
      createPortalConfig(config, owner);
      createPage(config, owner);
      createPageNavigation(config, owner);
      createPortletPreferences(config, owner);
      createWidgets(config, owner);
    }
  }
  
  private void createPortalConfig(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if(config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "portal");
    } else {
      xml = getTemplateConfig(config, owner, "portal");
    }
    PortalConfig pconfig = fromXML(xml, PortalConfig.class);
    pdcService_.create(pconfig);
  }
  
  private void createPage(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if(config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "pages");
    } else {
      xml = getTemplateConfig(config, owner, "pages");
    }
    PageSet pageSet = fromXML(xml, PageSet.class);
    ArrayList<Page> list = pageSet.getPages();
    for(Page page : list) {
      pdcService_.create(page);
    }
  }
  
  private void createPageNavigation(NewPortalConfig config, String owner) throws Exception {    
    String xml = null;
    if(config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "navigation");
    } else {
      xml = getTemplateConfig(config, owner, "navigation");
    }
    PageNavigation navigation = fromXML(xml, PageNavigation.class);
    pdcService_.create(navigation);
  }
  
  private void createWidgets(NewPortalConfig config, String owner) throws Exception { 
    String xml = null;
    if(config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "widgets");
    } else {
      xml = getTemplateConfig(config, owner, "widgets");
    }
    Widgets widgets = fromXML(xml, Widgets.class);
    pdcService_.create(widgets);
  }
  
  private void createPortletPreferences(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if(config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "portlet-preferences");
    } else {
      xml = getTemplateConfig(config, owner, "portlet-preferences");
    }
    PortletPreferencesSet portletSet = fromXML(xml, PortletPreferencesSet.class);
    ArrayList<PortletPreferences> list = portletSet.getPortlets();
    for(PortletPreferences portlet : list){
      pdcService_.save(portlet);
    }
  } 
  
  private String getDefaultConfig(NewPortalConfig portalConfig, String owner, String dataType) throws Exception {
    String ownerType = portalConfig.getOwnerType();
    String path = "/" + ownerType + "/" + owner +"/"+dataType+".xml";
    String location = portalConfig.getTemplateLocation() ;
    return IOUtil.getStreamContentAsString(cmanager_.getInputStream(location + path));
  }
  
  private String getTemplateConfig(NewPortalConfig portalConfig, String owner, String dataType) throws Exception {
    String ownerType = portalConfig.getOwnerType();
    String templateLoc = portalConfig.getTemplateLocation() ;
    String path = "/" + ownerType + "/template/" +portalConfig.getTemplateOwner()+ "/" + dataType+".xml";
    InputStream is = cmanager_.getInputStream(templateLoc + path);
    String template = IOUtil.getStreamContentAsString(is);
    return StringUtils.replace(template, "@owner@", owner);
  }
  
  private <T> T fromXML(String xml, Class<T> clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8")) ;
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    return clazz.cast(uctx.unmarshalDocument(is, "UTF-8"));
  }
  
  String getDefaultPortal() { return defaultPortal; }
  
}
