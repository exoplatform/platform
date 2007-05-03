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
   * When the service is launched, the service  should check for the following condition
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
    
    Session session = repoService.getRepository().getSystemSession(WORKSPACE) ;
    Node exoRegistry = session.getRootNode().getNode("/exo:registry") ;
    if(exoRegistry == null) {
      //TODO: create
    }
    Node users = session.getRootNode().getNode("/users") ;
    if(users == null) {
      //create
    }
    session.logout() ;
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
   * @param desc
   * @throws Exception
   */
  public void createApplicationRegistry(ApplicationRegistry desc, boolean overwrite) throws Exception {
    desc.preAction(null) ;
    desc.postAction(this, null) ;
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
    desc.postAction(this, null) ;
  }
}