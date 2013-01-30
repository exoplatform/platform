/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.portlet.juzu.branding.provider;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.container.PortalContainer;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by The eXo Platform SAS
 * Author : Nguyen Viet Bang
 *          bangnv@exoplatform.com
 * Jan 28, 2013  
 */
public class SettingServiceProvider  implements FactoryBean<SettingService> {
  @Override
  public SettingService getObject() throws Exception {
      return (SettingService) PortalContainer.getInstance().getComponentInstanceOfType(SettingService.class);
  }

  @Override
  public Class<?> getObjectType() {
      return SettingService.class;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isSingleton() {
      return true;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
