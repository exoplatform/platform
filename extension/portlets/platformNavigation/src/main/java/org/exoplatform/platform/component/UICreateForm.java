package org.exoplatform.platform.component;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.commons.EventUIComponent;
import org.exoplatform.webui.commons.UISpacesSwitcher;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.wiki.mow.api.Page;
import org.exoplatform.wiki.mow.api.Wiki;
import org.exoplatform.wiki.service.WikiPageParams;
import org.exoplatform.wiki.service.WikiService;
import org.exoplatform.wiki.utils.Utils;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 08/11/12
 */

@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template = "app:/groovy/platformNavigation/portlet/UICreatePlatformToolBarPortlet/UICreateForm.gtmpl",
        events = {
                @EventConfig(
                        listeners = UICreateForm.NextActionListener.class , phase = Event.Phase.DECODE

                ),
                @EventConfig(
                        listeners = UICreateForm.CancelActionListener.class , phase = Event.Phase.DECODE
                ),

                @EventConfig(
                        listeners = UICreateForm.SwitchSpaceActionListener.class
                )
        }
)

public class UICreateForm extends UIForm {

    private static final Log LOG = ExoLogger.getLogger(UICreateForm.class);
    static String LOCATION = "In Location".intern();
    private static final String SWITCH_SPACE_ACTION = "SwitchSpace";
    public static final String SPACE_SWITCHER = "uiWikiSpaceSwitcher_CreateWiki";
    private static final String CREATE_FORM_CONTAINER = "UICreateForm";
    private static final String ADD_WIKI_PAGE = "#AddPage";
    private String urlWiki = "";

    public UICreateForm() throws Exception {
        // Init space switcher
        UISpacesSwitcher uiWikiSpaceSwitcher = addChild(UISpacesSwitcher.class, null, SPACE_SWITCHER);
        uiWikiSpaceSwitcher.setCurrentSpaceName(getCurrentWiki());
        EventUIComponent eventComponent1 = new EventUIComponent(CREATE_FORM_CONTAINER, SWITCH_SPACE_ACTION, EventUIComponent.EVENTTYPE.EVENT);
        uiWikiSpaceSwitcher.init(eventComponent1);
        urlWiki = uiWikiSpaceSwitcher.getPortalSpaceId();
    }

    public String[] getActions() {
        return new String[]{"Next","Cancel"};
    }

    public String getUrlWiki() {
        return urlWiki;
    }

    public void setUrlWiki(String urlWiki) {
        this.urlWiki = urlWiki;
    }

    static public class NextActionListener extends EventListener<UICreateForm> {

        public void execute(Event<UICreateForm> event) throws Exception {

            UICreateForm uiCreateWiki = event.getSource();
            UISpacesSwitcher uiWikiSpaceSwitcher = uiCreateWiki.getChildById(SPACE_SWITCHER);
            WikiService wikiService = (WikiService) PortalContainer.getComponent(WikiService.class);
            String wikiId = "";
            if (uiWikiSpaceSwitcher.getCurrentSpaceName().equals(Util.getPortalRequestContext().getPortalOwner())) {
              wikiId = "/"+PortalContainer.getCurrentPortalContainerName()+"/"+Util.getPortalRequestContext().getPortalOwner();
            } else {
              wikiId = uiCreateWiki.getUrlWiki();
            }
            Wiki wiki = wikiService.getWikiById(wikiId);
            if(wiki == null) {
              wiki = wikiService.createWiki(org.exoplatform.wiki.commons.Utils.getWikiTypeFromWikiId(wikiId), org.exoplatform.wiki.commons.Utils.getWikiOwnerFromWikiId(wikiId));
            }
            Page wikiHome = wiki.getWikiHome();
            String permalink = Utils.getPermanlink(new WikiPageParams(wiki.getType(), wiki.getOwner(), wikiHome.getName()),true);
            permalink =new StringBuffer(permalink).append(ADD_WIKI_PAGE).toString();
            event.getRequestContext().getJavascriptManager().getRequireJS().addScripts("(function(){ window.location.href = '" + permalink + "';})();");
            
            Event<UIComponent> cancelEvent = uiCreateWiki.<UIComponent>getParent().createEvent("Cancel", Event.Phase.PROCESS, event.getRequestContext());
            if (cancelEvent != null) {
                cancelEvent.broadcast();
            }
        }
    }

   public static class CancelActionListener extends EventListener<UICreateForm> {

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

    /**
     * SwitchSpace should be implemented when UISpaceSwitcher is used
     */
    public static class SwitchSpaceActionListener extends EventListener<UICreateForm> {

        public void execute(Event<UICreateForm> event) throws Exception {
            UICreatePlatformToolBarPortlet uiParent = (UICreatePlatformToolBarPortlet) event.getSource().getAncestorOfType(UICreatePlatformToolBarPortlet.class);
            UICreateForm uiCreateWiki = event.getSource();
            StringBuffer wikiUrlPattern =  new  StringBuffer();

            // --- get The Id of the selected wiki (company | User | Space)
            String wikiId = event.getRequestContext().getRequestParameter(UISpacesSwitcher.SPACE_ID_PARAMETER);
            // --- workaround to load the correct company wiki
            if (Util.getPortalRequestContext().getPortalOwner().equals(wikiId)) {
                wikiUrlPattern.append("/"+PortalContainer.getCurrentPortalContainerName()+"/").append(wikiId);
            } else {
                wikiUrlPattern.append(wikiId);
            }

            WikiService wikiService = (WikiService) PortalContainer.getComponent(WikiService.class);
            // --- get the real name of the selected wiki (label to display)
            String wikiName = "";
            try {
              wikiName = wikiService.getWikiNameById(wikiUrlPattern.toString());
            } catch (NullPointerException e) {
              Wiki wiki = wikiService.createWiki(org.exoplatform.wiki.commons.Utils.getWikiTypeFromWikiId(wikiId), org.exoplatform.wiki.commons.Utils.getWikiOwnerFromWikiId(wikiId));
              wikiName = wikiService.getWikiNameById(wikiUrlPattern.toString());
            }
            // --- set the wiki navigation URL
            uiCreateWiki.setUrlWiki(wikiUrlPattern.toString());
            // --- Update Selected wiki in UISpaceSwitcher
            UISpacesSwitcher uiWikiSpaceSwitcher = uiCreateWiki.getChildById(SPACE_SWITCHER);
            uiWikiSpaceSwitcher.setCurrentSpaceName(wikiName);
            // --- Update Front Office Container
            event.getRequestContext().getJavascriptManager().require("SHARED/navigation-toolbar", "toolbarnav").addScripts("toolbarnav.UIPortalNavigation.ClickActionButton('"+uiParent.getId()+"');");
        }
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public static String getCurrentWiki() throws Exception {
        return "intranet";
    }

}
