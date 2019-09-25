package org.exoplatform.platform.upgrade.plugins;

import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticSession;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.application.registry.impl.ApplicationRegistryChromatticLifeCycle;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.model.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.List;

public class UpgradeRemoveGadgetPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeRemoveGadgetPlugin.class);

    private ApplicationRegistryService applicationRegistryService;
    private ApplicationRegistryChromatticLifeCycle chromatticLifeCycle;

    public UpgradeRemoveGadgetPlugin(SettingService settingService,
                                     ApplicationRegistryService applicationRegistryService,
                                     ChromatticManager chromatticManager,
                                     InitParams initParams) {
        super(settingService, initParams);
        this.applicationRegistryService = applicationRegistryService;

        ApplicationRegistryChromatticLifeCycle lifeCycle = (ApplicationRegistryChromatticLifeCycle)chromatticManager.getLifeCycle("app");
        this.chromatticLifeCycle = lifeCycle;

    }

    public UpgradeRemoveGadgetPlugin(InitParams initParams) {
        super(initParams);
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {
        this.removeAppRegistry();
    }

    private void removeAppRegistry() {
        try {
            RequestLifeCycle.begin(PortalContainer.getInstance());
            List<ApplicationCategory> categories = this.applicationRegistryService.getApplicationCategories();
            for (ApplicationCategory category : categories) {
                List<org.exoplatform.application.registry.Application> applications =
                        this.applicationRegistryService.getApplications(category);
                for (org.exoplatform.application.registry.Application app : applications) {
                    String contentId = app.getContentId();
                    if (app.getType() == ApplicationType.GADGET
                    || "dashboard/GadgetPortlet".equals(contentId)
                            || "dashboard/TabbedDashboardPortlet".equals(contentId)
                            || "dashboard/DashboardPortlet".equals(contentId)) {
                        LOG.info("Remove gadget " + app.getApplicationName() + " in category " + app.getCategoryName());
                        this.applicationRegistryService.remove(app);
                    }
                }
            }

            LOG.info("Start remove all gadget definition");
            Chromattic chromattic = this.chromatticLifeCycle.getChromattic();
            ChromatticSession session = chromattic.openSession();
            Node rootNode = session.getJCRSession().getRootNode();
            Node gadgets = rootNode.getNode("production").getNode("app:gadgets");
            NodeIterator iterator = gadgets.getNodes();
            while (iterator.hasNext()) {
                Node gadget = iterator.nextNode();
                LOG.info("Remove gadget " + gadget.getName());
                gadget.remove();
                gadgets.save();
            }
            LOG.info("Remove the app:gadgets node");
            gadgets.remove();
            session.save();

            LOG.info("Remove node exo:gadget-groovy");
            gadgets = rootNode.getNode("exo:gadget-groovy");
            gadgets.remove();
            session.save();

        } catch (Exception ex) {
            LOG.error("Exception while removing gadgets definitions", ex);
        } finally {
            RequestLifeCycle.end();
        }
    }
}
