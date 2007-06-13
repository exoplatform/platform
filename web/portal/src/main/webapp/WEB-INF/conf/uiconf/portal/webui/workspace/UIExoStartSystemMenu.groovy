import java.util.ArrayList;
import org.exoplatform.portal.webui.workspace.UIExoStart ;

ArrayList menus = new ArrayList(3) ;
UIExoStart.MenuItemContainer systemMenu = new UIExoStart.MenuItemContainer("System", "SystemIcon").
  add(new UIExoStart.MenuItemAction("Support")).
//  add(new UIExoStart.MenuItemAction("Debug")).
//add(new UIExoStart.MenuItemAction("Web20", "Web20Icon", "Web20", false)) ;  
  add(new UIExoStart.MenuItemAction("Refresh", "RefreshIcon", "Refresh", false));

menus.add(systemMenu) ;

return menus ;
