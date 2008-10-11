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

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * 9 Oct 2007  
 */

public class ApplicationCategoriesPlugins extends BaseComponentPlugin {
  private ConfigurationManager cmanager_ ;
  private ApplicationRegistryService pdcService_;  
  private List<?> configs;
  
  
  public ApplicationCategoriesPlugins(ApplicationRegistryService pdcService,
                                 ConfigurationManager cmanager,                                        
                                 InitParams params) throws Exception {
    configs = params.getObjectParamValues(ApplicationCategory.class);
    cmanager_ = cmanager ;
    pdcService_ = pdcService;
  }
  
  public void run() throws Exception{
    if( configs == null) return ;
    if(pdcService_.getApplicationCategories().size() > 0) return ;
    for (Object ele : configs) {
      ApplicationCategory category  = (ApplicationCategory)ele;
      pdcService_.save(category);
      List<Application> apps = category.getApplications();
      for(Application app: apps) pdcService_.save(category, app);
    }
  }
}
