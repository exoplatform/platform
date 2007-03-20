/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.services.portletregistery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.exoplatform.commons.utils.IdentifierUtil;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;

/**
 * Created y the eXo platform team
 * User: lebienthuy@gmail.com
 * Date: 3/7/2007
 */
public class JCRPortletRegisteryService extends BaseJCRService implements PortletRegisteryService {

  private List defaultPortletPermissions_ ;

  public JCRPortletRegisteryService(InitParams params) {
    defaultPortletPermissions_ = params.getObjectParamValues(PortletPermission.class) ;
  }

  public PortletCategory createPortletCategoryInstance() { return new PortletCategory(); }

  public List getPortletCategories() throws Exception {
    List<PortletCategory> lists = new ArrayList<PortletCategory>();
    Node portletCategoryNode = getPortletRegistryNode(true);
    NodeIterator iterator = portletCategoryNode.getNodes();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      String name = node.getPrimaryNodeType().getName();
      if(PORTLET_CATEGORY_TYPE.equals(name)) lists.add(nodeToPortletCategory(node));
    }
    return lists;
  }

  public PortletCategory getPortletCategory(String id) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, id);
    if(node != null) return nodeToPortletCategory(node);
    throw new PortletRegisteryException("the portlet category " + id + " does not exist",
                                        PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
  }

  public PortletCategory addPortletCategory(PortletCategory portletCategory) throws Exception {
    Node rootNode = getPortletRegistryNode(true);
    if(rootNode.hasNode(portletCategory.getPortletCategoryName()))  return portletCategory;
    portletCategory.setId(portletCategory.getPortletCategoryName().replace(' ', '_'));
    Node node = rootNode.addNode(portletCategory.getPortletCategoryName(), PORTLET_CATEGORY_TYPE);
    portletCategoryToNode(portletCategory, node);
    rootNode.save();
    getSession().save();
    return portletCategory;
  }

  public PortletCategory updatePortletCategory(PortletCategory portletCategory) throws Exception {
    Node portletNode = getPortletRegistryNode(false);
    Node node = portletNode.getNode( portletCategory.getPortletCategoryName());
    portletCategoryToNode(portletCategory, node);
    portletNode.save();
    getSession().save();
    return portletCategory;
  }

  public PortletCategory removePortletCategory(String id) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, id);
    if(node == null) {
      throw new PortletRegisteryException("the portlet category " + id + " does not exist",
                                          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
    }
    PortletCategory category = nodeToPortletCategory(node);
    Node parentNode  = node.getParent();
    node.remove();
    parentNode.save();
    getSession().save();
    return category;
  }

  public PortletCategory removePortletCategoryByName(String name) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), NAME, name);
    if(node == null) {
      throw new PortletRegisteryException("the portlet category " + name + " does not exist",
                                          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
    }
    PortletCategory category = nodeToPortletCategory(node);
    Node parentNode  = node.getParent();
    node.remove();
    parentNode.save();
    getSession().save();
    return category;
  }

  public PortletCategory findPortletCategoryByName(String name) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), NAME, name);
    if(node != null)  return  nodeToPortletCategory(node);
    throw new PortletRegisteryException("the portlet category " + name + " does not exist",
                                        PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND);
  }

  public List getPortlets(String portletCategoryId) throws Exception {
    List<Portlet> list = new ArrayList<Portlet>();
    Node categoryNode = getNode(getPortletRegistryNode(false), ID, portletCategoryId);
    NodeIterator iterator  = categoryNode.getNodes();
    while(iterator.hasNext()){
      Node portletNode = iterator.nextNode();
      list.add(nodeToPortlet(portletNode ));
    }
    return list;
  }

  public Portlet getPortlet(String id) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, id);
    if(node != null) return nodeToPortlet(node);
    throw new PortletRegisteryException("the portlet " + id + " does not exist",
                                        PortletRegisteryException.PORTLET_NOT_FOUND);
  }

  public Portlet addPortlet(PortletCategory category, Portlet portlet) throws Exception {
    portlet.setId(category.getId() + "/" + portlet.getPortletName().replace(' ', '_'));
    portlet.setPortletCategoryId(category.getId());
    
    Node rootNode = getPortletRegistryNode(true);
    Node categoryNode ;
    if(rootNode.hasNode(category.getPortletCategoryName())) { 
      categoryNode = rootNode.getNode(category.getPortletCategoryName());
    } else {
      categoryNode = rootNode.addNode(category.getPortletCategoryName(), PORTLET_CATEGORY_TYPE);
      portletCategoryToNode(category, categoryNode);
      rootNode.save();
      getSession().save();
    }

    Node portletNode ;
    if(categoryNode.hasNode(portlet.getPortletName())) { 
      portletNode = categoryNode.getNode(portlet.getPortletName());
      return nodeToPortlet(portletNode);
    }
    
    portletNode = categoryNode.addNode(portlet.getPortletName(), PORTLET_TYPE);
    portletToNode(portlet, portletNode);
    categoryNode.save();
    getSession().save();
    return portlet;
    
  }

  public Portlet removePortlet(String id) throws Exception {
    Node node = getPortletRegistryNode(false);
    Node portletNode = getNode(getPortletRegistryNode(false), ID, id);
    if(portletNode == null) {
      throw new PortletRegisteryException("the portlet " + id + " does not exist",
                                          PortletRegisteryException.PORTLET_NOT_FOUND);
    }
    Portlet k = nodeToPortlet(portletNode);
    portletNode.remove();
    node.save();
    getSession().save();
    return k;
  }

  public Portlet updatePortlet(Portlet portlet) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, portlet.getId());
    if(node == null) return null;
    portletToNode(portlet, node);
    node.save();
    getSession().save();
    return portlet;
  }

  @SuppressWarnings("unused")
  public Portlet findPortletByDisplayName(String portletCategoryId, String displayName) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, portletCategoryId);
    if(node == null) {
      throw new PortletRegisteryException("Portlet not found", PortletRegisteryException.PORTLET_NOT_FOUND);
    }
    Node portletNode = getNode(node, DISPLAY_NAME, displayName);
    if(portletNode == null) {
      throw new PortletRegisteryException("Portlet not found", PortletRegisteryException.PORTLET_NOT_FOUND);
    }
    return nodeToPortlet(portletNode);
  }

  public Portlet createPortletInstance() { return new Portlet(); }

  public List getPortletPermissions(String portletId) throws Exception {
    Node portletNode = getNode(getPortletRegistryNode(false), ID, portletId);
    NodeIterator iterator = portletNode.getNodes();
    List<PortletPermission> result = new ArrayList<PortletPermission>();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      result.add(nodeToPortletPermission(node));
    }
    return result;
  }

  public PortletPermission getPortletPermission(String id) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, id);
    if(node != null) return nodeToPortletPermission(node);
    throw new PortletRegisteryException("the portlet role " + id + " does not exist",
                                        PortletRegisteryException.PORTLET_ROLE_NOT_FOUND);
  }

  public PortletPermission addPortletPermission(Portlet portlet, PortletPermission portletRole) throws Exception {
    if (portletRole.getId() == null) portletRole.setId(IdentifierUtil.generateUUID(portletRole));
    portletRole.setPortletId(portlet.getId());
    
    Node portletNode = getPortletNode(portlet);
    if(portletNode == null) return null;   
    
    Node permissionNode = portletNode.addNode(portletRole.getId(), PORTLET_PERMISSION_TYPE);
    portletPermissionToNode(portletRole, permissionNode);
    portletNode.save();
    getSession().save();
    return portletRole;
  }

  public PortletPermission removePortletPermission(String id) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, id);
    if (node == null) {
      throw new PortletRegisteryException("the portlet role " + id + " does not exist",
                                          PortletRegisteryException.PORTLET_ROLE_NOT_FOUND);
    }
    PortletPermission permission = nodeToPortletPermission(node);
    Node parentNode = node.getParent();
    node.remove();
    parentNode.save();
    getSession().save();
    return permission;
  }

  public PortletPermission updatePortletRole(PortletPermission portletRole) throws Exception {
    Node node = getNode(getPortletRegistryNode(false), ID, portletRole.getId());
    portletPermissionToNode(portletRole, node);
    return portletRole;
  }

  public void clearPortletPermissions(String portletId) throws Exception {
    Node portletNode = getNode(getPortletRegistryNode(false), ID, portletId);
    NodeIterator iterator = portletNode.getNodes();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      String name = node.getPrimaryNodeType().getName();
      if(PORTLET_PERMISSION_TYPE.equals(name)) node.remove();
    }
    portletNode.save();
    getSession().save();    
  }

  public PortletPermission createPortletPermissionInstance() {
    return new PortletPermission();
  }

  public void updatePortletRoles(String portletId, Collection currentRoles) throws Exception {
    clearPortletPermissions(portletId);
    Portlet portlet = nodeToPortlet(getNode(getPortletRegistryNode(false), ID, portletId));
    for (Iterator iterator = currentRoles.iterator(); iterator.hasNext();) {
//    String role = (String) iterator.next();
      PortletPermission portletRole = createPortletPermissionInstance();
      //portletRole.setPortletRoleName(role);
      addPortletPermission(portlet, portletRole);
    }
  }

  public void importPortlets(Collection portletDatas) throws Exception {
    Iterator iterator = portletDatas.iterator();
    while(iterator.hasNext()) {
      PortletRuntimeData portletRuntimeData = (PortletRuntimeData) iterator.next();
      String portletCategoryName = portletRuntimeData.getPortletAppName();
      String portletName = portletRuntimeData.getPortletName();
      PortletCategory portletCategory = null;

      try {
        portletCategory = findPortletCategoryByName(portletCategoryName);
      } catch (Exception e) {
        portletCategory = createPortletCategoryInstance();
        portletCategory.setPortletCategoryName(portletCategoryName);
        portletCategory = addPortletCategory(portletCategory);
      }
      

      Portlet portlet = null;
      try{
        findPortletByDisplayName(portletCategory.getId(), portletName);
      } catch (Exception e) {
        portlet = createPortletInstance();
        portlet.setDisplayName(portletName);
        portlet.setPortletApplicationName(portletCategoryName);
        portlet.setPortletName(portletName);
        addPortlet(portletCategory, portlet);
        
        for(Object defaultPortletPermission : defaultPortletPermissions_) {
          PortletPermission defaultPermission = (PortletPermission) defaultPortletPermission;
          PortletPermission newPermission = createPortletPermissionInstance();
          newPermission.setMembership(defaultPermission.getMembership()) ;
          newPermission.setGroupId(defaultPermission.getGroupId()) ;
          newPermission.setDescription(defaultPermission.getDescription()) ;
          addPortletPermission(portlet, newPermission);
        }
      }
    }
  }

  public void clearRepository() throws Exception {
    Node homeNode = getPortletRegistryNode(false);
    Node parentNode = homeNode.getParent();
    homeNode.remove();
    parentNode.save();
    getSession().save();
    parentNode.addNode(PORTLET_TYPE);
    parentNode.save();
    getSession().save();
  }
}