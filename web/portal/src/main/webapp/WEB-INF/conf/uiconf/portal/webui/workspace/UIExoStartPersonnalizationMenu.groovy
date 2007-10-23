import java.util.ArrayList;
import org.exoplatform.portal.webui.workspace.UIExoStart ;

ArrayList menus = new ArrayList(4) ;
UIExoStart.MenuItemContainer menu = new UIExoStart.MenuItemContainer("Administration").
  add(new UIExoStart.MenuItemContainer("Basic").
      add(new UIExoStart.MenuItemAction("PageCreationWizard", "PageCreationWizardIcon","PageCreationWizard", true)).
      add(new UIExoStart.MenuItemAction("EditCurrentPage","EditCurrentPageIcon","EditCurrentPage", true))).
      
  add(new UIExoStart.MenuItemContainer("Advanced").
      add(new UIExoStart.MenuItemAction("EditPage","EditNavigationIcon", "EditPage", true)).
      add(new UIExoStart.MenuItemAction("EditPortal", "EditCurrentPortalIcon", "EditPortal", true)).
      add(new UIExoStart.MenuItemAction("BrowsePage", "BrowsePagesIcon", "BrowsePage", true)).
      add(new UIExoStart.MenuItemAction("BrowsePortal", "BrowsePortalsIcon", "BrowsePortal", true))).  
      
  add(new UIExoStart.MenuItemAction("SkinSettings", "SkinSettingIcon", "SkinSettings", true)).
  add(new UIExoStart.MenuItemAction("LanguageSettings", "LanguageSettingIcon", "LanguageSettings", true)).
  add(new UIExoStart.MenuItemAction("ChangePortal", "ChangePortalIcon", "ChangePortal", true)).
  add(new UIExoStart.MenuItemAction("AccountSettings", "AccountSettingIcon", "AccountSettings", true)) ;
 	
menus.add(menu) ;
return menus ;
