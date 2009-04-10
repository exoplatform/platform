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
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIItemSelector;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
@ComponentConfig(
  template = "app:/groovy/portal/webui/portal/UISkinSelector.gtmpl",
  events = {
    @EventConfig(listeners = UISkinSelector.SaveActionListener.class),
    @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
  }
)
public class UISkinSelector extends UIContainer {
  private String name_;
  
  @SuppressWarnings("unchecked")
  public UISkinSelector() throws Exception  { 
    name_ = "UIChangeSkin";    
    UIPortal uiPortal = Util.getUIPortal();
    List<SelectItemCategory> itemCategories = new ArrayList<SelectItemCategory>();
    SkinService skinService = uiPortal.getApplicationComponent(SkinService.class);
    for (String skin : skinService.getAvailableSkinNames()) {
      SelectItemCategory skinCategory = new  SelectItemCategory(skin, false);
      skinCategory.addSelectItemOption(new SelectItemOption(skin, skin, skin));
      itemCategories.add(skinCategory);
    }
    itemCategories.get(0).setSelected(true);
    
    UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class) ;
    String currentSkin = uiPortalApp.getSkin() ;
    
    if(currentSkin == null ) currentSkin = "Default"; 
    for(SelectItemCategory ele : itemCategories) {
      if(ele.getName().equals(currentSkin)) ele.setSelected(true);
      else  ele.setSelected(false);
    }
    
    UIItemSelector selector = new UIItemSelector("Skin");
    selector.setItemCategories(itemCategories);
    selector.setRendered(true);
    addChild(selector);
  }
  
  @Override
  public String url(String name) throws Exception {
    // TODO Auto-generated method stub
    return super.url(name);
  }
  
  public String getName() { return name_; }

  public void setName(String name) { name_ = name; }
  
  static public class SaveActionListener  extends EventListener<UISkinSelector> {
    public void execute(Event<UISkinSelector> event) throws Exception {
      String skin  = event.getRequestContext().getRequestParameter("skin");
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);    
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
      //event.getRequestContext().addUIComponentToUpdateByAjax(uiApp) ;
      Util.getPortalRequestContext().setFullRender(false) ;
      if(skin == null || skin.trim().length() < 1) return;       
      uiApp.setSkin(skin);
    }
  }

}
