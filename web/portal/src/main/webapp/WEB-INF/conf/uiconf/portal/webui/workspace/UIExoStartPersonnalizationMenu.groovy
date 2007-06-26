import java.util.ArrayList;
import org.exoplatform.portal.webui.workspace.UIExoStart ;

ArrayList menus = new ArrayList(3) ;
UIExoStart.MenuItemContainer menu = new UIExoStart.MenuItemContainer("Administration").
  add(new UIExoStart.MenuItemContainer("Basic").
      add(new UIExoStart.MenuItemAction("PageCreationWizard", "PageCreationWizardIcon","PageCreationWizard", true)).
      add(new UIExoStart.MenuItemAction("EditCurrentPage","EditCurrentPageIcon","EditCurrentPage", true))).
      
  add(new UIExoStart.MenuItemContainer("Advanced").
      add(new UIExoStart.MenuItemContainer("Portal").
          add(new UIExoStart.MenuItemAction("Edit", "EditCurrentPortalIcon", "EditPortal", true)).
          add(new UIExoStart.MenuItemAction("Browse", "BrowsePortalsIcon", "BrowsePortal", true))).
      add(new UIExoStart.MenuItemContainer("Page").
          add(new UIExoStart.MenuItemAction("Edit","EditNavigationIcon", "EditPage", true)).
          add(new UIExoStart.MenuItemAction("Browse", "BrowsePagesIcon", "BrowsePage", true)))).  
  add(new UIExoStart.MenuItemAction("SkinSettings", "SkinSettingIcon", "SkinSettings", true)).
  add(new UIExoStart.MenuItemAction("LanguageSettings", "LanguageSettingIcon", "LanguageSettings", true)).
  add(new UIExoStart.MenuItemAction("ChangePortal", "ChangePortalIcon", "ChangePortal", true));
 
menus.add(menu) ;
return menus ;
