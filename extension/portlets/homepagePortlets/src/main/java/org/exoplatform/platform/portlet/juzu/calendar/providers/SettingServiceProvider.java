package org.exoplatform.platform.portlet.juzu.calendar.providers;

import org.exoplatform.commons.settings.api.SettingService;
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
