/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
/**
 * @author Tuan Nguyen (tuan08@users.sourceforge.net)
 * @since Dec 4, 2004
 * @version $Id$
 */
public class UserPortalConfigService {
  
  private OrganizationService orgService_ ;
  private SharedConfigDAO sharedConfig_;
  private PortalDAO pdcService_;
  private UserACL userACL_ ;
  
  private String defaultUser = "site";
  
  public UserPortalConfigService(InitParams params,
                                 OrganizationService  orgService,
                                 SharedConfigDAO sharedConfig,
                                 PortalDAO pdcService, UserACL userACL) throws Exception {
    orgService_ = orgService ;
    sharedConfig_ = sharedConfig;
    pdcService_ = pdcService;
    userACL_ = userACL;
    
    ValueParam valueParam = params.getValueParam("default.user.template");
    if(valueParam != null) defaultUser = valueParam.getValue();
    if(defaultUser == null  || defaultUser.trim().length() == 0) defaultUser = "site";
    initDb(params) ;
  }
  
  public SharedConfigDAO getSharedConfigDAO() { return sharedConfig_; }
  
  @SuppressWarnings("unchecked")
  private void initDb(InitParams params) throws Exception {   
    List list = sharedConfig_.getSharedPortals() ;    
    if(list != null && list.size() > 0)  return;
    list = params.getObjectParamValues(SharedPortal.class);
    sharedConfig_.saveSharedPortal(list);

    list = sharedConfig_.getSharedNavigations();
    if(list != null && list.size() > 0)  return;
    list = params.getObjectParamValues(SharedNavigation.class);
    if(list != null) sharedConfig_.saveSharedNavigation(list);
  }
  
  @SuppressWarnings("unused")
  public Page getPage(String pageId, String accessUser) throws Exception {
    Page page = pdcService_.getPage(pageId);
    if(page == null) return null;
    if(!userACL_.hasPermission(page.getOwner(), accessUser, page.getViewPermission())) return null;
    return page;
  }
  
  public UserPortalConfig computeUserPortalConfig(String portalOwner, 
                                                  String accessUser) throws Exception {
    UserPortalConfig config  = new UserPortalConfig() ;
    //TODO  should merge the method  findSharedPortal and  findSharedNavigation to  avoid the
    //method  organization service findMemberships are called  twice  
    
    //find shared portal and shared navigation    
    List<SharedNavigation> sharedNavs = new ArrayList<SharedNavigation>() ;
    SharedPortal sharedPortal = null ;
    
    Collection memberships = orgService_.getMembershipHandler().findMembershipsByUser(portalOwner);
    Iterator mitr = memberships.iterator() ;
    while(mitr.hasNext()) {
      Membership m = (Membership) mitr.next() ;
      SharedPortal cp = sharedConfig_.getSharedPortal(m.getGroupId()) ;     
      if(cp != null && cp.getMembership().equals(m.getMembershipType())) {
        if(sharedPortal == null 
            || cp.getPriority() < sharedPortal.getPriority()) sharedPortal = cp;
      }
      
      SharedNavigation cn  = sharedConfig_.getSharedNavigation(m.getGroupId());    
      if(cn != null && cn.getMembership().equals(m.getMembershipType())) sharedNavs.add(cn) ;      
    }   
    
    config.setPortal(getPortalConfig(sharedPortal, portalOwner, accessUser)) ;
    List<PageNavigation> navigations = getPageNavigation(sharedNavs, portalOwner, accessUser);
    userACL_.computeNavigation(accessUser, navigations);
    config.setNavigations(navigations) ;
    return config ;
  }
  
  @SuppressWarnings("unused")
  private PortalConfig getPortalConfig(SharedPortal sp, 
                                       String portalOwner, String accessUser) throws Exception {
    String portal = portalOwner ;
    if(sp != null)  portal = sp.getPortal() ;
    PortalConfig pconfig = pdcService_.getPortalConfig(portal);
    if(pconfig == null) pconfig = pdcService_.getPortalConfig(defaultUser);
    boolean shared = (sp != null && !sp.getPortal().equals("#{owner}"));
    if(!shared)  return pconfig;
    pconfig.setOwner(portalOwner) ;
    pconfig.setEditPermission(null) ;
    return pconfig;
  }   
  
  @SuppressWarnings("unused")
  private List<PageNavigation> getPageNavigation(
      List<SharedNavigation> shareNavs, String portalOwner, String accessUser) throws Exception{    
//  load  the page navigation according to the portal owner username
    PageNavigation pnav =  pdcService_.getPageNavigation(portalOwner) ;   
    if(pnav == null && (shareNavs == null || shareNavs.size() < 1)){
      pnav = pdcService_.getPageNavigation(defaultUser);
    }
    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    if(pnav != null) navigations.add(pnav);
        
    Collections.sort(shareNavs, new CommunityNavigationComparator());    
    for(SharedNavigation shareNav : shareNavs){
      PageNavigation pageNav = pdcService_.getPageNavigation(shareNav.getNavigation());
      if(pageNav == null) continue;
      boolean add  = true;
      for(PageNavigation ele : navigations){
        if(ele.getOwner().equals(pageNav.getOwner())) {
          add = false;
          break;
        }
      }
      if(add) navigations.add(pageNav);
    }    
    
    Collections.sort(navigations, new NavigationComparator());   
    return navigations;
  }  
  
  @SuppressWarnings("unused")
  public void initListener(ComponentPlugin listener) { }
  
  private class CommunityNavigationComparator implements  Comparator<SharedNavigation> {
    public int compare(SharedNavigation cn1 , SharedNavigation cn2) {
      return cn1.getPriority() - cn2.getPriority() ;
    }
  }
  
  private class NavigationComparator implements  Comparator<PageNavigation> {
    public int compare(PageNavigation cn1 , PageNavigation cn2) {
      return cn1.getPriority() - cn2.getPriority() ;
    }
  }
  
}
