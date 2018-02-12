package org.exoplatform.platform.component.organization.test;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.migration.EnableUserUpgradePlugin;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserStatus;
import org.exoplatform.services.organization.idm.PicketLinkIDMCacheService;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.test.BasicTestCase;

public class TestEnableUserUpgradePlugin extends BasicTestCase {
    PortalContainer container ;
    HibernateService hibernateService;
    PicketLinkIDMCacheService picketLinkIDMCacheService;
    OrganizationService organizationService ;

    @Override
    protected void setUp() throws Exception {
        container = PortalContainer.getInstance();
        organizationService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
        hibernateService = (HibernateService) container.getComponentInstanceOfType(HibernateService.class);
        picketLinkIDMCacheService = (PicketLinkIDMCacheService) container.getComponentInstanceOfType(PicketLinkIDMCacheService.class);
    }

    public void testProcessUpgrade() throws Exception {
        initData();
        InitParams initParams =  new InitParams();
        EnableUserUpgradePlugin upgradePlugin =  new EnableUserUpgradePlugin(initParams, hibernateService, organizationService, picketLinkIDMCacheService);
        assertNotNull(upgradePlugin);
        upgradePlugin.processUpgrade("4.4", "5.0");

        checkUserStatusAfterUpgrade();
        cleanData();
    }

    private void checkUserStatusAfterUpgrade() throws Exception {
        try {
            RequestLifeCycle.begin(container);
            UserHandler userHandler = organizationService.getUserHandler();
            User user = userHandler.findUserByName("testEnable", UserStatus.ANY);
            assertNotNull(user);
            assertTrue(user.isEnabled());

            user = userHandler.findUserByName("testDisable", UserStatus.ANY);
            assertNotNull(user);
            assertFalse(user.isEnabled());

        } finally {
            RequestLifeCycle.end();
        }
    }

    private void initData() throws Exception {
        try {
            RequestLifeCycle.begin(container);
            UserHandler userHandler = organizationService.getUserHandler();
            User user = new UserImpl("testEnable");
            userHandler.createUser(user, false);

            user = new UserImpl("testDisable");
            userHandler.createUser(user, false);
            userHandler.setEnabled("testDisable", false, false );
        } finally {
            RequestLifeCycle.end();
        }
    }

    private void cleanData() throws Exception {
        try {
            RequestLifeCycle.begin(container);
            UserHandler userHandler = organizationService.getUserHandler();
            userHandler.removeUser("testEnable", true);
            userHandler.removeUser("testDisable", false);
        } finally {
            RequestLifeCycle.end();
        }
    }
}
