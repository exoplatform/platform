import java.util.ArrayList;
import org.exoplatform.portal.component.control.UIExoStart ;

ArrayList menus = new ArrayList(3) ;
UIExoStart.MenuItemContainer menu = new UIExoStart.MenuItemContainer("Administration").
  add(new UIExoStart.MenuItemContainer("Basic").
      add(new UIExoStart.MenuItemAction("PageCreationWizard", "PageCreationWizardIcon","PageCreationWizard", true)).
      add(new UIExoStart.MenuItemAction("EditCurrentPage","EditCurrentPageIcon","EditCurrentPage", true))).
      
  add(new UIExoStart.MenuItemContainer("Advanced").
      add(new UIExoStart.MenuItemContainer("Portal").
          add(new UIExoStart.MenuItemAction("Edit", "EditCurrentPortalIcon", "PortalManagement", true)).
          add(new UIExoStart.MenuItemAction("Browse", "BrowsePortalsIcon", "PortalManagement", true))).
      add(new UIExoStart.MenuItemContainer("Page").
          add(new UIExoStart.MenuItemAction("Edit","EditNavigationIcon", "PageManagement", true)).
          add(new UIExoStart.MenuItemAction("Browse", "BrowsePagesIcon", "PageManagement", true)))).  
      /*add(new UIExoStart.MenuItemContainer("ChangeSkin").
              add(new UIExoStart.MenuItemAction("Default", "DefaultSkinIcon", "ChangeSkin", false)).
              add(new UIExoStart.MenuItemAction("Mac", "MacSkinIcon", "ChangeSkin", false)).
              add(new UIExoStart.MenuItemAction("Vista", "VistaSkinIcon", "ChangeSkin", false))).*/                  
      add(new UIExoStart.MenuItemContainer("ChangeLanguage").
              add(new UIExoStart.MenuItemAction("English", "LanguageItemIcon", "ChangeLanguage", false)).
              add(new UIExoStart.MenuItemAction("Vietnamese", "LanguageItemIcon", "ChangeLanguage", false)).
              add(new UIExoStart.MenuItemAction("French", "LanguageItemIcon", "ChangeLanguage", false))).
  add(new UIExoStart.MenuItemAction("SkinSettings", "SkinSettingIcon", "SkinSettings", true)).
  add(new UIExoStart.MenuItemAction("LanguageSettings", "LanguageSettingIcon", "LanguageSettings", true));
 
menus.add(menu) ;
return menus ;
