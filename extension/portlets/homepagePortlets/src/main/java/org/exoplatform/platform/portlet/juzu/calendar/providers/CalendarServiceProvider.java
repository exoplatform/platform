
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

import org.exoplatform.calendar.service.CalendarService;
import org.exoplatform.container.PortalContainer;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 12/31/12
 */
public class CalendarServiceProvider implements FactoryBean<CalendarService> {

    public CalendarService getObject() throws Exception {
        return (CalendarService) PortalContainer.getInstance().getComponentInstanceOfType(CalendarService.class);
    }

    public Class<CalendarService> getObjectType() {
        return CalendarService.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
