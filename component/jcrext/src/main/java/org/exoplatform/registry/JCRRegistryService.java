/*******************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL All rights reserved. * Please look
 * at license.txt in info directory for more license detail. *
 ******************************************************************************/
package org.exoplatform.registry;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.services.jcr.RepositoryService;

public class JCRRegistryService  { 
  
  public final static String WORKSPACE = "production".intern();
  
  private RepositoryService repositoryService ;
  
  /**
   * The constructor should:
   * 
   * 1. Create the /exo:registry node if it is not existed
   * 2. Create the /exo:registry/exo:applications node if it is not existed
   * 3. Create the /exo:registry/exo:services node if it is not existed
   * 4. Create the /users node if  it is not existed
   *  
   * @param repoService
   * @throws Exception
   */
  public JCRRegistryService(RepositoryService repoService) throws Exception {
    this.repositoryService = repoService ;
    
    Session session = getSession();
    Node exoRegistry = getNode(session.getRootNode(), "exo:registry", true) ;
    getNode(exoRegistry, "exo:applications", true);
    getNode(exoRegistry, "exo:services", true);
    getNode(session.getRootNode(), "users", true);
    session.logout() ;
  }
  
  private Node getNode(Node parentNode, String name, boolean autoCreate) throws Exception {
    if(parentNode.hasNode(name)) return parentNode.getNode(name);
    if(!autoCreate) return null;
    Node node  = parentNode.addNode(name);
    parentNode.save();
    return node;
  }
  
  public Session getSession() throws Exception{
    return repositoryService.getRepository().getSystemSession(WORKSPACE);
  }
  
  public RepositoryService  getJCRRepositoryService() { return repositoryService ; }
  
  /**
   * This method should: 
   * 1. Call the method ServiceRegistry.preCreate(..)
   * 2. Remove the /exo:registry/exo:services/$serviceName if it is  existed and overwrite is true
   * 3. Create the /exo:registry/exo:services/$serviceName if it is not existed
   * 4. Call the method ServiceRegistry.postCreate(..)
   * 
   * @param desc
   * @throws Exception
   */
  public void createServiceRegistry(ServiceRegistry desc, boolean overwrite) throws Exception {
    desc.preAction(this) ;
    Session session = getSession();
    Node servicesNode = session.getRootNode().getNode("exo:registry/exo:services");
    if( servicesNode.hasNode(desc.getName())){
      if(overwrite){
        servicesNode.getNode(desc.getName()).remove();
        servicesNode.save();
        servicesNode.addNode(desc.getName());
        servicesNode.save();
      } 
    } else {
      getNode(servicesNode, desc.getName(), true);
    }
    session.save();
    session.logout();
    desc.postAction(this, null) ;
  }
  
  /**
   * This method should: 
   * 1. Call the method ApplicationRegistry.preAction(..)
   * 2. Remove the /exo:registry/exo:applications/$applicationName if it is  existed 
   * and overwrite is true
   * 2. Create the /exo:registry/exo:applications/$applicationName if it is not existed.
   * 3. Call the method ApplicationRegistr.postCreate(..)
   * 
   * @param app
   * @throws Exception
   */
  public void createApplicationRegistry(ApplicationRegistry app, boolean overwrite) throws Exception {
    Session session = getSession();
    Node appNode = session.getRootNode().getNode("exo:registry/exo:applications");
    if(appNode.hasNode(app.getName())) {
      if(!overwrite) {
        session.logout();
        return;
      }
      appNode.getNode(app.getName()).remove();
      appNode.save();
    }
    app.preAction(this);    
    Node node  = appNode.addNode(app.getName());
    appNode.save();
    session.save();
    session.logout();
    app.postAction(this, node) ;
  }
  
  public Node getApplicationRegistryNode(String appName) throws Exception {
    
    return null;
  }
  
  public Node getApplicationRegistryNode(String userName, String appName) throws Exception {
    
    return null;
  }

  /**
   * This method should: 
   * 
   * 1. Create the /users/$username 
   * 2. Create the /users/$username/exo:registry
   * 3. Create the /users/$username/exo:registry/exo:services
   * 4. Create the /users/$username/exo:registry/exo:applications
   * 
   * @param desc
   * @throws Exception
   */
  public void createUserHome(String username, boolean overwrite) throws Exception {
    Session session = getSession();
    Node usersNode = session.getRootNode().getNode("users");
    Node userNode = null;
    if( usersNode.hasNode(username)){
      if(overwrite){
        usersNode.getNode(username).remove();
        usersNode.save();
        userNode =usersNode.addNode(username);
        usersNode.save();
      } 
    } else {
      userNode = getNode(usersNode, username, true);
    }
    Node registryNode = getNode(userNode, "exo:registry", true );
    getNode(registryNode, "exo:services", true);
    getNode(registryNode, "exo:applications", true);
    session.save();
    session.logout();
  }
  
  /**
   * This method should: 
   * 
   * 1. Call the method ApplicationRegistry.preAction(..)
   * 2. Remove the /user/$username/exo:registry/exo:services/$serviceName if it is existed 
   * and overwrite is  true
   * 3. Create the /user/$username/exo:registry/exo:services/$serviceName if it is not existed
   * 4. Call the method ServiceRegistry.postCreate(..)
   * 
   * @param desc
   * @throws Exception
   */
  public void createServiceRegistry(String username, ServiceRegistry desc, boolean overwrite) throws Exception {
    desc.preAction(null) ;
    Session session = getSession();
    Node usersNode = session.getRootNode().getNode("users/" +username +"/exo:registry/exo:services");
    if( usersNode.hasNode(desc.getName())){
      if(overwrite){
        usersNode.getNode(desc.getName()).remove();
        usersNode.save();
        usersNode.addNode(desc.getName());
        usersNode.save();
      } 
    } else {
      getNode(usersNode, desc.getName(), true);
    }
    session.save();
    session.logout();
    desc.postAction(this, null) ;
  }
  
  /**
   * This method should:
   * 1. Call the method ServiceRegistry.preAction(..)
   * 2. Remove the /user/$username/exo:registry/exo:applications/$applicationName if it is 
   * existed and overwrite is  true
   * 3. Create the /users/$username/exo:registry/exo:applications/$applicationName if it is 
   * not existed
   * 4. Call the method ServiceRegistry.postAction(..)
   * 
   * @param desc
   * @throws Exception
   */
  public void createApplicationRegistry(String username, ApplicationRegistry desc, boolean overwrite) throws Exception {
    desc.preAction(null) ;
    Session session = repositoryService.getRepository().getSystemSession(WORKSPACE);
    Node servicesNode = session.getRootNode().getNode("users/" +username +"/exo:registry/exo:applications");
    if( servicesNode.hasNode(desc.getName())){
      if(overwrite){
        servicesNode.getNode(desc.getName()).remove();
        servicesNode.save();
        servicesNode.addNode(desc.getName());
      } 
    } else {
      getNode(servicesNode, desc.getName(), true);
    }
    session.save();
    session.logout();
    desc.postAction(this, null) ;
  }
  
  public Node getServiceRegistryNode(String appName) throws Exception {
    
    return null;
  }
  
  public Node getServiceRegistryNode(String userName, String appName) throws Exception {
    
    return null;
  }
}