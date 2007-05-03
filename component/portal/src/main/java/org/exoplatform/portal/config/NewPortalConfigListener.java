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
  
  private NewPortalConfig config_ ;
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
    
    config_ = (NewPortalConfig) params.getObjectParamValues(NewPortalConfig.class).get(0) ;
    
    valueParam = params.getValueParam("owner.type");
    if(valueParam != null) checkPortal = valueParam.getValue();
    initDB(valueParam.getValue());
  }
  
  private boolean isInitedDB(String user) throws Exception {
    PortalConfig pconfig = pdcService_.getPortalConfig(user);
    return pconfig != null;
  }
  
  private void initDB(String ownerType) throws Exception {
    HashSet owners = config_.getPredefinedOwner();
    Iterator iter  = owners.iterator();
    while(iter.hasNext()){
      String ownerId = (String)iter.next();
      if(ownerType.equals("portal")) createPortalConfig(ownerType, ownerId);
      createPage(ownerType, ownerId);
      createPageNavigation(ownerType, ownerId);
//      createPortletPreferencesForUser(user);
    }
  }
  
  private void createPortalConfig(String ownerType, String ownerId) throws Exception {    
    PortalConfig pconfig = fromXML(getDefaultConfig(ownerType, ownerId, "portal"), PortalConfig.class);
    pdcService_.create(pconfig);
  }
  
  private void createPage(String ownerType, String ownerId) throws Exception {
    PageSet pageSet = fromXML(getDefaultConfig(ownerType, ownerId, "pages"), PageSet.class);
    ArrayList<Page> list = pageSet.getPages();
    for(Page page : list) pdcService_.create(page);
  }
  
  private void createPageNavigation(String ownerType, String ownerId) throws Exception {
    PageNavigation navigation = fromXML(getDefaultConfig(ownerType, ownerId, "navigation"), PageNavigation.class);
    pdcService_.create(navigation);
  }
  
  /*private void createPortletPreferencesForUser(String owner) throws Exception {
    PortletPreferencesSet portletSet = 
      (PortletPreferencesSet)pdcService_.fromXML(
          getDefaultConfig(owner, "portlet-preferences"), PortletPreferencesSet.class);
    ArrayList<PortletPreferences> list = portletSet.getPortlets();
    for(PortletPreferences portlet : list){
      pdcService_.savePortletPreferencesConfig(portlet);
    }
  }*/ 
  
  private String getDefaultConfig(String ownerType, String ownerId, String dataType) throws Exception {
    String config = null;
    String templateLoc = config_.getTemplateLocation() ;
    String path = "/" + ownerType + "/" + ownerId +"/"+dataType+".xml";
    if(config_.isPredefinedOwner(ownerId)) {
      String location = config_.getTemplateLocation() ;
      config = IOUtil.getStreamContentAsString(cmanager_.getInputStream(location + path));      
    }else {
      InputStream is = cmanager_.getInputStream(templateLoc + path);
      String template = IOUtil.getStreamContentAsString(is);
      config = StringUtils.replace(template, "@owner@", ownerId);
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
