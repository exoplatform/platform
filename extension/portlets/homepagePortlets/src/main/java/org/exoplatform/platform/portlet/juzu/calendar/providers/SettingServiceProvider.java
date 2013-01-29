/**
 * Copyright ( C ) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.portlet.juzu.calendar.providers;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.container.PortalContainer;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 1/7/13
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
