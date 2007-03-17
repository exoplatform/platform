/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.services.portletregistery;

import java.util.Collection;
import java.util.List;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 */
public interface PortletRegisteryService {
  
  public PortletCategory createPortletCategoryInstance();
  
  public List getPortletCategories() throws Exception;
  
  
  public PortletCategory getPortletCategory(String id) throws Exception;
  
  public PortletCategory addPortletCategory(PortletCategory portletCategory) throws Exception;
  
  public PortletCategory updatePortletCategory(PortletCategory portletCategory) throws Exception;
  
  public PortletCategory removePortletCategory(String id) throws Exception;
  
  public PortletCategory removePortletCategoryByName(String name) throws Exception;
  
  
  public List getPortlets(String portletCategoryId) throws Exception;
  
  public Portlet getPortlet(String id) throws Exception;

  public Portlet addPortlet(PortletCategory category, Portlet portlet) throws Exception;
  
  public Portlet removePortlet(String id) throws Exception;
  
  public Portlet updatePortlet(Portlet portlet) throws Exception;
  
  public Portlet createPortletInstance();
  
  public List getPortletPermissions(String portletId) throws Exception;
  
  public PortletPermission getPortletPermission(String id) throws Exception;
  
  public PortletPermission addPortletPermission(Portlet portlet, PortletPermission portletRole) throws Exception;
  
  public PortletPermission removePortletPermission(String id) throws Exception;
  
  public PortletCategory findPortletCategoryByName(String name) throws Exception;
  
  public PortletPermission updatePortletRole(PortletPermission portletRole) throws Exception;
  
  public void clearPortletPermissions(String portletId) throws Exception;
  
  public PortletPermission createPortletPermissionInstance();
  
  public void updatePortletRoles(String portletId, Collection currentRoles) throws Exception;
  
  public void importPortlets(Collection portletDatas) throws Exception;
  
  public void clearRepository() throws Exception;
  
}