/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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
  
  public void importAllPortlets() throws Exception;  
  //TODO: dang.tung
  public void importExoGadgets() throws Exception;
  
  public void clearAllRegistries() throws Exception;
}