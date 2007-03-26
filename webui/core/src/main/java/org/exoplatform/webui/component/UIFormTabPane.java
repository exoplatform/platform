package org.exoplatform.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;

abstract public class UIFormTabPane extends UIForm {
	
	public String name_ ;
  private boolean withRenderTab = true;
  private boolean withInfoBar = true;
  private boolean withRenderTabName = true ;
  private String renderTabId = "";
  public static String RENDER_TAB = "currentSelectedTab";
	
	public UIFormTabPane(String name) throws Exception {
	  this(name, true);
	}
  
  public UIFormTabPane(String name, boolean hasQuickHelp) throws Exception {
    name_ = name;
    if(!hasQuickHelp) return;    
  }
  
  public String getRenderTabId() { return renderTabId; }
  public void setRenderTabId(String renderTabId) { this.renderTabId = renderTabId; }

  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    String renderTab = context.getRequestParameter(RENDER_TAB) ;
    if(renderTab != null) renderTabId  = renderTab;
    UIComponent uiComp = findComponentById(renderTabId);  
    if(uiComp == null) return;
    for(UIComponent child : getChildren()){
      child.setRendered(false);
    }
    uiComp.setRendered(true);
  }
		
	public String getName() { return name_ ;}  

//	public UIQuickHelp getUIQuickHelp() {  return uiQuickHelp_;  }
//  public boolean hasQuickHelp(){ return uiQuickHelp_ != null; }
  
  public boolean hasInfoBar(){ return withInfoBar; }
  public void setInfoBar(boolean value) { withInfoBar  = value; }
  
  public boolean hasRenderResourceTabName() { return withRenderTabName ; }
  public void setRenderResourceTabName(boolean bool) { withRenderTabName = bool ; }

  public void setWithRenderTab(boolean bool) { withRenderTab = bool; }
  public boolean hasWithRenderTab() { return withRenderTab; }

}