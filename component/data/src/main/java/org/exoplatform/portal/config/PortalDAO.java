/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.portal.portlet.PortletPreferences;
/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: Jun 14, 2003
 * Time: 1:12:22 PM
 */
public interface PortalDAO {
  
  public PortalConfig getPortalConfig(String owner) throws Exception ;
  
  public PageList getPortalConfigs() throws Exception ;
  
  public void savePortalConfig(PortalConfig config) throws Exception  ;
  
  public void removePortalConfig(String owner) throws Exception ;
  
  
  public Page getPage(String pageId) throws Exception ;
  
  public PageSet getPageOfOwner(String owner) throws Exception ;
  
  public void  savePage(Page page) throws Exception  ;
  
  public void removePage(String pageId) throws Exception ;
  
  public void removePageOfOwner(String owner) throws Exception ;

  
  public void savePageNavigation(PageNavigation navigation) throws Exception ;
  
  public void removePageNavigation(String owner) throws Exception ;
  
  public PageNavigation getPageNavigation(String owner) throws Exception ;
  
  
  public void savePortletPreferencesConfig(PortletPreferences portletPreferences) throws Exception  ;
  
  
  public PageList  findDataDescriptions(Query q) throws Exception ;
  
  public Data getData(String id) throws Exception ;
  
  public void removeData(String id) throws Exception ;
  
  public void removeData(String owner, String type) throws Exception ;
  
  public String toXML(Object object) throws Exception ;
  
  public Object fromXML(String xml, Class type) throws Exception ;
   
}