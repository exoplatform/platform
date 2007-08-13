package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.webui.navigation.UIPageManagement;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
    template = "system:/groovy/webui/core/UIToolbar.gtmpl",
    events = { 
        @EventConfig(listeners = UIPageBrowseControlBar.BackActionListener.class),
        @EventConfig(listeners = UIPageBrowseControlBar.FinishActionListener.class)
    }
)
public class UIPageBrowseControlBar extends UIToolbar {

  private UIComponent uiBackComponent ;

  public UIComponent getBackComponent() { return uiBackComponent ; }
  public void setBackComponent(UIComponent uiComp) { uiBackComponent = uiComp ; }

  public boolean hasBackEvent(){ return uiBackComponent != null; }


  public UIPageBrowseControlBar() throws Exception { setToolbarStyle("ControlToolbar") ; }

  static public class BackActionListener extends EventListener<UIPageBrowseControlBar> {
    public void execute(Event<UIPageBrowseControlBar> event) throws Exception {
      UIPageBrowseControlBar uiBrowseControlBar = event.getSource();

      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel(); 
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class);
      UIPageBrowser uiPageBrowser = (UIPageBrowser) uiBrowseControlBar.getBackComponent() ;
      uiPageBrowser.reset();
      uiToolPanel.setUIComponent(uiPageBrowser) ;
      uiToolPanel.setShowMaskLayer(false);
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;

      UIPageManagement uiManagement = uiBrowseControlBar.getParent();
      uiManagement.setRenderedChild(UIDescription.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
  }

  static public class FinishActionListener extends EventListener<UIPageBrowseControlBar> {
    public void execute(Event<UIPageBrowseControlBar> event) throws Exception {
      UIPageBrowseControlBar uiBrowseControlBar = event.getSource();
      UIPageManagement pageManagement = uiBrowseControlBar.getParent();
      UIPageEditBar uiEditBar = pageManagement.getChild(UIPageEditBar.class);
      uiEditBar.savePage();

      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();      
      uiToolPanel.setShowMaskLayer(false);
      UIPageBrowser uiPageBrowser = (UIPageBrowser) uiBrowseControlBar.getBackComponent() ;
      uiPageBrowser.reset();
      uiToolPanel.setUIComponent(uiPageBrowser) ;

      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;

      UIPageManagement uiManagement = uiBrowseControlBar.getParent();
      uiManagement.setRenderedChild(UIDescription.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
  }
}
