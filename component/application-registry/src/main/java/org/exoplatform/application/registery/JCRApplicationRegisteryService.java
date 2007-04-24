/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registery;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * Created y the eXo platform team
 * User: lebienthuy@gmail.com
 * Date: 3/7/2007
 */
public class JCRApplicationRegisteryService implements ApplicationRegisteryService {
  
  private final String applicationNodeType = "exo:application";
  private final String appCategoryNodeType = "exo:applicationCategory";
  
  private final static String ID = "id";
  private final static String NAME = "name";

  private DataMapper mapper = new DataMapper();

  public JCRApplicationRegisteryService() {
  }
  
  public Application createApplicationInstance() { return new Application(); }
  
  public ApplicationCategory createApplicationCategoryInstance() { return new ApplicationCategory(); }
  

  public List<ApplicationCategory> getApplicationCategories() throws Exception {
    List<ApplicationCategory> lists = new ArrayList<ApplicationCategory>();
    Node portletCategoryNode = mapper.getApplicationRegistryNode(true);
    NodeIterator iterator = portletCategoryNode.getNodes();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      String name = node.getPrimaryNodeType().getName();
      if(appCategoryNodeType.equals(name)) lists.add(mapper.nodeToApplicationCategory(node));
    }
    return lists;
  }
  
  public List<ApplicationCategory> getApplicationCategories(String accessUser) throws Exception {
    return null;
  }

  public void save(ApplicationCategory appCategory) throws Exception {
    Node rootNode = mapper.getApplicationRegistryNode(true);
    appCategory.setName(appCategory.getName().replace(' ', '_'));
    Node node = null;
    if(rootNode.hasNode(appCategory.getName())){
      node = rootNode.getNode( appCategory.getName());
    } else {
      node = rootNode.addNode(appCategory.getName(), appCategoryNodeType);
    }
    mapper.applicationCategoryToNode(appCategory, node);
    rootNode.save();
    mapper.getSession().save();
  }
  
  public void remove(ApplicationCategory category) throws Exception {
    Node node = mapper.getNode(mapper.getApplicationRegistryNode(false), NAME, category.getName());
    if(node == null) return ;
    Node parentNode  = node.getParent();
    node.remove();
    parentNode.save();
    mapper.getSession().save();
  }

  public ApplicationCategory getApplicationCategory(String name) throws Exception {
    Node node = mapper.getNode(mapper.getApplicationRegistryNode(false), NAME, name);
    if(node != null)  return  mapper.nodeToApplicationCategory(node);
    throw new ApplicationRegisteryException("the portlet category " + name + " does not exist", 
                                              ApplicationRegisteryException.APPLICATION_NOT_FOUND );
  }

  public List<Application> getApplications(ApplicationCategory category) throws Exception {
    List<Application> list = new ArrayList<Application>();
    Node categoryNode = mapper.getNode(mapper.getApplicationRegistryNode(false), NAME, category.getName());
    NodeIterator iterator  = categoryNode.getNodes();
    while(iterator.hasNext()){
      Node portletNode = iterator.nextNode();
      list.add(mapper.nodeToApplication(portletNode ));
    }
    return list;
  }

  public Application getApplication(String id) throws Exception {
    Node node = mapper.getNode(mapper.getApplicationRegistryNode(false), ID, id);
    if(node != null) return mapper.nodeToApplication(node);
    throw new ApplicationRegisteryException("the portlet " + id + " does not exist",
                                        ApplicationRegisteryException.APPLICATION_NOT_FOUND);
  }

  public void save(ApplicationCategory category, Application application) throws Exception {
    application.setId(category.getName() + "/" + application.getApplicationName().replace(' ', '_'));
    application.setCategoryName(category.getName());
    
    Node rootNode = mapper.getApplicationRegistryNode(true);
    Node categoryNode ;
    if(rootNode.hasNode(category.getName())) { 
      categoryNode = rootNode.getNode(category.getName());
    } else {
      categoryNode = rootNode.addNode(category.getName(), appCategoryNodeType);
      mapper.applicationCategoryToNode(category, categoryNode);
      rootNode.save();
      mapper.getSession().save();
    }

    Node portletNode ;
    if(categoryNode.hasNode(application.getApplicationName())) { 
      portletNode = categoryNode.getNode(application.getApplicationName());
      return ;
    }
    
    portletNode = categoryNode.addNode(application.getApplicationName(), applicationNodeType);
    mapper.applicationToNode(application, portletNode);
    categoryNode.save();
    mapper.getSession().save();
  }

  public void remove(Application application) throws Exception {
    Node node = mapper.getApplicationRegistryNode(false);
    Node applicationNode = mapper.getNode(mapper.getApplicationRegistryNode(false), ID, application.getId());
    if(applicationNode == null) return;
    applicationNode.remove();
    node.save();
    mapper.getSession().save();
  }

  public void update(Application application) throws Exception {
    Node node = mapper.getNode(mapper.getApplicationRegistryNode(false), ID, application.getId());
    if(node == null) return ;
    mapper.applicationToNode(application, node);
    node.save();
    mapper.getSession().save();
  }

 /* @SuppressWarnings("unused")
  public Application findApplicationByDisplayName(String portletCategoryId, String displayName) throws Exception {
    Node node = mapper.getNode(mapper.getApplicationRegistryNode(false), ID, portletCategoryId);
    if(node == null) {
      throw new ApplicationRegisteryException("Application not found", ApplicationRegisteryException.APPLICATION_NOT_FOUND);
    }
    Node portletNode = mapper.getNode(node, "displayName", displayName);
    if(portletNode == null) {
      throw new ApplicationRegisteryException("Application not found", ApplicationRegisteryException.APPLICATION_NOT_FOUND);
    }
    return mapper.nodeToApplication(portletNode);
  }*/

  
  public void importJSR168Portlets() throws Exception {
    // TODO Auto-generated method stub
    
  }

  /*public void updateApplicationRoles(String portletId, Collection currentRoles) throws Exception {
    clearApplicationPermissions(portletId);
    Application portlet = nodeToApplication(getNode(getApplicationRegistryNode(false), ID, portletId));
    for (Iterator iterator = currentRoles.iterator(); iterator.hasNext();) {
//    String role = (String) iterator.next();
      ApplicationPermission portletRole = createApplicationPermissionInstance();
      //portletRole.setApplicationRoleName(role);
      addApplicationPermission(portlet, portletRole);
    }
  }

  public void importApplications(Collection portletDatas) throws Exception {
    Iterator iterator = portletDatas.iterator();
    while(iterator.hasNext()) {
      ApplicationRuntimeData portletRuntimeData = (ApplicationRuntimeData) iterator.next();
      String portletCategoryName = portletRuntimeData.getApplicationAppName();
      String portletName = portletRuntimeData.getApplicationName();
      ApplicationCategory portletCategory = null;

      try {
        portletCategory = findApplicationCategoryByName(portletCategoryName);
      } catch (Exception e) {
        portletCategory = createApplicationCategoryInstance();
        portletCategory.setApplicationCategoryName(portletCategoryName);
        portletCategory = addApplicationCategory(portletCategory);
      }
      

      Application portlet = null;
      try{
        findApplicationByDisplayName(portletCategory.getId(), portletName);
      } catch (Exception e) {
        portlet = createApplicationInstance();
        portlet.setDisplayName(portletName);
        portlet.setApplicationApplicationName(portletCategoryName);
        portlet.setApplicationName(portletName);
        addApplication(portletCategory, portlet);
        
        for(Object defaultApplicationPermission : defaultApplicationPermissions_) {
          ApplicationPermission defaultPermission = (ApplicationPermission) defaultApplicationPermission;
          ApplicationPermission newPermission = createApplicationPermissionInstance();
          newPermission.setMembership(defaultPermission.getMembership()) ;
          newPermission.setGroupId(defaultPermission.getGroupId()) ;
          newPermission.setDescription(defaultPermission.getDescription()) ;
          addApplicationPermission(portlet, newPermission);
        }
      }
    }
  }*/
  
  public void clearAllRegistries() throws Exception {    
    Node homeNode = mapper.getApplicationRegistryNode(false);
    Node parentNode = homeNode.getParent();
    homeNode.remove();
    parentNode.save();
    mapper.getSession().save();
    parentNode.addNode(applicationNodeType);
    parentNode.save();
    mapper.getSession().save();
  }
}