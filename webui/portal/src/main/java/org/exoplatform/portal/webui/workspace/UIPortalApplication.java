/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.workspace;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.skin.Skin;
import org.exoplatform.portal.skin.SkinConfig;
import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.portal.skin.SkinURL;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Orientation;
import org.exoplatform.web.application.javascript.JavascriptConfigService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;

/**
 * This extends the UIApplication and hence is a sibling of UIPortletApplication 
 * (used by any eXo Portlets as the Parent class to build the portlet component tree).
 * 
 * The UIPortalApplication is responsible to build its subtree according to some configuration parameters. 
 * If all components are displayed it is composed of 3 UI components:

 *  - UIControlWorkSpace : the left expandable column that can contains widgets containers and the start menu
 *  - UIWorkingWorkSpace: the right part that can display the normal or webos portal layouts
 *  - UIPopupWindow: a popup window that display or not
 * 
 */
@ComponentConfig (
    lifecycle = UIPortalApplicationLifecycle.class,
    template = "system:/groovy/portal/webui/workspace/UIPortalApplication.gtmpl"
)
public class UIPortalApplication extends UIApplication {

  private boolean isEditting = false ;
  private String nodePath_;
  private Locale locale_ = Locale.ENGLISH  ;
  private Orientation orientation_ = Orientation.LT;

  final static public String UI_CONTROL_WS_ID = "UIControlWorkspace" ;
  final static public String UI_WORKING_WS_ID = "UIWorkingWorkspace" ;
  final static public String UI_MASK_WS_ID = "UIMaskWorkspace" ;

  private String skin_ = "Default" ;

  private UserPortalConfig userPortalConfig_;
  
  private boolean isSessionOpen = false ;
  
  public boolean isViewMode = false;
  public boolean isPortletMode = true;

  /**
   * The constructor of this class is used to build the tree of UI components that will be aggregated
   * in the portal page. 
   * 
   * 1) The component is stored in the current PortalRequestContext ThreadLocal 
   * 2) The configuration for the portal associated with the current user request is extracted from the 
   *    PortalRequestContext
   * 3) Then according to the context path, either a public or private portal is initiated. Usually a public
   *    portal does not contain the left column and only the private one has it.
   * 4) The skin to use is setup
   * 5) Finally, the current component is associated with the current portal owner      
   * 
   * @throws Exception
   */
  public UIPortalApplication() throws Exception {
    log = ExoLogger.getLogger("portal:UIPortalApplication"); 
    PortalRequestContext  context = PortalRequestContext.getCurrentInstance() ;
    userPortalConfig_ = (UserPortalConfig)context.getAttribute(UserPortalConfig.class);
    if(userPortalConfig_ == null) throw new Exception("Can't load user portal config");
    
    //  dang.tung - set portal language by user preference -> browser -> default
    //------------------------------------------------------------------------------
    String portalLanguage = null ;
    LocaleConfigService localeConfigService  = getApplicationComponent(LocaleConfigService.class) ;
    OrganizationService orgService = getApplicationComponent(OrganizationService.class) ;
    LocaleConfig localeConfig = localeConfigService.getLocaleConfig(userPortalConfig_.getPortalConfig().getLocale());
    String user = context.getRemoteUser();
    if(user != null) {
      UserProfile userProfile = orgService.getUserProfileHandler().findUserProfileByName(user) ;
      if(userProfile != null) {
        portalLanguage = userProfile.getUserInfoMap().get("user.language") ;
       } else {
         if (log.isWarnEnabled()) log.warn("Could not load user profile for " + user + ". Using default portal locale.");
       }
    }
    localeConfig = localeConfigService.getLocaleConfig(portalLanguage) ;
    if(portalLanguage == null || !portalLanguage.equals(localeConfig.getLanguage())) {
      // if user language no support by portal -> get browser language if no -> get portal
      portalLanguage = context.getRequest().getLocale().getLanguage() ;
      localeConfig = localeConfigService.getLocaleConfig(portalLanguage) ;
      if(!portalLanguage.equals(localeConfig.getLanguage())) {
        localeConfig = localeConfigService.getLocaleConfig(userPortalConfig_.getPortalConfig().getLocale()) ;
      }
    }
    setLocale(localeConfig.getLocale()) ;
    setOrientation(localeConfig.getOrientation());
    //-------------------------------------------------------------------------------
    context.setUIApplication(this);
    UserACL acl = getApplicationComponent(UserACL.class);
    if(acl.hasAccessControlWorkspacePermission())
      addChild(UIControlWorkspace.class, UIPortalApplication.UI_CONTROL_WS_ID, null) ;
    addWorkingWorkspace() ;

    String currentSkin = userPortalConfig_.getPortalConfig().getSkin();
    if(currentSkin != null && currentSkin.trim().length() > 0) skin_ = currentSkin;
    setOwner(context.getPortalOwner());
  }

