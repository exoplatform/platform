package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.*;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.pom.data.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class UpgradeGadgetToPortletPlugin extends AbstractGadgetToPortletPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeGadgetToPortletPlugin.class);

    public UpgradeGadgetToPortletPlugin(SettingService settingService,
                                        ModelDataStorage modelDataStorage,
                                        PageService pageService,
                                        NavigationService navigationService,
                                        InitParams initParams) {
        super(settingService, modelDataStorage, pageService, navigationService, initParams);
    }

    public UpgradeGadgetToPortletPlugin(InitParams initParams) {
        super(initParams);
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {

        LOG.info("Remove the Monitoring and Management pages");
        this.removeNavs();

        LOG.info("Migrate Gadgets on portal pages");
        this.migratePages(SiteType.PORTAL);

        LOG.info("Migrate Gadgets on group pages");
        this.migratePages(SiteType.GROUP);

        LOG.info("Migrate Gadgets on portal site layout");
        this.migratePortals(SiteType.PORTAL);

        LOG.info("Migrate Gadgets on group sites layout");
        this.migratePortals(SiteType.GROUP);

        LOG.info("Migrate gadgets done");
    }

    private void removeNavs() {
        try {
            RequestLifeCycle.begin(PortalContainer.getInstance());

            NavigationContext navigation = this.navigationService.loadNavigation(SiteKey.group("/platform/administrators"));
            NodeContext node = this.navigationService.loadNode(NodeModel.SELF_MODEL, navigation, Scope.ALL, null);

            this.removeNavNode(node, "monitoring");
            this.removeNavNode(node, "servicesManagement");

            this.navigationService.saveNode(node, null);
        } finally {
            RequestLifeCycle.end();
        }
    }

    private void removeNavNode(NodeContext root, String name) {
        NodeContext node = root.get(name);
        NodeState state = node.getState();
        org.exoplatform.portal.mop.page.PageKey pageKey = state.getPageRef();

        LOG.info("Remove page: " + name);
        pageService.destroyPage(pageKey);

        LOG.info("Remove navigation node: " + name);
        node.removeNode();
    }
}
