/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registry;

import java.util.List;

import org.exoplatform.container.component.ComponentPlugin;

/**
 * Created y the eXo platform team
 * User: Tuan Nguyen
 * Date: 20 april 2007
 */
public interface ApplicationRegistryService {
  
  public List<ApplicationCategory> getApplicationCategories(String accessUser, String ... appTypes) throws Exception;
  public void initListener(ComponentPlugin com) throws Exception;
  public List<ApplicationCategory> getApplicationCategories() throws Exception;
  public ApplicationCategory getApplicationCategory(String name) throws Exception;
  public void save(ApplicationCategory category) throws Exception;
  public void remove(ApplicationCategory category) throws Exception;
  
  public List<Application> getApplications(ApplicationCategory category, String...appTypes) throws Exception;
  public List<Application> getAllApplications() throws Exception;
  public Application getApplication(String id) throws Exception;
  public Application getApplication(String category, String name) throws Exception;
  public void save(ApplicationCategory category, Application application) throws Exception;
  public void update(Application application) throws Exception;
  public void remove(Application app) throws Exception;
  
  public void importJSR168Portlets() throws Exception;
  
  public void importExoWidgets() throws Exception;
  
  public void clearAllRegistries() throws Exception;
}