  public boolean isSessionOpen() {
    return isSessionOpen;
  }

  public void setSessionOpen(boolean isSessionOpen) {
    this.isSessionOpen = isSessionOpen;
  }

  public Orientation getOrientation() {
    return orientation_;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation_ = orientation;
  }

  public Locale getLocale() {  return locale_ ; }
  public void   setLocale(Locale locale) { locale_ = locale ; }

  public void setEditting(boolean bln) { this.isEditting = bln ; }  
  public boolean isEditting() { return isEditting ; }

  public Collection<String> getJavascriptURLs() {
    JavascriptConfigService service = getApplicationComponent(JavascriptConfigService.class);
    return service.getAvailableScriptsPaths();
  }

  public Collection<Skin> getPortalSkins() {
    SkinService skinService = getApplicationComponent(SkinService.class) ;

    //
    Collection<Skin> skins = new ArrayList<Skin>(skinService.getPortalSkins(skin_));

    //
    SkinConfig skinConfig = skinService.getSkin(Util.getUIPortal().getName(),skin_);
    if(skinConfig != null) {
      skins.add(skinConfig);
    }

    //
    Set<SkinConfig> portletConfigs = getPortalPortletSkins();
    // don't merge portlet if portlet not available 
    if (!portletConfigs.isEmpty()) {
      skins.add(skinService.merge(portletConfigs));
    }
    //
    return skins;
  }

  private Set<SkinConfig> getPortalPortletSkins() {
    Set<SkinConfig> portletConfigs = new HashSet<SkinConfig>();
    for (UIComponent child : findFirstComponentOfType(UIPortal.class).getChildren()) {
      if (child instanceof UIPortlet) {
        SkinConfig portletConfig = getPortletSkinConfig((UIPortlet)child);
        if (portletConfig != null) {
          portletConfigs.add(portletConfig);
        }
      }
    }
    return portletConfigs;
  }

  public String getSkin() {  return skin_ ; }
  public void setSkin(String skin){ this.skin_ = skin; }

  private SkinConfig getSkin(String module) {
    SkinService skinService = getApplicationComponent(SkinService.class) ;
    return skinService.getSkin(module, skin_) ;
  }

  /**
   * Returns a list of portlets skin that have to be added in the HTML
   * head tag. The skin can directly point to a real css file (this
   * is the case of all the porlet included in a page) or point to a
   * servlet that agregates different portlet CSS files into one to
   * lower the number of HTTP calls (this is the case in production as
   * all the portlets included in a portal, and hence there on everypage
   * are merged into a single CSS file)
   *
   * @return the portlet skins
   */
  public Set<Skin> getPortletSkins() {
    // Set to avoid repetition
    Set<Skin> skins = new HashSet<Skin>();

    // Determine portlets visible on the page
    List<UIPortlet> uiportlets = new ArrayList<UIPortlet>();
    UIWorkingWorkspace uiWorkingWS = getChildById(UI_WORKING_WS_ID);
    UIPortal uiPortal = uiWorkingWS.getChild(UIPortal.class);
    uiPortal.findComponentOfType(uiportlets, UIPortlet.class);
    UIPortalToolPanel toolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
    if (toolPanel != null && toolPanel.isRendered()) {
      toolPanel.findComponentOfType(uiportlets, UIPortlet.class);
    }

    // Get portal portlets to filter since they are already in the portal skins
    Set<SkinConfig> portletConfigs = getPortalPortletSkins();

    //
    for (UIPortlet uiPortlet : uiportlets) {
      SkinConfig skinConfig = getPortletSkinConfig(uiPortlet);
      if (skinConfig != null && !portletConfigs.contains(skinConfig)) {
        skins.add(skinConfig);
      }
    }

    //
    return skins;
  }

