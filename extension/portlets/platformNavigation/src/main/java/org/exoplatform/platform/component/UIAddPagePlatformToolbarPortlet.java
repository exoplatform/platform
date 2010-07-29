package org.exoplatform.platform.component;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig
 (lifecycle = UIApplicationLifecycle.class,
		  template = "app:/groovy/platformNavigation/portlet/UIAddPagePlatformToolbarPortlet/UIAddPagePlatformToolbarPortlet.gtmpl"
	   )
public class UIAddPagePlatformToolbarPortlet extends UIPortletApplication
{
   // Minh Hoang TO
   // TODO: Add a ThreadLocal cache to avoid double invocation of editPermission
   // check ( one in processRender method, and one in Groovy template )

   public UIAddPagePlatformToolbarPortlet() throws Exception
   {
   }

   public PageNavigation getSelectedNavigation() throws Exception
   {
      return Util.getUIPortal().getSelectedNavigation();
      
      /*
      PageNavigation nav = Util.getUIPortal().getSelectedNavigation();
      if (nav != null)
         return nav;
      if (Util.getUIPortal().getNavigations().size() < 1)
         return null;
      return Util.getUIPortal().getNavigations().get(0);
      */
   }

   @Override
   public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception
   {
      // A user could view the toolbar portlet iff he/she has edit permission
      // either on
      // 'active' page, 'active' portal or 'active' navigation
      if (hasEditPermissionOnNavigation() || hasEditPermissionOnPage() || hasEditPermissionOnPortal())
      {
         super.processRender(app, context);
      }
   }

   private boolean hasEditPermissionOnNavigation() throws Exception
   {
      PageNavigation selectedNavigation = getSelectedNavigation();
      UIPortalApplication portalApp = Util.getUIPortalApplication();
      UserACL userACL = portalApp.getApplicationComponent(UserACL.class);
      if (selectedNavigation == null || userACL == null)
      {
         return false;
      }
      else
      {
         return userACL.hasEditPermission(selectedNavigation);
      }
   }
   
   private boolean hasEditPermissionOnPortal() throws Exception
   {
      UIPortalApplication portalApp = Util.getUIPortalApplication();
      UIPortal currentUIPortal = portalApp.<UIWorkingWorkspace>findComponentById(UIPortalApplication.UI_WORKING_WS_ID).findFirstComponentOfType(UIPortal.class);
      UserACL userACL = portalApp.getApplicationComponent(UserACL.class);
      return userACL.hasEditPermissionOnPortal(currentUIPortal.getOwnerType(), currentUIPortal.getOwner(), currentUIPortal.getEditPermission());
   }

   private boolean hasEditPermissionOnPage() throws Exception
   {
      UIPortalApplication portalApp = Util.getUIPortalApplication();
      UIWorkingWorkspace uiWorkingWS = portalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPageBody pageBody = uiWorkingWS.findFirstComponentOfType(UIPageBody.class);
      UIPage uiPage = (UIPage)pageBody.getUIComponent();

      if (uiPage == null)
      {
         return false;
      }
      else
      {
         UserACL userACL = portalApp.getApplicationComponent(UserACL.class);
         return userACL.hasEditPermissionOnPage(uiPage.getOwnerType(), uiPage.getOwnerId(), uiPage.getEditPermission());
      }
   }

}
