package org.exoplatform.platform.portlet.juzu.calendar.providers;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.OrganizationService;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 12/31/12
 */
public class OrganisationServiceProvider implements FactoryBean<OrganizationService> {

    public OrganizationService getObject() throws Exception {
        return (OrganizationService) PortalContainer.getInstance().getComponentInstanceOfType(OrganizationService.class);
    }

    public Class<?> getObjectType() {
        return OrganizationService.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
