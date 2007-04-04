/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

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
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.portal.portlet.PortletPreferences.PortletPreferencesSet;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 22, 2006
 */
public class UserPortalConfigTemplateListener extends BaseComponentPlugin {
  
  private NewPortalConfig config_ ;
  private ConfigurationManager cservice_ ;
  private PortalDAO pdcService_;  
  
  public UserPortalConfigTemplateListener(PortalDAO pdcService,
                                          ConfigurationManager cservice,                                        
                                          InitParams params) throws Exception {
    cservice_ = cservice ;
    pdcService_ = pdcService;
    String checkUser = "exo";
    ValueParam valueParam = params.getValueParam("check.user");
    if(valueParam != null) checkUser = valueParam.getValue();
    if(checkUser == null  || checkUser.trim().length() == 0) checkUser = "exo";    
    if(isInitedDB(checkUser)) return;
    config_ = (NewPortalConfig) params.getObjectParamValues(NewPortalConfig.class).get(0) ;
    initDB();
  }
  
  private boolean isInitedDB(String user) throws Exception {
    PortalConfig pconfig = pdcService_.getPortalConfig(user);
    return pconfig != null;
  }
  
  private void initDB() throws Exception {
    HashSet users = config_.getPredefinedUser();
    Iterator iter  = users.iterator();
    while(iter.hasNext()){
      String user = (String)iter.next();
      System.out.println("creating config for "+user+"...");
      createPortalConfigForUser(user);
      createPageForUser(user);
      createPageNavigationForUser(user);
      createPortletPreferencesForUser(user);
    }
  }
  
  private void createPortalConfigForUser(String owner) throws Exception {    
    PortalConfig pconfig = 
      (PortalConfig)pdcService_.fromXML(getDefaultConfig(owner, "portal"), PortalConfig.class);
    pdcService_.savePortalConfig(pconfig);
  }
  
  private void createPageForUser(String owner) throws Exception {
    PageSet pageSet = 
      (PageSet)pdcService_.fromXML(getDefaultConfig(owner, "pages"), PageSet.class);
    ArrayList<Page> list = pageSet.getPages();
    for(Page page : list) pdcService_.savePage(page);
  }
  
  private void createPageNavigationForUser(String owner) throws Exception {
    PageNavigation navigation = (PageNavigation) pdcService_.fromXML(
        getDefaultConfig(owner, "navigation"), PageNavigation.class);
    pdcService_.savePageNavigation(navigation);
  }
  
  private void createPortletPreferencesForUser(String owner) throws Exception {
    PortletPreferencesSet portletSet = 
      (PortletPreferencesSet)pdcService_.fromXML(
          getDefaultConfig(owner, "portlet-preferences"), PortletPreferencesSet.class);
    ArrayList<PortletPreferences> list = portletSet.getPortlets();
    for(PortletPreferences portlet : list){
      pdcService_.savePortletPreferencesConfig(portlet);
    }
  } 
  
  private String getDefaultConfig(String owner, String type) throws Exception {
    String config = null;
    String templateLoc = config_.getTemplateLocation() ;
    if(config_.isPredefinedUser(owner)) {
      String location = config_.getTemplateLocation() ;
      config = IOUtil.getStreamContentAsString(
            cservice_.getInputStream(location + "/" + owner +"/"+type+".xml"));      
    }else {
      InputStream is = cservice_.getInputStream(templateLoc + "/"+owner+"/" +type+".xml");
      String template = IOUtil.getStreamContentAsString(is);
      config = StringUtils.replace(template, "@owner@", owner);
    }
    return config;
  }
  
}
