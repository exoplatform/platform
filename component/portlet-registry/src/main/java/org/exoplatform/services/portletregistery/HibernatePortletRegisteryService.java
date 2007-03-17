/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.services.portletregistery;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.IdentifierUtil;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 */
public class HibernatePortletRegisteryService implements PortletRegisteryService {
  
  private static final String queryFindAllPortletCategories =
    "from u in class org.exoplatform.services.portletregistery.PortletCategory";
  private static final String queryPortletCategoryById =
    "from portletCategory in class org.exoplatform.services.portletregistery.PortletCategory " +
    "where portletCategory.id = ?";
  private static final String queryPortletCategoryByName =
    "from portletCategory in class org.exoplatform.services.portletregistery.PortletCategory " +
    "where portletCategory.portletCategoryName = ?";
  
  private static final String queryPortletByCategory =
    "from portlet in class org.exoplatform.services.portletregistery.Portlet " +
    "where portlet.portletCategoryId = ?";
  private static final String queryPortletById =
    "from portlet in class org.exoplatform.services.portletregistery.Portlet where portlet.id = ?";
  private static final String queryPortletByDisplayName =
    "from p in class org.exoplatform.services.portletregistery.Portlet " +
    "where p.portletCategoryId = ? and p.displayName = ?";
  
  private static final String queryRoleByPortlet =
    "from pr in class org.exoplatform.services.portletregistery.PortletPermission " +
    "where pr.portletId = ?";
  private static final String queryRoleById =
    "from pr in class org.exoplatform.services.portletregistery.PortletPermission where pr.id = ?";
  
  private HibernateService hservice_;
  private List defaultPortletPermissions_ ;
  
  public HibernatePortletRegisteryService(HibernateService hservice, InitParams params) {
    hservice_ = hservice ;
    defaultPortletPermissions_ = params.getObjectParamValues(PortletPermission.class) ;
  }
  
  public PortletCategory createPortletCategoryInstance() {
    return new PortletCategory();
  }
  
  public List getPortletCategories() throws Exception {
    Session session = hservice_.openSession();
    return getPortletCategories(session) ;
  }
  
  private List getPortletCategories(Session session) throws Exception {
    return session.createQuery(queryFindAllPortletCategories).list();
  }
  
  public PortletCategory getPortletCategory(String id) throws Exception {
    Session session = hservice_.openSession();
    PortletCategory category = getPortletCategory(id, session);
    return category;
  }
  
