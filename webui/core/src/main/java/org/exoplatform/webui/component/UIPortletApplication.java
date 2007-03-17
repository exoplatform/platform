package org.exoplatform.webui.component;

import java.io.Writer;
import java.util.List;

import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.RequestContext;

abstract public class UIPortletApplication extends  UIApplication {
  static public String VIEW_MODE = "ViewMode" ;
  static public String EDIT_MODE = "EditMode" ;
  static public String HELP_MODE = "HelpMode" ;
  static public String CONFIG_MODE = "ConfigMode" ;

  public UIPortletApplication() throws Exception {
    
  }
  
  public void  processRender(Application app, RequestContext context) throws Exception {
    RequestContext pContext = context.getParentAppRequestContext();
    if(context.isAjaxRequest() && !pContext.isForceFullUpdate()) {
      Writer w =  context.getWriter() ;
      w.write("<div class=\"PortletResponse\">") ;
      w.  append("<div class=\"PortletResponsePortletId\"></div>") ;
      w.  append("<div class=\"PortletResponsePortletTitle\"></div>") ;
      w.  append("<div class=\"PortletResponsePortletMode\"></div>") ;
      w.  append("<div class=\"PortletResponsePortletState\"></div>") ;
      w.  append("<div class=\"PortletResponseData\">") ;
      List<UIComponent> list = context.getUIComponentToUpdateByAjax() ;
      if(list == null) list = app.getDefaultUIComponentToUpdateByAjax(context) ;      
      for(UIComponent uicomponent : list) {
        renderBlockToUpdate(uicomponent, context, w) ;
      }
      w.  append("</div>") ;
      w.  append("<div class=\"PortletResponseScript\"></div>") ;
      w.write("</div>") ;
    }  else {
      super.processRender(context) ;
    }
  }
}