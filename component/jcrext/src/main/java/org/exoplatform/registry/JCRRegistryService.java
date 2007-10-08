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
  
  private RepositoryService repositoryService_ ;
  
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
    this.repositoryService_ = repoService ;
    
    Session session = getSession();
    Node exoRegistry = createNode(session.getRootNode(), "exo:registry", true) ;
    createNode(exoRegistry, "exo:applications", true);
    createNode(exoRegistry, "exo:services", true);
    createNode(session.getRootNode(), "users", true);
    createNode(session.getRootNode(), "groups", true);
    session.logout() ;
  }
  
  private Node createNode(Node parentNode, String name, boolean autoCreate) throws Exception {
    if(parentNode.hasNode(name)) return parentNode.getNode(name);
    if(!autoCreate) return null;
    Node node  = parentNode.addNode(name);
    parentNode.save();
    return node;
  }
  
  public Session getSession() throws Exception{
    return repositoryService_.getDefaultRepository().getSystemSession(WORKSPACE);
  }
  
  public RepositoryService  getJCRRepositoryService() { return repositoryService_ ; }
  
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
      if(!overwrite){
        session.logout();
        return;
      }
      servicesNode.getNode(desc.getName()).remove();
      servicesNode.save();
    } 
    Node node = servicesNode.addNode(desc.getName());
    servicesNode.save() ;
    desc.postAction(this, node) ;
    session.save();
    session.logout();
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
    app.postAction(this, node) ;
    session.save();
    session.logout();
  }
  
  public Node getUserNode(Session session, String userName) throws Exception{
    if(session.getRootNode().hasNode("users/" + userName)) {
      return session.getRootNode().getNode("users/" + userName);
    }
    return null;
  }
  
  public Node getApplicationRegistryNode(Session session, String appName) throws Exception {
    Node appNode = session.getRootNode().getNode("exo:registry/exo:applications");
    if(appNode.hasNode(appName)) return appNode.getNode(appName);
    return null;
  }
  
  public Node getApplicationRegistryNode(Session session, String userName, String appName) throws Exception {
    Node userNode = getUserNode(session, userName);
    if(userNode == null) return null;
    if( userNode.hasNode("exo:registry/exo:applications/" + appName)){
      return userNode.getNode("exo:registry/exo:applications/" + appName);
    }
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
    if(usersNode.hasNode(username)){
      if(!overwrite){
        session.logout();
        return;
      }
      usersNode.getNode(username).remove();
      usersNode.save();
    } 
    userNode =usersNode.addNode(username);
   
    Node registryNode = userNode.addNode("exo:registry");
    registryNode.addNode("exo:services");
    registryNode.addNode("exo:applications");
    usersNode.save();
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
    desc.preAction(this) ;
    Session session = getSession();
    Node servicesNode = session.getRootNode().getNode("users/" +username +"/exo:registry/exo:services");
    if(servicesNode.hasNode(desc.getName())){
      if(!overwrite){
        session.logout();
        return;
      }
      servicesNode.getNode(desc.getName()).remove();
      servicesNode.save();
    }
    Node node = servicesNode.addNode(desc.getName());
    servicesNode.save();
    desc.postAction(this, node) ;
    session.save();
    session.logout();
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
    Session session = repositoryService_.getDefaultRepository().getSystemSession(WORKSPACE);
    Node appsNode = session.getRootNode().getNode("users/" +username +"/exo:registry/exo:applications");
    if( appsNode.hasNode(desc.getName())){
      if(!overwrite){
        session.logout();
        return;
      }
      appsNode.getNode(desc.getName()).remove();
      appsNode.save();
    }
    Node node = appsNode.addNode(desc.getName());
    appsNode.save();
    desc.postAction(this, node) ;
    session.save();
    session.logout();
  }
  
  public Node getServiceRegistryNode(Session session,  String appName) throws Exception {
    Node appNode = session.getRootNode().getNode("exo:registry/exo:services");
    if(appNode.hasNode(appName)) {
      return appNode.getNode(appName);
    }
    return null;
  }
  
  public Node getServiceRegistryNode(Session session, String userName, String appName) throws Exception {
    Node userNode  = getUserNode(session, userName);
    if(userNode == null) return null;
    if( userNode.hasNode("exo:registry/exo:services/" + appName)) {
      return userNode.getNode("exo:registry/exo:services/" + appName);
    }
    return null;
  }
}