  private PortletCategory getPortletCategory(String id, Session session) throws Exception {
    List l = session.createQuery(queryPortletCategoryById).setString(0, id).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet category " + id + " does not exist",
          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
    }
    return (PortletCategory) l.get(0);
  }
  
  public PortletCategory addPortletCategory(PortletCategory portletCategory) throws Exception {
    Session session = hservice_.openSession();
    portletCategory = addPortletCategory(portletCategory, session);
    session.flush();
    return portletCategory;
  }
  
  private PortletCategory addPortletCategory(PortletCategory portletCategory, Session session) throws Exception {
    Date now = new Date();
    portletCategory.setId(portletCategory.getPortletCategoryName().replace(' ', '_'));
    portletCategory.setCreatedDate(now);
    portletCategory.setModifiedDate(now); 
    session.save(portletCategory);
    return portletCategory;
  }
  
  public PortletCategory updatePortletCategory(PortletCategory portletCategory) throws Exception {
    Session session = hservice_.openSession();
    portletCategory = updatePortletCategory(portletCategory, session);
    session.flush();
    return portletCategory;
  }
  
  private PortletCategory updatePortletCategory(PortletCategory portletCategory, Session session) throws Exception {
    Date now = new Date();
    portletCategory.setModifiedDate(now);
    session.update(portletCategory);
    return portletCategory;
  }
  
  public PortletCategory removePortletCategory(String id) throws Exception {
    Session session = hservice_.openSession();
    PortletCategory category = removePortletCategory(id, session);
    session.flush();
    return category;
  }
  
  private PortletCategory removePortletCategory(String id, Session session) throws Exception {
    List l = session.createQuery(queryPortletCategoryById).setString(0, id).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet category " + id + " does not exist",
          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
    }
    PortletCategory category = (PortletCategory) l.get(0);
    List portlets = getPortlets(category.getId(), session) ;
    for(Object obj : portlets) {
      Portlet portlet = (Portlet) obj;
      removePortlet(portlet.getId(), session) ;
    }
    session.delete(category);
    return category;
  }
  
  public PortletCategory removePortletCategoryByName(String name) throws Exception {
    Session session = hservice_.openSession();
    PortletCategory category = removePortletCategoryByName(name, session);
    session.flush();
    return category;
  }
  
  private PortletCategory removePortletCategoryByName(String name, Session session) throws Exception {
    List l = session.createQuery(queryPortletCategoryByName).setString(0, name).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet category " + name + " does not exist",
          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
    }
    PortletCategory category = (PortletCategory) l.get(0) ;
    List portlets = getPortlets(category.getId(), session) ;
    for(Object obj : portlets) {
      Portlet portlet = (Portlet)obj ;
      removePortlet(portlet.getId(), session) ;
    }
    session.delete(category);
    return category;
  }
  
  public PortletCategory findPortletCategoryByName(String portletCategoryName) throws Exception {
    Session session = hservice_.openSession();
    PortletCategory category = findPortletCategoryByName(portletCategoryName, session);
    return category;
  }
  
  private PortletCategory findPortletCategoryByName(String portletCategoryName, Session session) throws Exception {
    List l = session.createQuery(queryPortletCategoryByName).setString(0, portletCategoryName).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet category " + portletCategoryName + " does not exist",
          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
    }
    return (PortletCategory) l.get(0);
  }
  
  public List getPortlets(String portletCategoryId) throws Exception {
    Session session = hservice_.openSession();
    List portlets = getPortlets(portletCategoryId, session);
    return portlets;
  }
  
  private List getPortlets(String portletCategoryId, Session session) throws Exception {
    return session.createQuery(queryPortletByCategory).setString(0, portletCategoryId).list();
  }
  
  public Portlet getPortlet(String id) throws Exception {
    Session session = hservice_.openSession();
    Portlet portlet = getPortlet(id, session);
    return portlet;
  }
  
  private Portlet getPortlet(String id, Session session) throws Exception {
    List l = session.createQuery(queryPortletById).setString(0, id).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet " + id + " does not exist",
          PortletRegisteryException.PORTLET_NOT_FOUND);
    }
    return (Portlet) l.get(0);
  }
  
  public Portlet addPortlet(PortletCategory category, Portlet portlet) throws Exception {
    Session session = hservice_.openSession();
    portlet = addPortlet(category, portlet, session);
    session.flush();
    return portlet;
  }
  
  private Portlet addPortlet(PortletCategory category, Portlet portlet, Session session) throws Exception {
    Date now = new Date();
    String id = category.getId() + "/" + portlet.getPortletName().replace(' ', '_') ;
    portlet.setId(id);
    portlet.setCreatedDate(now);
    portlet.setModifiedDate(now);
    portlet.setPortletCategoryId(category.getId());
    session.save(portlet);
    return portlet;
  }
  
  public Portlet removePortlet(String id) throws Exception {
    Session session = hservice_.openSession();
    Portlet portlet = removePortlet(id, session);
    session.flush();
    return portlet;
  }
  
  private Portlet removePortlet(String id, Session session) throws Exception {
    List l = session.createQuery(queryPortletById).setString(0, id).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet " + id + " does not exist",
                                           PortletRegisteryException.PORTLET_NOT_FOUND);
    }
    Portlet portlet = (Portlet) l.get(0);
    List entries = session.createQuery(queryRoleByPortlet).setString(0, portlet.getId()).list();
    for(Object entry : entries){
      session.delete(entry);
    }
    session.delete(portlet);
    return portlet;
  }
  
  public Portlet updatePortlet(Portlet portlet) throws Exception {
    Session session = hservice_.openSession();
    portlet = updatePortlet(portlet, session);
    session.flush();
    return portlet;
  }
  
  private Portlet updatePortlet(Portlet portlet, Session session) throws Exception {
    Date now = new Date();
    portlet.setModifiedDate(now);
    session.update(portlet);
    return portlet;
  }
  
  @SuppressWarnings("unused")
  public void findPortletByDisplayName(String portletCategory, String displayName, Session session) throws Exception {
    Object[] args = new Object[]{portletCategory, displayName};
    Type[] types = new Type[]{Hibernate.STRING, Hibernate.STRING};
    
    Query query = session.createQuery(queryPortletByDisplayName);   
    List l = query.setString(0, portletCategory).setString(1, displayName).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("Portlet not found", PortletRegisteryException.PORTLET_NOT_FOUND);
    }
  }
  
  public Portlet createPortletInstance() { return new Portlet(); }
  
  public List getPortletPermissions(String portletId) throws Exception {
    Session session = hservice_.openSession();
    return  getPortletRoles(portletId, session);
  }
  
  private List getPortletRoles(String portletId, Session session) throws Exception {
    return session.createQuery(queryRoleByPortlet).setString(0, portletId).list();
  }

  public PortletPermission getPortletPermission(String id) throws Exception {
    Session session = hservice_.openSession();
    return  getPortletRole(id, session);
  }
  
  private PortletPermission getPortletRole(String id, Session session) throws Exception {
    List l = session.createQuery(queryRoleById).setString(0, id).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet role " + id + " does not exist",
                                           PortletRegisteryException.PORTLET_ROLE_NOT_FOUND);
    }
    return (PortletPermission) l.get(0);
  }
  
  public PortletPermission addPortletPermission(Portlet portlet, PortletPermission portletRole) throws Exception {
    Session session = hservice_.openSession();
    portletRole = addPortletRole(portlet, portletRole, session);
    session.flush();
    return portletRole;
  }
  
  private PortletPermission addPortletRole(Portlet portlet, PortletPermission portletRole, Session session) throws Exception {
    if (portletRole.getId() == null) portletRole.setId(IdentifierUtil.generateUUID(portletRole));
    portletRole.setPortletId(portlet.getId());
    session.save(portletRole);
    return portletRole;
  }
  
  public PortletPermission removePortletPermission(String id) throws Exception {
    Session session = hservice_.openSession();
    PortletPermission portletRole = removePortletRole(id, session);
    session.flush();
    return portletRole;
  }
  
  private PortletPermission removePortletRole(String id, Session session) throws Exception {
    List l = session.createQuery(queryRoleById).setString(0, id).list();
    if (l.size() == 0) {
      throw new PortletRegisteryException("the portlet role " + id + " does not exist",
                                          PortletRegisteryException.PORTLET_ROLE_NOT_FOUND);
    }
    PortletPermission portletRole = (PortletPermission) l.get(0);
    session.delete(portletRole);
    return portletRole;
  }
  
  public PortletPermission updatePortletRole(PortletPermission portletRole) throws Exception {
    Session session = hservice_.openSession();
    portletRole = updatePortletRole(portletRole, session);
    session.flush();
    return portletRole;
  }
  
  private PortletPermission updatePortletRole(PortletPermission portletRole, Session session) throws Exception {
    session.save(portletRole);
    return portletRole;
  }
  
  public void clearPortletPermissions(String portletId) throws Exception {
    Session session = hservice_.openSession();
    clearPortletRoles(portletId, session);
    session.flush();
  }
  
  private void clearPortletRoles(String portletId, Session session) throws Exception {
    List currentRoles = getPortletRoles(portletId, session);
    for (Iterator iterator = currentRoles.iterator(); iterator.hasNext();) {
      PortletPermission portletRole = (PortletPermission) iterator.next();
      removePortletRole(portletRole.getId(), session);
    }
  }
  
  public PortletPermission createPortletPermissionInstance() {
    return new PortletPermission();
  }
  
  public void updatePortletRoles(String portletId, Collection currentRoles) throws Exception {
    Session session = hservice_.openSession();
    clearPortletRoles(portletId, session);
    Portlet portlet = getPortlet(portletId, session);
    for (Iterator iterator = currentRoles.iterator(); iterator.hasNext();) {
//      String role = (String) iterator.next();
      PortletPermission portletRole = createPortletPermissionInstance();
      //portletRole.setPortletRoleName(role);
      addPortletRole(portlet, portletRole, session);
    }
    session.flush();
  }
  
  public void importPortlets(Collection portletDatas) throws Exception {
    Session session = hservice_.openSession();
    for (Iterator iterator = portletDatas.iterator(); iterator.hasNext();) {
      PortletRuntimeData portletRuntimeData = (PortletRuntimeData) iterator.next();
      String portletCategoryName = portletRuntimeData.getPortletAppName();
      String portletName = portletRuntimeData.getPortletName();
      PortletCategory portletCategory = null;
      try {
        portletCategory = findPortletCategoryByName(portletCategoryName, session);
      } catch (Exception e) {
        portletCategory = createPortletCategoryInstance();
        portletCategory.setPortletCategoryName(portletCategoryName);
        portletCategory = addPortletCategory(portletCategory, session);
      }
      try {
        findPortletByDisplayName(portletCategory.getId(), portletName, session);
      } catch (Exception e) {
        Portlet portlet = createPortletInstance();
        portlet.setDisplayName(portletName);
        portlet.setPortletApplicationName(portletCategoryName);
        portlet.setPortletName(portletName);
        addPortlet(portletCategory, portlet, session);
        for(Object defaultPortletPermission : defaultPortletPermissions_) {
          PortletPermission defaultPermission = (PortletPermission) defaultPortletPermission;
          PortletPermission newPermission = createPortletPermissionInstance();
          newPermission.setMembership(defaultPermission.getMembership()) ;
          newPermission.setGroupId(defaultPermission.getGroupId()) ;
          newPermission.setDescription(defaultPermission.getDescription()) ;
          addPortletRole(portlet, newPermission, session);
        }
      }
    }
    session.flush() ;
  }
  
  public void clearRepository() throws Exception {
    Session session = hservice_.openSession();
    List entries = session
        .createQuery("from pr in class org.exoplatform.services.portletregistery.PortletPermission").list();
    for (Object entry : entries){
      session.delete(entry);
    }
    entries = session
        .createQuery("from pr in class org.exoplatform.services.portletregistery.Portlet").list();
    for (Object entry : entries){
      session.delete(entry);
    }
    entries = session
        .createQuery("from pr in class org.exoplatform.services.portletregistery.PortletCategory").list();
    for (Object entry : entries){
      session.delete(entry);
    }

  }
  
}