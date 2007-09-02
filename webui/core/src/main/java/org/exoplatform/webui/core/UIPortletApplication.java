package org.exoplatform.webui.core;

import java.io.Writer;
import java.util.List;

import org.exoplatform.services.portletcontainer.ExoPortletRequest;
import org.exoplatform.services.portletcontainer.pci.WindowID;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;

abstract public class UIPortletApplication extends  UIApplication {
  private int minWidth = 300 ;
  private int minHeight = 300 ;
  
  static public String VIEW_MODE = "ViewMode" ;
  static public String EDIT_MODE = "EditMode" ;
  static public String HELP_MODE = "HelpMode" ;
  static public String CONFIG_MODE = "ConfigMode" ;

  public UIPortletApplication() throws Exception {}
  
  public int getMinWidth() { return minWidth ; }
  public void setMinWidth(int minWidth) { this.minWidth = minWidth ; }
  
  public int getMinHeight() { return minHeight ; }
  public void setMinHeight(int minHeight) { this.minHeight = minHeight ; }
  
  /**
   * The default processRender for an UIPortletApplication handles two cases:
   * 
   *   A. Ajax is used 
   *   ---------------
   *     If Ajax is used and that the entire portal should not be re rendered, then an AJAX fragment is 
   *     generated with information such as the portlet id, the portlet title, the portlet modes, the window 
   *     states as well as the HTML for the block to render
   *   
   *   B. A full render is made
   *   ------------------------
   *      a simple call to the method super.processRender(context) which will delegate the call to all the 
   *      Lifecycle components
   *   
   */
  public void  processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    WebuiRequestContext pContext = (WebuiRequestContext)context.getParentAppRequestContext();
    if(context.useAjax() && !pContext.getFullRender()) {
      Writer w =  context.getWriter() ;
      List<UIComponent> list = context.getUIComponentToUpdateByAjax() ;
      if(list == null) list = app.getDefaultUIComponentToUpdateByAjax(context) ;      
      for(UIComponent uicomponent : list) {
        renderBlockToUpdate(uicomponent, context, w) ;
      }
      UIApplication uiApplication = context.getUIApplication();
      uiApplication.processRender(context);
      
    }  else {
      super.processRender(context) ;
    }
    
  }
}