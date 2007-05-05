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
  
  public NewPortalConfigListener(DataStorage pdcService,
                                      ConfigurationManager cmanager,                                        
                                      InitParams params) throws Exception {
    cmanager_ = cmanager ;
    pdcService_ = pdcService;
    
    String checkPortal = "site";
    ValueParam valueParam = params.getValueParam("check.portal");
    if(valueParam != null) checkPortal = valueParam.getValue();
    if(checkPortal == null  || checkPortal.trim().length() == 0) checkPortal = "site";    
    if(isInitedDB(checkPortal)) return;
    
    List list = params.getObjectParamValues(NewPortalConfig.class);
    for (Object ele : list) {
      NewPortalConfig portalConfig  = (NewPortalConfig)ele;
      if(portalConfig.getOwnerType().equals("user")) {
        initUserTypeDB(portalConfig);  
      } else if (portalConfig.getOwnerType().equals("group")){
        initGroupTypeDB(portalConfig);
      } else {
        initPortalTypeDB(portalConfig);
      }
    }
  }
  
  private boolean isInitedDB(String user) throws Exception {
    PortalConfig pconfig = pdcService_.getPortalConfig(user);
    return pconfig != null;
  }
  
  private void initUserTypeDB(NewPortalConfig config) throws Exception {
    HashSet owners = config.getPredefinedOwner();
    Iterator iter  = owners.iterator();
    while(iter.hasNext()){
      String owner = (String)iter.next();
      createPage(config, owner);
      createPageNavigation(config, owner);
    }
  }
  
  private void initGroupTypeDB(NewPortalConfig config) throws Exception {
    HashSet owners = config.getPredefinedOwner();
    Iterator iter  = owners.iterator();
    while(iter.hasNext()){
      String owner = (String)iter.next();
      createPage(config, owner);
      createPageNavigation(config, owner);
    }
  }
  
  private void initPortalTypeDB(NewPortalConfig config) throws Exception {
    HashSet owners = config.getPredefinedOwner();
    Iterator iter  = owners.iterator();
    while(iter.hasNext()){
      String owner = (String)iter.next();
      createPortalConfig(config, owner);
      createPage(config, owner);
      createPageNavigation(config, owner);
      createPortletPreferences(config, owner);
    }
  }
  
  private void createPortalConfig(NewPortalConfig config, String owner) throws Exception { 
    PortalConfig pconfig = fromXML(getDefaultConfig(config, owner, "portal"), PortalConfig.class);
    pdcService_.create(pconfig);
  }
  
  private void createPage(NewPortalConfig config, String owner) throws Exception {
    PageSet pageSet = fromXML(getDefaultConfig(config, owner, "pages"), PageSet.class);
    ArrayList<Page> list = pageSet.getPages();
    for(Page page : list) pdcService_.create(page);
  }
  
  private void createPageNavigation(NewPortalConfig config, String owner) throws Exception {
    PageNavigation navigation = fromXML(getDefaultConfig(config, owner, "navigation"), PageNavigation.class);
    pdcService_.create(navigation);
  }
  
  private void createPortletPreferences(NewPortalConfig config, String owner) throws Exception {
    PortletPreferencesSet portletSet = fromXML(getDefaultConfig(config, owner, "portlet-preferences"), PortletPreferencesSet.class);
    ArrayList<PortletPreferences> list = portletSet.getPortlets();
    for(PortletPreferences portlet : list){
      pdcService_.savePortletPreferencesConfig(portlet);
    }
  } 
  
  private String getDefaultConfig(NewPortalConfig portalConfig, String owner, String dataType) throws Exception {
    String ownerType = portalConfig.getOwnerType();
    
    String config = null;
    String templateLoc = portalConfig.getTemplateLocation() ;
    String path = "/" + ownerType + "/" + owner +"/"+dataType+".xml";
    if(portalConfig.isPredefinedOwner(owner)) {
      String location = portalConfig.getTemplateLocation() ;
      config = IOUtil.getStreamContentAsString(cmanager_.getInputStream(location + path));      
    }else {
      InputStream is = cmanager_.getInputStream(templateLoc + path);
      String template = IOUtil.getStreamContentAsString(is);
      config = StringUtils.replace(template, "@owner@", owner);
    }
    return config;
  }
  
  private <T> T fromXML(String xml, Class<T> clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes()) ;
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    return clazz.cast(uctx.unmarshalDocument(is, null));
  }
  
}