  private SkinConfig getPortletSkinConfig(UIPortlet portlet) {
    String module = portlet.getExoWindowID().getPortletApplicationName()
    + "/" + portlet.getExoWindowID().getPortletName();
    return getSkin(module);
  }

  /**
   * The central area is called the WorkingWorkspace. It is composed of:
   * 
   * 1) A UIPortal child which is filled with portal data using the PortalDataMapper helper tool
   * 2) A UIPortalToolPanel which is not rendered by default
   * 
   * A UIMaskWorkspace is also added to provide powerfull focus only popups
   * 
   * @throws Exception
   */
  private void addWorkingWorkspace() throws Exception {
    UIWorkingWorkspace uiWorkingWorkspace = 
      addChild(UIWorkingWorkspace.class, UIPortalApplication.UI_WORKING_WS_ID, null) ;
    UIPortal uiPortal = createUIComponent(UIPortal.class, null, null);
    PortalDataMapper.toUIPortal(uiPortal, userPortalConfig_);
    uiWorkingWorkspace.addChild(uiPortal) ;    
    uiWorkingWorkspace.addChild(UIPortalToolPanel.class, null, null).setRendered(false) ;    
    addChild(UIMaskWorkspace.class, UIPortalApplication.UI_MASK_WS_ID, null) ;
  }


