package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.pom.data.*;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractGadgetToPortletPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(AbstractGadgetToPortletPlugin.class);

    protected static final String DEFAULT_WORKSPACE_NAME = "portal-system";

    protected ExecutorService executor = Executors.newFixedThreadPool(100);

    protected ModelDataStorage modelDataStorage;
    protected PageService pageService;
    protected NavigationService navigationService;
    protected RepositoryService repoService;
    protected String workspaceName = DEFAULT_WORKSPACE_NAME;

    public AbstractGadgetToPortletPlugin(SettingService settingService,
                                         ModelDataStorage modelDataStorage,
                                         PageService pageService,
                                         NavigationService navigationService,
                                         RepositoryService repoService,
                                         InitParams initParams) {
        super(settingService, initParams);
        this.modelDataStorage = modelDataStorage;
        this.pageService = pageService;
        this.navigationService = navigationService;
        this.repoService = repoService;

        ValueParam workspaceParam = initParams.getValueParam("workspace");
        if (workspaceParam != null) {
            this.workspaceName = workspaceParam.getValue();
        } else {
            this.workspaceName = DEFAULT_WORKSPACE_NAME;
        }
    }

    public AbstractGadgetToPortletPlugin(InitParams initParams) {
        super(initParams);
    }

    protected void migratePortals(SiteType type) {
        int count = 0;
        try {
            final PortalContainer portalContainer = PortalContainer.getInstance();
            RequestLifeCycle.begin(portalContainer);

            Set<PortalKey> portalKeys = this.findPortalContainGadgets(type);
            if (portalKeys.isEmpty()) {
                LOG.info("There is no site in type " + type.getName() + " contains gadgets. No need to migrate!");
                return;
            }

            List<Future> futures = new ArrayList<>();
            count = portalKeys.size();

            LOG.info("START migrate for " + count + " portal of type " + type.getName());

            for (PortalKey key : portalKeys) {

                PortalData portal = this.modelDataStorage.getPortalConfig(key);

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

        Set<org.exoplatform.portal.mop.page.PageKey> pageKeys = this.findPagesContainGadget(siteType);
        if (pageKeys.isEmpty()) {
            LOG.info("There is no page contains gadget in site type " + siteType.getName());
            return;
        }

        int countPage = pageKeys.size();
        List<Future> futures = new ArrayList<>();
        try {

            final PortalContainer portalContainer = PortalContainer.getInstance();
            RequestLifeCycle.begin(portalContainer);

            LOG.info("START migrate for " + countPage + " pages in portal type " + siteType.getName());

            for (org.exoplatform.portal.mop.page.PageKey pageKey : pageKeys) {
                PageContext page = this.pageService.loadPage(pageKey);

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

            for (Future f : futures) {
                try {
                    f.get();
                } catch (ExecutionException | InterruptedException e) {
                    LOG.error(e);
                }
            }

        } finally {
            RequestLifeCycle.end();
        }

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

    protected Set<PortalKey> findPortalContainGadgets(SiteType type) {
        Set<PortalKey> result = new HashSet<>();

        String siteType = type.getName().toLowerCase() + "sites";
        String basePath = "/production/mop:workspace/mop:" + siteType + "/";

        try {
            String query = "select * from mop:customization where mop:mimetype = 'application/gadget' and jcr:path like '" + basePath + "%/mop:rootpage/mop:children/mop:templates/mop:children/mop:default/%'";
            javax.jcr.query.QueryResult rs = this.exeQuery(query);

            NodeIterator iterator = rs.getNodes();
            while (iterator.hasNext()) {
                Node node = iterator.nextNode();
                String path = node.getPath();
                path = path.substring(basePath.length());
                String siteName = path.substring(path.indexOf(':') + 1, path.indexOf('/'));

                result.add(new PortalKey(type.getName().toLowerCase(), siteName));
            }

        } catch (RepositoryException ex) {
            LOG.error("Error while retrieve portal", ex);
        }

        return result;
    }

    protected Set<org.exoplatform.portal.mop.page.PageKey> findPagesContainGadget(SiteType type) {
        Set<org.exoplatform.portal.mop.page.PageKey> pageKeys = new HashSet<>();

        String siteType = type.getName().toLowerCase() + "sites";
        String basePath = "/production/mop:workspace/mop:" + siteType + "/";
        String likePath = basePath + "%/mop:rootpage/mop:children/mop:pages/mop:children/%" ;

        try {
            String query = "select * from mop:customization where mop:mimetype = 'application/gadget' and jcr:path like '" + likePath + "'";
            javax.jcr.query.QueryResult rs = this.exeQuery(query);

            NodeIterator iterator = rs.getNodes();
            while (iterator.hasNext()) {
                Node node = iterator.nextNode();
                String path = node.getPath();
                path = path.substring(basePath.length());
                int idx = path.indexOf('/');

                String siteName = path.substring(path.indexOf(':') + 1, idx);

                path = path.substring(idx + 1).replace("mop:rootpage/mop:children/mop:pages/mop:children/", "");
                String pageName = path.substring(path.indexOf(':') + 1, path.indexOf('/'));

                pageKeys.add(new org.exoplatform.portal.mop.page.PageKey(type.key(siteName), pageName));
            }

        } catch (RepositoryException ex) {
            LOG.error("Error while retrieve portal", ex);
        }

        return pageKeys;
    }

    protected javax.jcr.query.QueryResult exeQuery(String query) throws RepositoryException {
        ManageableRepository currentRepository = this.repoService.getCurrentRepository();
        Session session = SessionProvider.createSystemProvider().getSession(workspaceName, currentRepository);
        QueryManager queryManager = session.getWorkspace().getQueryManager();

        javax.jcr.query.Query q = queryManager.createQuery(query, javax.jcr.query.Query.SQL);
        javax.jcr.query.QueryResult rs = q.execute();

        return rs;
    }
}
