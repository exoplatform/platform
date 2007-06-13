import org.exoplatform.webui.core.model.SelectItemOption ;
import org.exoplatform.webui.core.model.SelectItemCategory ;
import java.util.List;
import java.util.ArrayList;

  List options = new ArrayList() ;
  
  SelectItemCategory itemDefault  = new SelectItemCategory("Default");
  itemDefault.addSelectItemOption(new SelectItemOption("Page template",
                                  "system:/groovy/portal/webui/page/UIPage.gtmpl",
                                  "Description", "Default", true));  
  
  SelectItemCategory itemDesktop  = new SelectItemCategory("Desktop");
  itemDesktop.addSelectItemOption(new SelectItemOption("Page template",
                                  "system:/groovy/portal/webui/page/UIPageDesktop.gtmpl",
                                  "Description", "Desktop", false));  
  options.add(itemDefault);
  options.add(itemDesktop);

return options;

