package org.exoplatform.platform.component;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.mop.user.UserNavigation;
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

   public UserNavigation getSelectedNavigation() throws Exception
   {
      return Util.getUIPortal().getUserNavigation(); 
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
      UserNavigation selectedNavigation = getSelectedNavigation();
      if (selectedNavigation == null)
      {
         return false;
      }
      return selectedNavigation.isModifiable();
   }
   
   private boolean hasEditPermissionOnPortal() throws Exception
   {
      UIPortalApplication portalApp = Util.getUIPortalApplication();
      UIPortal currentUIPortal = portalApp.<UIWorkingWorkspace>findComponentById(UIPortalApplication.UI_WORKING_WS_ID).findFirstComponentOfType(UIPortal.class);
      UserACL userACL = portalApp.getApplicationComponent(UserACL.class);
      return userACL.hasEditPermissionOnPortal(currentUIPortal.getOwnerType(), currentUIPortal.getName(), currentUIPortal.getEditPermission());
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
