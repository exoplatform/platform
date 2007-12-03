package org.exoplatform.webui.form;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
/**
 * Represents a tabbed pane
 * 
 */
abstract public class UIFormTabPane extends UIForm {
	/**
   * name of this element 
	 */
	public String name_ ;

  private boolean withRenderTab = true;
  /**
   * Whether to represent an info bar
   */
  private boolean withInfoBar = true;
  private boolean withRenderTabName = true ;
  /**
   * The tab to render
   */
  private String selectedTabId = "";
  /**
   * The tab to render by default (DECODE phase)
   */
  public static String RENDER_TAB = "currentSelectedTab";
	
	public UIFormTabPane(String name) throws Exception {
	  this(name, true);
	}
  
  public UIFormTabPane(String name, boolean hasQuickHelp) throws Exception {
    name_ = name;
    if(!hasQuickHelp) return;    
  }
  
  public String getSelectedTabId() { return selectedTabId; }
  public void setSelectedTab(String renderTabId) { selectedTabId = renderTabId; }
  public void setSelectedTab(int index) { selectedTabId = ((UIComponent)getChild(index-1)).getId();}
  public void processDecode(WebuiRequestContext context) throws Exception {   
    String renderTab = context.getRequestParameter(RENDER_TAB) ;
    if(renderTab != null) selectedTabId  = renderTab;
    super.processDecode(context);
  }
		
	public String getName() { return name_ ;}  

  public boolean hasInfoBar(){ return withInfoBar; }
  public void setInfoBar(boolean value) { withInfoBar  = value; }
  
  public boolean hasRenderResourceTabName() { return withRenderTabName ; }
  public void setRenderResourceTabName(boolean bool) { withRenderTabName = bool ; }

  public void setWithRenderTab(boolean bool) { withRenderTab = bool; }
  public boolean hasWithRenderTab() { return withRenderTab; }

}