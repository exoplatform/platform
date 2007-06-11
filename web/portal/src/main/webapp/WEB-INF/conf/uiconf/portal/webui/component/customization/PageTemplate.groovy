import org.exoplatform.webui.bean.SelectItemOption ;
import org.exoplatform.webui.bean.SelectItemCategory ;
import java.util.List;
import java.util.ArrayList;

  List options = new ArrayList() ;
  
  SelectItemCategory itemDefault  = new SelectItemCategory("Default");
  itemDefault.addSelectItemOption(new SelectItemOption("Page template",
                                  "system:/groovy/portal/webui/component/view/UIPage.gtmpl",
                                  "Description", "Default", true));  
  
  SelectItemCategory itemDesktop  = new SelectItemCategory("Desktop");
  itemDesktop.addSelectItemOption(new SelectItemOption("Page template",
                                  "system:/groovy/portal/webui/component/view/UIPageDesktop.gtmpl",
                                  "Description", "Desktop", false));  
  options.add(itemDefault);
  options.add(itemDesktop);

return options;

