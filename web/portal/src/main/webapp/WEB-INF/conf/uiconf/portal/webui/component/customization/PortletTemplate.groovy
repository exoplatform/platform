import org.exoplatform.webui.component.model.SelectItemOption ;
import org.exoplatform.webui.component.model.SelectItemCategory ;
import java.util.List;
import java.util.ArrayList;

  List options = new ArrayList() ;
  
  SelectItemCategory item  = new SelectItemCategory("Default");
  item.addSelectItemOption(new SelectItemOption("Portlet template",
                           "war:/groovy/portal/webui/component/view/UIPortlet.gtmpl",
                           "Description","Default"));
  
  options.add(item); 

return options;

