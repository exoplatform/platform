/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registry;

import java.util.List;

/**
 * Created y the eXo platform team
 * User: Tuan Nguyen
 * Date: 20 april 2007
 */
public interface ApplicationRegistryService {
  
  public List<ApplicationCategory> getApplicationCategories(String accessUser, String ... appTypes) throws Exception;
  
  public List<ApplicationCategory> getApplicationCategories() throws Exception;
  public ApplicationCategory getApplicationCategory(String name) throws Exception;
  public void save(ApplicationCategory category) throws Exception;
  public void remove(ApplicationCategory category) throws Exception;
  
  public List<Application> getApplications(ApplicationCategory category, String...appTypes) throws Exception;
  public Application getApplication(String id) throws Exception;
  public void save(ApplicationCategory category, Application application) throws Exception;
  public void update(Application application) throws Exception;
  public void remove(Application app) throws Exception;
  
  public void importJSR168Portlets() throws Exception;
  /**
   * This method should go through  the list of the available ExoApplication, create the catagory
   * if it is not available yet and add the application to  the category
   * @throws Exception
   */
  public void importExoApplications() throws Exception;
  
  public void importExoWidgets() throws Exception;
  
  public void clearAllRegistries() throws Exception;
}