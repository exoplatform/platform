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
        return false;
    }

}
