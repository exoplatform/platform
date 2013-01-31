package org.exoplatform.platform.component;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.commons.EventUIComponent;
import org.exoplatform.webui.commons.UISpacesSwitcher;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.wiki.mow.api.WikiType;
import org.exoplatform.wiki.service.WikiService;
import org.exoplatform.wiki.mow.core.api.wiki.PageImpl;
import org.exoplatform.wiki.mow.api.Wiki;
import org.exoplatform.wiki.service.WikiPageParams;
import java.net.URLEncoder;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 08/11/12
 */

@ComponentConfig(
        lifecycle = Lifecycle.class,
        template = "app:/groovy/platformNavigation/portlet/UICreatePlatformToolBarPortlet/UICreateForm.gtmpl",
        events = {
                @EventConfig(
                        listeners = UICreateForm.NextActionListener.class

                ),
                @EventConfig(
                        listeners = UICreateForm.CancelActionListener.class
                ),

                @EventConfig(
                        listeners = UICreateForm.SwitchSpaceActionListener.class
                )
        }
)

public class UICreateForm extends UIContainer {
    static String LOCATION = "In Location".intern();
    private static final String SWITCH_SPACE_ACTION = "SwitchSpace";
    public static final String SPACE_SWITCHER = "UIWikiSpaceSwitcher_CreateWiki";
    private static final String CREATE_FORM_CONTAINER = "UICreateForm";
    private static final String ADD_WIKI_PAGE = "#AddPage";

    private static Log log = ExoLogger.getLogger(UICreateForm.class);

    public UICreateForm() throws Exception {
        // Init space switcher
        UISpacesSwitcher uiWikiSpaceSwitcher = addChild(UISpacesSwitcher.class, null, SPACE_SWITCHER);
        uiWikiSpaceSwitcher.setCurrentSpaceName(getCurrentWiki());
        EventUIComponent eventComponent1 = new EventUIComponent(CREATE_FORM_CONTAINER, SWITCH_SPACE_ACTION, EventUIComponent.EVENTTYPE.EVENT);
        uiWikiSpaceSwitcher.init(eventComponent1);
    }

    public String[] getActions() {
        return new String[]{"Next", "Cancel"};
    }

    static public class NextActionListener extends EventListener<UICreateForm> {

        public void execute(Event<UICreateForm> event) throws Exception {

            UICreateForm uiCreateWiki = event.getSource();
            UISpacesSwitcher uiWikiSpaceSwitcher = uiCreateWiki.getChildById(SPACE_SWITCHER);
            WikiService wikiService = (WikiService) PortalContainer.getComponent(WikiService.class);
            Wiki wiki = wikiService.getWikiById(uiWikiSpaceSwitcher.getCurrentSpaceName());
            if (wiki != null) {
                PageImpl wikiHome = (PageImpl) wiki.getWikiHome();
                // String permalink = org.exoplatform.wiki.utils.Utils.getPermanlink(new WikiPageParams(wiki.getType(), wiki.getOwner(), wikiHome.getName()));
                String permalink = getPermanlink(new WikiPageParams(wiki.getType(), wiki.getOwner(), wikiHome.getName()));
                permalink += ADD_WIKI_PAGE;
                event.getRequestContext().getJavascriptManager().getRequireJS().addScripts("(function(){ window.location.href = '" + permalink + "';})();");

            } else {
                log.warn(String.format("Wrong wiki id: [%s], can not change space", uiWikiSpaceSwitcher.getCurrentSpaceName()));
            }
            Event<UIComponent> cancelEvent = uiCreateWiki.<UIComponent>getParent().createEvent("Cancel", Event.Phase.PROCESS, event.getRequestContext());
            if (cancelEvent != null) {
                cancelEvent.broadcast();
            }
        }

    }


    static public class CancelActionListener extends EventListener<UICreateForm> {

        public void execute(Event<UICreateForm> event)
                throws Exception {
            UICreateList uiparent = event.getSource().getAncestorOfType(UICreateList.class);
            WebuiRequestContext context = event.getRequestContext();
            if (uiparent.getChild(UICreateForm.class) != null) {
                uiparent.removeChild(UICreateForm.class);
            }
            context.addUIComponentToUpdateByAjax(uiparent);

        }
    }

    public static class SwitchSpaceActionListener extends EventListener<UICreateForm> {

        public void execute(Event<UICreateForm> event) throws Exception {
            UICreatePlatformToolBarPortlet uiParent = (UICreatePlatformToolBarPortlet) event.getSource().getAncestorOfType(UICreatePlatformToolBarPortlet.class);
            String wikiId = event.getRequestContext().getRequestParameter(UISpacesSwitcher.SPACE_ID_PARAMETER);
            UICreateForm uiCreateWiki = event.getSource();
            UISpacesSwitcher uiWikiSpaceSwitcher = uiCreateWiki.getChildById(SPACE_SWITCHER);
            uiWikiSpaceSwitcher.setCurrentSpaceName(wikiId);
            event.getRequestContext().addUIComponentToUpdateByAjax(uiCreateWiki);
            event.getRequestContext().addUIComponentToUpdateByAjax(uiParent);
            event.getRequestContext().getJavascriptManager().require("SHARED/navigation-toolbar", "toolbarnav").addScripts("toolbarnav.UIPortalNavigation.ClickActionButton('"+uiParent.getId()+"') ;");
        }
    }

    public static String getCurrentWiki() throws Exception {
        return "intranet";
    }

    private static String getPermanlink(WikiPageParams params) throws Exception {

        WikiService wikiService = (WikiService) PortalContainer.getComponent(WikiService.class);

        // get wiki webapp name
        String wikiWebappUri = wikiService.getWikiWebappUri();

        // Create permalink
        StringBuilder sb = new StringBuilder(wikiWebappUri);
        sb.append("/");

        if (!params.getType().equalsIgnoreCase(WikiType.PORTAL.toString())) {
            sb.append(params.getType().toLowerCase());
            sb.append("/");
            sb.append(validateWikiOwner(params.getType(), params.getOwner()));
            sb.append("/");
        }

        if (params.getPageId() != null) {
            sb.append(URLEncoder.encode(params.getPageId(), "UTF-8"));
        }

        return getDomainUrl() + fillPortalName(sb.toString());
    }
    private static String getDomainUrl() {
        PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
        StringBuilder domainUrl = new StringBuilder();
        domainUrl.append(portalRequestContext.getRequest().getScheme());
        domainUrl.append("://");

        domainUrl.append(portalRequestContext.getRequest().getLocalName());
        int port = portalRequestContext.getRequest().getLocalPort();
        if (port != 80) {
            domainUrl.append(":");
            domainUrl.append(port);
        }
        return domainUrl.toString();
    }

    private static String fillPortalName(String url) {
        RequestContext ctx = RequestContext.getCurrentInstance();
        NodeURL nodeURL =  ctx.createURL(NodeURL.TYPE);
        NavigationResource resource = new NavigationResource(SiteType.PORTAL, Util.getPortalRequestContext().getPortalOwner(), url);
        return nodeURL.setResource(resource).toString();
    }

    private static String validateWikiOwner(String wikiType, String wikiOwner){
        if(wikiType != null && wikiType.equals(PortalConfig.GROUP_TYPE)) {
            if(wikiOwner == null || wikiOwner.length() == 0){
                return "";
            }
            if(wikiOwner.startsWith("/")){
                wikiOwner = wikiOwner.substring(1,wikiOwner.length());
            }
            if(wikiOwner.endsWith("/")){
                wikiOwner = wikiOwner.substring(0,wikiOwner.length()-1);
            }
        }
        return wikiOwner;
    }
}