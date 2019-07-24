package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.QueryResult;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.pom.data.*;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractGadgetToPortletPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(AbstractGadgetToPortletPlugin.class);

    protected ExecutorService executor = Executors.newFixedThreadPool(100);

    protected ModelDataStorage modelDataStorage;
    protected PageService pageService;
    protected NavigationService navigationService;

    public AbstractGadgetToPortletPlugin(SettingService settingService,
                                         ModelDataStorage modelDataStorage,
                                         PageService pageService,
                                         NavigationService navigationService,
                                         InitParams initParams) {
        super(settingService, initParams);
        this.modelDataStorage = modelDataStorage;
        this.pageService = pageService;
        this.navigationService = navigationService;
    }

    public AbstractGadgetToPortletPlugin(InitParams initParams) {
        super(initParams);
    }

    protected void migratePortals(SiteType type) {
        int count = 0;
        try {
            final PortalContainer portalContainer = PortalContainer.getInstance();
            RequestLifeCycle.begin(portalContainer);

            Query<PortalData> query = new Query<>(type.getName(), null, PortalData.class);
            LazyPageList<PortalData> portals = this.modelDataStorage.find(query);

            List<Future> futures = new ArrayList<>();
            List<PortalData> list = portals.getAll();
            count = list.size();

            LOG.info("START migrate for " + count + " portal of type " + type.getName());

            for (PortalData portal : list) {

                Runnable task = () -> {
                    ExoContainerContext.setCurrentContainer(portalContainer);
                    RequestLifeCycle.begin(portalContainer);
                    try {
                        LOG.info("START migrate layout for portal: " + portal.getName());
                        ContainerData container = portal.getPortalLayout();
                        container = this.migrateContainer(container, portal.getName(), true);

                        PortalData migrated = new PortalData(
                                portal.getStorageId(),
                                portal.getName(),
                                portal.getType(),
                                portal.getLocale(),
                                portal.getLabel(),
                                portal.getDescription(),
                                portal.getAccessPermissions(),
                                portal.getEditPermission(),
                                portal.getProperties(),
                                portal.getSkin(),
                                container,
                                portal.getRedirects()
                        );
                        this.modelDataStorage.save(migrated);
                        LOG.info("DONE migrate layout for portal: " + portal.getName());
                    } catch (Exception ex) {
                        LOG.error("Error when migrate portal layout: " + ex.getMessage(), ex);
                    } finally {
                        RequestLifeCycle.end();
                    }
                };
                futures.add(executor.submit(task));
            }

            for (Future f : futures) {
                f.get();
            }

        } catch (Exception ex) {
            LOG.error("Error when migrate portal layout: " + ex.getMessage(), ex);
        } finally {
            RequestLifeCycle.end();
            LOG.info("DONE migrate for " + count + " portal of type " + type.getName());
        }
    }

    protected void migratePages(SiteType siteType) {
        int offset = 0;
        final int limit = 100;

        LOG.info("START migrate for pages in portal type " + siteType.getName());
        int countPage = 0;
        QueryResult<PageContext> result = null;
        do {

            final PortalContainer portalContainer = PortalContainer.getInstance();
            RequestLifeCycle.begin(portalContainer);
            try {

                List<Future> futures = new ArrayList<>();
                result = this.pageService.findPages(offset, limit, siteType, null, null, null);
                if (result != null && result.getSize() > 0) {
                    countPage += result.getSize();
                    Iterator<PageContext> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        PageContext page = iterator.next();

                        Runnable task = () -> {
                            ExoContainerContext.setCurrentContainer(portalContainer);
                            RequestLifeCycle.begin(portalContainer);
                            try {
                                LOG.info("START remove/replace gadgets in page: " + page.getKey().format());
                                migratePage(page);
                                LOG.info("DONE remove/replace gadgets in page: " + page.getKey().format());
                            } catch (Exception e) {
                                LOG.error("Error during remove/replace gadgets in pages of " + siteType.getName()  + " sites: " + e.getMessage(), e);
                            } finally {
                                RequestLifeCycle.end();
                            }
                        };
                        futures.add(executor.submit(task));
                    }
                    offset += limit;
                }

                for (Future f : futures) {
                    f.get();
                }

            } catch (Exception e) {

            } finally {
                RequestLifeCycle.end();
            }

        } while (result != null && result.getSize() > 0);

        LOG.info("DONE migrate for " + countPage + " pages in type " + siteType.getName());
    }

    protected void migratePage(PageContext page) throws Exception {
        PageData pageData = this.modelDataStorage.getPage(PageKey.create(page.getKey().format()));
        pageData = migratePage(pageData, page.getKey().format());
        this.modelDataStorage.save(pageData);
    }

    protected PageData migratePage(PageData pageData, String pageName) throws Exception {
        List<ComponentData> children = this.migrateComponents(pageData.getChildren(),  pageName, false);
        return new PageData(
                pageData.getStorageId(),
                pageData.getId(),
                pageData.getName(),
                pageData.getIcon(),
                pageData.getTemplate(),
                pageData.getFactoryId(),
                pageData.getTitle(),
                pageData.getDescription(),
                pageData.getWidth(),
                pageData.getHeight(),
                pageData.getAccessPermissions(),
                children,
                pageData.getOwnerType(),
                pageData.getOwnerId(),
                pageData.getEditPermission(),
                pageData.isShowMaxWindow(),
                pageData.getMoveAppsPermissions(),
                pageData.getMoveContainersPermissions()
        );
    }

    protected List<ComponentData> migrateComponents(List<ComponentData> components, String name, boolean isPortal) throws Exception {
        List<ComponentData> children = new ArrayList<>();
        for(ComponentData ele : components) {
            ComponentData migrated = this.migrateComponent(ele, name, isPortal);
            if (migrated != null) {
                children.add(migrated);
            }
        }
        return children;
    }

    protected ComponentData migrateComponent(ComponentData component, String name, boolean isPortal) throws Exception {
        if (component instanceof ApplicationData) {
            ApplicationData<?> app = (ApplicationData<?>)component;
            ApplicationState state = app.getState();
            String contentId = this.modelDataStorage.getId(state);

            if (app.getType() == ApplicationType.GADGET) {
                LOG.info("Migrate for gadget: " + contentId + " in " + (isPortal ? "portal" : "page") + " " + name);

                String newContentId = null;
                if ("Bookmark".equals(contentId)) {
                    newContentId = "portlets/Bookmark";
                } else if ("rssAggregator".equals(contentId)) {
                    newContentId = "portlets/RSSReader";
                } else if ("LoginHistory".equals(contentId)) {
                    newContentId = "portlets/LoginHistory";
                } else if ("FeaturedPoll".equals(contentId)) {
                    newContentId = "portlets/FeaturedPoll";
                }

                if (newContentId != null) {
                    LOG.info("Replace gadget " + contentId + " by new portlet " + newContentId);
                    Application<Portlet> a = Application.createPortletApplication();
                    a.setState(new TransientApplicationState<>(newContentId, null));
                    a.setAccessPermissions(app.getAccessPermissions().toArray(new String[0]));
                    a.setIcon(app.getIcon());
                    a.setShowApplicationMode(app.isShowApplicationMode());
                    a.setShowApplicationState(app.isShowApplicationState());
                    a.setShowInfoBar(app.isShowInfoBar());
                    a.setTheme(app.getTheme());
                    a.setTitle(app.getTitle());
                    a.setDescription(app.getDescription());
                    a.setWidth(app.getWidth());
                    a.setHeight(app.getHeight());

                    return (ApplicationData)a.build();
                }
                LOG.info("Gadget " + contentId + " removed from " + (isPortal ? "portal" : "page") + " " + name);

                return null;
            } else {
                if ("dashboard/GadgetPortlet".equals(contentId)
                        || "dashboard/TabbedDashboardPortlet".equals(contentId)
                        || "dashboard/DashboardPortlet".equals(contentId)
                ) {
                    LOG.info("Remove portlet " + contentId + " from " + (isPortal ? "portal" : "page") + " " + name);
                    return null;
                }

                return component;
            }
        } else if (component instanceof ContainerData){
            return this.migrateContainer((ContainerData)component, name, isPortal);
        } else {
            return component;
        }
    }

    protected ContainerData migrateContainer(ContainerData container, String name, boolean isPortal) throws Exception {
        List<ComponentData> children = this.migrateComponents(container.getChildren(), name, isPortal);
        String template = container.getTemplate();

        return new ContainerData(container.getStorageId(),
                container.getId(),
                container.getName(),
                container.getIcon(),
                template,
                container.getFactoryId(),
                container.getTitle(),
                container.getDescription(),
                container.getWidth(),
                container.getHeight(),
                container.getAccessPermissions(),
                container.getMoveAppsPermissions(),
                container.getMoveContainersPermissions(),
                children
        );
    }
}
