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
package org.exoplatform.webui.form;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Represents a tabbed pane
 * 
 */
abstract public class UIFormTabPane extends UIForm {
	/**
   * name of this element 
	 */
	public String name_ ;

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
    name_ = name;
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
  
  static public class SelectTabActionListener extends EventListener<UIFormTabPane> {
    public void execute(Event<UIFormTabPane> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext() ;
      String renderTab = context.getRequestParameter(UIComponent.OBJECTID) ;
      if(renderTab == null) return;
      event.getSource().setSelectedTab(renderTab) ;
    }
  }

}