  /**
   * The processDecode() method is doing 3 actions:
   * 1) if the nodePath is null (case of the first request) a call to super.processDecode(context) 
   *    is made and we end the method here
   * 2) if the nodePath exist but is equals to the current one then we also call super and stops here
   * 3) if the requested nodePath is not equals to the current one , then an event of type 
   *    PageNodeEvent.CHANGE_PAGE_NODE is sent to the asociated EventListener; a call to super is then
   *    done
   */
  public void  processDecode(WebuiRequestContext context) throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext) context;
    String nodePath = pcontext.getNodePath().trim();

    if(!nodePath.equals(nodePath_)) {
	    nodePath_ = nodePath;
	    UIPortal uiPortal = findFirstComponentOfType(UIPortal.class);
	    PageNodeEvent<UIPortal> pnevent = 
	      new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, nodePath_) ;
	    uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    }
    super.processDecode(context);
  }

  /**
   * The processrender() method handles the creation of the returned HTML either for a full
   * page render or in the case of an AJAX call
   * 
   * The first request, Ajax is not enabled (means no ajaxRequest parameter in the request) and 
   * hence the super.processRender() method is called. This will hence call the processrender() of 
   * the Lifecycle object as this method is not overidden in UIPortalApplicationLifecycle. There we 
   * simply render the bounded template (groovy usually). Note that bounded template are also defined
   * in component annotations, so for the current class it is UIPortalApplication.gtmpl
   * 
   * On second calls, request have the "ajaxRequest" parameter set to true in the URL. In that case 
   * the algorithm is a bit more complex:
   * 
   *    a) The list of components that should be updated is extracted using the 
   *       context.getUIComponentToUpdateByAjax() method. That list was setup during the process action
   *       phase
   *    b) Portlets and other UI components to update are split in 2 different lists
   *    c) Portlets full content are returned and set with the tag <div class="PortalResponse">
   *    d) Block to updates (which are UI components) are set within 
   *       the <div class="PortalResponseData"> tag
   *    e) Then the scripts and the skins to reload are set in the <div class="PortalResponseScript">
   * 
   */
  public void  processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    if(!context.useAjax()) {
      super.processRender(context) ;
    } else {
      PortalRequestContext pcontext = (PortalRequestContext)context;

      UIMaskWorkspace uiMaskWS = getChildById(UIPortalApplication.UI_MASK_WS_ID);
      if(uiMaskWS.isUpdated()) pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
      if(getUIPopupMessages().hasMessage()) {
        pcontext.addUIComponentToUpdateByAjax(getUIPopupMessages()) ;
      }

      Set<UIComponent> list = context.getUIComponentToUpdateByAjax() ;
      List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>(3);
      List<UIComponent> uiDataComponents = new ArrayList<UIComponent>(5);

      if(list != null) {
        for(UIComponent uicomponent : list) {
          if(uicomponent instanceof UIPortlet) uiPortlets.add((UIPortlet)uicomponent) ;
          else uiDataComponents.add(uicomponent) ;
        }
      }
      w.write("<div class=\"PortalResponse\">") ;
      w.  write("<div class=\"PortalResponseData\">");
      for(UIComponent uicomponent : uiDataComponents) {
        if(log.isDebugEnabled())
          log.debug("AJAX call: Need to refresh the UI component " + uicomponent.getName());
        renderBlockToUpdate(uicomponent, context, w) ;
      }
      w.  write("</div>");

      if(!context.getFullRender()) {
        for(UIPortlet uiPortlet : uiPortlets) {
          if(log.isDebugEnabled())
            log.debug("AJAX call: Need to refresh the Portlet " + uiPortlet.getWindowId());

          w.write("<div class=\"PortletResponse\" style=\"display: none\">") ;
          w.  append("<div class=\"PortletResponsePortletId\">" + uiPortlet.getExoWindowID().getUniqueID()+"</div>") ;
          w.  append("<div class=\"PortletResponsePortletTitle\"></div>") ;
          w.  append("<div class=\"PortletResponsePortletMode\"></div>") ;
          w.  append("<div class=\"PortletResponsePortletState\"></div>") ;
          w.  append("<div class=\"PortletResponseData\">") ;

          /*
           * If the portlet is using our UI framework or supports it then it will return a set of block
           * to updates. If there is not block to update the javascript client will see that as a full 
           * refresh of the content part
           */
          uiPortlet.processRender(context) ;       

          w.  append("</div>") ;
          w.  append("<div class=\"PortletResponseScript\"></div>") ;
          w.write("</div>") ;
        }
      }

      w.  write("<div class=\"PortalResponseScript\">"); 
      w.    write(pcontext.getJavascriptManager().getJavascript());
      w.    write("eXo.core.Browser.onLoad();\n"); 
      w.    write(pcontext.getJavascriptManager().getCustomizedOnLoadScript()) ;
      String skin  = getAddSkinScript(list);
      if(skin != null){
        w.  write(skin) ;
      }
      w.  write("</div>") ;
      w.write("</div>") ;
    }
  }

  private String getAddSkinScript(Set<UIComponent> updateComponents) {
    if(updateComponents == null) return null;
    List<UIPortlet> uiportlets = new ArrayList<UIPortlet>() ;
    for(UIComponent uicomponent : updateComponents) {
      if(uicomponent instanceof UIContainer){
        UIContainer uiContainer = (UIContainer) uicomponent;
        uiContainer.findComponentOfType(uiportlets, UIPortlet.class) ;
      }
    }
    List<SkinConfig> skins = new ArrayList<SkinConfig>() ;
    SkinService skinService = getApplicationComponent(SkinService.class);
    for(UIPortlet uiPortlet : uiportlets){
      String module = uiPortlet.getExoWindowID().getPortletApplicationName() + "/" + uiPortlet.getExoWindowID().getPortletName() ;
      SkinConfig skinConfig = skinService.getSkin(module,skin_) ;
      if(skinConfig != null) skins.add(skinConfig);
    }
    StringBuilder b = new StringBuilder(1000) ;
    for(SkinConfig ele : skins) {
      SkinURL url = ele.createURL();
      url.setOrientation(orientation_);
      b.append("eXo.core.Skin.addSkin('").append(ele.getId()).
      append("','").append(url).append("');\n");
    }
    return b.toString() ;
  }

  public UserPortalConfig getUserPortalConfig() { return userPortalConfig_; }
  public void setUserPortalConfig(UserPortalConfig userPortalConfig) {
    this.userPortalConfig_ = userPortalConfig; 
  }
}