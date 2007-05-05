/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.NewPortalConfig;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 20, 2006  
 */
public class NewContentNavListener extends BaseComponentPlugin {
  
  private ConfigurationManager cservice_ ;
  private ContentDAO contentService_;
  
  public NewContentNavListener(ContentDAO contentService,
                               ConfigurationManager cservice,
                               InitParams params) throws Exception {
    cservice_ = cservice ;
    contentService_ = contentService;    
    
    String checkPortal = "site";
    ValueParam valueParam = params.getValueParam("check.portal");
    if(valueParam != null) checkPortal = valueParam.getValue();
    if(checkPortal == null  || checkPortal.trim().length() == 0) checkPortal = "site";    
    if(isInitedDB(checkPortal)) return;
    
    List list = params.getObjectParamValues(NewPortalConfig.class);
    for (Object ele : list) {
      NewPortalConfig config  = (NewPortalConfig)ele;
      initDB(config);  
    }
  }
  
  private boolean isInitedDB(String user) throws Exception {
    ContentNavigation nav = contentService_.get(user);
    return nav != null;
  }
  
  private void initDB(NewPortalConfig config) throws Exception {
    HashSet users = config.getPredefinedOwner();
    Iterator iter  = users.iterator();
    while(iter.hasNext()){
      String user = (String)iter.next();
      createContentConfigForUser(config, user);
    }
  }
  
  private void createContentConfigForUser(NewPortalConfig pConfig, String owner) throws Exception {    
    String config  = null;
    String templateLoc = pConfig.getTemplateLocation() ;
    String ownerType = pConfig.getOwnerType();
    if(pConfig.isPredefinedOwner(owner)) {
      String id = pConfig.getTemplateLocation() + "/" + ownerType + "/" + owner +"/content.xml";
      config = IOUtil.getStreamContentAsString(cservice_.getInputStream(id));      
    } else {
      InputStream is = cservice_.getInputStream(templateLoc + "/"+ ownerType + "/" + owner +"/content.xml");
      String template = IOUtil.getStreamContentAsString(is);
      config = StringUtils.replace(template, "@owner@", owner);
    }   
    if(config == null) return;
    ContentNavigation contentNavigation =  fromXML(config, ContentNavigation.class); 
    contentService_.save(contentNavigation) ;
  }
  
  public <T> T fromXML(String xml, Class<T> type) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes()) ;    
    IBindingFactory bfact = BindingDirectory.getFactory(type);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return type.cast(uctx.unmarshalDocument(is, null));
  }
  
}
