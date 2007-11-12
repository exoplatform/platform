import java.util.ArrayList;

SkinService.addSkin("PortalSkin","Default", "/eXoResources/skin/DefaultSkin/portal/webui/component/UIPortalApplicationSkin.css");

SkinService.addSkin("CoreSkin", "Default", "/eXoResources/skin/DefaultSkin/webui/component/Stylesheet.css");

SkinService.addThemeURL("/eXoResources/skin/PortletThemes/Stylesheet.css");

ArrayList theme = new ArrayList(5) ;
theme.add("SimpleBlue") ;
theme.add("SimpleViolet") ;
theme.add("SimpleOrange") ;
theme.add("SimplePink") ;
theme.add("SimpleGreen") ;
SkinService.addTheme("Simple", theme);

theme = new ArrayList(5) ;
theme.add("RoundComerBlue") ;
theme.add("RoundComerViolet") ;
theme.add("RoundComerOrange") ;
theme.add("RoundComerPink") ;
theme.add("RoundComerGreen") ;
SkinService.addTheme("RoundComer", theme);

theme = new ArrayList(5) ;
theme.add("ShadowBlue") ;
theme.add("ShadowViolet") ;
theme.add("ShadowOrange") ;
theme.add("ShadowPink") ;
theme.add("ShadowGreen") ;
SkinService.addTheme("Shadow", theme);

theme = new ArrayList(5) ;
theme.add("MiscBlue") ;
theme.add("MiscViolet") ;
theme.add("MiscOrange") ;
theme.add("MiscPink") ;
theme.add("MiscGreen") ;
SkinService.addTheme("Misc", theme);