/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.NewPortalConfig;
import org.exoplatform.portal.content.model.ContentNavigation;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 20, 2006  
 */
public class UserContentNavigationInitListener extends BaseComponentPlugin {
  
  private NewPortalConfig config_ ;
  private ConfigurationManager cservice_ ;
  private ContentDAO contentService_;
  
  public UserContentNavigationInitListener(ContentDAO contentService,
                                           ConfigurationManager cservice,
                                           InitParams params) throws Exception {
    cservice_ = cservice ;
    contentService_ = contentService;    
    String checkUser = "exo";
    ValueParam valueParam = params.getValueParam("check.user");
    if(valueParam != null) checkUser = valueParam.getValue();
    if(checkUser == null  || checkUser.trim().length() == 0) checkUser = "exo";    
    if(isInitedDB(checkUser)) return;
    config_ = (NewPortalConfig) params.getObjectParamValues(NewPortalConfig.class).get(0) ;
    initDB();
  }
  
  private boolean isInitedDB(String user) throws Exception {
    ContentNavigation nav = contentService_.getContentNavigation(user);
    return nav != null;
  }
  
  private void initDB() throws Exception {
    HashSet users = config_.getPredefinedUser();
    Iterator iter  = users.iterator();
    while(iter.hasNext()){
      String user = (String)iter.next();
      createContentConfigForUser(user);
    }
  }
  
  private void createContentConfigForUser(String owner) throws Exception {    
    String config  = null;
    String templateLoc = config_.getTemplateLocation() ;
    if(config_.isPredefinedUser(owner)) {
      String id = config_.getTemplateLocation() + "/" + owner + "/content.xml" ;
      config = IOUtil.getStreamContentAsString(cservice_.getInputStream(id));      
    } else {
      InputStream is = cservice_.getInputStream(templateLoc + "/"+ owner+"/content.xml");
      String template = IOUtil.getStreamContentAsString(is);
      config = StringUtils.replace(template, "@owner@", owner);
    }   
    if(config == null) return;
    ContentNavigation contentNavigation = 
      (ContentNavigation)contentService_.fromXML(config, ContentNavigation.class); 
    contentService_.saveContentNavigation(contentNavigation) ;
  }
  
}
