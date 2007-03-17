package org.exoplatform.webui.component;


abstract public class UITabPane extends UIContainer {
  
  private UIQuickHelp uiQuickHelp_ ;
  
	public UITabPane() throws Exception {
    uiQuickHelp_ = createUIComponent(UIQuickHelp.class, null, null) ;
    uiQuickHelp_.setParent(this) ;
	}
  
  public UIQuickHelp getUIQuickHelp() { return uiQuickHelp_ ; }
  
  public  void renderUIQuickHelp()  throws Exception {
    if(uiQuickHelp_.isRendered())renderUIComponent(uiQuickHelp_) ;
  }
  
}